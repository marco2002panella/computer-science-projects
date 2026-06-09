<script>
export default{
	data(){
		return {
			username: ""
		}
	},
	methods: {
		 async login(){
			const usernamePulito = this.username.trim();
			// 2. Controllo di sicurezza: deve essere lungo almeno 3 caratteri
			if (usernamePulito.length < 3) {
				this.errorMessage = "L'username deve contenere almeno 3 caratteri!";
				return; 
			}
			// 3. Invia la richiesta di login a Go
			try {
				const response = await this.$axios.post('/session', {
					name: usernamePulito 
				});

				this.userId = response.data.identifier; 
				this.username = usernamePulito;
				
				console.log("salviamo ID nel local storage e nel bearer token : %d", this.userId);
				localStorage.setItem('userId', this.userId);
				localStorage.setItem('username', this.username);
				this.$axios.defaults.headers.common['Authorization'] = `Bearer ${this.userId}`; //cosi in pratica usiamo sempre l'authorization header
				this.$router.push('/chat'); // Reindirizza alla pagina della chat

			} catch (error) {
				console.error("Errore:", error);
				this.errorMessage = "Impossibile connettersi al server.";
			}
		}
	}
}
</script>

<template>
	<div style="display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 70vh;">
		
		<h2>Accedi a WASAtext</h2>
		
		<form @submit.prevent="login">
			<label for="username">Username:</label>
			<input type="text" v-model="username" placeholder="Inserisci il tuo nome..." />
			<button type="submit">Entra</button>
		</form>

	</div>
</template>

<style>
</style>
