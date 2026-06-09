package api

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/marco2002panella/WASAtext/service/database"
)

// get
func (a *AppHandler) userGetChat(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	if token != parts[1] {
		http.Error(w, "Not authorized", http.StatusUnauthorized)
		return
	}
	n, err3 := strconv.Atoi(parts[1])
	if err3 != nil {
		http.Error(w, "ID utente non valido", http.StatusBadRequest)
		return
	}
	if r.Method != http.MethodGet {
		http.Error(w, "method not allowed", http.StatusBadRequest)
		return
	}
	//restituisce i dettagli della chat con id parts[3]
	//converto l'id in numero
	chatid, err1 := strconv.Atoi(parts[3])
	if err1 != nil {
		http.Error(w, "id not valid", http.StatusBadRequest)
		return
	}
	query := r.URL.Query()
	sort := query.Get("sort")
	isGroupStr := query.Get("isGroup")
	if isGroupStr != "true" && isGroupStr != "false" {
		isGroupStr = "false"
	}
	isGroup := isGroupStr == "true"

	messages, err2 := database.GetChat(a.DB, int64(chatid), sort, isGroup, int64(n))
	if err2 != nil {
		http.Error(w, "chat not found", http.StatusNotFound)
		return
	}
	//risposta in json
	w.WriteHeader(http.StatusOK)
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(messages)
	return

}
