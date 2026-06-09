package api

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/marco2002panella/WASAtext/service/database"
)

func (a *AppHandler) userListChats(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	//restituisce la lista di chat
	switch r.Method {
	case http.MethodGet:
		n, err1 := strconv.Atoi(parts[1])
		if err1 != nil {
			// Se l'ID nel path non è un numero, è un Bad Request (400), non un 500
			http.Error(w, "ID utente non valido", http.StatusBadRequest)
			return
		}
		if token != parts[1] {
			http.Error(w, "Not authorized", http.StatusUnauthorized)
			return
		}

		//autorizzazione riuscita vediamo se ci sono parametri nelle query
		queryParams := r.URL.Query()
		sort := queryParams.Get("sort")
		identifier := queryParams.Get("identifier")
		if sort == "" {
			sort = "desc"
		}

		chats, err3 := database.ListConversations(a.DB, int64(n), sort, identifier)
		if err3 != nil {
			http.Error(w, "Internal server error", http.StatusInternalServerError)
			return
		}
		//risposta in json
		w.WriteHeader(http.StatusOK)
		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(chats)
		return

	default:
		http.Error(w, "method not allowed", http.StatusBadRequest)
		return
	}

}
