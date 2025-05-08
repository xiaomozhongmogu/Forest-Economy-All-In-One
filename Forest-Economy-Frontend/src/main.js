import { createApp } from 'vue'
import pinia from './stores/index.js'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// import Echarts from 'echarts'

const app = createApp(App)

// app.config.globalProperties.$echarts = Echarts
app.use(pinia)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
