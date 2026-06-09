package api

import (
	"net/http"
	"strconv"

	"github.com/marco2002panella/WASAtext/service/database"
)

func (a *AppHandler) deleteMessage(w http.ResponseWriter, r *http.Request, token string, parts []string) {
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

	if r.Method != http.MethodDelete {
		http.Error(w, "method not allowed", http.StatusBadRequest)
		return
	}
	//elimina il messaggio con id parts[5]
	messageid, err1 := strconv.Atoi(parts[5])
	if err1 != nil {
		http.Error(w, "id not valid", http.StatusBadRequest)
		return
	}
	query := r.URL.Query()
	isGroupStr := query.Get("isGroup")
	isGroup := isGroupStr == "true"
	//verifico se l'utente è membro della chat
	if !database.IsMember(a.DB, int(userid), int(chatid), isGroup) {
		http.Error(w, "Not authorized: user is not a member of the chat", http.StatusUnauthorized)
		return
	}
	err2 := database.DeleteMessage(a.DB, int64(messageid), int64(userid), int64(chatid), isGroup)
	if err2 != nil {
		http.Error(w, "message not found or not authorized to delete", http.StatusNotFound)
		return
	}
	w.WriteHeader(http.StatusOK)
	return
}
