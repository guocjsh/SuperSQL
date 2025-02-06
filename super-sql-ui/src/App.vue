<template>
  <a-config-provider
    :theme="{
      token: {
        // colorPrimary: '#e8e8ff',
       colorPrimary: '#535bf2'
      },
    }"
  >
    <div id="app">
      <router-view />
    </div>
  </a-config-provider>
</template>

<script lang="ts" setup>
import { theme } from 'ant-design-vue'
</script>
