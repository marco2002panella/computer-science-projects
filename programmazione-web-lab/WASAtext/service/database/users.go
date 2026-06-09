package database

import (
	"database/sql"
	"errors"
	"fmt"
)

// User struct per rappresentare un utente
type User struct {
	ID       int    `json:"Id"`
	Username string `json:"Username"`
	Url      string `json:"Image_URL"`
}

// Crea un nuovo utente con un username ma se esiste restituisce l'id
func CreateUser(db *sql.DB, username string, profileURL *string) (int64, error) {
	// Controllo se l'utente esiste già
	id, err := GetUserIDByUsername(db, username)
	if err == sql.ErrNoRows {
		// L'utente non esiste, lo creiamo
		var res sql.Result
		var errInsert error

		if profileURL != nil {
			res, errInsert = db.Exec(
				"INSERT INTO users(username, profile_url) VALUES (?, ?)",
				username, *profileURL,
			)
		} else {
			res, errInsert = db.Exec(
				"INSERT INTO users(username) VALUES (?)",
				username,
			)
		}

		if errInsert != nil {
			return 0, errInsert
		}

		id, err = res.LastInsertId()
		if err != nil {
			return 0, err
		}
	} else if err != nil {
		return 0, err
	}

	return id, nil
}

// Cambia il nome di un utente dato l'id
func UpdateUsername(db *sql.DB, id int, newUsername string) error {
	// 1. Controlla se il nuovo username esiste già per UN ALTRO utente
	var exists bool
	queryCheck := "SELECT EXISTS(SELECT 1 FROM users WHERE username = ? AND id != ?)"
	err := db.QueryRow(queryCheck, newUsername, id).Scan(&exists)
	if err != nil {
		return fmt.Errorf("errore durante il controllo dell'username: %w", err)
	}

	// 2. Se esiste già, restituisce un errore
	if exists {
		return fmt.Errorf("username '%s' già in uso da un altro utente", newUsername)
	}

	// 3. Se è libero, procedi con l'aggiornamento
	_, err = db.Exec("UPDATE users SET username = ? WHERE id = ?", newUsername, id)
	if err != nil {
		return fmt.Errorf("errore durante l'aggiornamento dell'username: %w", err)
	}

	return nil
}

// Restituisce l'id di un utente dato lo username
func GetUserIDByUsername(db *sql.DB, username string) (int64, error) {
	var id int64
	err := db.QueryRow("SELECT id FROM users WHERE username = ?", username).Scan(&id)
	if err != nil {
		return 0, err
	}
	return id, nil
}

// Restituisce tutti gli utenti come slice di User
func GetAllUsers(db *sql.DB) ([]User, error) {
	rows, err := db.Query("SELECT id, username,COALESCE(image_URL, '') FROM users")
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var users []User
	for rows.Next() {
		var u User
		err := rows.Scan(&u.ID, &u.Username, &u.Url)
		if err != nil {
			return nil, err
		}
		users = append(users, u)
	}
	return users, nil
}

// Rimuove un utente dato l'id
func DeleteUserByID(db *sql.DB, id int) error {
	_, err := db.Exec("DELETE FROM users WHERE id = ?", id)
	return err
}

// Rimuove un utente dato lo username
func DeleteUserByUsername(db *sql.DB, username string) error {
	_, err := db.Exec("DELETE FROM users WHERE username = ?", username)
	return err
}

// Cerca utenti il cui username inizia con una stringa
func SearchUsersByPrefix(db *sql.DB, prefix string) ([]User, error) {
	rows, err := db.Query("SELECT id, username, COALESCE(image_url, '') FROM users WHERE username LIKE ?", prefix+"%")
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var users []User
	for rows.Next() {
		var u User
		err := rows.Scan(&u.ID, &u.Username, &u.Url)
		if err != nil {
			return nil, err
		}
		users = append(users, u)
	}
	return users, nil
}

// GetProfileURL restituisce l'URL della foto profilo di un utente
func GetProfileURL(db *sql.DB, userID int) (string, error) {
	var u User

	// Selezioniamo la colonna profile_url dal database
	query := `SELECT image_URL FROM users WHERE id = ?`

	err := db.QueryRow(query, userID).Scan(&u.Url)
	if err != nil {
		if err == sql.ErrNoRows {
			return "", errors.New("utente non trovato")
		}
		return "", err
	}

	return u.Url, nil
}

// UpdateProfileURL aggiorna il campo profile_url per l'utente specificato
func UpdateProfileURL(db *sql.DB, userID int, newURL string) error {
	query := `UPDATE users SET image_URL = ? WHERE id = ?`

	result, err := db.Exec(query, newURL, userID)
	if err != nil {
		return err
	}

	// Verifichiamo se l'utente esisteva effettivamente
	rows, _ := result.RowsAffected()
	if rows == 0 {
		return errors.New("nessun utente trovato con questo ID")
	}

	return nil
}

func GetUsername(db *sql.DB, userID int) (string, error) {
	var username string
	err := db.QueryRow("SELECT username FROM users WHERE id = ?", userID).Scan(&username)
	if err != nil {
		if err == sql.ErrNoRows {
			return "", errors.New("utente non trovato")
		}
		return "", err
	}
	return username, nil
}

// Funzione helper: stampa tutti gli utenti (
func PrintAllUsers(db *sql.DB) {
	users, err := GetAllUsers(db)
	if err != nil {
		fmt.Println("Errore ottenendo utenti:", err)
		return
	}
	fmt.Println("Utenti:")
	for _, u := range users {
		fmt.Printf("ID: %d, Username: %s\n", u.ID, u.Username)
	}
}
