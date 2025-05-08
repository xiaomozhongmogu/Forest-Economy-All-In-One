<template>
  <div class="farmer-info-container">
    <el-card class="farmer-info-card" shadow="never">
      <template #header>
        <div class="farmer-info-title">农户信息查询</div>
      </template>
      <el-form :model="queryForm" :inline="true" class="farmer-info-query-form">
        <el-form-item label="农户姓名">
          <el-input v-model="queryForm.name" placeholder="请输入农户姓名"></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="queryForm.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button color="#556B2F" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button @click="handleNewFarmer">新建</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="farmerList" style="width: 100%">
        <el-table-column prop="name" label="姓名"></el-table-column>
        <el-table-column prop="phone" label="电话"></el-table-column>
        <el-table-column prop="address" label="地址"></el-table-column>
        <el-table-column prop="crop" label="种植作物"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        class="farmer-info-pagination"
      ></el-pagination>

    </el-card>
    <el-dialog v-model="dialogVisible" center="true">
      <template #title>
        <div style="color: #556B2F; text-align: center; font-size: large;">新建农户</div>
      </template>
      <el-form :model="newFarmerForm" label-width="100px">
        <el-form-item label="农户姓名">
          <el-input v-model="newFarmerForm.name"></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="newFarmerForm.phone"></el-input>
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
  name: '',
  phone: ''
})

// 农户列表数据
const farmerList = ref([])

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

  // 假设查询成功后，更新 farmerList 和 total
  farmerList.value = [
    { name: '张三', phone: '13812345678', address: '北京市朝阳区', crop: '小麦' },
    { name: '李四', phone: '13912345678', address: '上海市浦东新区', crop: '水稻' }
  ]
}

// 重置方法
const handleReset = () => {
  queryForm.value = {
    name: '',
    phone: ''
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

// 新建农户表单数据
const newFarmerForm = ref({ name: '', phone: '' })
// 弹窗显示状态
const dialogVisible = ref(false)
// 新建方法
const handleNewFarmer = () => {
  dialogVisible.value = true
  newFarmerForm.value = { name: '', phone: '' }
}
// 提交方法
const handleSubmit = () => {
  console.log('提交新建农户信息', newFarmerForm.value)
  dialogVisible.value = false
  // 这里可以添加实际的提交逻辑
  handleQuery()
}
</script>

<style scoped>
.farmer-info-container {
  background-color: #FDFBF7;
  padding: 0px;
}

.farmer-info-card {
  background-color: #ffffff;
  border-radius: 10px;
  border: #E8E0D0 solid 1px;
  box-shadow: 4px 4px 8px rgba(0, 0, 0, 0.1), 4px 4px 12px rgba(0, 0, 0, 0.08);
}

.farmer-info-title {
  font-weight: 600;
  font-size: 14px;
  color: #556B2F;
}

.farmer-info-query-form {
  margin-bottom: 20px;
}

.farmer-info-pagination {
  margin-top: 20px;
  text-align: right;
}
.sialog-header-class {
  background-color: #FDFBF7;
  color: #556B2F;
  font-size: large;
  border: #556B2F solid 1px;
}
.dialog-footer {
  padding: 40px;
}
</style>
