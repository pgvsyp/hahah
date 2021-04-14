<template>
  <div id="app">
    <div v-if="$route.meta.keepAlive" class="header">
      <Nav></Nav>
    </div>
    <div class="content">
      <transition>
        <keep-alive>
          <router-view></router-view>
        </keep-alive>
      </transition>
    </div>
    <Footer></Footer>
  </div>
</template>

<script>


  import Nav from "./components/Nav";
  import Footer from "@/components/Footer";

  export default {
    name: 'app',
    components: {
      Nav,
      Footer
    }
  }
</script>

<style scoped>
#app {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
}
.content {
  flex: 1;
}
</style>
