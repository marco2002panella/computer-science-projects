package database

import (
	"database/sql"
	"fmt"
	"time"
)

// Message matches the MESSAGES table structure
type Message struct {
	ID          int64         `json:"id"`
	ChatID      sql.NullInt64 `json:"chat_id"`
	GroupID     sql.NullInt64 `json:"group_id"`
	AuthorID    int64         `json:"AuthorId"`
	Content     string        `json:"content"`
	ContentType string        `json:"content_type"` // "text", "photo", "video", etc.
	CreatedAt   time.Time     `json:"created_at"`
	AllRead     bool          `json:"all_read"`
}

type Reaction struct {
	UserID       int64     `json:"user_id"`
	ReactionType string    `json:"reaction_type"` // "like", "love", "laugh", etc.
	CreatedAt    time.Time `json:"created_at"`
}

func MarkMessageAsRead(db *sql.DB, messageID int64, userID int64, targetID int64, isGroup bool) error {

	// 1. Verifica se l'utente è membro della chat/gruppo
	var exists bool
	var checkQuery string
	if isGroup {
		checkQuery = "SELECT EXISTS(SELECT 1 FROM members WHERE user_id = ? AND group_id = ?)"
	} else {
		checkQuery = "SELECT EXISTS(SELECT 1 FROM members WHERE user_id = ? AND chat_id = ?)"
	}

	err := db.QueryRow(checkQuery, userID, targetID).Scan(&exists)
	if err != nil || !exists {
		return err
	}

	// 2. Aggiorna lo stato di lettura del messaggio per questo utente
	updateQuery := ""
	if isGroup {
		updateQuery = "UPDATE members SET last_read_message_id = ? WHERE user_id = ? AND group_id = ?"
	} else {
		updateQuery = "UPDATE members SET last_read_message_id = ? WHERE user_id = ? AND chat_id = ?"
	}

	_, err = db.Exec(updateQuery, messageID, userID, targetID)
	if err != nil {
		fmt.Printf("[DEBUG] Errore SQL durante l'aggiornamento dello stato di lettura: %v\n", err)
		return fmt.Errorf("errore durante l'aggiornamento dello stato di lettura: %w", err)
	}

	fmt.Printf("[DEBUG] MarkMessageAsRead completato con successo per MsgID: %d e UserID: %d\n", messageID, userID)
	return nil
}

func DeleteMessage(db *sql.DB, messageID int64, requestingUserID int64, targetID int64, isGroup bool) error {
	fmt.Printf("[DEBUG] Inizio DeleteMessage - MsgID: %d, Richiesto da User: %d, In Target: %d, IsGroup: %t\n", messageID, requestingUserID, targetID, isGroup)

	var query string
	if isGroup {
		query = "DELETE FROM messages WHERE id = ? AND sender_id = ? AND group_id = ?"
	} else {
		query = "DELETE FROM messages WHERE id = ? AND sender_id = ? AND chat_id = ?"
	}

	fmt.Printf("[DEBUG] Esecuzione Query: %s con parametri [%d, %d, %d]\n", query, messageID, requestingUserID, targetID)

	result, err := db.Exec(query, messageID, requestingUserID, targetID)
	if err != nil {
		fmt.Printf("[DEBUG] Errore SQL durante l'esecuzione: %v\n", err)
		return fmt.Errorf("errore durante l'eliminazione del messaggio: %w", err)
	}

	// Verifichiamo se è stata effettivamente eliminata una riga
	rowsAffected, err := result.RowsAffected()
	if err != nil {
		fmt.Printf("[DEBUG] Errore nel recupero RowsAffected: %v\n", err)
	}

	fmt.Printf("[DEBUG] Righe eliminate: %d\n", rowsAffected)

	if rowsAffected == 0 {
		fmt.Printf("[DEBUG] Fallimento: Messaggio %d non eliminato. Cause possibili: 1. ID inesistente, 2. L'utente %d non è l'autore, 3. Il messaggio non appartiene al target %d\n", messageID, requestingUserID, targetID)
		return fmt.Errorf("impossibile eliminare: messaggio non trovato o non sei l'autore")
	}

	fmt.Printf("[DEBUG] DeleteMessage completato con successo per MsgID: %d\n", messageID)
	return nil
}

// MessageBelongsToContext verifica se un messaggio esiste e appartiene alla chat o al gruppo specificato
func MessageBelongsToContext(db *sql.DB, messageID int64, contextID int64, isGroup bool) (bool, error) {
	fmt.Printf("[DEBUG] Controllo appartenenza messaggio %d al contesto %d (isGroup: %t)\n", messageID, contextID, isGroup)

	var exists bool
	var query string

	if isGroup {
		// Verifica se il messaggio appartiene al gruppo
		query = "SELECT EXISTS(SELECT 1 FROM messages WHERE id = ? AND group_id = ?)"
	} else {
		// Verifica se il messaggio appartiene alla chat privata
		query = "SELECT EXISTS(SELECT 1 FROM messages WHERE id = ? AND chat_id = ?)"
	}

	err := db.QueryRow(query, messageID, contextID).Scan(&exists)
	if err != nil {
		fmt.Printf("[DEBUG] Errore SQL in MessageBelongsToContext: %v\n", err)
		return false, fmt.Errorf("errore verifica contesto messaggio: %w", err)
	}

	if !exists {
		fmt.Printf("[DEBUG] Il messaggio %d NON appartiene al contesto %d\n", messageID, contextID)
	} else {
		fmt.Printf("[DEBUG] Il messaggio %d appartiene correttamente al contesto %d\n", messageID, contextID)
	}

	return exists, nil
}

func GetReactions(db *sql.DB, messageID int64) ([]Reaction, error) {
	query := "SELECT user_id, reaction_type, created_at FROM reactions WHERE message_id = ?"
	rows, err := db.Query(query, messageID)
	if err != nil {
		return nil, fmt.Errorf("errore recupero reazioni: %w", err)
	}
	defer rows.Close()

	var reactions []Reaction
	for rows.Next() {
		var r Reaction
		var createdAtStr string
		err := rows.Scan(&r.UserID, &r.ReactionType, &createdAtStr)
		if err != nil {
			return nil, fmt.Errorf("errore scan reazione: %w", err)
		}

		// Conversione della stringa ISO8601 di SQLite in time.Time
		r.CreatedAt, _ = time.Parse(time.RFC3339, createdAtStr)

		reactions = append(reactions, r)
	}

	return reactions, nil
}

func AddReaction(db *sql.DB, messageID int64, requestingUserID int64, targetID int64, isGroup bool, reactionType string) error {
	// 1.Verifica se l'utente appartiene alla chat/gruppo
	var exists bool
	checkQuery := ""
	if isGroup {
		checkQuery = "SELECT EXISTS(SELECT 1 FROM members WHERE user_id = ? AND group_id = ?)"
	} else {
		checkQuery = "SELECT EXISTS(SELECT 1 FROM members WHERE user_id = ? AND chat_id = ?)"
	}

	err := db.QueryRow(checkQuery, requestingUserID, targetID).Scan(&exists)
	if err != nil || !exists {
		return fmt.Errorf("accesso negato: l'utente non fa parte di questa conversazione")
	}

	// 2. Inserimento della reazione
	now := time.Now().Format(time.RFC3339)
	query := `
        INSERT INTO reactions (message_id, user_id, reaction_type, created_at)
        VALUES (?, ?, ?, ?)
        ON CONFLICT(message_id, user_id, reaction_type) DO NOTHING`
	// DO NOTHING evita errori se la reazione esiste già

	_, err = db.Exec(query, messageID, requestingUserID, reactionType, now)
	if err != nil {
		return fmt.Errorf("errore inserimento reazione: %w", err)
	}

	return nil
}

func DeleteReaction(db *sql.DB, messageID int64, requestingUserID int64, reactionId int64, isGroup bool, chatId int64) error {
	fmt.Printf("[DEBUG] Inizio DeleteReaction - MsgID: %d, User: %d, ReactionID: %d, ChatID: %d, IsGroup: %t\n", messageID, requestingUserID, reactionId, chatId, isGroup)

	// 1. CONTROLLO INCROCIATO: Il messaggio appartiene a questa chat/gruppo?
	var msgExists bool
	var msgCheckQuery string
	if isGroup {
		msgCheckQuery = "SELECT EXISTS(SELECT 1 FROM messages WHERE id = ? AND group_id = ?)"
	} else {
		msgCheckQuery = "SELECT EXISTS(SELECT 1 FROM messages WHERE id = ? AND chat_id = ?)"
	}

	err := db.QueryRow(msgCheckQuery, messageID, chatId).Scan(&msgExists)
	if err != nil {
		fmt.Printf("[DEBUG] Errore verifica appartenenza messaggio: %v\n", err)
		return fmt.Errorf("errore database verifica messaggio: %w", err)
	}
	if !msgExists {
		fmt.Printf("[DEBUG] Fallito: Il messaggio %d non appartiene alla conversazione %d\n", messageID, chatId)
		return fmt.Errorf("operazione non valida: il messaggio non appartiene a questa conversazione")
	}

	// 2. Verifica se l'utente appartiene alla chat/gruppo
	var userExists bool
	checkQuery := ""
	if isGroup {
		checkQuery = "SELECT EXISTS(SELECT 1 FROM members WHERE user_id = ? AND group_id = ?)"
	} else {
		checkQuery = "SELECT EXISTS(SELECT 1 FROM members WHERE user_id = ? AND chat_id = ?)"
	}

	err = db.QueryRow(checkQuery, requestingUserID, chatId).Scan(&userExists)
	if err != nil || !userExists {
		fmt.Printf("[DEBUG] Accesso negato: Utente %d non è membro della conversazione %d\n", requestingUserID, chatId)
		return fmt.Errorf("accesso negato: l'utente non fa parte di questa conversazione")
	}

	// 3. Esecuzione eliminazione reazione
	fmt.Printf("[DEBUG] Tentativo eliminazione reazione %d per messaggio %d dall'utente %d\n", reactionId, messageID, requestingUserID)
	query := "DELETE FROM reactions WHERE message_id = ? AND user_id = ? AND id = ?"
	result, err := db.Exec(query, messageID, requestingUserID, reactionId)
	if err != nil {
		fmt.Printf("[DEBUG] Errore SQL DELETE: %v\n", err)
		return fmt.Errorf("errore durante l'eliminazione della reazione: %w", err)
	}

	rowsAffected, _ := result.RowsAffected()
	fmt.Printf("[DEBUG] Righe eliminate: %d\n", rowsAffected)

	if rowsAffected == 0 {
		fmt.Printf("[DEBUG] Fallimento: Reazione %d non trovata o l'utente %d non è l'autore\n", reactionId, requestingUserID)
		return fmt.Errorf("impossibile eliminare: reazione non trovata o non sei l'autore")
	}

	fmt.Printf("[DEBUG] DeleteReaction completata con successo\n")
	return nil
}

func ForwardMessage(db *sql.DB, messageID int64, sourceID int64, targetID int64, senderID int64, targetIsGroup bool, sourceIsGroup bool) error {
	fmt.Printf("[DEBUG] Inizio ForwardMessage - MsgID Originale: %d, Target: %d, Sender: %d, IsGroup: %t\n", messageID, targetID, senderID, targetIsGroup)
	if IsMember(db, int(senderID), int(targetID), targetIsGroup) {
		fmt.Printf("[DEBUG] Utente %d è membro del target %d, procedo con l'inoltro\n", senderID, targetID)
	} else {
		fmt.Printf("[DEBUG] Accesso negato: Utente %d non è membro del target %d\n", senderID, targetID)
		return fmt.Errorf("accesso negato: l'utente non fa parte di questo contesto o il contesto non esiste")
	}

	var content, contentType string
	var query string
	// 1. Recupero con controllo di appartenenza alla sorgente
	if sourceIsGroup {
		// Cerca il messaggio ID solo se appartiene al gruppo sourceID
		query = "SELECT content, contentType FROM messages WHERE id = ? AND group_id = ?"
	} else {
		// Cerca il messaggio ID solo se appartiene alla chat sourceID
		query = "SELECT content, contentType FROM messages WHERE id = ? AND chat_id = ?"
	}

	err := db.QueryRow(query, messageID, sourceID).Scan(&content, &contentType)
	if err != nil {
		if err == sql.ErrNoRows {

			return err
		}
		return err
	}

	fmt.Printf("[DEBUG] Messaggio recuperato con successo - Tipo: %s, Contenuto: %.20s...\n", contentType, content)

	// 2. Usa CreateMessage per l'inoltro
	// Inoltrare significa creare un nuovo messaggio con lo stesso contenuto
	fmt.Printf("[DEBUG] Chiamata a CreateMessage per l'inoltro verso Target: %d\n", targetID)
	_, err = CreateMessage(db, targetID, senderID, content, contentType, targetIsGroup)
	if err != nil {
		fmt.Printf("[DEBUG] Errore durante l'inoltro tramite CreateMessage: %v\n", err)
		return fmt.Errorf("errore durante l'inoltro: %w", err)
	}

	fmt.Printf("[DEBUG] ForwardMessage completato con successo verso Target: %d\n", targetID)
	return nil
}

// CreateMessage inserts a new message
func CreateMessage(db *sql.DB, targetID int64, senderID int64, content string, contentType string, isGroup bool) (int64, error) {
	fmt.Printf("[DEBUG] CreateMessage - Target: %d, Sender: %d, Type: %s, IsGroup: %t\n", targetID, senderID, contentType, isGroup)

	// 1. CONTROLLO DI SICUREZZA
	if contentType != "text" && contentType != "photo" {
		fmt.Printf("[DEBUG] Fallito controllo contentType: %s\n", contentType)
		return 0, fmt.Errorf("contentType non valido: deve essere 'text' o 'photo'")
	}

	var exists bool
	var checkQuery string
	if isGroup {
		checkQuery = `
        SELECT EXISTS(
            SELECT 1 FROM groups g
            JOIN members m ON g.id = m.group_id
            WHERE g.id = ? AND m.user_id = ?
        )`
	} else {
		checkQuery = `
        SELECT EXISTS(
            SELECT 1 FROM chats c
            JOIN members m ON c.id = m.chat_id
            WHERE c.id = ? AND m.user_id = ?
        )`
	}

	err := db.QueryRow(checkQuery, targetID, senderID).Scan(&exists)
	if err != nil {
		fmt.Printf("[DEBUG] Errore durante l'esecuzione della QueryRow di controllo: %v\n", err)
		return 0, fmt.Errorf("errore database nel controllo sicurezza: %w", err)
	}

	fmt.Printf("[DEBUG] Risultato controllo esistenza/appartenenza: %t\n", exists)

	if !exists {
		fmt.Printf("[DEBUG] Accesso negato: La chat/gruppo %d non esiste o l'utente %d non è membro\n", targetID, senderID)
		return 0, fmt.Errorf("accesso negato: l'utente %d non fa parte di questo contesto o il contesto non esiste", senderID)
	}

	var chatID, groupID sql.NullInt64
	if isGroup {
		groupID = sql.NullInt64{Int64: targetID, Valid: true}
		chatID = sql.NullInt64{Valid: false}
	} else {
		chatID = sql.NullInt64{Int64: targetID, Valid: true}
		groupID = sql.NullInt64{Valid: false}
	}

	// 2. Prepariamo il timestamp
	createdAt := time.Now().Format("2006-01-02 15:04:05")

	// 3. Eseguiamo l'inserimento
	query := `
        INSERT INTO messages (chat_id, group_id, sender_id, content, contentType, created_at)
        VALUES (?, ?, ?, ?, ?, ?)`

	fmt.Printf("[DEBUG] Esecuzione INSERT per messaggio da %d verso target %d\n", senderID, targetID)
	result, err := db.Exec(query, chatID, groupID, senderID, content, contentType, createdAt)
	if err != nil {
		fmt.Printf("[DEBUG] Errore INSERT: %v\n", err)
		return 0, fmt.Errorf("errore inserimento messaggio: %w", err)
	}

	// 4. Recuperiamo l'ID appena generato
	lastID, err := result.LastInsertId()
	if err != nil {
		fmt.Printf("[DEBUG] Errore recupero LastInsertId: %v\n", err)
		return 0, fmt.Errorf("errore recupero last ID: %w", err)
	}

	fmt.Printf("[DEBUG] Messaggio inserito con successo! ID Messaggio: %d\n", lastID)
	return lastID, nil
}
