package api

import (
	"encoding/json"
	"net/http"

	"github.com/marco2002panella/WASAtext/service/database"
)

func (a *AppHandler) searchByName(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusBadRequest)
		return
	}
	type requestBody struct {
		Prefix string `json:"prefix"`
	}
	var body requestBody
	err := json.NewDecoder(r.Body).Decode(&body)
	if err != nil {
		http.Error(w, "Invalid JSON", http.StatusBadRequest)
		return
	}

	users, err4 := database.SearchUsersByPrefix(a.DB, body.Prefix)
	if err4 != nil {
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(users)
	w.WriteHeader(http.StatusOK)

}
