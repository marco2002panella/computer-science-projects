package api

import (
	"encoding/json"
	"net/http"

	"github.com/julienschmidt/httprouter"
	"github.com/marco2002panella/WASAtext/service/database"
)

// we already have struct user in database package
func (a *AppHandler) ListUsers(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	//retriving the list of users
	users, err := database.GetAllUsers(a.DB)
	if err != nil {
		http.Error(w, "internal error", http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	if err := json.NewEncoder(w).Encode(users); err != nil {
		http.Error(w, "Errore nella codifica JSON", http.StatusInternalServerError)
	}
}
