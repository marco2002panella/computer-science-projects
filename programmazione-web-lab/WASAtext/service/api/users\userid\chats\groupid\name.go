package api

import (
	"encoding/json"
	"net/http"
	"strconv"

	"github.com/marco2002panella/WASAtext/service/database"
)

func (a *AppHandler) handleGroupName(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	if len(parts) < 5 {
		http.Error(w, "Invalid URL", http.StatusBadRequest)
		return
	}
	if r.Method == http.MethodPut {
		a.changeGroupName(w, r, token, parts)
	} else if r.Method == http.MethodGet {
		a.getGroupName(w, r, token, parts)
	} else {
		http.Error(w, "method not allowed", http.StatusBadRequest)
		return
	}
}

func (a *AppHandler) changeGroupName(w http.ResponseWriter, r *http.Request, token string, parts []string) {
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
		Name string `json:"name"`
	}

	var req UpdateRequest

	// 2. Leggi il Body
	err := json.NewDecoder(r.Body).Decode(&req)
	if err != nil {
		http.Error(w, "JSON non valido", http.StatusBadRequest)
		return
	}
	err5 := database.ChangeGroupName(a.DB, int64(chatid), req.Name, int64(userid))
	if err5 != nil {
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}
	w.WriteHeader(http.StatusOK)
	return
}

func (a *AppHandler) getGroupName(w http.ResponseWriter, r *http.Request, token string, parts []string) {
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

	name, err5 := database.GetGroupName(a.DB, int64(chatid))
	if err5 != nil {
		http.Error(w, "group not found", http.StatusNotFound)
		return
	}
	w.WriteHeader(http.StatusOK)
	w.Write([]byte(name))
	return
}
