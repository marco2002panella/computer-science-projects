<template>
    <div class="selection-section">
        <div v-if="modalita!==null"class="search-section">
          <img src="https://static.thenounproject.com/png/1147394-200.png" alt="Logo" class="logo-icon" />
          <div class="search-wrapper">
            <input 
              type="text" 
              class="search-input" 
              placeholder="Inizia una nuova chat..."
              v-model="searchQuery"
              @input="$emit('query',$event.target.value)"
            />
            <img src="https://cdn-icons-png.flaticon.com/512/54/54481.png" alt="Cerca" class="icon-search" />
            <button @click="$emit('cambia-modalita',null)">Indietro</button>
          </div>
        </div>
        <div v-else class="choice-buttons">
          <button class="standard-button" @click="$emit('cambia-modalita','chat')">Crea nuova chat</button>
          <button class="standard-button" @click="$emit('cambia-modalita','gruppo')">Crea nuovo gruppo</button>
        </div>
      </div>
      <div class="navbar-title">
        <h3>WASAtext</h3>
      </div>

      <div class="actions-section">
        
        <button class="action-btn" @click="vaiAlProfilo" title="Il mio profilo">
          <img src="https://cdn-icons-png.flaticon.com/512/1077/1077114.png" alt="Profilo" class="icon-avatar" />
        </button>

        <button class="logout-btn" @click="eseguiLogout">
          <img src="https://www.pngfind.com/pngs/m/339-3396821_png-file-svg-download-icon-logout-transparent-png.png" alt="Logout" class="icon-logout" />
        </button>

      </div>
</template>
<script>   
export default{
    props:['modalita'],
    emits:['cambia-modalita','query'],
    name: "SearchSection",
    data(){
        return{
            searchQuery:''
        }
    },
    methods:{
    vaiAlProfilo() {
      console.log("Apertura impostazioni profilo");

    },
    eseguiLogout() {
      localStorage.clear();
      delete this.$axios.defaults.headers.common['Authorization'];
      this.$router.push('/');
    },
    }
}
</script>
<style>
@import '../assets/chatView.css';
@import '../assets/someStyles.css';
</style>