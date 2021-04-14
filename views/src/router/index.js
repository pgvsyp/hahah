import Vue from 'vue'
import Router from 'vue-router'
import Register from "../views/Register"
import Admin from "../views/Admin"
import Index from "../views/Index";
import Login from "../views/Login";
import Buy from "@/views/Buy";
import User from "@/views/User";
import UserMain from "@/components/UesrMain";
import ChangePassword from "@/components/ChangePassword";
import ChangeAvator from "@/components/ChangeAvator";
import DesAccount from "@/components/DesAccount";
import Address from "@/components/Address";
import OrderLog from "@/components/OrderLog";

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/admin',
      name: 'Admin',
      component: Admin,
      meta: {
        keepAlive: true,
        title: '后台',
      }
    },
    {
      path: '/',
      name: 'Index',
      component: Index,
      meta: {
        keepAlive: true,
        title: '首页',
      }
    },
    {
      path: '/register',
      name: 'Register',
      component: Register,
      meta: {
        keepAlive: false,
        title: '注册',
      },
    },
    {
      path: '/admin',
      name: 'Admin',
      component: Admin,
      meta: {
        keepAlive: false
      },
    },{
      path: '/login',
      name: 'Login',
      component: Login,
      meta: {
        keepAlive: false
      },
    },{
      path: '/buy',
      name: 'Buy',
      component: Buy,
      meta: {
        keepAlive: true
      },
    },{
      path: '/myinf',
      name: 'User',
      component: User,
      meta: {
        keepAlive: true
      },
      children:[
        {
          path: '/myinf/main',
          name: UserMain,
          component: UserMain,
          meta: {
            keepAlive: true
          }
        },{
          path: '/myinf/changepassword',
          name: ChangePassword,
          component: ChangePassword,
          meta: {
            keepAlive: true
          }
        },
        {
          path: '/myinf/changeavator',
          name: ChangeAvator,
          component: ChangeAvator,
          meta: {
            keepAlive: true
          }
        },{
          path: '/myinf/destroyac',
          name: DesAccount,
          component: DesAccount,
          meta: {
            keepAlive: true
          }
        },{
          path: '/myinf/address',
          name: Address,
          component: Address,
          meta: {
            keepAlive: true
          }
        },{
          path: '/myinf/orderlog',
          name: OrderLog,
          component: OrderLog,
          meta: {
            keepAlive: true
          }
        },

      ]
    }
  ]
})
