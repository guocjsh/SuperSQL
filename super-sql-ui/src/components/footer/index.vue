<template>
 <a-layout-footer class="footer"
    >Super SQL ©2025 Created by ChengJie.Guo</a-layout-footer
  >
</template>

<script lang="ts" setup></script>

<style lang="less" scoped>
.ant-pro-global-footer {
  margin: 24px 0 !important;
  padding: 0 16px;
  text-align: center;
  .ant-pro-global-footer-links {
    margin-bottom: 8px;
    a {
      color: rgba(0, 0, 0, 0.45);
      transition: all 0.3s;
    }
    a:not(:last-child) {
      margin-right: 40px;
    }
    .iconfont {
      font-size: 14px !important;
    }
  }
  .ant-pro-global-footer-copyright {
    color: rgba(0, 0, 0, 0.45);
    font-size: 14px;
    .iconfont {
      font-size: 14px !important;
    }
    a {
      color: #1890ff;
      text-decoration: none;
      background-color: transparent;
      outline: none;
      cursor: pointer;
      transition: color 0.3s;
      -webkit-text-decoration-skip: objects;
    }
  }
}

.footer {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 40px;
  color: var(--color-text-2);
  background-color: #f2f2ff;
  text-align: center;
}
</style>
