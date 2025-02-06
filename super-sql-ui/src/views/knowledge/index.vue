<template>
  <a-card class="global-card">
    <a-form
        ref="formRef"
        layout="inline"
        :model="formState"
        class="query-form">
      <a-form-item
          name="文档名称"
      >
        <a-input v-model:value="formState.question" placeholder="请根据文档名称查询"></a-input>
      </a-form-item>

      <a-form-item>
        <a-button :icon="h(SearchOutlined)" @click="getList" type="primary">查询</a-button>
      </a-form-item>

      <a-form-item>
        <a-button :icon="h(PlusOutlined)" @click="handAdd" type="primary">新建</a-button>
      </a-form-item>

    </a-form>


    <a-table :loading="loading" :columns="columns" :data-source="docData" :pagination="false" :scroll="{y: 400 }">
      <template #bodyCell="{ column, text }">
        <template v-if="column.dataIndex === 'metadata'">
          <a-tag color="geekblue">{{ text.script_type }}</a-tag>
        </template>

        <template v-if="column.key === 'opt'">
          <a-space warp>
          <a-button type="primary" @click="showModal(text)">查看</a-button>
            <a-popconfirm
                title="Are you sure delete this document?"
                ok-text="Yes"
                cancel-text="No"
                @confirm="confirm(text.id)"
                @cancel="cancel"
            >
              <a-button danger >删除</a-button>
            </a-popconfirm>

          </a-space>
        </template>
      </template>


    </a-table>
  </a-card>

  <a-modal v-model:visible="visible" title="文档内容" @ok="handleOk">
    {{docContent}}
  </a-modal>

  <a-modal v-model:visible="ragVisible"  @ok="handleRagOk" :width="800">
    <template #title>
      <div style="text-align: center;font-weight: bold;font-size: 18px">Add Training Data</div>

    </template>
    <a-form layout="vertical" :model="ragformState">

      <a-form-item label="Training Data Type">
        <a-radio-group v-model:value="ragformState.policy">
          <a-radio  value="DDL">

              <div style="font-weight: bold">DDL</div>

            <div>These are the CREATE TABLE statements that define your database structure.</div>

          </a-radio>
          <a-radio value="SQL">

            <div style="font-weight: bold">SQL</div>

            <div>This can be any SQL statement that works. The more the merrier.</div>
          </a-radio>
          <a-radio value="DOCUMENTATION">

            <div style="font-weight: bold">DOCUMENTATION</div>

            <div>This can be any text-based documentation. Keep the chunks small and focused on a single topic.</div>
          </a-radio>
        </a-radio-group>
      </a-form-item>

      <a-form-item v-if="ragformState.policy==='SQL'" label="Your Question">
        <a-input v-model:value="ragformState.question" />
      </a-form-item>

      <a-form-item label="Your Documentation">
        <a-textarea v-model:value="ragformState.content" />
      </a-form-item>
    </a-form>
  </a-modal>

</template>
<script lang="ts" setup>
import {h, onMounted, reactive, ref} from 'vue';
import {PlusOutlined, SearchOutlined} from '@ant-design/icons-vue';
import {getDocuments, removeDocument, trainData} from "@/api/knowledge.ts";
import {message} from "ant-design-vue";


const formState=ref({
  question:""
});

const ragformState=ref({
  policy:'DDL'
});

const ragVisible=ref(false);

const loading=ref(false);
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    align: 'center',
    key: 'id',
  },
  {
    title: '文档',
    dataIndex: 'text',
    align: 'center',
    key: 'text',
    ellipsis: true,
  },
  {
    title: '脚本类型',
    dataIndex: 'metadata',
    align: 'center',
    key: 'metadata',
    ellipsis: true,
  },
  {
    title: '操作',
    align: 'center',
    key: 'opt'
  }

];

const visible = ref<boolean>(false);
const docContent=ref()
const showModal = (i:any) => {
  console.log(i)
  docContent.value=i.text
  visible.value = true;
};


const confirm = (id:string) => {
  console.log(id);
  removeDocument({id:id}).then(res=>{
    console.log(res);
    if(res.data){
      message.success('deletion successful...');
      getList()
    }
  })
};

const cancel = (e: MouseEvent) => {
  console.log(e);
  //message.error('Click on No');
};

const handleOk = (e: MouseEvent) => {
  console.log(e);
  visible.value = false;
};

const handAdd = () => {
  ragVisible.value = true;
}

const handleRagOk = () => {
  loading.value=true
  console.log(ragformState.value)
  trainData(ragformState.value).then(res=>{
    console.log(res);
    message.success('training successful...');
    ragVisible.value = false;
    getList()

  })
}


const docData=ref();

const getList = () => {
  console.log(formState.value)
  loading.value=true
  getDocuments({question:formState.value.question}).then(res=>{
       console.log(res);
      docData.value = res.data;
      loading.value=false;
  })
}
onMounted(()=>{
  getList();
})

</script>

<style lang="less" scoped>
.query-form{
  margin-bottom: 20px;
  display: flex;
  align-items: center;
    .ant-input{
      height: 40px;
      width: 300px;
    }

  .ant-btn{
    height: 40px;
    width: 100px;
  }


}

</style>

