package api

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/marco2002panella/WASAtext/service/database"
)

func (a *AppHandler) sendMessage(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	if token != parts[1] {
		http.Error(w, "Not authorized", http.StatusUnauthorized)
		return
	}
	n, err := strconv.Atoi(parts[1])
	if err != nil {
		http.Error(w, "ID utente non valido", http.StatusBadRequest)
		return
	}
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusBadRequest)
		return
	}
	query := r.URL.Query()
	isGroupStr := query.Get("isGroup")
	isGroup := isGroupStr == "true"

	//inserisce un nuovo messaggio nella chat con id parts[3]
	chatid, err1 := strconv.Atoi(parts[3])
	if err1 != nil {
		http.Error(w, "id not valid", http.StatusBadRequest)
	}

	type MessageRequest struct {
		Text        string `json:"text"`
		ContentType string `json:"contentType"` // "text", "photo"
	}
	var msgReq MessageRequest
	err2 := json.NewDecoder(r.Body).Decode(&msgReq)
	if err2 != nil {
		http.Error(w, "Invalid JSON", http.StatusBadRequest)
		return
	}
	_, err = database.CreateMessage(a.DB, int64(chatid), int64(n), msgReq.Text, msgReq.ContentType, isGroup)
	if err != nil {
		http.Error(w, "Error creating message", http.StatusInternalServerError)
		return
	}
	w.WriteHeader(http.StatusOK)
	return
}
