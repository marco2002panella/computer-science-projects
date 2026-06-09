package api

import (
	"encoding/json"
	"io"
	"net/http"
	"strconv"

	"github.com/marco2002panella/WASAtext/service/database"
)

// users/{userId}/username metodo accettabile: put, modifica lo username
func (a *AppHandler) UserNameHandle(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	if r.Method == http.MethodPut {
		//operazione di aggiornamento nel database
		//converto l'id in numero
		n, err1 := strconv.Atoi(parts[1])
		if err1 != nil {
			http.Error(w, "id not valid", http.StatusBadRequest)
			return
		}

		if token != parts[1] {
			http.Error(w, "Not authorized", http.StatusUnauthorized)
			return
		}

		type requestBody struct {
			Username string `json:"name"`
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

		err3 := database.UpdateUsername(a.DB, n, req.Username)
		if err3 != nil {
			http.Error(w, "internal server error", http.StatusInternalServerError)
			return
		}
	} else if r.Method == http.MethodGet {
		//operazione di lettura dal database
		n, err1 := strconv.Atoi(parts[1])
		if err1 != nil {
			http.Error(w, "id not valid", http.StatusBadRequest)
			return
		}

		username, err2 := database.GetUsername(a.DB, n)
		if err2 != nil {
			http.Error(w, "internal server error", http.StatusInternalServerError)
			return
		}

		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(map[string]string{"username": username})
	} else {
		w.WriteHeader(http.StatusBadRequest)
		http.Error(w, "method not allowed", http.StatusBadRequest)
		return
	}
}
