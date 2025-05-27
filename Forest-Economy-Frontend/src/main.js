import { createApp } from 'vue'
import pinia from './stores/index.js'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import "vue-data-ui/style.css"
import { VueUiRadar } from "vue-data-ui"
import zhCn from 'element-plus/es/locale/lang/zh-cn'

const app = createApp(App)

// app.config.globalProperties.$echarts = Echarts
app.use(pinia)
app.use(router)
app.component("VueUiRadar", VueUiRadar)
app.use(ElementPlus,{
        locale:zhCn,})
app.mount('#app')
