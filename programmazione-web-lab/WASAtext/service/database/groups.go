package database

import (
	"database/sql"
	"fmt"
	"time"
)

// Group struct per rappresentare un gruppo
type Group struct {
	ID        int    `json:"Id"`
	Name      string `json:"Name"`
	CreatedAt string `json:"CreatedAt"`
}

func AddUserToGroup(db *sql.DB, userID int64, groupID int64, requestingId int64) error {
	// Controlla se l'utente è già membro del gruppo
	var exists bool
	err := db.QueryRow("SELECT EXISTS(SELECT 1 FROM members WHERE user_id = ? AND group_id = ?)", userID, groupID).Scan(&exists)
	if err != nil {
		return fmt.Errorf("errore controllo membro: %w", err)
	}
	if exists {
		return fmt.Errorf("utente già membro del gruppo")
	}

	// Controlla se chi fa la richiesta è un admin del gruppo
	isadmin, err1 := IsAdmin(db, groupID, requestingId)
	if err1 != nil {
		return fmt.Errorf("errore controllo admin: %w", err1)
	}
	if isadmin {
		// Aggiungi l'utente al gruppo
		query := `INSERT INTO members (user_id, group_id, chat_id, joined_at) VALUES (?, ?, NULL, ?)`
		_, err = db.Exec(query, userID, groupID, time.Now().Format(time.RFC3339))
		if err != nil {
			return fmt.Errorf("errore aggiunta membro: %w", err)
		}
		return nil
	}
	return fmt.Errorf("accesso negato: solo gli admin possono aggiungere membri al gruppo")
}

func LeaveGroup(db *sql.DB, userID int64, groupID int64) error {
	// Controlla se l'utente è già membro del gruppo
	var exists bool
	err := db.QueryRow("SELECT EXISTS(SELECT 1 FROM members WHERE user_id = ? AND group_id = ?)", userID, groupID).Scan(&exists)
	if err != nil {
		return fmt.Errorf("errore controllo membro: %w", err)
	}
	if !exists {
		return fmt.Errorf("utente non è membro del gruppo")
	}

	// Rimuovi l'utente dal gruppo
	_, err = db.Exec("DELETE FROM members WHERE user_id = ? AND group_id = ? and chat_id IS NULL", userID, groupID)
	if err != nil {
		return fmt.Errorf("errore rimozione membro: %w", err)
	}
	return nil
}

func RemoveUserFromGroup(db *sql.DB, userID int64, groupID int64, requestingId int64) error {
	// Controlla se l'utente è già membro del gruppo
	var exists bool
	err := db.QueryRow("SELECT EXISTS(SELECT 1 FROM members WHERE user_id = ? AND group_id = ?)", userID, groupID).Scan(&exists)
	if err != nil {
		return fmt.Errorf("errore controllo membro: %w", err)
	}
	if !exists {
		return fmt.Errorf("utente non è membro del gruppo")
	}

	// Controlla se chi fa la richiesta è un admin del gruppo
	isadmin, err1 := IsAdmin(db, groupID, requestingId)
	if err1 != nil {
		return fmt.Errorf("errore controllo admin: %w", err1)
	}
	if isadmin {
		// Rimuovi l'utente dal gruppo
		_, err = db.Exec("DELETE FROM members WHERE user_id = ? AND group_id = ? and chat_id IS NULL", userID, groupID)
		if err != nil {
			return fmt.Errorf("errore rimozione membro: %w", err)
		}
		return nil
	}
	return fmt.Errorf("accesso negato: solo gli admin possono rimuovere membri dal gruppo")
}

func ChangeGroupName(db *sql.DB, groupID int64, newName string, requestingId int64) error {
	// Controlla se chi fa la richiesta è un admin del gruppo
	isadmin, err1 := IsAdmin(db, groupID, requestingId)
	if err1 != nil {
		return fmt.Errorf("errore controllo admin: %w", err1)
	}
	if isadmin {
		// Cambia il nome del gruppo
		_, err := db.Exec("UPDATE groups SET name = ? WHERE id = ?", newName, groupID)
		if err != nil {
			return fmt.Errorf("errore cambio nome gruppo: %w", err)
		}
		return nil
	}
	return fmt.Errorf("accesso negato: solo gli admin possono cambiare il nome del gruppo")
}

func GetGroupName(db *sql.DB, groupID int64) (string, error) {
	var name string
	err := db.QueryRow("SELECT name FROM groups WHERE id = ?", groupID).Scan(&name)
	if err != nil {
		return "", fmt.Errorf("errore recupero nome gruppo: %w", err)
	}
	return name, nil
}

func GetGroupPhoto(db *sql.DB, groupID int64) (string, error) {
	var photo sql.NullString
	err := db.QueryRow("SELECT photo FROM groups WHERE id = ? ", groupID).Scan(&photo)
	if err != nil {
		return "", fmt.Errorf("errore recupero foto gruppo: %w", err)
	}
	if photo.Valid {
		return photo.String, nil
	}
	return "", nil
}

func SetGroupPhoto(db *sql.DB, groupID int64, photoURL string, requestingId int64) error {
	// Controlla se chi fa la richiesta è un admin del gruppo
	isadmin, err1 := IsAdmin(db, groupID, requestingId)
	if err1 != nil {
		return fmt.Errorf("errore controllo admin: %w", err1)
	}
	if isadmin {
		// Cambia la foto del gruppo
		_, err := db.Exec("UPDATE groups SET photo = ? WHERE id = ?", photoURL, groupID)
		if err != nil {
			return fmt.Errorf("errore cambio foto gruppo: %w", err)
		}
		return nil
	}
	return fmt.Errorf("accesso negato: solo gli admin possono cambiare la foto del gruppo")
}

func GetGroupMembers(db *sql.DB, groupID int64) ([]int64, error) {
	rows, err := db.Query("SELECT user_id FROM members WHERE group_id = ? and chat_id IS NULL", groupID)
	if err != nil {
		return nil, fmt.Errorf("errore recupero membri gruppo: %w", err)
	}
	defer rows.Close()
	var members []int64
	for rows.Next() {
		var userID int64
		err := rows.Scan(&userID)
		if err != nil {
			return nil, fmt.Errorf("errore scan membro gruppo: %w", err)
		}
		members = append(members, userID)
	}
	return members, nil
}

func GetGroupAdmins(db *sql.DB, groupID int64) ([]int64, error) {
	rows, err := db.Query("SELECT user_id FROM group_admins WHERE group_id = ?", groupID)
	if err != nil {
		return nil, fmt.Errorf("errore recupero admin gruppo: %w", err)
	}
	defer rows.Close()
	var admins []int64
	for rows.Next() {
		var userID int64
		err := rows.Scan(&userID)
		if err != nil {
			return nil, fmt.Errorf("errore scan admin gruppo: %w", err)
		}
		admins = append(admins, userID)
	}
	return admins, nil
}
