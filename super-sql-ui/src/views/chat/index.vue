<script setup lang="ts">
import {SendOutlined, UserOutlined} from "@ant-design/icons-vue";
import {onBeforeMount, onMounted, ref} from "vue";
import {fetchChatProcess} from "@/api/chat.ts";
import {useScroll} from "@/util/useScroll.ts";
import MarkdownIt from 'markdown-it'
import mdKatex from '@traptitech/markdown-it-katex'
import hljs from 'highlight.js';
import "highlight.js/styles/vs2015.css";

const {scrollRef, scrollToBottom, scrollToBottomIfAtBottom} = useScroll()

const messages=ref([] as any)
const blockIndex=ref(0)
const filterText = ref<string>()
let controller = new AbortController()

const mdi = new MarkdownIt({
  linkify: true,
  highlight(code, language) {
    const validLang = !!(language && hljs.getLanguage(language))
    if (validLang) {
      const lang = language ?? ''
      return highlightBlock(hljs.highlight(lang, code, true).value, lang)
    }
    return highlightBlock(hljs.highlightAuto(code).value, '')
  }
})
mdi.use(mdKatex, { blockClass: 'katexmath-block rounded-md p-[10px]', errorColor: ' #cc0000' })

function highlightBlock(str:any, lang:any) {
  console.log('str :', str)
  console.log('lang :',lang)
  return `<pre class="pre-code-box"><div class="pre-code-header"><span class="code-block-header__lang">${lang}</span><span class="code-block-header__copy"></span></div><div class="pre-code"><code class="hljs code-block-body ${lang}"> ${str}</code></div></pre>`
}

const getMdiText = (value:any) => {
  return mdi.render(value)
}


onMounted(()=> {
  const text= "您好，我是由SuperSQL研发团队研发的SQL智能助手，请问我有什么可以帮您？"
  text.split("").forEach((item)=>{
    if(!messages.value[blockIndex.value]){
      const message={
        messageType: "ai",
        content:item
      }
      messages.value.push(message)
    }else {
      messages.value[blockIndex.value].content += item
    }


  })
  blockIndex.value = blockIndex.value+1
})

const send = async () => {
  const data={
    question: filterText.value,
  }
  const message={
    messageType: "your",
    content:filterText.value
  }
  messages.value.push(message)
  filterText.value="";
  scrollToBottom()
  await fetchChatProcess({
    data,
    signal: controller.signal,
    onDownloadProgress: ({event}) => {
      const xhr = event.target
      const {responseText} = xhr
      const content={
        messageType:'ai',
        content:responseText//.replace('\n','')
      }
      if(!messages.value[blockIndex.value]){
        messages.value.push(content)
      }else {
        messages.value[blockIndex.value].content = responseText//.replace('\n','');
      }
      scrollToBottom()
      scrollToBottomIfAtBottom()
    },
    beforeRequest:()=>{
      //steamWord.value=true;

      blockIndex.value=messages.value.length
    },
    afterRequest:()=>{
      console.log("请求完成")
      //steamWord.value=false;
      //endPrint();
    }
  })
}


</script>

<template>

  <a-card class="global-card">
    <div class="center-main">

      <div class="chat-list" id="scrollRef" ref="scrollRef">
       <div v-for="(item, index) in messages" :key="index">
         <div class="ai-message" v-if="item.messageType === 'ai'">
           <a-space align="center">
             <a-avatar :size="50" style="background-color: rgba(255,255,255,0) ">
               <template #icon>
                 <img style="width: 48px !important;height: 48px !important;" src="@/assets/images/ai_logo.png">
               </template>
             </a-avatar>
             <div class="ai-content" v-html="getMdiText(item.content)">
             </div>
           </a-space>
         </div>

         <div class="you-message" v-if="item.messageType === 'your'">

           <a-space align="center">
             <div class="you-content" v-html="item.content">
             </div>

             <a-avatar :size="50" style="background-color: #535bf2">U</a-avatar>

           </a-space>

         </div>
       </div>




      </div>


      <div class="send-area">

        <a-input @pressEnter="send" :bordered="false" class="send-input" v-model:value.trim="filterText" placeholder="请输入你的问题">
          <template #suffix>
            <a-tooltip title="Extra information">
              <SendOutlined  @click="send" style="color: #535bf2;font-size: 26px" />
            </a-tooltip>
          </template>
        </a-input>

      </div>

    </div>
  </a-card>

</template>


<style scoped lang="less">

:deep(img){
  width: 100px !important;
  height: auto !important;
  border: 4px solid #ffffff;
  border-radius: 10px;
}
.center-main{
  position: relative;
  width: 100%;
  height: 70vh;
  overflow: hidden;
  background-color: #fff;
}


.chat-list{
  width: 100%;
  height: calc(100% - 110px);
  padding:0 80px;
  overflow-x: hidden;
  scrollbar-width: none;
  .ai-message{
    //float: left;
    margin: 10px 0;
    width: 100%;
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    flex-shrink: 1;
    .ai-content{
      background-color: rgba(83, 91, 242, 0.09);
      border-radius: 10px;
      padding: 15px;
      img{
        width: 400px !important;
        height: auto !important;
      }
    }
  }
  .you-message{
    margin: 10px 0;
    //float: right;
    width: 100%;
    display: flex;
    flex-direction: row;
    justify-content: flex-end;
    .you-content{
      background-color: rgba(83, 91, 242, 0.09);
      border-radius: 10px;
      padding: 15px;
    }
  }
}

.send-area{
  position: absolute;
  bottom: 0;
  z-index: 99;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-end;
  width: 100%;
  height: 120px;
  background: linear-gradient(0deg, #fff, hsla(0, 0%, 100%, .8));
}

.send-input{
  padding: 0 2vw !important;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 90%;
  min-height: 50px;
  padding: 0 10px 0 0;
  overflow: hidden;
  line-height: 50px;
  border: 1px solid #535bf2;
  border-radius: 6px;
}
</style>
