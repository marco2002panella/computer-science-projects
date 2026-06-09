package api

import (
	"encoding/json"
	"io"
	"net/http"
	"strconv"

	"github.com/marco2002panella/WASAtext/service/database"
)

func (a *AppHandler) userCreateChat(w http.ResponseWriter, r *http.Request, token string, parts []string) {
	if token != parts[1] {
		http.Error(w, "Not authorized", http.StatusUnauthorized)
		return
	}
	if r.Method != http.MethodPost {
		http.Error(w, "method not allowed", http.StatusBadRequest)
		return
	}
	//creazione chat, prendo dal body della richiesta il nome della chat e se è una chat normale o di gruppo
	body, err1 := io.ReadAll(r.Body)
	if err1 != nil {
		http.Error(w, "Internal error", http.StatusInternalServerError)
		return
	}

	type ChatCreationRequest struct {
		Type    string  `json:"type"` // "chat" o "group"
		Name    string  `json:"name"` // opzionale
		UserIDs []int64 `json:"user_ids"`
	}
	var chatReq ChatCreationRequest

	err := json.Unmarshal(body, &chatReq)
	if err != nil {
		http.Error(w, "Invalid JSON", http.StatusBadRequest)
		return
	}

	// aggiungo l'utente che crea la chat se non è già presente
	creatoreID, err := strconv.Atoi(parts[1])
	if err != nil {
		http.Error(w, "ID utente non valido", http.StatusBadRequest)
		return
	}

	// validazione base
	if chatReq.Type != "chat" && chatReq.Type != "group" {
		http.Error(w, "type non valido", 400)
		return
	}

	// controllo nome solo per gruppi
	if chatReq.Type == "group" && chatReq.Name == "" {
		http.Error(w, "name richiesto per gruppi", 400)
		return
	}
	if len(chatReq.UserIDs) == 0 || (len(chatReq.UserIDs) == 1 && chatReq.UserIDs[0] == int64(creatoreID)) {
		http.Error(w, "Almeno 1 utenti richiesto diverso dal richiedente", 400)
		return
	}
	alreadyPresent := false
	for _, id := range chatReq.UserIDs {
		if id == int64(creatoreID) {
			alreadyPresent = true
			break
		}
	}
	if !alreadyPresent {
		chatReq.UserIDs = append(chatReq.UserIDs, int64(creatoreID))
	}

	// chiamata al database per creare la chat o il gruppo
	var chatID int64
	if chatReq.Type == "chat" && len(chatReq.UserIDs) == 2 {
		chatID, err = database.CreateChat(a.DB, chatReq.UserIDs)
	} else if chatReq.Type == "group" {
		chatID, err = database.CreateGroup(a.DB, chatReq.Name, chatReq.UserIDs)
	} else {
		http.Error(w, "Invalid request: chat type 'chat' requires exactly 2 users", http.StatusBadRequest)
		return
	}
	if err != nil {
		http.Error(w, "Errore nella creazione della chat/gruppo", http.StatusInternalServerError)
		return
	}
	// risposta con ID della chat/gruppo creato
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	var success string = "Chat creata con successo " + string(chatID)
	w.Write([]byte(success))
}
