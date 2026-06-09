package api

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/marco2002panella/WASAtext/service/database"
)

// working
func (a *AppHandler) listComments(w http.ResponseWriter, r *http.Request, token string, parts []string) {
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

	if r.Method != http.MethodGet {
		http.Error(w, "method not allowed", http.StatusBadRequest)
		return
	}
	messageid, err1 := strconv.Atoi(parts[5])
	if err1 != nil {
		http.Error(w, "id not valid", http.StatusBadRequest)
		return
	}
	query := r.URL.Query()
	isGroupStr := query.Get("isGroup")
	isGroup := isGroupStr == "true"
	if !database.IsMember(a.DB, int(userid), int(chatid), isGroup) {
		http.Error(w, "Not authorized: user is not a member of the chat", http.StatusUnauthorized)
		return
	}
	msgbc, err5 := database.MessageBelongsToContext(a.DB, int64(messageid), int64(chatid), isGroup)
	if !msgbc {
		http.Error(w, "Message does not belong to the specified context", http.StatusNotFound)
		return
	}
	if err5 != nil {
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}
	comments, err2 := database.GetReactions(a.DB, int64(messageid))
	if err2 != nil {
		http.Error(w, "message not found", http.StatusNotFound)
		return
	}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(comments)
	w.WriteHeader(http.StatusOK)
	return
}
