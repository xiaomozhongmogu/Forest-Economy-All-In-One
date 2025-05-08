<template>
  <div class="common-layout">
    <el-container class="data_management_content">
      <el-header class="header_content">
        <h1 class="title">数据查询与管理</h1>
        <el-row :gutter="10" class="header_row_box">
          <el-col :span="6">
            <div class="header_button_box">
              <el-button
                size="large"
                class="header_button" :class="{ 'selected': selectedFunction === '1' }"
                @click="() => handleHeaderButtonClick('1')"
              >农户信息</el-button>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="header_button_box">
              <el-button
                size="large"
                class="header_button" :class="{ 'selected': selectedFunction === '2' }"
                @click="() => handleHeaderButtonClick('2')"
              >地块信息</el-button>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="header_button_box">
              <el-button
                size="large"
                class="header_button" :class="{ 'selected': selectedFunction === '3' }"
                @click="() => handleHeaderButtonClick('3')"
              >经济作物信息</el-button>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="header_button_box">
              <el-button
                size="large"
                class="header_button" :class="{ 'selected': selectedFunction === '4' }"
                @click="() => handleHeaderButtonClick('4')"
              >市场信息</el-button>
            </div>
          </el-col>
        </el-row>
      </el-header>
      <el-main class="main-content">
        <FarmerInfo v-if="selectedFunction === '1'" />
        <FarmLandInfo v-if="selectedFunction === '2'"/>
        <CropInfo v-if="selectedFunction === '3'"/>
        <div v-if="selectedFunction === '4'">市场信息内容区域</div>
      </el-main>
      <el-footer class="footer">© 2024 林下经济数据管理系统</el-footer>
    </el-container>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import FarmerInfo from '@/views/dataManagementLayout/farmerInfo.vue';
import FarmLandInfo from '@/views/dataManagementLayout/farmLandInfo.vue';
import CropInfo from '@/views/dataManagementLayout/cropInfo.vue';


const selectedFunction = ref('1');

const handleHeaderButtonClick = (index) => {
  if (selectedFunction.value !== index) {
    selectedFunction.value = index;
  }
};
</script>

<style scoped>
.common-layout {
  background-color: #FDFBF7;
}
.title {
  color: #556B2F;
  font-size: large;
  margin-bottom: 20px;
}
.header_row_box {
  display: flex;
  justify-content: space-between;
}
.header_button_box {
  flex: 1;
  margin: 0 5px;
}
.data_management_content {
  padding: 20px;
  background-color: #ffffff;
  border-radius: 10px;

}

.header_content {
  height: 140px;
  background-color: #FDFBF7;
  border-radius: 10px;
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  padding: 20px;

}

.header_button {
  background-color: #FDFBF7;
  color: #556B2F;
  border-radius: 8px;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header_button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.header_button.selected {
  background-color: #556B2F !important;
  color: white !important;
  transform: translateY(-2px);
}

.main-content {
  padding: 0px;
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.footer {
  text-align: center;
  color: #2B3F2B;
}
</style>
