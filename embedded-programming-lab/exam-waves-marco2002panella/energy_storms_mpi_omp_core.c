#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <mpi.h>
#include "energy_storms.h"
#include <string.h>

/* THIS FUNCTION CAN BE MODIFIED */
/* Function to update a single position of the layer */
//risolto problema di parallelizzazione
static void update( float *layer, int layer_size, int k, int pos, float energy, int rank, int size) {
    /* 1. Compute the absolute value of the distance between the
        impact position and the k-th position of the layer */
    int posizione_globale;
    posizione_globale=k+(rank*(layer_size/size));
    int distance = pos - posizione_globale;
    if ( distance < 0 ) distance = - distance;

    /* 2. Impact cell has a distance value of 1 */
    distance = distance + 1;

    /* 3. Square root of the distance */
    /* NOTE: Real world atenuation typically depends on the square of the distance.
       We use here a tailored equation that affects a much wider range of cells */
    float atenuacion = sqrtf( (float)distance );

    /* 4. Compute attenuated energy */
    float energy_k = energy / layer_size / atenuacion;

    /* 5. Do not add if its absolute value is lower than the threshold */
    if ( energy_k >= THRESHOLD / layer_size || energy_k <= -THRESHOLD / layer_size )
        layer[k] = layer[k] + energy_k;
}

void core(int layer_size, int num_storms, Storm *storms, float *maximum, int *positions) {
    int rank, size;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    MPI_Comm_size(MPI_COMM_WORLD, &size);

    // 1. Calcolo dimensioni e offset
    int base_size = layer_size / size;
    int local_layer_size = (rank == size - 1) ? (base_size + layer_size % size) : base_size;
    int offset = rank * base_size;

    float *thisLayer = (float *)calloc(local_layer_size, sizeof(float));
    float *layer_copy = (float *)calloc(local_layer_size, sizeof(float));

    float rcv_L = 0.0f, rcv_R = 0.0f;
    float peak_rcv_L = 0.0f, peak_rcv_R = 0.0f;
    float globalMax;
    int globalPos;

    #pragma omp parallel
    {
        for (int i = 0; i < num_storms; i++) {
            
            // --- 4.1 AGGIORNAMENTO ENERGIA ---
            #pragma omp for schedule(static)
            for (int k = 0; k < local_layer_size; k++) {
                for (int j = 0; j < storms[i].size; j++) {
                    float energy = (float)storms[i].posval[j*2+1] * 1000;
                    int position = storms[i].posval[j*2];
                    update(thisLayer, layer_size, k, position, energy, rank, size);
                }
            }

            // --- 4.2 RILASSAMENTO ---
            // Sincronizzazione: tutti devono aver finito l'update prima di copiare
            #pragma omp barrier 
            
            #pragma omp single
            memcpy(layer_copy, thisLayer, local_layer_size * sizeof(float));

            #pragma omp master
            {
                // Scambio bordi per il rilassamento
                MPI_Request reqs[4];
                if (rank > 0) {
                    MPI_Irecv(&rcv_L, 1, MPI_FLOAT, rank - 1, 0, MPI_COMM_WORLD, &reqs[0]);
                    MPI_Isend(&layer_copy[0], 1, MPI_FLOAT, rank - 1, 1, MPI_COMM_WORLD, &reqs[1]);
                } else { reqs[0] = reqs[1] = MPI_REQUEST_NULL; }
                
                if (rank < size - 1) {
                    MPI_Irecv(&rcv_R, 1, MPI_FLOAT, rank + 1, 1, MPI_COMM_WORLD, &reqs[2]);
                    MPI_Isend(&layer_copy[local_layer_size-1], 1, MPI_FLOAT, rank + 1, 0, MPI_COMM_WORLD, &reqs[3]);
                } else { reqs[2] = reqs[3] = MPI_REQUEST_NULL; }
                
                MPI_Waitall(4, reqs, MPI_STATUSES_IGNORE);
            }
            
            #pragma omp barrier 

            // Calcolo rilassamento (tutte le celle tranne quelle esterne)
            #pragma omp for schedule(static)
            for (int k = 0; k < local_layer_size; k++) {
                if ((rank == 0 && k == 0) || (rank == size - 1 && k == local_layer_size - 1))
                    continue;

                float prev = (k == 0) ? rcv_L : layer_copy[k-1];
                float next = (k == local_layer_size - 1) ? rcv_R : layer_copy[k+1];
                thisLayer[k] = (prev + layer_copy[k] + next) / 3.0f;
            }

    #pragma omp master
    {
        MPI_Request peak_reqs[4];

        if (rank > 0) {
            MPI_Irecv(&peak_rcv_L, 1, MPI_FLOAT, rank - 1, 2,
                    MPI_COMM_WORLD, &peak_reqs[0]);

            MPI_Isend(&thisLayer[0], 1, MPI_FLOAT, rank - 1, 3,
                    MPI_COMM_WORLD, &peak_reqs[1]);
        } else {
            peak_reqs[0] = peak_reqs[1] = MPI_REQUEST_NULL;
        }

        if (rank < size - 1) {
            MPI_Irecv(&peak_rcv_R, 1, MPI_FLOAT, rank + 1, 3,
                    MPI_COMM_WORLD, &peak_reqs[2]);

            MPI_Isend(&thisLayer[local_layer_size - 1], 1, MPI_FLOAT,
                    rank + 1, 2,
                    MPI_COMM_WORLD, &peak_reqs[3]);
        } else {
            peak_reqs[2] = peak_reqs[3] = MPI_REQUEST_NULL;
        }

            MPI_Waitall(4, peak_reqs, MPI_STATUSES_IGNORE);
        }

    #pragma omp barrier

                // --- 4.3 RICERCA MASSIMO ---
                // Resettiamo i massimi del rank (solo uno lo fa)
                #pragma omp single
                {
                    globalMax = -1.0f;
                    globalPos = -1;
                }

                float threadMax = -1.0f;
                int threadPos = -1;

                #pragma omp for schedule(static)
                for (int k = 0; k < local_layer_size; k++) {
                    if ((rank == 0 && k == 0) || (rank == size - 1 && k == local_layer_size - 1))
                        continue;

                    float prev = (k == 0) ? peak_rcv_L : thisLayer[k-1];
                    float next = (k == local_layer_size - 1) ? peak_rcv_R : thisLayer[k+1];

                    if (thisLayer[k] > prev && thisLayer[k] > next) {
                        if (thisLayer[k] > threadMax) {
                            threadMax = thisLayer[k];
                            threadPos = k;
                        }
                    }
                }

                #pragma omp critical
                {
                    if (threadMax > globalMax) {
                        globalMax = threadMax;
                        globalPos = threadPos;
                    }
                }

                // --- 4.4 RIDUZIONE MPI ---
                #pragma omp barrier
                #pragma omp master
                {
                    struct { double val; int rank; } local_res, global_res;
                    local_res.val = globalMax;
                    local_res.rank = (globalPos != -1) ? (globalPos + offset) : -1;

                    MPI_Reduce(&local_res, &global_res, 1, MPI_DOUBLE_INT, MPI_MAXLOC, 0, MPI_COMM_WORLD);
                    if (rank == 0) {
                        maximum[i] = global_res.val;
                        positions[i] = global_res.rank;
                    }
                }
                #pragma omp barrier // Fine tempesta
            }
        }
        free(thisLayer); free(layer_copy);
}