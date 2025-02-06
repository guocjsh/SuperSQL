import { createApp,ref } from 'vue'
import './style.css'
import './static/iconfont/iconfont.css'
import App from './App.vue';
import router from './router'
import Antd from 'ant-design-vue';
import store from './store';
import * as Icons from '@ant-design/icons-vue'


import {
    message
  } from 'ant-design-vue'
// 没有按需引入的话，需要引入以下css
 import 'ant-design-vue/dist/reset.css'
// import 'ant-design-vue/dist/antd.css'

// import common from "@/common/common"


const app = createApp(App);
// app.config.globalProperties.$common = common
// app.provide('$message', message)

// 去掉Vue warn警告
app.config.warnHandler = () => null;


//全局注册图标
const icons = ref<any>(Icons);
for (const i in icons) {
    app.component(i, icons[i])
}


app.use(router);
app.use(Antd);
app.use(store);
// app.use(message)
app.mount('#app');

