package api

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/marco2002panella/WASAtext/service/database"
)

func (a *AppHandler) handleGroupPhoto(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	if len(parts) < 5 {
		http.Error(w, "Invalid URL", http.StatusBadRequest)
		return
	}
	if r.Method == http.MethodPut {
		a.setGroupPhoto(w, r, token, parts)
	} else if r.Method == http.MethodGet {
		a.getGroupPhoto(w, r, token, parts)
	} else {
		http.Error(w, "method not allowed", http.StatusBadRequest)
		return
	}
}

func (a *AppHandler) setGroupPhoto(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	if r.Method != http.MethodPut {
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
	type UpdateRequest struct {
		URL string `json:"url"`
	}

	var req UpdateRequest

	// 2. Leggi il Body
	err := json.NewDecoder(r.Body).Decode(&req)
	if err != nil {
		http.Error(w, "JSON non valido", http.StatusBadRequest)
		return
	}
	err5 := database.SetGroupPhoto(a.DB, int64(chatid), req.URL, int64(userid))
	if err5 != nil {
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}
	w.WriteHeader(http.StatusOK)
	return
}

func (a *AppHandler) getGroupPhoto(w http.ResponseWriter, r *http.Request, token string, parts []string) {
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
	photoURL, err5 := database.GetGroupPhoto(a.DB, int64(chatid))
	if err5 != nil {
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}
	type PhotoResponse struct {
		URL string `json:"url"`
	}
	resp := PhotoResponse{URL: photoURL}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(resp)
	return
}
