package database

import (
	"database/sql"
	"errors"
	"fmt"
	"log"
	"sort"
	"strings"
	"time"
)

// Chat
type Chat struct {
	ID        int64     `json:"id"`
	Name      string    `json:"name"`
	Photo     string    `json:"photo"`
	Flag      bool      `json:"IsGroup"`
	CreatedAt time.Time `json:"createdAt"`
}

// Struttura di supporto temporanea per l'ordinamento
type chatWithDate struct {
	chat            Chat
	lastMessageTime time.Time
}

// OrderByFilter prende l'array di chat miste, interroga il DB per trovare l'ultimo messaggio
// di ciascuna, le ordina (ASC o DESC) e restituisce l'array di Chat ordinato.
func OrderByFilter(db *sql.DB, chats []Chat, direction string) ([]Chat, error) {
	// Se l'array è vuoto, non c'è nulla da fare
	if len(chats) == 0 {
		return chats, nil
	}

	// 1. Creiamo l'array di supporto temporaneo
	tempList := make([]chatWithDate, 0, len(chats))

	// 2. Giriamo su ogni chat per cercare la data dell'ultimo messaggio sul DB
	for _, c := range chats {
		var lastTimeStr sql.NullString
		var err error

		if c.Flag {
			// È UN GRUPPO: cerchiamo l'ultimo messaggio usando group_id
			err = db.QueryRow(`
				SELECT MAX(created_at) 
				FROM messages 
				WHERE group_id = ?`, c.ID).Scan(&lastTimeStr)
		} else {
			// È UNA CHAT PRIVATA: cerchiamo l'ultimo messaggio usando chat_id
			err = db.QueryRow(`
				SELECT MAX(created_at) 
				FROM messages 
				WHERE chat_id = ?`, c.ID).Scan(&lastTimeStr)
		}

		if err != nil {
			return nil, err
		}

		var finalTime time.Time
		if lastTimeStr.Valid && lastTimeStr.String != "" {
			finalTime, err = time.Parse("2006-01-02 15:04:05", lastTimeStr.String)
			if err != nil {
				// Ripiego in formato RFC3339 se usi quello
				finalTime, _ = time.Parse(time.RFC3339, lastTimeStr.String)
			}
		} else {
			finalTime = c.CreatedAt
		}

		// Aggiungiamo l'elemento alla nostra lista di supporto
		tempList = append(tempList, chatWithDate{
			chat:            c,
			lastMessageTime: finalTime,
		})
	}

	// 3. Facciamo il Sorting della lista temporanea in base a lastMessageTime
	dir := strings.ToUpper(strings.TrimSpace(direction))
	sort.Slice(tempList, func(i, j int) bool {
		if dir == "ASC" {
			return tempList[i].lastMessageTime.Before(tempList[j].lastMessageTime)
		}
		// Default DESC (messaggi più recenti in alto)
		return tempList[i].lastMessageTime.After(tempList[j].lastMessageTime)
	})

	// 4. Estraiamo solo le Chat pulite dall'array temporaneo ordinato
	sortedChats := make([]Chat, len(tempList))
	for i, item := range tempList {
		sortedChats[i] = item.chat

	}

	return sortedChats, nil
}

func IsMember(db *sql.DB, id int, chatID int, isGroup bool) bool {
	// Implementa la logica per verificare se l'utente è membro della chat
	// Puoi eseguire una query al database per controllare la tabella dei membri
	var isMember bool = false
	var authQuery string
	if isGroup {
		authQuery = "SELECT EXISTS(SELECT 1 FROM members WHERE user_id = ? AND group_id = ?)"
	} else {
		authQuery = "SELECT EXISTS(SELECT 1 FROM members WHERE user_id = ? AND chat_id = ?)"
	}

	err := db.QueryRow(authQuery, id, chatID).Scan(&isMember)
	if err != nil {
		return false
	}
	return isMember
}

func GetChatMembers(db *sql.DB, chatID int64, isGroup bool) ([]User, error) {
	// Log di ingresso per capire cosa sta cercando il backend
	contextType := "Chat Privata"
	if isGroup {
		contextType = "Gruppo"
	}
	fmt.Printf("[DEBUG] GetChatMembers - Tipo: %s, ID: %d\n", contextType, chatID)

	var users []User
	var query string

	if isGroup {
		query = `
            SELECT u.id, u.username, COALESCE(u.image_URL, '')
            FROM users u
            JOIN members m ON u.id = m.user_id
            WHERE m.group_id = ?`
	} else {
		query = `
            SELECT u.id, u.username, COALESCE(u.image_URL, '')
            FROM users u
            JOIN members m ON u.id = m.user_id
            WHERE m.chat_id = ?`
	}

	// Esecuzione Query
	rows, err := db.Query(query, chatID)
	if err != nil {
		fmt.Printf("[DEBUG] Errore Query GetChatMembers: %v\n", err)
		return nil, fmt.Errorf("errore query membri: %w", err)
	}
	defer rows.Close()

	// Iterazione dei risultati
	count := 0
	for rows.Next() {
		var user User
		// u.Url deve corrispondere al campo nel tuo struct User (es. string)
		if err := rows.Scan(&user.ID, &user.Username, &user.Url); err != nil {
			fmt.Printf("[DEBUG] Errore Scan utente al numero %d: %v\n", count+1, err)
			return nil, fmt.Errorf("errore scan membro: %w", err)
		}

		users = append(users, user)
		count++
		fmt.Printf("[DEBUG] Membro trovato: %s (ID: %d)\n", user.Username, user.ID)
	}

	// Controllo errori post-iterazione
	if err := rows.Err(); err != nil {
		fmt.Printf("[DEBUG] Errore durante l'iterazione dei membri: %v\n", err)
		return nil, err
	}

	fmt.Printf("[DEBUG] GetChatMembers completato. Totale membri trovati: %d\n", count)
	return users, nil
}

func GetChat(db *sql.DB, chatID int64, sort string, isGroup bool, requestingUserID int64) ([]Message, error) {
	fmt.Printf("[DEBUG] Inizio GetChat - chatID: %d, isGroup: %v, UserID: %d\n", chatID, isGroup, requestingUserID)

	if !IsMember(db, int(requestingUserID), int(chatID), isGroup) {
		fmt.Printf("[DEBUG] Accesso negato per utente %d su risorsa %d\n", requestingUserID, chatID)
		return nil, fmt.Errorf("accesso negato: l'utente non è membro di questa conversazione")
	}

	sortDir := strings.ToUpper(sort)
	if sortDir != "ASC" && sortDir != "DESC" {
		sortDir = "DESC"
	}

	targetColumn := "chat_id"
	if isGroup {
		targetColumn = "group_id"
	}
	fmt.Printf("[DEBUG] Ordinamento: %s, Colonna target: %s\n", sortDir, targetColumn)

	query := fmt.Sprintf(`
        SELECT id, chat_id, group_id, sender_id, content, contentType, created_at 
        FROM messages 
        WHERE %s = ? 
        ORDER BY created_at %s`, targetColumn, sortDir)

	rows, err := db.Query(query, chatID)
	if err != nil {
		fmt.Printf("[DEBUG] Errore esecuzione query: %v\n", err)
		return nil, fmt.Errorf("errore query messaggi: %w", err)
	}
	defer rows.Close()

	var messages []Message
	for rows.Next() {
		var m Message
		var createdAtStr string

		err := rows.Scan(
			&m.ID,
			&m.ChatID,
			&m.GroupID,
			&m.AuthorID,
			&m.Content,
			&m.ContentType,
			&createdAtStr,
		)
		if err != nil {
			fmt.Printf("[DEBUG] Errore durante rows.Scan: %v\n", err)
			return nil, fmt.Errorf("errore scan messaggio: %w", err)
		}

		parsedTime, err := time.Parse("2006-01-02 15:04:05", createdAtStr)
		if err != nil {
			fmt.Printf("[DEBUG] Formato data '2006-01-02 15:04:05' fallito, provo RFC3339 per: %s\n", createdAtStr)
			parsedTime, err = time.Parse(time.RFC3339, createdAtStr)
			if err != nil {
				fmt.Printf("[DEBUG] Fallita anche la conversione RFC3339 per: %s\n", createdAtStr)
			}
		}
		m.CreatedAt = parsedTime

		var countUnread int
		var checkQuery string

		if isGroup {
			checkQuery = `
                SELECT COUNT(*) 
                FROM members 
                WHERE group_id = ? AND user_id != ? AND last_read_message_id < ?`
		} else {
			checkQuery = `
                SELECT COUNT(*) 
                FROM members 
                WHERE chat_id = ? AND user_id != ? AND last_read_message_id < ?`
		}

		err = db.QueryRow(checkQuery, chatID, m.AuthorID, m.ID).Scan(&countUnread)
		if err != nil {
			m.AllRead = false
		} else {
			m.AllRead = (countUnread == 0)
		}

		messages = append(messages, m)
	}

	// Controllo se ci sono stati errori durante l'iterazione
	if err = rows.Err(); err != nil {
		fmt.Printf("[DEBUG] Errore iterazione rows: %v\n", err)
		return nil, err
	}

	fmt.Printf("[DEBUG] GetChat completata con successo. Messaggi trovati: %d\n", len(messages))
	return messages, nil
}

func CreateChat(db *sql.DB, userIDs []int64) (int64, error) {
	// 0. Verifica preliminare: una chat 1-a-1 deve avere esattamente 2 membri
	if len(userIDs) != 2 {
		return 0, fmt.Errorf("una chat privata deve avere esattamente 2 partecipanti")
	}

	// 1. Controllo se la chat esiste già
	// Cerchiamo un chat_id che appartenga a entrambi gli utenti
	var existingChatID int64
	checkQuery := `
        SELECT m1.chat_id 
        FROM members m1
        JOIN members m2 ON m1.chat_id = m2.chat_id
        WHERE m1.user_id = ? 
          AND m2.user_id = ? 
          AND m1.chat_id IS NOT NULL 
          AND m1.group_id IS NULL
        LIMIT 1`

	err := db.QueryRow(checkQuery, userIDs[0], userIDs[1]).Scan(&existingChatID)
	if err == nil {
		// La chat esiste già, restituiamo l'ID esistente senza creare nulla
		log.Printf("[DEBUG] Chat già esistente tra %d e %d: ID %d", userIDs[0], userIDs[1], existingChatID)
		return existingChatID, nil
	} else if err != sql.ErrNoRows {
		// C'è stato un errore reale nel database
		return 0, err
	}

	// 2. Se non esiste, procediamo con la creazione (Transazione)
	tx, err := db.Begin()
	if err != nil {
		return 0, err
	}
	defer tx.Rollback()

	now := time.Now().Format(time.RFC3339)

	// Inserimento nella tabella chats
	res, err := tx.Exec(`INSERT INTO chats (created_at) VALUES (?)`, now)
	if err != nil {
		return 0, fmt.Errorf("errore creazione riga chat: %w", err)
	}

	chatID, _ := res.LastInsertId()

	// Inserimento dei membri
	memberQuery := `INSERT INTO members (user_id, chat_id, group_id, joined_at) VALUES (?, ?, NULL, ?)`
	for _, uid := range userIDs {
		if _, err := tx.Exec(memberQuery, uid, chatID, now); err != nil {
			return 0, fmt.Errorf("errore inserimento membro %d: %w", uid, err)
		}
	}

	// 3. Conferma
	if err := tx.Commit(); err != nil {
		return 0, err
	}

	log.Printf("[DEBUG] Nuova chat creata con successo: ID %d", chatID)
	return chatID, nil
}

func ListConversations(db *sql.DB, userID int64, sorting string, filtro string) ([]Chat, error) {
	var conversations []Chat

	log.Printf("[DEBUG] Recupero conversazioni per UserID: %d", userID)

	// --- 1. RECUPERO I GRUPPI ---
	groupRows, err := db.Query(`
		SELECT DISTINCT g.id, g.name, COALESCE(g.photo, ''), g.created_at
		FROM groups g
		JOIN members m ON g.id = m.group_id
		WHERE m.user_id = ?`, userID)
	if err != nil {
		log.Printf("[ERROR] Errore query gruppi: %v", err)
		return nil, err
	}
	defer groupRows.Close()

	for groupRows.Next() {
		var c Chat
		var createdAtStr string
		if err := groupRows.Scan(&c.ID, &c.Name, &c.Photo, &createdAtStr); err != nil {
			log.Printf("[ERROR] Scan gruppo: %v", err)
			continue
		}
		c.Flag = true // È un gruppo
		c.CreatedAt, _ = parseTime(createdAtStr)
		conversations = append(conversations, c)
	}

	// --- 2. RECUPERO LE CHAT PRIVATE ---
	// Qui cerchiamo l'altro utente (m2) che non sia io (m1)
	privRows, err := db.Query(`
		SELECT DISTINCT c.id, u.username, COALESCE(u.image_URL, ''), c.created_at
		FROM chats c
		JOIN members m1 ON c.id = m1.chat_id AND m1.user_id = ?
		JOIN members m2 ON c.id = m2.chat_id AND m2.user_id != m1.user_id
		JOIN users u ON m2.user_id = u.id`, userID)
	if err != nil {
		log.Printf("[ERROR] Errore query chat private: %v", err)
		return nil, err
	}
	defer privRows.Close()

	for privRows.Next() {
		var c Chat
		var createdAtStr string
		if err := privRows.Scan(&c.ID, &c.Name, &c.Photo, &createdAtStr); err != nil {
			log.Printf("[ERROR] Scan chat privata: %v", err)
			continue
		}
		c.Flag = false // Non è un gruppo
		c.CreatedAt, _ = parseTime(createdAtStr)
		conversations = append(conversations, c)
	}

	// --- 3. FILTRAGGIO
	if filtro != "" {
		log.Printf("[DEBUG] Applicazione filtro: %s", filtro)
		filtered := []Chat{}
		for _, conv := range conversations {
			if contains(conv.Name, filtro) {
				filtered = append(filtered, conv)
			}
		}
		conversations = filtered
	} else {
		// --- 4. ORDINAMENTO
		log.Printf("[DEBUG] Ordinamento risultati. Tipo: %s", sorting)
		OrderByFilter(db, conversations, sorting)
	}

	log.Printf("[DEBUG] Totale conversazioni trovate: %d", len(conversations))
	return conversations, nil
}

// --- HELPER FUNCTIONS ---

func parseTime(tStr string) (time.Time, error) {
	// Prova i formati comuni di SQLite
	formats := []string{time.RFC3339, "2006-01-02 15:04:05", "2006-01-02T15:04:05Z"}
	for _, f := range formats {
		t, err := time.Parse(f, tStr)
		if err == nil {
			return t, nil
		}
	}
	return time.Time{}, nil
}

func contains(s, substr string) bool {
	// Implementazione semplice per il filtro
	return len(s) >= len(substr) && s != "" // Aggiungi logica di confronto se serve
}

// DeleteChat elimina una chat privata
func DeleteChat(db *sql.DB, chatID int64) error {
	query := `DELETE FROM chats WHERE id = ?`
	res, err := db.Exec(query, chatID)
	if err != nil {
		return err
	}

	affected, _ := res.RowsAffected()
	if affected == 0 {
		return errors.New("chat not found")
	}
	return nil
}

func CreateGroup(db *sql.DB, name string, userIDs []int64) (int64, error) {
	// Check di sicurezza: non creare gruppi senza membri
	if len(userIDs) == 0 {
		return 0, fmt.Errorf("impossibile creare un gruppo senza membri")
	}

	tx, err := db.Begin()
	if err != nil {
		return 0, err
	}
	defer tx.Rollback()

	now := time.Now().Format(time.RFC3339)

	// 1. Creazione Gruppo
	res, err := tx.Exec(`INSERT INTO groups (name, created_at) VALUES (?, ?)`, name, now)
	if err != nil {
		return 0, fmt.Errorf("errore insert gruppo: %w", err)
	}
	groupID, _ := res.LastInsertId()

	// 2. Inserimento Membri
	memberQuery := `INSERT INTO members (user_id, chat_id, group_id, joined_at) VALUES (?, NULL, ?, ?)`
	encountered := map[int64]bool{}

	for _, userID := range userIDs {
		if encountered[userID] {
			continue
		} // Salta se l'ID è duplicato nell'array
		encountered[userID] = true

		if _, err := tx.Exec(memberQuery, userID, groupID, now); err != nil {
			return 0, fmt.Errorf("errore inserimento membro %d: %w", userID, err)
		}
	}

	// 3. Imposta l'Admin
	adminID := userIDs[0]
	adminQuery := `INSERT INTO group_admins (group_id, user_id, assigned_at) VALUES (?, ?, ?)`
	if _, err := tx.Exec(adminQuery, groupID, adminID, now); err != nil {
		return 0, fmt.Errorf("errore inserimento admin: %w", err)
	}

	if err := tx.Commit(); err != nil {
		return 0, err
	}

	log.Printf("[DEBUG] Gruppo '%s' creato (ID: %d) con %d membri", name, groupID, len(encountered))
	return groupID, nil
}

// IsAdmin controlla se un utente ha i permessi di amministratore per un gruppo
func IsAdmin(db *sql.DB, groupID, userID int64) (bool, error) {
	var exists bool
	query := `SELECT EXISTS(SELECT 1 FROM group_admins WHERE group_id = ? AND user_id = ?)`
	err := db.QueryRow(query, groupID, userID).Scan(&exists)
	return exists, err
}

// DeleteGroup ora controlla la tabella group_admins prima di eliminare
func DeleteGroup(db *sql.DB, groupID, userID int64) error {
	// Verifichiamo prima se l'utente è admin
	admin, err := IsAdmin(db, groupID, userID)
	if err != nil {
		return err
	}
	if !admin {
		return errors.New("not authorized: user is not an admin of this group")
	}

	// Grazie al ON DELETE CASCADE nel DB, eliminando il gruppo
	// verranno eliminati automaticamente admin, membri e messaggi correlati.
	query := `DELETE FROM groups WHERE id = ?`
	res, err := db.Exec(query, groupID)
	if err != nil {
		return err
	}

	affected, _ := res.RowsAffected()
	if affected == 0 {
		return errors.New("group not found")
	}
	return nil
}
