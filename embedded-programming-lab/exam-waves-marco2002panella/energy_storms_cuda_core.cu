#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "energy_storms.h"

/* THIS FUNCTION CAN BE MODIFIED */
/* Function to update a single position of the layer */
__global__ void update( float *layer, int layer_size, int pos, float energy ) {
    /* 1. Compute the absolute value of the distance between the
        impact position and the k-th position of the layer */
    int k=blockIdx.x * blockDim.x + threadIdx.x;
     if (k >= layer_size) return;
    int distance = pos - k;
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

__global__ void relax(float *layer, float *layer_copy, int layer_size) {
    int k=blockIdx.x * blockDim.x + threadIdx.x;
     if (k >= layer_size) return;
    if (k > 0 && k < layer_size - 1)
        layer[k] = ( layer_copy[k-1] + layer_copy[k] + layer_copy[k+1] ) / 3;
}

__global__ void trovaMassimo(float *layer, int layer_size, float *maxVal, float *copy)
{
    int k = blockIdx.x * blockDim.x + threadIdx.x;

    if (k >= layer_size-1 || k<=0) return;

    float val = layer[k];

    if ( layer[k] > layer[k-1] && layer[k] > layer[k+1] ){
        atomicMax((int*)maxVal, __float_as_int(val));
        copy[k]=1;
    }
}

__global__ void trovaPosizione(float *layer,float *copy, int *pos, int layer_size, float *maxVal) {
    int k = blockIdx.x * blockDim.x + threadIdx.x;
    if (k >= layer_size-1 || k<=0) return;
    if (copy[k] == 1){
        if(layer[k] == *maxVal){
            *pos = k;
        }
    }
}


void core(int layer_size, int num_storms, Storm *storms, float *maximum, int *positions) {
    int i, j, k;
    int grid,block;
    block=256;
    grid = (layer_size + block - 1) / block;
    float *clayer;
    float *clayer_copy;
    float *d_max;
    int *d_pos;
    cudaMalloc(&d_max, sizeof(float));
    cudaMalloc(&d_pos, sizeof(int));
    cudaMalloc(&clayer,sizeof(float)*layer_size);
    cudaMalloc(&clayer_copy,sizeof(float)*layer_size);
    //inizializzo a zero con cudamemset
    cudaMemset(clayer, 0, layer_size * sizeof(float));
    cudaMemset(clayer_copy, 0, layer_size * sizeof(float));
    /* 4. Storms simulation */
    for( i=0; i<num_storms; i++) {
        /* 4.1. Add impacts energies to layer cells */
        /* For each particle */
        for( j=0; j<storms[i].size; j++ ) {
            /* Get impact energy (expressed in thousandths) */
            float energy = (float)storms[i].posval[j*2+1] * 1000;
            /* Get impact position */
            int position = storms[i].posval[j*2];
            update<<<grid,block>>>( clayer, layer_size, position, energy );
            
        }
        /* 4.2. Energy relaxation between storms */
        /* 4.2.1. Copy values to the ancillary array */
        cudaMemcpy(clayer_copy, clayer, sizeof(float) * layer_size, cudaMemcpyDeviceToDevice);
        /* 4.2.2. Update layer using the ancillary values.
                  Skip updating the first and last positions */
        relax<<<grid,block>>>( clayer, clayer_copy, layer_size );
        /* 4.3. Locate the maximum value in the layer, and its position */
        cudaMemset(d_max, 0, sizeof(float));
        cudaMemset(d_pos, 0, sizeof(int));
        cudaMemset(clayer_copy, 0, sizeof(float) * layer_size);
        trovaMassimo<<<grid,block>>>(clayer, layer_size, d_max, clayer_copy);
        trovaPosizione<<<grid,block>>>(clayer, clayer_copy, d_pos, layer_size, d_max);
        cudaDeviceSynchronize();
        cudaMemcpy(&maximum[i], d_max, sizeof(float), cudaMemcpyDeviceToHost);
        cudaMemcpy(&positions[i], d_pos, sizeof(int), cudaMemcpyDeviceToHost);
    }
}