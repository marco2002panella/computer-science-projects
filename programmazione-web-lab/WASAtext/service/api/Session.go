package api

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net/http"

	"github.com/julienschmidt/httprouter"
	"github.com/marco2002panella/WASAtext/service/database"
	"github.com/sirupsen/logrus"
)

type loginRequest struct {
	Name string `json:"name"`
}

type AppHandler struct {
	DB         *sql.DB
	BaseLogger *logrus.Logger
}

func (a *AppHandler) Session(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	if r.Method == http.MethodPost {
		body, err := io.ReadAll(r.Body)
		if err != nil {
			http.Error(w, "errore nella lettura dello stream di dati", http.StatusBadRequest)
			return
		}
		var req loginRequest
		if err := json.Unmarshal(body, &req); err != nil {
			http.Error(w, "JSON non valido", http.StatusBadRequest)
			return
		}
		// Validazione minima
		if len(req.Name) < 3 || len(req.Name) > 24 {
			http.Error(w, "Il campo 'name' deve avere tra 3 e 24 caratteri", http.StatusBadRequest)
			return
		}
		//retrive user from database
		id, err := database.CreateUser(a.DB, req.Name, nil)
		if err != nil {
			http.Error(w, "errore nel retrive dell'userIdentifier", http.StatusInternalServerError)
			log.Println("Database error:", err)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusCreated)
		type identifierResponse struct {
			Identifier string `json:"identifier"`
		}
		var resp identifierResponse
		resp.Identifier = fmt.Sprintf("%d", id)
		json.NewEncoder(w).Encode(resp)
		return

	} else {
		w.WriteHeader(http.StatusMethodNotAllowed)
		w.Write([]byte("Metodo non supportato"))
		return
	}
}
