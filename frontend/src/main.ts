//Aggancia Vue, Pinia, Router e il CSS base.


// src/main.ts
import { createApp } from "vue"
import { createPinia } from "pinia"
import { router } from "./router"
import App from "./App.vue"
import Toast, { POSITION, type PluginOptions } from "vue-toastification"

// CSS
import "./assets/styles.css"
import "vue-toastification/dist/index.css"
import "leaflet/dist/leaflet.css"


const app = createApp(App)

// Config toast
const options: PluginOptions = {
  position: POSITION.TOP_RIGHT,
  timeout: 3000,
  closeOnClick: true,
  pauseOnHover: true,
  draggable: true,
  hideProgressBar: false
}

app.use(createPinia())
app.use(router)

// 👉 castiamo Toast a `any` per bypassare il problema di overload TS
app.use(Toast as any, options)

app.mount("#app")
