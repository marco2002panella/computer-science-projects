
<template>
  <div class="chat-container">
    <header class="custom-navbar">
      <SearchSection
      :modalita="modalita"
      @cambia-modalita="aggiornaMod"
      @query="aggiornaQuery"
      />
    </header>
    
    <div class="chat-main-layout ">
      <aside class="chat-sidebar">
        <SideBar
        :modalita="modalita"
        :searchQuery="searchQuery"
        @clickChatId="aggiornaChatId"
        />
      </aside>
      
      <main class="chat-view-area">
        <Chat 
        :isGroup="currentIsGroup"
        :chatId="currentChatId" />
      </main>
    </div>
  </div>
</template>

<script>
import ChatListButton from './ChatListButtons.vue';
import SearchSection from './SearchSection.vue';
import SideBar from './SideBar.vue';
import Chat from './Chat.vue';
export default {
  name: "ChatView",
   components: {
    ChatListButton,
    SearchSection, 
    SideBar,
    Chat        
  },
  data(){
    return {
      currentIsGroup: false,
      modalita:null,
      searchQuery:'',
      currentChatId: null
    }
  },
  methods:{
    aggiornaChatId(newChatId,isGroup){ 
      console.log("chatId aggiornato in chatView: ", newChatId);
      console.log("isGroup aggiornato in chatView: ", isGroup);
      this.currentChatId = newChatId;
      this.currentIsGroup = isGroup;
    },
    aggiornaMod(newmod){
      console.log("cambiata modalita in %s",newmod)
      this.modalita=newmod
    },
    aggiornaQuery(newQ){
      this.searchQuery=newQ
    }
  }
}
</script>

<style scoped>
@import '../assets/chatView.css';
@import '../assets/someStyles.css';
</style>