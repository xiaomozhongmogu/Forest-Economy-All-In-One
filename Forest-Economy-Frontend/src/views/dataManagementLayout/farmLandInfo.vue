<template>
  <div class="farm-land-info-container">
    <el-card class="farm-land-info-card" shadow="never">
      <template #header>
        <div class="farm-land-info-title" style="font-size: large;">地块信息查询</div>
      </template>
      <el-form :model="queryForm" :inline="true" class="farm-land-info-query-form">
        <el-form-item label="种植作物">
          <el-input v-model="queryForm.plotNumber" placeholder="请输入查询的作物"></el-input>
        </el-form-item>
        <el-form-item label="地块负责人">
          <el-input v-model="queryForm.responsiblePerson" placeholder="请输入地块负责人"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button color="#556B2F" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="handleNewPlot">新建</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="farmLandList" style="width: 100%">
        <el-table-column prop="plotNumber" label="地块编号"></el-table-column>
        <el-table-column prop="responsiblePerson" label="地块负责人"></el-table-column>
        <el-table-column prop="crops" label="种植作物"></el-table-column>
        <el-table-column prop="coordinates" label="坐标"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
            <el-button size="small" @click="goToMap(scope.row)">查看地图</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        class="farm-land-info-pagination"
      ></el-pagination>
    </el-card>
    <el-dialog v-model="dialogVisible" :center="true">
      <template #header>
        <div style="color: #556B2F; text-align: center; font-size: large;">{{ dialogTitle }}</div>
      </template>
      <el-form :model="newPlotForm" label-width="100px">
        <el-form-item label="地块编号" v-if="operationType === 'new'">
          <el-input v-model.number="newPlotForm.id" placeholder="请输入地块编号"></el-input>
          <span style="font-size: small; color: #999;">（新建时必填，且必须是数字）</span>
        </el-form-item>
        <el-form-item v-if="operationType === 'new' || operationType === 'edit'" label="地块负责人">
          <el-input v-model="newPlotForm.username" placeholder="请输入地块负责人"></el-input>
        </el-form-item>
         <el-form-item v-if="operationType === 'new' || operationType === 'edit'" label="坐标">
          <el-input v-model="newPlotForm.coordinate" placeholder="请输入地块坐标，JSON格式">
            <template #append>
                <el-tooltip content="从地图选点" placement="top">
                  <el-button :icon="MapLocation" circle />
                </el-tooltip>
              </template>
          </el-input>
          <span style="font-size: small; color: #999; margin-top: 5px;">（例如：`{"type": "Polygon", "coordinates": [...]}`）</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button color="#556B2F" @click="dialogVisible = false">取消</el-button>
          <el-button color="#556B2F" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { MapLocation } from '@element-plus/icons-vue';
import {
  fetchPlotListApi, // 引入查询接口
  addPlotApi,       // 引入添加接口
  editPlotApi,      // 引入编辑接口
  deletePlotApi     // 引入删除接口
} from '@/api/plot' // 假设你的 api/plot.js 文件路径是 src/api/plot.js

// === 引入模拟的 WorkspaceCropListApi ===
import { fetchWorkspaceCropListApi } from '@/api/plot'; // 确保路径正确

// 查询表单数据
const queryForm = ref({
  plotNumber: '',      // 对应地块编号 (实际查询时可能对应后端某个字段，如id)
  responsiblePerson: '' // 对应地块负责人 (实际查询时可能对应后端 username)
})

// 地块列表数据
const farmLandList = ref([])

// 当前页码
const currentPage = ref(1)

// === 新增的响应式数据 ===
const cropOptions = ref([]); // 作物选择列表
const cropSearchLoading = ref(false); // 作物搜索加载状态

// 每页显示数量
const pageSize = ref(10)

// 数据总数
const total = ref(0)

// 新建/编辑地块表单数据
const newPlotForm = ref({
  id: null,        // 地块编码 (新建时必填，编辑时作为标识)
  coordinate: '',  // 地块坐标 (JSON 字符串)
  username: '',    // 农户姓名 (对应负责人)
  plotName: '',    // 地块名称 (仅用于新建)
  cropName: ''     // 作物名称 (仅用于编辑)
})

// 弹窗显示状态
const dialogVisible = ref(false)
// 弹窗标题 (用于区分新建和编辑)
const dialogTitle = ref('新建地块')
// 当前操作类型，'new' 或 'edit'
const operationType = ref('new')

// 组件挂载时，执行一次查询
onMounted(() => {
  handleQuery()
})

/**
 * 查询地块信息
 */
const handleQuery = async () => {
  console.log('执行查询操作', queryForm.value)
  try {
    const { list, total: totalCount } = await fetchPlotListApi({
      currentPage: currentPage.value,
      pageSize: pageSize.value,
      plotNumber: queryForm.value.plotNumber,
      responsiblePerson: queryForm.value.responsiblePerson
    })
    // 将后端返回的字段映射到前端表格需要的字段
    farmLandList.value = list.map(item => ({
      plotNumber: item.id, // 假设后端 id 对应前端地块编号
      responsiblePerson: item.username, // 假设后端 username 对应前端负责人
      crops: item.cropName || item.plotName, // 假设 cropName 或 plotName 对应种植作物
      coordinates: item.geometry, // *** 重点调整：后端返回的是 geometry，映射到 coordinates ***
      id: item.id // 存储原始 id 用于后续操作
    }))
    total.value = totalCount // 更新总数
  } catch (error) {
    ElMessage.error(error.message || '查询地块信息失败')
  }
}

// 重置方法
const handleReset = () => {
  queryForm.value = {
    plotNumber: '',
    responsiblePerson: ''
  }
  handleQuery() // 重置后重新查询
}

// 分页切换方法
const handleCurrentChange = (val) => {
  currentPage.value = val
  handleQuery()
}

/**
 * 远程搜索作物方法
 * @param {string} query 用户输入的查询字符串
 */
const searchCrops = async (query) => {
  cropSearchLoading.value = true; // 无论是否有查询字符串都显示加载状态

  try {
    // === 关键修改：调用模拟的 WorkspaceCropListApi ===
    const res = await fetchWorkspaceCropListApi({ cropName: query });

    // 检查 res.list 是否存在且为数组，如果不存在则设为 []
    const cropData = res.list && Array.isArray(res.list) ? res.list : [];

    cropOptions.value = cropData.map(crop => ({
      label: crop.name, // 显示作物名称
      value: crop.name  // 实际选中值也为作物名称
    }));
  } catch (error) {
    console.error('搜索作物失败:', error);
    ElMessage.error('获取作物列表失败！');
    cropOptions.value = []; // 搜索失败时清空选项
  } finally {
    cropSearchLoading.value = false; // 无论成功或失败都关闭加载状态
  }
};


/**
 * 编辑方法
 * @param {Object} row 当前行的数据
 */
const handleEdit = async (row) => {
  console.log('编辑操作', row)
  operationType.value = 'edit'
  dialogTitle.value = '编辑地块'
  // 将当前行的数据填充到表单
  newPlotForm.value = {
    id: row.id,
    username: row.responsiblePerson,
    coordinate: row.coordinates,
    cropName: row.crops, // 种植作物映射到作物名称
    plotName: ''
  }
  dialogVisible.value = true;

  // 在编辑模式下，当打开对话框时，预先加载一次作物列表
  // 如果 newPlotForm.value.cropName 有值，则搜索该作物以确保其在选项中
  if (newPlotForm.value.cropName) {
    await searchCrops(newPlotForm.value.cropName);
  } else {
    // 如果没有作物名称，可以加载所有作物列表（如果接口支持无参查询）
    await searchCrops(''); // 第一次打开时，可以加载一些默认作物
  }
};

/**
 * 删除方法
 * @param {Object} row 当前行的数据
 */
const handleDelete = async (row) => {
  console.log('删除操作', row)
  try {
    await ElMessageBox.confirm('确定要删除此地块信息吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    // 假设 deletePlotApi 直接接收 id
    await deletePlotApi(row.id)
    ElMessage.success('删除成功！')
    handleQuery() // 删除成功后刷新列表
  } catch (error) {
    if (error === 'cancel') {
      ElMessage.info('已取消删除。')
    } else {
      console.error('删除地块失败:', error)
      ElMessage.error(error.message || '删除地块失败，请稍后再试。')
    }
  }
}

// 新建方法
const handleNewPlot = () => {
  operationType.value = 'new'
  dialogTitle.value = '新建地块'
  // 清空表单数据
  newPlotForm.value = { id: null, coordinate: '', username: '', plotName: '', cropName: '' }
  dialogVisible.value = true
  // 新建时，可能也需要加载一些默认的作物选项
  searchCrops('');
}

/**
 * 提交方法 (新建或编辑)
 */
const handleSubmit = async () => {
  try {
    if (operationType.value === 'new') {
      console.log('提交新建地块信息', newPlotForm.value)
      // 检查必填项
      if (!newPlotForm.value.id || !newPlotForm.value.coordinate || !newPlotForm.value.username || !newPlotForm.value.plotName) {
        ElMessage.warning('请填写所有必填项！')
        return
      }
      // 检查 id 是否为数字
      if (typeof newPlotForm.value.id !== 'number' || isNaN(newPlotForm.value.id)) {
        ElMessage.warning('地块编号必须是数字！')
        return
      }

      await addPlotApi({
        id: newPlotForm.value.id,
        geometry: newPlotForm.value.coordinate, // 注意这里，如果后端需要 geometry 字段，而不是 coordinate
        username: newPlotForm.value.username,
        plotName: newPlotForm.value.plotName
      })
      ElMessage.success('地块添加成功！')

    } else if (operationType.value === 'edit') {
      console.log('提交编辑地块信息', newPlotForm.value)
      // 检查必填项 (id 必须有)
      if (!newPlotForm.value.id) {
        ElMessage.warning('地块编码缺失，无法编辑！')
        return
      }

      await editPlotApi({
        id: newPlotForm.value.id,
        username: newPlotForm.value.username,
        geometry: newPlotForm.value.coordinate, // 注意这里，如果后端需要 geometry 字段，而不是 coordinate
        cropName: newPlotForm.value.cropName
      })
      ElMessage.success('地块更新成功！')
    }
    dialogVisible.value = false
    handleQuery() // 刷新列表
  } catch (error) {
    console.error('提交操作失败:', error)
    ElMessage.error(error.message || `操作失败，请稍后再试。`)
  }
}

// 进入地图界面方法
const goToMap = (row) => {
  console.log('进入地图界面，查看地块坐标', row.coordinates)
  ElMessage.info(`正在跳转到地图查看地块 ${row.plotNumber} 的坐标。`)
  // 实际的路由跳转逻辑应在这里实现，例如使用 Vue Router:
  // import { useRouter } from 'vue-router';
  // const router = useRouter();
  // router.push({ name: 'MapView', query: { coordinates: row.coordinates } });
}
</script>

<style scoped>
.farm-land-info-container {
  background-color: #FDFBF7;
  padding: 0px;
}

.farm-land-info-card {
  background-color: #ffffff;
  border-radius: 10px;
  border: #E8E0D0 solid 1px;
  box-shadow: 4px 4px 8px rgba(0, 0, 0, 0.1), 4px 4px 12px rgba(0, 0, 0, 0.08);
}

.farm-land-info-title {
  font-weight: 600;
  font-size: 14px;
  color: #556B2F;
}

.farm-land-info-query-form {
  margin-bottom: 20px;
}

.farm-land-info-pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  padding: 40px;
}
</style>
