<template>
  <div class="nvwa-login-container">
    <!-- 视频背景 -->
<!--    <video src="@/assets/video/ucenter.mp4" class="bg-video" autoplay loop muted></video>-->
    <div class="content">

      <div class="content-inner">
        <LoginForm />
      </div>
      <div class="footer">
        <Footer />
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import Footer from './components/footer.vue'
import LoginForm from "./components/login-form.vue";
</script>

<style lang="less" scoped>
.nvwa-login-container{
  display: flex;
  height: 100vh;
}
.bg-video{
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -10;
  object-fit: cover;
}

.content {
  background-image: url(https://static.zhihu.com/heifetz/assets/sign_bg.47eec442.png);
  background-color: #b8e5f8;
  background-repeat: no-repeat;
  background-size: cover;
  -webkit-box-sizing: border-box;
  box-sizing: border-box;
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
  -ms-flex-direction: column;
  flex-direction: column;
  height: 100vh;
  overflow: auto;
  width: 100%;
  position: relative;
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;
  //padding-bottom: 40px;
}

.content-inner{
  width: 380px;
}
.logo {
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 20px;
  &-text {
    margin-right: 4px;
    margin-left: 4px;
    color: var(--color-fill-1);
    font-size: 20px;
  }
}

.footer {
  position: absolute;
  right: 0;
  bottom: 0;
  width: 100%;
}
</style>
