<template>
    <form @submit.prevent="handleRegister">
      <input v-model="email" type="email" placeholder="Email" />
      <input v-model="password" type="password" placeholder="Password" />
      <button type="submit">Registrati</button>
    </form>
  </template>
  
  <script>
  import { registerUser } from "@/services/userService";
  
  export default {
    data() {
      return {
        email: "",
        password: "",
      };
    },
    methods: {
      async handleRegister() {
        try {
          await registerUser({ email: this.email, password: this.password });
          alert("Registrazione riuscita!");
          this.$router.push("/");
        } catch (error) {
          alert("Errore: " + error.response?.data?.message || "Registrazione fallita");
        }
      },
    },
  };
  </script>
  