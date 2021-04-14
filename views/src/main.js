import Vue from 'vue'
import App from './App.vue'
import router from './router'
import './plugins/element.js'
import axios from 'axios'
import VueAxios from 'vue-axios'
import './assets/js/iconfont.js'
import './assets/css/tailwind.css'
import './assets/css/app.css'
import auth from './js/auth';

Vue.use(VueAxios, axios)
Vue.config.productionTip = false
Vue.prototype.GLOBAL = auth;
axios.defaults.baseURL = "http://localhost:8081/"

router.beforeEach((to,from,next) =>{
  if(to.meta.title){
    document.title = to.meta.title
  }
  next();
})

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
