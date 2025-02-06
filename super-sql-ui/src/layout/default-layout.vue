<template>
  <a-layout>
    <a-layout  :style="{ background: colorConfirm }">
      <div class="width-000">
        <a-layout-header
          :style="{ background: '#ffffff' ,display:'flex'}"
        >
            <img class="logo" src="../assets/images/logo.png" alt=""/>
          <Menu/>
          <Header/>
        </a-layout-header>
        <a-layout-content class="main-other'">
          <PageLayout />
        </a-layout-content>
        <Footer />
      </div>
    </a-layout>
  </a-layout>
</template>

<script lang="ts" setup>
import { ref} from 'vue'
import Footer from '@/components/footer/index.vue'
import Menu from '@/components/menu/index.vue'
import Header from '@/components/header/index.vue'
import PageLayout from '@/layout/page-layout.vue'
import { useRouter } from 'vue-router'
const router = useRouter()

import { MenuUnfoldOutlined, MenuFoldOutlined } from '@ant-design/icons-vue'
import { deepStrictEqual } from 'assert'
import NoFound from 'ant-design-vue/es/result/noFound'

const collapsed = ref<boolean>(false)
var colorConfirm = ref<string>('#f2f2ff')

const rightIsoOpen = ref<boolean>()
//获得子组件传过来的数据
const rightSideFun = (val: boolean) => {
  rightIsoOpen.value = val
  console.log('rightIsoOpen:', rightIsoOpen.value)
}
// 监听当前路由
// watch(
//   () => router.currentRoute.value,
//   (newValue: any) => {
//     if (newValue.name.includes(routerChat.value)) {
//       colorConfirm.value = '#ffffff'
//       isChat.value = true
//       //调用子组件的 play方法
//       // childComp.value.setOpenData(true)
//       rightSideFun(true)
//     } else {
//       colorConfirm.value = 'rgb(242, 242, 255)'
//       isChat.value = false
//       rightSideFun(false)
//     }
//   },
//   { immediate: true }
// )
</script>

<style lang="less" scoped>

.logo{
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  float: left;
}

.ant-layout-header {
}
.width-300 {
  width: calc(100% - 300px) !important;
}
.width-000 {
  width: calc(100%) !important;
}

.main-chat {
  margin: 16px 16px 0px 16px;
  overflow: auto;
  height: 100%;
}
.main-other {
  margin: 16px 16px 16px 16px;
  overflow: auto;
  height: 100vh;
}
</style>
