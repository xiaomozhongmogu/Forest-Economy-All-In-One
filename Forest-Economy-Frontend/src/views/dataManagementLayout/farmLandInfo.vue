<template>
  <div class="farm-land-info-container">
    <el-card class="farm-land-info-card" shadow="never">
      <template #header>
        <div class="farm-land-info-title">地块信息查询</div>
      </template>
      <el-form :model="queryForm" :inline="true" class="farm-land-info-query-form">
        <el-form-item label="地块编号">
          <el-input v-model="queryForm.plotNumber" placeholder="请输入地块编号"></el-input>
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
    <el-dialog v-model="dialogVisible" center="true">
      <template #title>
        <div style="color: #556B2F; text-align: center; font-size: large;">新建地块</div>
      </template>
      <el-form :model="newPlotForm" label-width="100px">
        <el-form-item label="地块编号">
          <el-input v-model="newPlotForm.plotNumber"></el-input>
        </el-form-item>
        <el-form-item label="地块负责人">
          <el-input v-model="newPlotForm.responsiblePerson"></el-input>
        </el-form-item>
        <el-form-item label="种植作物">
          <el-input v-model="newPlotForm.crops"></el-input>
        </el-form-item>
        <el-form-item label="坐标">
          <el-input v-model="newPlotForm.coordinates">
            <template #append>
              <el-button type="primary">
                <span>从地图选点</span>
              </el-button>
            </template>
          </el-input>
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
import { ref } from 'vue'

// 查询表单数据
const queryForm = ref({
  plotNumber: '',
  responsiblePerson: ''
})

// 地块列表数据
const farmLandList = ref([])

// 当前页码
const currentPage = ref(1)

// 每页显示数量
const pageSize = ref(10)

// 数据总数
const total = ref(0)

// 查询方法
const handleQuery = () => {
  // 模拟查询逻辑
  console.log('执行查询操作', queryForm.value)

  // 假设查询成功后，更新 farmLandList 和 total
  farmLandList.value = [
    { plotNumber: '001', responsiblePerson: '张三', crops: '小麦, 玉米', coordinates: '116.4074, 39.9042' },
    { plotNumber: '002', responsiblePerson: '李四', crops: '水稻, 大豆', coordinates: '121.4737, 31.2304' }
  ]
}

// 重置方法
const handleReset = () => {
  queryForm.value = {
    plotNumber: '',
    responsiblePerson: ''
  }
}

// 分页切换方法
const handleCurrentChange = (val) => {
  currentPage.value = val
  handleQuery()
}

// 编辑方法
const handleEdit = (row) => {
  console.log('编辑操作', row)
}

// 删除方法
const handleDelete = (row) => {
  console.log('删除操作', row)
}

// 新建地块表单数据
const newPlotForm = ref({ plotNumber: '', responsiblePerson: '', crops: '', coordinates: '' })
// 弹窗显示状态
const dialogVisible = ref(false)
// 新建方法
const handleNewPlot = () => {
  dialogVisible.value = true
  newPlotForm.value = { plotNumber: '', responsiblePerson: '', crops: '', coordinates: '' }
}
// 提交方法
const handleSubmit = () => {
  console.log('提交新建地块信息', newPlotForm.value)
  dialogVisible.value = false
  // 这里可以添加实际的提交逻辑
  handleQuery()
}

// 进入地图界面方法
const goToMap = (row) => {
  console.log('进入地图界面，查看地块坐标', row.coordinates)
  // 这里可以添加实际的路由跳转逻辑
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
