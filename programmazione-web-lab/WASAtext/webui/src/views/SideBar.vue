<template>
    <div v-if="modalita===null">
        <div class="sidebar-chat-items">
          <div class="sidebar-text">
            <h4 class="search-title" style="font-size: 24px; align-items: center;">Le tue chat</h4>
              <ChatListButton 
                    v-for="chat in chats" 
                    :key="'chat-' + chat.id" 
                    :name="chat.name"
                    :avatar="chat.photo"
                    class="sidebar-chat-item"
                    @click="$emit('clickChatId',chat.id,chat.IsGroup)"
                  />
              <div v-if="!chats || chats.length === 0" class="no-results" key="no-chats-msg">
                <h4>Vai nella sezione "cerca" per iniziare una nuova chat!</h4>
              </div>
          </div>
        </div>
      </div>  

    <div v-else-if ="searchQuery && searchQuery.length > 0 && modalita!=null">
      <div class="sidebar-chat-items">
        <div class="sidebar-text">
          <h4 class="search-title" style="font-size: 24px; align-items: center;">Risultati ricerca</h4>
            <div v-if="modalita==='chat'">
                <ChatListButton 
                    v-for="user in searchResults" 
                    :key="'user-' + user.Id" 
                    :name="user.Username"
                    class="sidebar-chat-item on-mouse-create"
                    @click="createChat(user.Id)"
                    />
            </div>
            <div v-else>
                <ChatListButton 
                    v-for="user in searchResults" 
                    :key="'user-' + user.Id" 
                    :name="user.Username"
                    class="sidebar-chat-item on-mouse-add"
                    @click="addUserToGroup"
                    />
            </div>
            </div>

            <div v-if="!searchResults || searchResults.length === 0" class="no-results" key="no-res-msg">
              Nessun utente trovato
            </div>
          </div>
        </div>
</template>
<script>
import { searchByName } from '../services/users';
import { retriveChats } from '../services/chatMethods';
import ChatListButton from './ChatListButtons.vue';
import {createChat} from '../services/chatMethods';
export default{
    props:['modalita','searchQuery'],
    emits:['clickChatId'],
    components:{
        ChatListButton
    },
    data(){
        return{
            myId: localStorage.getItem('userId'), // Il tuo ID recuperato dal login
            myUsername: localStorage.getItem('username'),
            chats: [],
            searchResults: [],
            user_ids: [],
            timer: null
            }
        },
    watch: {
    modalita(newVal){
        if(newVal===null){
            this.searchResults=[]
            this.retriveChats();
        }else{
            this.cercaUtenti();
        }
    },
    searchQuery(newVal) {
        console.log("Valore ricevuto nella sidebar:", newVal);
            if (newVal && newVal.length > 0) {
                this.cercaUtenti();
            }else {
                this.searchResults = [];
            }
        }
    },
    mounted() {
        this.retriveChats();
        this.timer = setInterval(async () => {
        await this.retriveChats();
        }, 5000);
    },
    beforeUnmount() {
        clearInterval(this.timer);
    },
    methods: {
    async addUserToGroup(otherUserId){
        console.log("aggiungo l'utente % d al gruppo ",otherUserId)
    },
    async createChat(otherUserId){
        console.log("creo la chat con l'utente %d ",otherUserId)
        await createChat(this.myId,otherUserId)
    },
    async retriveChats() {
      this.chats = await retriveChats(this.myId);
    },
    async cercaUtenti() {
      this.searchResults = await searchByName(this.searchQuery);
      console.log("Risultati ricevuti dal server:", this.searchResults);
    },
  } 
}

</script>

<style>
</style>