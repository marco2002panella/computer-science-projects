package api

import (
	"net/http"
	"strings"

	"github.com/julienschmidt/httprouter"
)

func (a *AppHandler) ExtractContext(r *http.Request) (string, []string, error) {
	// 1. Estrai il token
	auth := r.Header.Get("Authorization")
	token, err := ExtractBearerToken(auth)
	if err != nil {
		return "", nil, err
	}

	// 2. Estrai i path parts
	path := strings.Trim(r.URL.Path, "/")
	parts := strings.Split(path, "/")

	return token, parts, nil
}

func (a *AppHandler) UserNameHandleR(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.UserNameHandle(w, r, token, parts)
}

func (a *AppHandler) UserPhotoHandleR(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.UserPhotoHandle(w, r, token, parts)
}

func (a *AppHandler) UserNameHandleRC(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.UserNameHandle(w, r, token, parts)
}

func (a *AppHandler) UserPhotoHandleRC(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.UserPhotoHandle(w, r, token, parts)
}

func (a *AppHandler) CreateConversation(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.userCreateChat(w, r, token, parts)
}

func (a *AppHandler) GetMyConversations(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.userListChats(w, r, token, parts)

}

func (a *AppHandler) GetConversation(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.userGetChat(w, r, token, parts)
}

func (a *AppHandler) SendMessage(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.sendMessage(w, r, token, parts)
}

func (a *AppHandler) DeleteMessage(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.deleteMessage(w, r, token, parts)
}

func (a *AppHandler) ForwardMessage(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.forwardMessage(w, r, token, parts)
}

func (a *AppHandler) CommentMessage(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.commentMessage(w, r, token, parts)
}

func (a *AppHandler) GetReactions(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.listComments(w, r, token, parts)
}

func (a *AppHandler) DeleteReaction(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.deleteComment(w, r, token, parts)

}

func (a *AppHandler) AddToGroup(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.addToGroup(w, r, token, parts)
}

func (a *AppHandler) LeaveGroup(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.leaveGroup(w, r, token, parts)
}

func (a *AppHandler) GetGroupName(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.getGroupName(w, r, token, parts)
}

func (a *AppHandler) SetGroupName(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.changeGroupName(w, r, token, parts)
}

func (a *AppHandler) GetGroupPhoto(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.getGroupPhoto(w, r, token, parts)
}

func (a *AppHandler) SetGroupPhoto(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.setGroupPhoto(w, r, token, parts)
}

func (a *AppHandler) RemoveFromGroup(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.removeFromGroup(w, r, token, parts)
}

func (a *AppHandler) GetChatMembers(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.chatMembersHandle(w, r, token, parts)
}

func (a *AppHandler) GetChatAdmins(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.getAdmins(w, r, token, parts)
}

func (a *AppHandler) SearchByName(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.searchByName(w, r, token, parts)
}

func (a *AppHandler) ReadMessage(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	token, parts, err := a.ExtractContext(r)
	if err != nil {
		http.Error(w, "Unauthorized", http.StatusUnauthorized)
		return
	}
	a.MarkMessageAsRead(w, r, token, parts)
}
