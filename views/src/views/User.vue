<template>
  <div class="container mx-auto">
    <p class="text-center text-4xl text-gray-400 font-black my-5">个人中心</p>
    <div class="flex">
      <div class="w-1/6">
        <div class="text-2xl">
          <div class="mb-1">
            <router-link to="/myinf/main">
              <el-button type="success" icon="el-icon-user-solid">个人信息</el-button>
            </router-link>
          </div>
          <div class="text-xl mb-1">
            <router-link to="/myinf/changepassword">
              <el-button type="primary" icon="el-icon-user-solid">修改密码</el-button>
            </router-link>
          </div>
          <div class="text-xl mb-1">
            <router-link to="/myinf/changeavator">
              <el-button type="primary" icon="el-icon-user-solid">修改头像</el-button>
            </router-link>
          </div>
          <div class="text-xl mb-1">
            <router-link to="/myinf/destroyac">
              <el-button type="primary" icon="el-icon-user-solid">注销账号</el-button>
            </router-link>
          </div>
          <div class="mb-1">
            <router-link to="/myinf/address">
              <el-button type="success" icon="el-icon-user-solid">收货地址</el-button>
            </router-link>
          </div>
          <div class="mb-1">
            <router-link to="/myinf/orderlog">
              <el-button type="success" icon="el-icon-user-solid">订单记录</el-button>
            </router-link>
          </div>
        </div>
      </div>
      <div class="w-3/6 mx-auto">
        <transition>
          <keep-alive>
            <router-view></router-view>
          </keep-alive>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "User"
}
</script>

<style scoped>

</style>