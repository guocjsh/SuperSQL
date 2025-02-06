<template>
  <a-menu
    v-model:selectedKeys="current"
    @select="handSelected"
    mode="horizontal"
    class="custom-menu"
    :items="items"
  />
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import router from "@/router";
const current = ref(['chat'])
const items = ref([
  {
    key: 'chat',
    label: 'AI 助手',
    title: 'AI 助手',
  },
  {
    key: 'knowledge',
    label: '知识库',
    title: '知识库',
  }
])

const handSelected = (item :any) => {
  router.push({ path: item.key })
}


</script>

<style scoped>
.custom-menu {
  border-bottom: none;
  flex: 1 1 0%;
  max-width: calc(100% - 256px);
  background: none;
}

.basic-layout .ant-menu-item-selected:after {
  border-bottom: 2px solid #152da5 !important;
}
:deep(.ant-menu-title-content) {
  font-size: 16.8px !important;
}

:deep(
    .ant-menu-overflow-item:hover,
    .ant-menu-item:hover,
    .ant-menu-submenu:hover,
    .ant-menu-item-selected:hover
  ) {
  color: #152da5 !important;
}

:deep(.ant-menu-item:not(.ant-menu-item-selected):active) {
  background: none !important;
  clear: #1890ff !important;
 }
</style>
