<script setup lang="ts">
import {UserOutlined,LoginOutlined,SettingOutlined} from "@ant-design/icons-vue";
</script>

<template>
  <div class="ant-header">



    <a-dropdown :placement="'bottom'">
      <a-avatar>
        <template #icon>
          <UserOutlined />
        </template>
      </a-avatar>
      <template #overlay>
        <a-menu >
          <a-menu-item>
            <a target="_blank" rel="noopener noreferrer" href="http://www.alipay.com/">
              <UserOutlined /> 个人中心
            </a>
          </a-menu-item>
          <a-menu-item>
            <a target="_blank" rel="noopener noreferrer" href="http://www.taobao.com/">
              <SettingOutlined /> 设置
            </a>
          </a-menu-item>
          <a-menu-item>
            <a target="_blank" rel="noopener noreferrer" href="http://www.tmall.com/">
              <LoginOutlined style="color: red" />  退出
            </a>
          </a-menu-item>
        </a-menu>
      </template>
    </a-dropdown>

  </div>

</template>

<style scoped lang="less">
.ant-header{
  display: flex;
  align-items: center;
  float: right;
  margin-right: 0;
  margin-left: auto;
}
</style>
