<template>
  <contextHolder />
  <a-spin :spinning="false">
    <div class="login-form-wrapper">
      <div class="login-slogan">
        <div class="login-slogan-title">
          <img
              alt="logo"
              class="login-slogan-logo"
              src="@/assets/images/logo.png"
          />
          N V W A
        </div>
        <div class="login-slogan-sub-title">Build your own AI assistant</div>
      </div>

      <a-form
          :model="formState"
          class="login-form"
          name="basic"
          :label-col="{ span: 8 }"
          :wrapper-col="{ span: 16 }"
          autocomplete="off"
          @finish="onFinish"
          @finishFailed="onFinishFailed"
      >
        <a-tabs centered>
          <a-tab-pane key="1">
            <template #tab>
              <span>
                <wechat-outlined/>
                微信登录
              </span>
            </template>

            <div style="text-align: center;">
              <a-image :preview="false" class="qr-code" :src="qrcode">
              </a-image>
              <a-typography-text>
                扫码关注公众号『 郭聪明AI 』登录
              </a-typography-text>
            </div>

            <a-typography-text class="login-sm" type="secondary">
              注册登录即表示同意
              <a>用户协议</a>
              和
              <a>隐私政策</a>
            </a-typography-text>
          </a-tab-pane>
        </a-tabs>
      </a-form>


    </div>


  </a-spin>


</template>

<script lang="ts" setup>

import {
  WechatOutlined
} from '@ant-design/icons-vue'
import {reactive, ref} from 'vue';
import {getUserByTicket, getWechatLoginCode} from "@/api/login";
import {setToken} from "@/util/auth";
import {useRouter} from "vue-router";
import { message } from 'ant-design-vue';
const [messageApi, contextHolder] = message.useMessage();

interface FormState {
  username: string;
  password: string;
  remember: boolean;
}

const activeKey = ref('1');
const qrcode = ref()
const formState = reactive<FormState>({
  username: '',
  password: '',
  remember: true,
});
const onFinish = (values: any) => {
  console.log('Success:', values);
};

const onFinishFailed = (errorInfo: any) => {
  console.log('Failed:', errorInfo);
};

const wxTicket = ref();
const refreshTask = ref()
const router = useRouter()

const wxLoginCode = async () => {
  const res = await getWechatLoginCode();
  console.log('res;;;;', res)
  qrcode.value = res.data.qrCodeUrl;
  wxTicket.value = res.data.ticket;
  getUserInfoTask();
}
wxLoginCode();


const getUserInfoTask = () => {
  refreshTask.value = setInterval(() => {
    const params = {"ticket": wxTicket.value}
    getUserByTicket(params).then(res => {
      console.log('rrree',res)
      if(res.code===20000){
        stopTask();
        localStorage.setItem('nvwa-user', JSON.stringify(res.data));
        setToken(res.data.token);
        router.push({ name: 'model' })
      }
      if(res.code ==54000) {
        messageApi.warning("用户已被禁用，请联系客服人员",6);
        stopTask();
      }
    })
  }, 1000);
}


const stopTask = () => {
  clearInterval(refreshTask.value)
}


</script>

<style lang="less" scoped>

.login-slogan {
  text-align: center;
  line-height: 44px;

  &-logo {
    width: 54px;
    height: 44px;
    vertical-align: top;
  }

  &-title {
    position: relative;
    top: 2px;
    color: var(--color-text-1);
    font-weight: 600;
    font-size: 20px;
  }

  &-sub-title {
    color: var(--color-text-3);
    font-size: 14px;
  }
}

.login-form-wrapper {
  background-color: #ffffff;
  padding: 30px 40px;
  border-radius: 10px;
  //display: flex;
  align-items: center;
  justify-content: center;
}

.login-form-content {
  .login-form-title {
    color: var(--color-text-1);
    font-weight: bold;
    font-size: 20px;
    line-height: 32px;
    text-align: center;
  }

  .login-form-sub-title {
    color: var(--color-text-3);
    font-size: 12px;
    line-height: 32px;
    text-align: center;
  }
}

.login-form {
  display: flex;
  flex-direction: column;
  width: 100%;
}

.qr-code {
  width: 100%;
  box-shadow: 1px 1px 10px 0 #eee;
}

.login-sm {
  margin-top: 10px;
  font-size: 13px;
  display: flex;
  justify-content: center;
}
</style>
