package api

import (
	"net/http"
	"strconv"

	"github.com/marco2002panella/WASAtext/service/database"
)

func (a *AppHandler) leaveGroup(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	if r.Method != http.MethodDelete {
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
	err5 := database.LeaveGroup(a.DB, int64(userid), int64(chatid))
	if err5 != nil {
		http.Error(w, "Internal Server Error", http.StatusInternalServerError)
		return
	}
	w.WriteHeader(http.StatusOK)
	return
}
