

<template>
  <div class="chat-container" v-if="chatId">

    <!-- Header -->
    <div class="chat-header">
      <img
        class="chat-avatar"
        :src="chatPhoto"
        alt="avatar"
      >
      <span class="chat-name">
        {{ chatName }}
      </span>
    </div>

    <!-- Messaggi -->
    <div class="chat-body" style="display:flex; flex-direction: column;">
      <div
        v-for="message in messages"
        :key="message.Id"
        class="message-item"
        :class="{ 'my-message': message.AuthorId === userId }"
      >
        <div 
            class="message-wrapper" 
            >
    <div class="message-header">
        <span class="message-sender">{{message.authorName }}</span>
        <span class="message-date">{{ formatDate(message.created_at) }}</span>
    </div>
    <div class="message-content">
        {{ message.content }}
    </div>
    <div >
        <span class="checkmarks" v-if="message.AuthorId === userId">
            <span v-if="message.all_read">✔✔</span>
            <span v-else>✔</span>
        </span>
    </div>
    </div>
    </div>
    <!-- Input -->
    <div class="chat-footer">
      <input
        style="flex: 1; margin-right: 10px; width: 100%;"
        v-model="newMessage"
        type="text"
        placeholder="Scrivi un messaggio..."
        @keyup.enter="sendTextMessage"
      >

      <button @click="chargePhoto" style="color:gray">
        <input type="file" @change="onFileChange" />
      </button>
    </div>

  </div>
  </div>
</template>

<style scoped>
.message-wrapper {
  border: 1px solid #ff0000;
  background-color  : #fff8f8;
  display: flex;
  flex-direction: column;
  margin-bottom: 10px;
  /* Larghezza massima del blocco messaggio */
   width: 40%; 
  /* Di default allineato a sinistra */

}

.message-item.my-message {
  justify-content: flex-end;
}

.message-item {
  display: flex;
  width: 100%;
  margin-bottom: 15px;
  align-self: flex-start; 
}

.chat-body{
    flex: 1;
    padding: 10px;
    height: calc(69vh);
    overflow-y: auto;
}
.chat-footer{
    display: flex;
    padding: 10px;
    width: 100%;
    border-top: 1px solid #ddd;
    background-color: whitesmoke;
}
.chat-header{
    display: flex;
    align-items: center;
    padding: 10px;
    padding-left: calc(50% - 90px);
    border-bottom: 1px solid #ddd;
    background-color: whitesmoke;
}

.message-content {
  margin-top: 5px;
  font-style: inherit;
  color: brown;
}
.message-header {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #555;
}
.message-sender {
  font-weight: bold;
}
.message-date {
  font-style: italic;
}
</style>

<script>
import { getMessages,sendMessage, getAuthorId, read, messagesAreEqual } from '../services/chatMethods';
export default {
  name: "Chat",
  props:['chatId','isGroup','chatName','chatPhoto'],
  watch: {
    chatPhoto(newPhoto) {
      console.log("chatPhoto aggiornato in Chat: ", newPhoto);
    },
    chatName(newName) {
      console.log("chatName aggiornato in Chat: ", newName);
    },
    chatId: {
      immediate: true,
      handler(newChatId) {
        this.newMessage= "";
        this.pollMessages();
      }
    }
  },
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  },
  mounted() {
    if (this.chatId) {
        this.pollMessages(this.chatId);
    }else {
      console.warn("chatId non definito al montaggio del componente Chat");
    }
  },
  data(){
    return {
      file: null, 
      newMessage: "",
      messages: [],
      timer: null,
      userId: Number(localStorage.getItem("userId"))
    }
  },
  methods:{
    onFileChange(event) {
      this.file = event.target.files[0];
      console.log("File selezionato:", this.file);
    },
     async getAuthor(authorId) {
      const authorName = await getAuthorId(authorId);
      console.log("authorName recuperato: ", authorName);
      return authorName;
    },
     formatDate(dateString) {
        return new Date(dateString).toLocaleString();
    },
    async pollMessages(){
        if (this.chatId) {
            this.loadMessages(this.chatId,this.isGroup);
            this.timer= setTimeout(this.pollMessages, 3000); // Poll ogni 3 secondi
        } else {
            console.warn("chatId non definito durante il polling dei messaggi");
        }
    },
    async loadMessages(chatId,isGroup) {
        try {
          const response = await getMessages(chatId,localStorage.getItem('userId'),isGroup);
          const flag=messagesAreEqual(this.messages,response)
          if(!flag){
            this.messages = response;
                for (const message of this.messages) {
                    message.authorName = await this.getAuthor(message.AuthorId);
                }
                if (response){
                    this.messages=this.messages.reverse();
                    await this.markAsRead(this.chatId, this.userId, this.isGroup, this.messages[this.messages.length-1].id);
                    console.log("Messaggi aggiornati in Chat: ", this.messages);

                }
          }else {
            console.log("Nessun nuovo messaggio da aggiornare in Chat.");
          }
                

        } catch (error) {
          console.error("Errore nel recupero dei messaggi:", error);
            }
        },
    
    async sendTextMessage() {
      if (this.newMessage.trim() === "") return;
      console.log("Invio messaggio:", this.newMessage);
      await sendMessage(this.chatId, this.newMessage, "text", this.isGroup);
      this.newMessage = "";
    },
    async markAsRead(chatId, userId, isGroup, messageId){
        await read(chatId, userId, isGroup, messageId);
    },
    async sendPhoto(){
        //TODO manda una foto
    }
    }
}
</script>