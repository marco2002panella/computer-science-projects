package api

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/marco2002panella/WASAtext/service/database"
)

func (a *AppHandler) getAdmins(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	if r.Method != http.MethodGet {
		http.Error(w, "method not allowed", http.StatusBadRequest)
		return
	}
	if token != parts[1] {
		http.Error(w, "Not authorized", http.StatusUnauthorized)
		return
	}
	userid, err3 := strconv.Atoi(parts[1])
	if err3 != nil {
		http.Error(w, "ID utente non valido", http.StatusBadRequest)
		return
	}

	chatid, err4 := strconv.Atoi(parts[3])
	if err4 != nil {
		http.Error(w, "ID chat non valido", http.StatusBadRequest)
		return
	}
	if !database.IsMember(a.DB, int(userid), int(chatid), true) {
		http.Error(w, "Not authorized: user is not a member of the chat", http.StatusUnauthorized)
		return
	}
	admins, err5 := database.GetGroupAdmins(a.DB, int64(chatid))
	if err5 != nil {
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(admins)
	w.WriteHeader(http.StatusOK)
}
