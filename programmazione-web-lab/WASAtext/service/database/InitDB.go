package database

import (
	"database/sql"
	"log"

	_ "modernc.org/sqlite"
)

// InitDB apre o crea il database e tutte le tabelle necessarie
func InitDB(databesePath string) *sql.DB {
	// Apri o crea il DB
	db, err := sql.Open("sqlite", databesePath)
	if err != nil {
		log.Fatal("Errore aprendo il database:", err)
	}

	// Imposta foreign keys attive
	_, err = db.Exec(`PRAGMA foreign_keys = ON;`)
	if err != nil {
		log.Fatal("Errore abilitando foreign keys:", err)
	}

	// Creazione tabelle
	CreateTables(db)
	return db
}

func CreateTables(db *sql.DB) {
	// 1. Tabelle INDIPENDENTI (Livello 0)
	// USERS
	_, err := db.Exec(`
	    CREATE TABLE IF NOT EXISTS users (
		id INTEGER PRIMARY KEY AUTOINCREMENT,
		image_URL TEXT UNIQUE,
		username TEXT NOT NULL UNIQUE
	    );`)
	if err != nil {
		log.Fatal("Errore USERS:", err)
	}

	// CHATS
	_, err = db.Exec(`
	    CREATE TABLE IF NOT EXISTS chats (
		id INTEGER PRIMARY KEY AUTOINCREMENT,
		created_at TEXT NOT NULL
	    );`)
	if err != nil {
		log.Fatal("Errore CHATS:", err)
	}

	// 2. GROUPS
	_, err = db.Exec(`
	CREATE TABLE IF NOT EXISTS groups (
	    id INTEGER PRIMARY KEY AUTOINCREMENT,
	    name TEXT NOT NULL,
		photo TEXT,
	    created_at TEXT NOT NULL
	);`)

	// 3. Tabella PIVOT per gli amministratori
	_, err = db.Exec(`
	CREATE TABLE IF NOT EXISTS group_admins (
	    group_id INTEGER NOT NULL,
	    user_id INTEGER NOT NULL,
	    assigned_at TEXT NOT NULL,
	    PRIMARY KEY (group_id, user_id),
	    FOREIGN KEY(group_id) REFERENCES groups(id) ON DELETE CASCADE,
	    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
	);`)

	// 3. Tabelle PIVOT per chat e gruppi(Livello 2)
	sqlMembers := `
	    CREATE TABLE IF NOT EXISTS members (
		user_id INTEGER NOT NULL,
		chat_id INTEGER,
		group_id INTEGER,
		last_read_message_id INTEGER default 0,
		joined_at TEXT NOT NULL,
		FOREIGN KEY(user_id) REFERENCES users(id),
		FOREIGN KEY(chat_id) REFERENCES chats(id),
		FOREIGN KEY(group_id) REFERENCES groups(id),
		CHECK ((chat_id IS NOT NULL AND group_id IS NULL) OR (chat_id IS NULL AND group_id IS NOT NULL)),
		PRIMARY KEY (user_id, chat_id, group_id)
	    );`
	if _, err = db.Exec(sqlMembers); err != nil {
		log.Fatal("Errore MEMBERS:", err)
	}

	// MESSAGES
	_, err = db.Exec(`
	    CREATE TABLE IF NOT EXISTS messages (
		id INTEGER PRIMARY KEY AUTOINCREMENT,
		chat_id INTEGER,
		group_id INTEGER,
		sender_id INTEGER NOT NULL,
		content TEXT,
		contentType TEXT,
		created_at TEXT NOT NULL,
		FOREIGN KEY(chat_id) REFERENCES chats(id),
		FOREIGN KEY(group_id) REFERENCES groups(id),
		FOREIGN KEY(sender_id) REFERENCES users(id)
	    );`)
	if err != nil {
		log.Fatal("Errore MESSAGES:", err)
	}

	// REACTIONS
	_, err = db.Exec(`
	    CREATE TABLE IF NOT EXISTS reactions (
		id INTEGER PRIMARY KEY AUTOINCREMENT,
		message_id INTEGER NOT NULL,
		user_id INTEGER NOT NULL,
		reaction_type TEXT NOT NULL,
		created_at TEXT NOT NULL,
		FOREIGN KEY(message_id) REFERENCES messages(id),
		FOREIGN KEY(user_id) REFERENCES users(id),
		UNIQUE(message_id, user_id, reaction_type)
	    );`)
	if err != nil {
		log.Fatal("Errore REACTIONS:", err)
	}
}
