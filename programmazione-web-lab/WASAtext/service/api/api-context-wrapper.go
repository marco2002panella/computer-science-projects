package api

import (
	"context"
	"net/http"
	"time"

	"github.com/gofrs/uuid"
	"github.com/julienschmidt/httprouter"
	"github.com/sirupsen/logrus"
)

func (a *AppHandler) Wrap(handler httprouter.Handle) httprouter.Handle {
	return func(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
		w.Header().Set("Access-Control-Allow-Origin", "http://localhost:5173")
		w.Header().Set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE")
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type, Authorization")

		if r.Method == "OPTIONS" {
			w.WriteHeader(http.StatusOK)
			return
		}
		reqUUID, err := uuid.NewV4()
		if err != nil {
			a.BaseLogger.WithError(err).Error("Impossibile generare l'UUID della richiesta")
			w.WriteHeader(http.StatusInternalServerError)
			return
		}
		ctxLogger := a.BaseLogger.WithFields(logrus.Fields{
			"reqid":     reqUUID.String(),
			"remote-ip": r.RemoteAddr,
			"method":    r.Method,
			"path":      r.URL.Path,
		})
		ctx := context.WithValue(r.Context(), "logger", ctxLogger)
		ctxLogger.Info("Richiesta HTTP ricevuta")
		// Teniamo traccia del tempo iniziale
		startTime := time.Now()
		handler(w, r.WithContext(ctx), ps)
		duration := time.Since(startTime)
		ctxLogger.WithField("latency_ms", duration.Milliseconds()).Info("Richiesta HTTP completata")
	}
}
