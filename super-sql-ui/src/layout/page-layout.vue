<template>
   <router-view  v-slot="{ Component, route }">
    <transition appear name="fade" mode="out-in">
      <div class="container">
        <component
            :is="Component"
            v-if="route.meta.ignoreCache"
            :key="route.fullPath"
        />
        <keep-alive v-else >
          <component :is="Component" :key="route.fullPath" />
        </keep-alive>
      </div>
    </transition>
  </router-view>
</template>

<script lang="ts" setup>

</script>

<style scoped lang="less">
.container{

}
/* 过度动画 */
.fade-enter-active {
  transition: all 0.3s ease-out;
}

.fade-leave-active {
  transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}

.fade-enter-from,
.fade-leave-to {
  transform: translateX(20px);
  opacity: 0;
}
</style>
