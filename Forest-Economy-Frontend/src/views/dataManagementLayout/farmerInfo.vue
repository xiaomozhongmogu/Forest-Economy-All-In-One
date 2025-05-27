<template>
  <div class="farmer-info-container">
    <el-card class="farmer-info-card" shadow="never">
      <template #header>
        <h2 class="farmer-info-title">农户信息查询</h2>
      </template>
      <el-form :model="queryForm" :inline="true" class="farmer-info-query-form">
        <el-form-item label="农户姓名">
          <el-input v-model="queryForm.name" placeholder="请输入农户姓名" clearable></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="queryForm.phone" placeholder="请输入联系电话" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button class="custom-green-button" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button class="custom-green-button" @click="handleOpenDialog('add')">新增农户</el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="farmerList"
        style="width: 100%"
        border
        class="custom-table"
      >
        <el-table-column prop="username" label="姓名" align="center"></el-table-column>
        <el-table-column prop="phoneNumber" label="电话" align="center"></el-table-column>
        <el-table-column prop="residenceAddress" label="地址" align="center"></el-table-column>
        <el-table-column prop="cropName" label="种植作物" align="center"></el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button size="small" class="custom-green-button" @click="handleOpenDialog('edit', scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        class="farmer-info-pagination"
      ></el-pagination>
    </el-card>

    <el-dialog v-model="dialogVisible" :header="dialogTitle" width="500px" center>
      <template #header>
          <div class="custom-dialog-header">
            <h2 class="custom-dialog-title">{{ dialogTitle }}</h2>
         </div>
      </template>
      <el-form
        :model="currentFarmerForm"
        :rules="formRules"
        ref="farmerFormRef"
        label-width="100px"
      >
        <el-form-item label="农户姓名" prop="username">
          <el-input v-model="currentFarmerForm.username"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phoneNumber">
          <el-input v-model="currentFarmerForm.phoneNumber"></el-input>
        </el-form-item>
        <el-form-item v-if="isEditMode" label="地址" prop="residenceAddress">
          <el-input v-model="currentFarmerForm.residenceAddress"></el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button class="custom-green-button" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { fetchFamersApi ,editFamerApi/* , addFarmer, updateFarmer, deleteFarmer */ } from '@/api/farmer';
import {  registerApi } from '@/api/register';
// --- 响应式数据 ---
const queryForm = ref({
  name: '',
  phone: ''
});

const farmerList = ref([]);
const loading = ref(false);

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});

const dialogVisible = ref(false);
const dialogTitle = ref('新建农户');
const currentFarmerForm = ref({}); // 用于新增/编辑的表单数据
const isEditMode = ref(false);

const farmerFormRef = ref(null);

// 表单验证规则 (字段名与接口文档一致)
const formRules = reactive({
  username: [
    { required: true, message: '请输入农户姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  phoneNumber: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' },
  ],
  residenceAddress: [
    { required: true, message: '请输入地址', trigger: 'blur' },
  ],
});

// --- 方法 ---
const fetchFarmerData = async () => {
  //显示查询中
  loading.value = true;
  try {
    const params = {
      currentPage: pagination.currentPage,
      pageSize: pagination.pageSize,
      name: queryForm.value.name, // 映射到接口的 username
      phone: queryForm.value.phone, // 映射到接口的 phoneNumber
    };
    const res = await fetchFamersApi(params); // 调用接口服务
    farmerList.value = res.list;
    pagination.total = res.total;
  } catch (error) {
    console.error('获取农户数据失败:', error);
    // 假设 request 封装已处理 ElMessage.error，这里不再重复
    // 如果需要更细致的错误提示，可以 ElMessage.error(error.message || '获取数据失败！');
  } finally {
    loading.value = false;
  }
};

const handleQuery = () => {
  pagination.currentPage = 1;
  fetchFarmerData();
};

const handleReset = () => {
  queryForm.value = { name: '', phone: '' };
  pagination.currentPage = 1;
  fetchFarmerData();
};

const handleSizeChange = (val) => {
  pagination.pageSize = val;
  pagination.currentPage = 1;
  fetchFarmerData();
};

const handleCurrentChange = (val) => {
  pagination.currentPage = val;
  fetchFarmerData();
};



const handleOpenDialog = (mode, row = {}) => {
  dialogVisible.value = true;
  if (mode === 'add') {
    dialogTitle.value = '新建农户';
    currentFarmerForm.value = { username: '', phoneNumber: '', residenceAddress: '', cropName: '', password: 'initialPassword' }; // 新增时可能需要默认密码
    isEditMode.value = false;
    if (farmerFormRef.value) {
      farmerFormRef.value.resetFields();
    }
  } else if (mode === 'edit') {
    dialogTitle.value = '编辑农户信息';
    currentFarmerForm.value = { ...row };
    isEditMode.value = true;
  }
  if (farmerFormRef.value) {
    farmerFormRef.value.resetFields();
  }
};


// 编辑农户信息提交
const handleSubmit = () => {
  farmerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEditMode.value) {
          const res = await editFamerApi({
            id: currentFarmerForm.value.id,
            username: currentFarmerForm.value.username,
            phoneNumber: currentFarmerForm.value.phoneNumber,
            residenceAddress: currentFarmerForm.value.residenceAddress
          });
          if (res.code === 1) {
            ElMessage.success('农户信息更新成功！');
          } else {
            ElMessage.error(res.message || '更新农户信息失败！');
          }
        } else {// 新增农户信息提交
          const res = await registerApi({
            username: currentFarmerForm.value.username,
            phoneNumber: currentFarmerForm.value.phoneNumber,
            password: "00000000"
          });
          if (res.code === 1) {
            ElMessage.success('农户信息新建成功！');
          } else {
            ElMessage.error(res.message || '新建农户信息失败！');
          }
        }
        dialogVisible.value = false; // 关闭弹窗
        fetchFarmerData(); // 重新加载数据以刷新列表
      } catch (error) {
        console.error('提交农户信息失败:', error);
        ElMessage.error(error.message || '操作失败，请重试！');
      }
    } else {
      ElMessage.warning('请检查表单填写是否正确！');
      return false;
    }
  });
};

const handleDelete = async (row) => {
  ElMessageBox.confirm(
    `确定要删除农户 "${row.username}" 吗？`, // 使用接口字段名 username
    '提示',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  )
    .then(async () => {
      try {
        // **这里需要调用您实际的删除接口，并传入农户ID**
        // 例如：const res = await deleteFarmer(row.id);
        ElMessage.success('删除农户成功！(待接入实际删除接口)');
        console.log('待发送删除请求，ID:', row.id);
        fetchFarmerData();
      } catch (error) {
        console.error('删除农户失败:', error);
        // request 封装通常会统一处理错误消息
        // 如果需要更细致的错误提示，可以 ElMessage.error(error.message || '删除失败，请重试！');
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除');
    });
};

onMounted(() => {
  fetchFarmerData();
});
</script>

<style scoped>
/* 所有颜色都已硬编码，以确保它们按照您的要求生效 */
.farmer-info-container {
  background-color: #FDFBF7;
  padding: 0px;
}

.farmer-info-card {
  background-color: #ffffff;
  border-radius: 10px;
  border: 1px solid #E8E0D0;
  box-shadow: 4px 4px 8px rgba(0, 0, 0, 0.1), 4px 4px 12px rgba(0, 0, 0, 0.08);
}

.farmer-info-card .farmer-info-title {
  font-weight: 600;
  font-size: large;
  color: #556B2F;
  text-align: left;
  margin: 0;
}

.farmer-info-query-form {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #E8E0D0;
}

.custom-green-button {
  background-color: #556B2F !important;
  color: white !important;
  border-color: #556B2F !important;
  transition: all 0.3s;
}

.custom-green-button:hover,
.custom-green-button:focus {
  background-color: #6C8145 !important;
  border-color: #6C8145 !important;
  color: white !important;
}

.custom-table {
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid #E8E0D0;
}

.farmer-info-pagination {
  margin-top: 20px;
  text-align: right;
  padding: 10px 0;
  background-color: #ffffff;
  border-radius: 0 0 10px 10px;
}

.dialog-footer {
  text-align: right;
}

.el-dialog__header .el-dialog__title {
  color: #556B2F;
  font-size: large;
  text-align: center;
  width: 100%;
  display: block;
}

.el-dialog__header {
  border-bottom: 1px solid #E8E0D0;
  padding-bottom: 15px;
}
.custom-dialog-header {
  /* 确保标题居中 */
  text-align: left;
  padding-bottom: 15px; /* 与你原有的 el-dialog__header padding 保持一致 */
}

.custom-dialog-title {
  font-weight: 600;
  font-size: large;
  color: #556B2F; /* **在这里设置你想要的颜色，例如橙红色** */
  margin: 0; /* 移除默认的 h2 外边距 */
  display: block; /* 确保标题是块级元素 */
}
</style>
