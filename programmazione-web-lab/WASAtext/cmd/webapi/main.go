package main

import (
	"errors"
	"net/http"
	"os"
	"os/signal"
	"syscall"

	"github.com/julienschmidt/httprouter"
	"github.com/marco2002panella/WASAtext/service/api"
	"github.com/marco2002panella/WASAtext/service/database"
	"github.com/sirupsen/logrus"
)

func main() {
	logger := logrus.New()
	logger.SetLevel(logrus.DebugLevel)

	logger.Info("Avvio di WASAtext in corso...")

	cfg, err := loadConfiguration()
	if err != nil {
		logger.WithError(err).Error("Impossibile caricare la configurazione")
		os.Exit(1)
	}

	db := database.InitDB(cfg.DB.Filename)
	defer db.Close()

	appHandler := &api.AppHandler{DB: db, BaseLogger: logger}

	router := httprouter.New()
	router.POST("/session", appHandler.Wrap(appHandler.Session))
	router.GET("/users", appHandler.Wrap(appHandler.ListUsers))
	router.GET("/users/:userId/username", appHandler.Wrap(appHandler.UserNameHandleR))
	router.PUT("/users/:userId/username", appHandler.Wrap(appHandler.UserNameHandleRC))
	router.GET("/users/:userId/photo", appHandler.Wrap(appHandler.UserPhotoHandleR))
	router.PUT("/users/:userId/photo", appHandler.Wrap(appHandler.UserPhotoHandleRC))
	router.POST("/users/", appHandler.Wrap(appHandler.SearchByName))
	router.GET("/users/:userId/chats/", appHandler.Wrap(appHandler.GetMyConversations))
	router.POST("/users/:userId/chats/", appHandler.Wrap(appHandler.CreateConversation))
	router.GET("/users/:userId/chats/:chatId", appHandler.Wrap(appHandler.GetConversation))
	router.POST("/users/:userId/chats/:chatId/", appHandler.Wrap(appHandler.SendMessage))
	router.DELETE("/users/:userId/chats/:chatId/messages/:messageId", appHandler.Wrap(appHandler.DeleteMessage))
	router.POST("/users/:userId/chats/:chatId/messages/:messageId", appHandler.Wrap(appHandler.ForwardMessage))
	router.PUT("/users/:userId/chats/:chatId/messages/:messageId", appHandler.Wrap(appHandler.ReadMessage))
	router.POST("/users/:userId/chats/:chatId/messages/:messageId/comments/", appHandler.Wrap(appHandler.CommentMessage))
	router.GET("/users/:userId/chats/:chatId/messages/:messageId/comments/", appHandler.Wrap(appHandler.GetReactions))
	router.DELETE("/users/:userId/chats/:chatId/messages/:messageId/comments/:reactionId", appHandler.Wrap(appHandler.DeleteReaction))
	router.POST("/users/:userId/groups/:groupId/members/", appHandler.Wrap(appHandler.AddToGroup))
	router.DELETE("/users/:userId/groups/:groupId/members/", appHandler.Wrap(appHandler.LeaveGroup))
	router.DELETE("/users/:userId/groups/:groupId/members/:targetUserId", appHandler.Wrap(appHandler.RemoveFromGroup))
	router.GET("/users/:userId/groups/:groupId/members/", appHandler.Wrap(appHandler.GetChatMembers))
	router.GET("/users/:userId/groups/:groupId/admins/", appHandler.Wrap(appHandler.GetChatAdmins))
	router.GET("/users/:userId/groups/:groupId/name", appHandler.Wrap(appHandler.GetGroupName))
	router.GET("/users/:userId/groups/:groupId/photo", appHandler.Wrap(appHandler.GetGroupPhoto))
	router.PUT("/users/:userId/groups/:groupId/name", appHandler.Wrap(appHandler.SetGroupName))
	router.PUT("/users/:userId/groups/:groupId/photo", appHandler.Wrap(appHandler.SetGroupPhoto))
	routerFinale, err := registerWebUI(router)
	if err != nil {
		logger.WithError(err).Error("Impossibile registrare la Web UI")
		os.Exit(1)
	}

	handlerConCORS := applyCORSHandler(routerFinale)

	server := &http.Server{
		Addr:    cfg.Web.APIHost,
		Handler: handlerConCORS,
	}

	go func() {
		logger.Infof("Server in ascolto su http://localhost%s (o sulla porta dei test)", cfg.Web.APIHost)
		if err := server.ListenAndServe(); !errors.Is(err, http.ErrServerClosed) {
			logger.WithError(err).Fatal("Errore durante l'esecuzione del server")
		}
	}()

	shutdown := make(chan os.Signal, 1)
	signal.Notify(shutdown, os.Interrupt, syscall.SIGTERM)
	<-shutdown

	logger.Info("Spegnimento del server in corso...")
	_ = server.Close()
	logger.Info("Server spento con successo. Alla prossima!")
}
