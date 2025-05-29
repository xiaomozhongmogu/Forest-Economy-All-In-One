<template>
  <div class="crop-info-container">
    <el-card class="crop-info-card" shadow="never">
      <template #header>
        <h2 class="crop-info-title">作物信息查询</h2>
      </template>
      <el-form :model="queryForm" :inline="true" class="crop-info-query-form">
        <el-form-item label="作物名称">
          <el-input v-model="queryForm.cropName" placeholder="请输入作物名称" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="custom-green-button" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <crop-collapse-table :crop-list="cropList" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import CropCollapseTable from './components/cropCollapseTable.vue';

/**
 * 查询表单数据
 * @type {ref<{cropName: string}>}
 */

const queryForm = ref({
  cropName: ''
});

/**
 * 作物列表数据
 * @type {Ref<Array<Object>>}
 */
const cropList = ref([]);

// --- 方法 ---

/**
 * 处理查询操作
 * 模拟从后端获取作物数据
 */
const handleQuery = () => {
  console.log('执行查询操作，查询条件：', queryForm.value);

  // 实际项目中，这里会替换为 API 调用，例如：
  // const res = await fetchCropDataApi(queryForm.value);
  // cropList.value = res.data;

  // 模拟查询成功后，更新 cropList
  cropList.value = [
    {
      cropName: '小麦',
      alias: '麦子',
      totalNumber: 1000,
      farmland: [
        { farmername: '小麦田1', phonenum: '123123123', number: 500 },
        { farmername: '小麦田2', phonenum: '123123123', number: 500 }
      ]
    },
    {
      cropName: '玉米',
      alias: '玉米',
      totalNumber: 1000,
      farmland: [
        { farmername: '玉米田A', phonenum: '18888888888', number: 300 },
        { farmername: '玉米田B', phonenum: '19999999999', number: 700 }
      ]
    },
    {
      cropName: '水稻',
      alias: '大米',
      totalNumber: 2500,
      farmland: [
        { farmername: '水稻田甲', phonenum: '13000000000', number: 1500 },
        { farmername: '水稻田乙', phonenum: '13111111111', number: 1000 }
      ]
    }
  ];
};

/**
 * 处理重置操作，清空查询表单并重新查询
 */
const handleReset = () => {
  queryForm.value = {
    cropName: ''
  };
  // 重置后通常会重新加载数据
  handleQuery();
};

// --- 生命周期钩子 ---
// 首次加载组件时执行查询
onMounted(() => {
  handleQuery();
});
</script>

<style scoped>

/* --- 容器样式 --- */
.crop-info-container {
  background-color: var(--background-color-page);
  padding: 0; /* 保持一致性，如果不需要内边距则设置为0 */
}

.crop-info-card {
  background-color: var(--background-color-card);
  border-radius: 10px;
  border: 1px solid var(--border-color);
  box-shadow: 4px 4px 8px rgba(0, 0, 0, 0.1), 4px 4px 12px rgba(0, 0, 0, 0.08);
}

/* --- 标题样式 --- */
.crop-info-title {
  font-weight: 600;
  font-size: large; /* 确保字体大小与设计一致 */
  color: var(--primary-green);
  text-align: left;
  margin: 0; /* 移除默认的 h2 外边距 */
  padding-bottom: 15px; /* 与 Element Plus card header 保持一致 */
  border-bottom: 1px dashed var(--border-color); /* 添加分割线，保持与查询表单下方的风格一致 */
}

/* --- 查询表单样式 --- */
.crop-info-query-form {
  margin-bottom: 20px;
  padding-bottom: 10px; /* 添加内边距和虚线边框，与上一个组件风格一致 */
  border-bottom: 1px dashed var(--border-color);
}

/* --- 自定义按钮样式 (与上一个组件保持一致) --- */
.custom-green-button {
  background-color: var(--primary-green) !important;
  color: white !important;
  border-color: var(--primary-green) !important;
  transition: all 0.3s ease; /* 添加过渡效果 */
}

.custom-green-button:hover,
.custom-green-button:focus {
  background-color: var(--light-green) !important;
  border-color: var(--light-green) !important;
  color: white !important;
}


</style>
