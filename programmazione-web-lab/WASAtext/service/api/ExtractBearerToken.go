package api

import (
	"errors"
	"strings"
)

// ExtractBearerToken prende in input il valore dell'header Authorization (es. "Bearer <token>")
// e restituisce il token puro se il formato è corretto.
const bearerPrefix = "Bearer "

func ExtractBearerToken(authHeader string) (string, error) {
	// 1. Verifica che l'header non sia vuoto
	if authHeader == "" {
		return "", errors.New("l'header Authorization è vuoto o mancante")
	}

	// 2. Verifica che l'header inizi con il prefisso "Bearer "
	// Usiamo strings.HasPrefix per una verifica case-sensitive, che è standard.
	if !strings.HasPrefix(authHeader, bearerPrefix) {
		return "", errors.New("formato header non valido: deve iniziare con 'Bearer '")
	}

	// 3. Estrai il token
	// La lunghezza del prefisso "Bearer " è 7 caratteri.
	token := authHeader[len(bearerPrefix):]

	// 4. Verifica che il token non sia solo spazi o vuoto dopo l'estrazione
	if strings.TrimSpace(token) == "" {
		return "", errors.New("token Bearer mancante dopo il prefisso")
	}
	return token, nil
}
