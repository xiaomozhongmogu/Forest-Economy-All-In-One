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
          <el-button class="custom-green-button" @click="openDialog('add')">新增农户</el-button>
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
            <el-button size="small" class="custom-green-button" @click="openDialog('edit', scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="confirmDelete(scope.row)">删除</el-button>
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
import { ref, onMounted, reactive, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { fetchFamersApi, editFamerApi } from '@/api/farmer';
import { registerApi } from '@/api/register';

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

// 表单验证规则
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
    // 只有在编辑模式下才需要验证地址
    { required: true, message: '请输入地址', trigger: 'blur' },
  ],
});

// --- 方法 ---

/**
 * 获取农户数据
 */
const fetchFarmerData = async () => {
  loading.value = true;
  try {
    const params = {
      currentPage: pagination.currentPage,
      pageSize: pagination.pageSize,
      name: queryForm.value.name,
      phone: queryForm.value.phone,
    };
    const res = await fetchFamersApi(params);
    farmerList.value = res.list;
    pagination.total = res.total;
  } catch (error) {
    console.error('获取农户数据失败:', error);
    ElMessage.error(error.message || '获取数据失败！');
  } finally {
    loading.value = false;
  }
};

/**
 * 处理查询操作
 */
const handleQuery = () => {
  pagination.currentPage = 1;
  fetchFarmerData();
};

/**
 * 处理重置操作
 */
const handleReset = () => {
  queryForm.value = { name: '', phone: '' };
  pagination.currentPage = 1;
  fetchFarmerData();
};

/**
 * 处理分页每页显示数量变化
 * @param {number} val - 每页显示数量
 */
const handleSizeChange = (val) => {
  pagination.pageSize = val;
  pagination.currentPage = 1;
  fetchFarmerData();
};

/**
 * 处理分页当前页码变化
 * @param {number} val - 当前页码
 */
const handleCurrentChange = (val) => {
  pagination.currentPage = val;
  fetchFarmerData();
};

/**
 * 打开新增/编辑农户弹窗
 * @param {string} mode - 'add' 或 'edit'
 * @param {object} row - 编辑时传入的农户数据
 */
const openDialog = (mode, row = {}) => {
  dialogVisible.value = true;
  isEditMode.value = mode === 'edit';
  dialogTitle.value = isEditMode.value ? '编辑农户信息' : '新建农户';

  if (isEditMode.value) {
    // 编辑模式，复制当前行数据
    currentFarmerForm.value = { ...row };
  } else {
    // 新增模式，初始化表单数据
    currentFarmerForm.value = {
      username: '',
      phoneNumber: '',
      residenceAddress: '',
      password: '000000'
    };
  }

  // 在 DOM 更新后重置表单验证状态
  if (farmerFormRef.value) {
    nextTick(() => {
      farmerFormRef.value.resetFields();
    });
  }
};

/**
 * 提交新增农户信息
 */
const addFarmer = async () => {
  const res = await registerApi({
    username: currentFarmerForm.value.username,
    phoneNumber: currentFarmerForm.value.phoneNumber,
    password: currentFarmerForm.value.password, // 使用表单中的密码
  });
  if (res.code === 1) {
    ElMessage.success('农户信息新建成功！');
  } else {
    ElMessage.error(res.message || '新建农户信息失败！');
  }
};

/**
 * 提交编辑农户信息
 */
const editFarmer = async () => {
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
};

/**
 * 处理弹窗表单提交
 */
const handleSubmit = () => {
  if (!isEditMode.value) {
    farmerFormRef.value.clearValidate('residenceAddress'); // 新增时清除地址的验证
  }

  farmerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEditMode.value) {
          await editFarmer();
        } else {
          await addFarmer();
        }
        dialogVisible.value = false;
        fetchFarmerData(); 
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

/**
 * 确认删除农户信息
 * @param {object} row - 待删除的农户数据
 */
const confirmDelete = async (row) => {
  ElMessageBox.confirm(
    `确定要删除农户 "${row.username}" 吗？`,
    '提示',
    { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
  )
    .then(async () => {
      try {
        // **这里需要调用您实际的删除接口，并传入农户ID**
        // const res = await deleteFarmerApi(row.id);
        // if (res.code === 1) {
        //   ElMessage.success('删除农户成功！');
        // } else {
        //   ElMessage.error(res.message || '删除农户失败！');
        // }
        ElMessage.success('删除农户成功！(待接入实际删除接口)');
        console.log('待发送删除请求，ID:', row.id);
        fetchFarmerData(); // 刷新列表
      } catch (error) {
        console.error('删除农户失败:', error);
        ElMessage.error(error.message || '删除失败，请重试！');
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除');
    });
};

// --- 生命周期钩子 ---
onMounted(() => {
  fetchFarmerData();
});
</script>

<style scoped>
/* 使用 CSS 变量管理颜色，提高可维护性 */
:root {
  --primary-green: #556B2F;
  --light-green: #6C8145;
  --border-color: #E8E0D0;
  --background-color-page: #FDFBF7;
  --background-color-card: #ffffff;
}

.farmer-info-container {
  background-color: var(--background-color-page);
  padding: 0px;
}

.farmer-info-card {
  background-color: var(--background-color-card);
  border-radius: 10px;
  border: 1px solid var(--border-color);
  box-shadow: 4px 4px 8px rgba(0, 0, 0, 0.1), 4px 4px 12px rgba(0, 0, 0, 0.08);
}

.farmer-info-card .farmer-info-title {
  font-weight: 600;
  font-size: large;
  color: var(--primary-green);
  text-align: left;
  margin: 0;
}

.farmer-info-query-form {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px dashed var(--border-color);
}

.custom-green-button {
  background-color: var(--primary-green) !important;
  color: white !important;
  border-color: var(--primary-green) !important;
  transition: all 0.3s;
}

.custom-green-button:hover,
.custom-green-button:focus {
  background-color: var(--light-green) !important;
  border-color: var(--light-green) !important;
  color: white !important;
}

.custom-table {
  border-radius: 8px;
  overflow: hidden;
  border: 2px solid var(--border-color);
}

.farmer-info-pagination {
  margin-top: 20px;
  text-align: right;
  padding: 10px 0;
  background-color: var(--background-color-card);
  border-radius: 0 0 10px 10px;
}

.dialog-footer {
  text-align: right;
}

/* 针对 Element Plus 弹窗头部样式进行优化 */
/* 使用深度选择器来修改 El-dialog 的内部样式 */
:deep(.el-dialog__header) {
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 15px;
  text-align: left; /* 保持你的自定义标题居左 */
}

.custom-dialog-header {
  /* 确保标题居中 */
  text-align: left; /* 保持你的自定义标题居左 */
  padding-bottom: 0; /* 这里的 padding 已经在 :deep(.el-dialog__header) 中设置 */
}

.custom-dialog-title {
  font-weight: 600;
  font-size: large;
  color: var(--primary-green);
  margin: 0;
  display: block;
}
</style>
