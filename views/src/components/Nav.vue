<template>
    <div>
      <div class="grid grid-flow-col auto-cols-max bg-gray-600 text-white text-2xl items-center justify-center h-10">
        <div>
          <router-link to="/">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icondaohang"></use>
            </svg>
            <span>首页</span>
         </router-link>
        </div>

        <div>
          <router-link to="/buy">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icondaohang"></use>
            </svg>
            <span>购物车</span>
          </router-link>
        </div>

        <div>
          <router-link to="/myinf">
            <svg class="icon" aria-hidden="true">
              <use xlink:href="#icondaohang"></use>
            </svg>
            <span>个人中心</span>
          </router-link>
        </div>

      </div>
    </div>

</template>

<script>
    export default {
        name: 'nav.vue',
        data() {
            return {
            };
        },
        methods: {
        }
    }
</script>

<style scoped>

</style>