package api

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/marco2002panella/WASAtext/service/database"
)

func (a *AppHandler) addToGroup(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	if r.Method != http.MethodPost {
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
	type AddToGroupRequest struct {
		UserID []int `json:"user_ids"`
	}
	var req AddToGroupRequest
	err2 := json.NewDecoder(r.Body).Decode(&req)
	if err2 != nil {
		http.Error(w, "Invalid JSON", http.StatusBadRequest)
		return
	}
	for _, id := range req.UserID {
		err5 := database.AddUserToGroup(a.DB, int64(id), int64(chatid), int64(userid))
		if err5 != nil {
			http.Error(w, "Internal Server Error", http.StatusInternalServerError)
			return
		}
	}
	w.WriteHeader(http.StatusOK)
	return
}
