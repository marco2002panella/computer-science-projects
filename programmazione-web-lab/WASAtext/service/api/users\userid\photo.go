package api

import (
	"encoding/json"
	"io"
	"net/http"
	"strconv"
	"strings"

	"github.com/marco2002panella/WASAtext/service/database"
)

func (a *AppHandler) UserPhotoHandle(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	switch r.Method {
	case http.MethodGet:
		n, err1 := strconv.Atoi(parts[1])
		if err1 != nil {
			// Se l'ID nel path non è un numero, è un Bad Request (404)
			http.Error(w, "ID utente non valido", http.StatusBadRequest)
			return
		}

		// Chiamata al database
		content, err := database.GetProfileURL(a.DB, n)

		// GESTIONE ERRORE: Fondamentale per non mandare risposte vuote
		if err != nil {
			http.Error(w, "Foto non trovata o utente inesistente", http.StatusNotFound)
			return
		}

		// Se arriviamo qui, err è nil, quindi procediamo con la risposta
		w.Header().Set("Content-Type", "application/json")
		// Nota: WriteHeader va chiamato DOPO aver impostato gli header e PRIMA di scrivere il corpo
		w.WriteHeader(http.StatusOK)

		response := map[string]string{
			"url": content,
		}

		// Encode scrive direttamente nel ResponseWriter (w)
		json.NewEncoder(w).Encode(response)
		return
	case http.MethodPut:
		n, err1 := strconv.Atoi(parts[1])
		if err1 != nil {
			// Se l'ID nel path non è un numero, è un Bad Request (404)
			http.Error(w, "ID utente non valido", http.StatusBadRequest)
			return
		}
		if token != parts[1] {
			http.Error(w, "Not authorized", http.StatusUnauthorized)
			return
		}
		type requestBody struct {
			URL string `json:"url"`
		}

		var req requestBody
		body, err2 := io.ReadAll(r.Body)
		if err2 != nil {
			http.Error(w, "Invalid request body", http.StatusBadRequest)
			return
		}
		err2 = json.Unmarshal(body, &req)
		if err2 != nil {
			http.Error(w, "Invalid JSON", http.StatusBadRequest)
			return
		}
		content := strings.TrimSpace(req.URL)
		if content == "" {
			http.Error(w, "URL non può essere vuoto", http.StatusBadRequest)
			return
		}
		err3 := database.UpdateProfileURL(a.DB, n, content)
		if err3 != nil {
			http.Error(w, "Errore nell'aggiornamento dell' image_url", http.StatusInternalServerError)
			return
		}
		w.WriteHeader(http.StatusOK)
		return
	default:
		http.Error(w, "method not allowed", http.StatusBadRequest)
		return
	}
}
