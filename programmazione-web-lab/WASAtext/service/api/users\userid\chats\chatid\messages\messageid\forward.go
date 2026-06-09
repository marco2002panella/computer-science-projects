package api

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/marco2002panella/WASAtext/service/database"
)

func (a *AppHandler) forwardMessage(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	if r.Method != http.MethodPost {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}
	if len(parts) != 7 || parts[6] != "forward" {
		http.NotFound(w, r)
		return
	}

	if token != parts[1] {
		http.Error(w, "Not authorized", http.StatusUnauthorized)
		return
	}
	id, err := strconv.Atoi(parts[1])
	chatid, err1 := strconv.Atoi(parts[3])
	messageid, err2 := strconv.Atoi(parts[5])
	if err != nil || err1 != nil || err2 != nil {
		http.Error(w, "ID not valid", http.StatusBadRequest)
		return
	}

	query := r.URL.Query()
	targetIsGroupStr := query.Get("targetIsGroup")
	targetIsGroup := targetIsGroupStr == "true"
	isGroupStr := query.Get("isGroup")
	isGroup := isGroupStr == "true"
	// Verifica se l'utente è membro della chat
	if database.IsMember(a.DB, int(id), int(chatid), isGroup) {
		// Se è membro, procedi con l'inoltro del messaggio
		type requestJSON struct {
			TargetChatID int64 `json:"targetChatId"`
		}
		var req requestJSON
		err := json.NewDecoder(r.Body).Decode(&req)
		if err != nil {
			http.Error(w, "Invalid JSON", http.StatusBadRequest)
			return
		}
		err4 := database.ForwardMessage(a.DB, int64(messageid), int64(chatid), int64(req.TargetChatID), int64(id), targetIsGroup, isGroup)
		if err4 != nil {
			http.Error(w, "Internal Server Error", http.StatusInternalServerError)
			return
		}
		w.WriteHeader(http.StatusOK)
		return
	} else {
		http.Error(w, "Not authorized: user is not a member of the chat", http.StatusUnauthorized)
		return
	}
}
