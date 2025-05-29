<template>
  <div class="farm-land-info-container">
    <el-card class="farm-land-info-card" shadow="never">
      <template #header>
        <h2 class="farm-land-info-title">地块信息查询</h2>
      </template>
      <el-form :model="queryForm" :inline="true" class="farm-land-info-query-form">
        <el-form-item label="种植作物">
          <el-input v-model="queryForm.plotNumber" placeholder="请输入查询的作物" clearable></el-input>
        </el-form-item>
        <el-form-item label="地块负责人">
          <el-input v-model="queryForm.responsiblePerson" placeholder="请输入地块负责人" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="custom-green-button" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button class="custom-green-button" @click="openPlotDialog('new')">新建</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="farmLandList" style="width: 100%" border class="custom-table" v-loading="loading">
        <el-table-column prop="name" label="地块名称" align="center"></el-table-column>
        <el-table-column prop="responsiblePerson" label="地块负责人" align="center"></el-table-column>
        <el-table-column prop="crops" label="种植作物" align="center"></el-table-column>
        <el-table-column prop="coordinates" label="坐标" align="center"></el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template #default="scope">
            <el-button size="small" class="custom-green-button" @click="openPlotDialog('edit', scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="confirmDelete(scope.row)">删除</el-button>
            <el-button size="small" @click="goToMap(scope.row)">查看地图</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next, jumper"
        class="farm-land-info-pagination"
      ></el-pagination>
    </el-card>

    <PlotFormDialog
      :visible="dialogVisible"
      :initial-data="currentPlotData"
      :operation-type="operationType"
      @update:visible="dialogVisible = $event"
      @submitSuccess="fetchFarmLandData"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  fetchPlotListApi,
  deletePlotApi,
} from '@/api/plot';

// 导入封装后的弹窗组件
import PlotFormDialog from './components/farmerLandDialog.vue'; // 确保路径正确

// --- 响应式数据 ---

/**
 * 查询表单数据
 * @type {ref<Object>}
 */
const queryForm = ref({
  plotNumber: '',
  responsiblePerson: ''
});

/**
 * 地块列表数据
 * @type {Ref<Array<Object>>}
 */
const farmLandList = ref([]);

/**
 * 加载状态
 * @type {Ref<boolean>}
 */
const loading = ref(false);

/**
 * 当前页码
 * @type {Ref<number>}
 */
const currentPage = ref(1);

/**
 * 每页显示数量
 * @type {Ref<number>}
 */
const pageSize = ref(10);

/**
 * 数据总数
 * @type {Ref<number>}
 */
const total = ref(0);

/**
 * 弹窗显示状态
 * @type {Ref<boolean>}
 */
const dialogVisible = ref(false);

/**
 * 当前操作类型：'new' (新建) 或 'edit' (编辑)
 * @type {Ref<string>}
 */
const operationType = ref('new'); // 默认新建

/**
 * 传给子组件的初始数据 (编辑时使用)
 * @type {Ref<Object>}
 */
const currentPlotData = ref({});

// --- 方法 ---

/**
 * 获取地块信息列表
 */
const fetchFarmLandData = async () => {
  loading.value = true;
  try {
    const { list, total: totalCount } = await fetchPlotListApi({
      currentPage: currentPage.value,
      pageSize: pageSize.value,
      plotNumber: queryForm.value.plotNumber,
      responsiblePerson: queryForm.value.responsiblePerson,
    });

    farmLandList.value = list.map(item => ({
      plotNumber: item.id,
      responsiblePerson: item.username,
      crops: item.cropName,
      name: item.plotName,
      coordinates: item.geometry,
      id: item.id, // 存储原始 id 用于后续操作
    }));
    total.value = totalCount;
  } catch (error) {
    console.error('获取地块数据失败:', error);
    ElMessage.error(error.message || '查询地块信息失败');
  } finally {
    loading.value = false;
  }
};

/**
 * 处理查询操作
 */
const handleQuery = () => {
  currentPage.value = 1;
  fetchFarmLandData();
};

/**
 * 处理重置操作，清空查询表单并重新查询
 */
const handleReset = () => {
  queryForm.value = {
    plotNumber: '',
    responsiblePerson: ''
  };
  handleQuery();
};

/**
 * 处理分页页码变化
 * @param {number} val - 新的页码
 */
const handleCurrentChange = (val) => {
  currentPage.value = val;
  fetchFarmLandData();
};

/**
 * 打开地块表单弹窗
 * @param {string} type - 'new' 或 'edit'
 * @param {Object} [data={}] - 编辑时传入的地块数据
 */
const openPlotDialog = (type, data = {}) => {
  operationType.value = type;
  currentPlotData.value = { ...data }; // 传递数据副本，避免直接修改props
  dialogVisible.value = true;
};

/**
 * 确认删除地块信息
 * @param {Object} row - 待删除的农户数据
 */
const confirmDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除地块 "${row.name}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    await deletePlotApi(row.id);
    ElMessage.success('删除成功！');
    fetchFarmLandData();
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消删除。');
    } else {
      console.error('删除地块失败:', error);
      ElMessage.error(error.message || '删除地块失败，请稍后再试。');
    }
  }
};

/**
 * 进入地图界面方法
 * @param {Object} row - 当前行的数据
 */
const goToMap = (row) => {
  console.log('进入地图界面，查看地块坐标', row.coordinates);
  ElMessage.info(`正在跳转到地图查看地块 ${row.plotNumber} 的坐标。`);
  // 实际的路由跳转逻辑应在这里实现，例如使用 Vue Router:
  // import { useRouter } from 'vue-router';
  // const router = useRouter();
  // router.push({ name: 'MapView', query: { coordinates: row.coordinates } });
};

// --- 生命周期钩子 ---
onMounted(() => {
  fetchFarmLandData();
});
</script>

<style scoped>
/* --- CSS 变量定义 (保持与前两个组件一致) --- */


/* --- 容器样式 --- */
.farm-land-info-container {
  background-color: var(--background-color-page);
  padding: 0;
}

.farm-land-info-card {
  background-color: var(--background-color-card);
  border-radius: 10px;
  border: 1px solid var(--border-color);
  box-shadow: 4px 4px 8px rgba(0, 0, 0, 0.1), 4px 4px 12px rgba(0, 0, 0, 0.08);
}

/* --- 标题样式 --- */
.farm-land-info-title {
  font-weight: 600;
  font-size: large;
  color: var(--primary-green);
  text-align: left;
  margin: 0;
  padding-bottom: 15px;
  border-bottom: 1px dashed var(--border-color);
}

/* --- 查询表单样式 --- */
.farm-land-info-query-form {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px dashed var(--border-color);
}

/* --- 自定义按钮样式 (与上一个组件保持一致) --- */
.custom-green-button {
  background-color: var(--primary-green) !important;
  color: white !important;
  border-color: var(--primary-green) !important;
  transition: all 0.3s ease;
}

.custom-green-button:hover,
.custom-green-button:focus {
  background-color: var(--light-green) !important;
  border-color: var(--light-green) !important;
  color: white !important;
}

/* --- 自定义表格样式 (与上一个组件保持一致) --- */
.custom-table {
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid var(--border-color);
}

/* --- 分页样式 --- */
.farm-land-info-pagination {
  margin-top: 20px;
  text-align: right;
  padding: 10px 0;
  background-color: var(--background-color-card);
  border-radius: 0 0 10px 10px;
}
</style>
