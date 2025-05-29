<template>
  <el-dialog
    v-model="dialogVisible"
    :center="true"
    width="500px"
    @close="handleClose"
  >
    <template #header>
      <div class="custom-dialog-header">
        <h2 class="custom-dialog-title">{{ dialogTitle }}</h2>
      </div>
    </template>
    <el-form
      :model="formData"
      :rules="formRules"
      ref="formRef"
      label-width="100px"
    >
      <el-form-item label="地块编号" prop="id" v-if="operationType === 'new'">
        <el-input v-model.number="formData.id" placeholder="请输入地块编号"></el-input>
        <span class="form-item-tip">（新建时必填，且必须是数字）</span>
      </el-form-item>
      <el-form-item label="地块名称" prop="plotName" v-if="operationType === 'new'">
        <el-input v-model="formData.plotName" placeholder="请输入地块名称"></el-input>
      </el-form-item>
      <el-form-item label="地块负责人" prop="username">
        <el-input v-model="formData.username" placeholder="请输入地块负责人"></el-input>
      </el-form-item>
      <el-form-item label="种植作物" prop="cropName" v-if="operationType === 'edit'">
        <el-select
          v-model="formData.cropName"
          filterable
          remote
          reserve-keyword
          placeholder="请输入作物名称"
          :remote-method="searchCrops"
          :loading="cropSearchLoading"
          clearable
          style="width: 100%;"
        >
          <el-option
            v-for="item in cropOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="坐标" prop="coordinate">
        <el-input v-model="formData.coordinate" placeholder="请输入地块坐标，JSON格式">
          <template #append>
            <el-tooltip content="从地图选点" placement="top">
              <el-button :icon="MapLocation" circle />
            </el-tooltip>
          </template>
        </el-input>
        <span class="form-item-tip">（例如：`{"type": "Polygon", "coordinates": [...]}`）</span>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="closeDialog">取消</el-button>
        <el-button class="custom-green-button" @click="handleSubmit">确定</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import { MapLocation } from '@element-plus/icons-vue';
import { fetchWorkspaceCropListApi,addPlotApi,
         editPlotApi } from '@/api/plot'; // 确保路径正确

// 定义组件的 props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  initialData: {
    type: Object,
    default: () => ({}),
  },
  operationType: {
    type: String, // 'new' or 'edit'
    required: true,
  },
});

// 定义组件的 emits
const emit = defineEmits(['update:visible', 'submitSuccess']);

// --- 响应式数据 ---
const dialogVisible = ref(props.visible);
const formData = ref({}); // 用于表单的数据
const formRef = ref(null); // 表单引用

const cropOptions = ref([]); // 作物选择列表
const cropSearchLoading = ref(false); // 作物搜索加载状态

const dialogTitle = ref(''); // 弹窗标题

// 表单校验规则
const formRules = reactive({
  id: [
    { required: true, message: '请输入地块编号', trigger: 'blur' },
    { type: 'number', message: '地块编号必须是数字', trigger: 'blur' },
  ],
  username: [
    { required: true, message: '请输入地块负责人', trigger: 'blur' },
    { min: 2, max: 20, message: '负责人姓名长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  plotName: [
    { required: true, message: '请输入地块名称', trigger: 'blur' },
    { min: 2, max: 50, message: '地块名称长度在 2 到 50 个字符', trigger: 'blur' },
  ],
  coordinate: [
    { required: true, message: '请输入地块坐标', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        try {
          const parsed = JSON.parse(value);
          if (parsed && parsed.type === 'Polygon' && Array.isArray(parsed.coordinates)) {
            callback();
          } else {
            callback(new Error('坐标格式不正确，请使用 GeoJSON Polygon 格式'));
          }
        } catch (e) {
          callback(new Error('坐标不是有效的 JSON 格式', e));
        }
      },
      trigger: 'blur',
    },
  ],
  cropName: [
    { required: true, message: '请选择种植作物', trigger: 'change' },
  ],
});

// 监听 props.visible 变化来控制弹窗显示
watch(
  () => props.visible,
  (newVal) => {
    dialogVisible.value = newVal;
    if (newVal) {
      // 弹窗打开时，根据 operationType 初始化表单数据和标题
      if (props.operationType === 'edit') {
        formData.value = { ...props.initialData };
        dialogTitle.value = '编辑地块';
        // 编辑模式下，预加载作物列表
        if (formData.value.cropName) {
          searchCrops(formData.value.cropName);
        } else {
          searchCrops('');
        }
      } else {
        formData.value = {
          id: null,
          coordinate: '',
          username: '',
          plotName: '',
          cropName: '',
          // 确保在新建时，id 和 plotName 字段能被正确识别
        };
        dialogTitle.value = '新建地块';
        searchCrops(''); // 新建时也加载一些默认作物
      }
      // 在 DOM 更新后重置表单验证状态
      nextTick(() => {
        formRef.value?.resetFields();
        // 如果是编辑模式，不需要校验新建时的 id 和 plotName 字段
        if (props.operationType === 'edit') {
          formRef.value?.clearValidate(['id', 'plotName']);
        } else {
            // 新建模式，清除编辑时的 cropName 验证
            formRef.value?.clearValidate('cropName');
        }
      });
    }
  }
);

// 监听 dialogVisible 变化，同步回父组件
watch(dialogVisible, (newVal) => {
  emit('update:visible', newVal);
});

// --- 方法 ---

/**
 * 关闭弹窗
 */
const closeDialog = () => {
  dialogVisible.value = false;
};

/**
 * 弹窗关闭时的回调，清空表单并重置校验
 */
const handleClose = () => {
    formRef.value?.resetFields(); // 确保弹窗关闭时清除所有表单数据和校验状态
    formData.value = {}; // 清空数据
};

/**
 * 远程搜索作物方法
 * @param {string} query - 用户输入的查询字符串
 */
const searchCrops = async (query) => {
  cropSearchLoading.value = true;
  try {
    const res = await fetchWorkspaceCropListApi({ cropName: query });
    const cropData = res.list && Array.isArray(res.list) ? res.list : [];
    cropOptions.value = cropData.map(crop => ({
      label: crop.name,
      value: crop.name
    }));
  } catch (error) {
    console.error('搜索作物失败:', error);
    ElMessage.error('获取作物列表失败！');
    cropOptions.value = [];
  } finally {
    cropSearchLoading.value = false;
  }
};

/**
 * 提交新建地块信息
 */
const submitAddPlot = async () => {
  const { id, coordinate, username, plotName } = formData.value;
  // 注意这里的字段名映射，后端可能需要 geometry
  const res = await addPlotApi({
    id,
    geometry: coordinate,
    username,
    plotName,
  });
  if (res.code === 1) { // 假设接口返回 code 为 1 表示成功
    ElMessage.success('地块添加成功！');
    return true;
  } else {
    ElMessage.error(res.message || '地块添加失败！');
    return false;
  }
};

/**
 * 提交编辑地块信息
 */
const submitEditPlot = async () => {
  const { id, coordinate, username, cropName, plotName } = formData.value;
  const res = await editPlotApi({
    id,
    username,
    geometry: coordinate,
    cropName,
    plotName, // 编辑时也传递 plotName
  });
  if (res.code === 1) {
    ElMessage.success('地块更新成功！');
    return true;
  } else {
    ElMessage.error(res.message || '地块更新失败！');
    return false;
  }
};

/**
 * 处理弹窗表单提交
 */
const handleSubmit = () => {
  let fieldsToValidate = ['username', 'coordinate'];
  if (props.operationType === 'new') {
    fieldsToValidate = ['id', 'plotName', ...fieldsToValidate];
  } else if (props.operationType === 'edit') {
    fieldsToValidate = ['cropName', ...fieldsToValidate];
  }

  formRef.value.validateField(fieldsToValidate, async (isValid) => {
    if (isValid) {
      try {
        let success = false;
        if (props.operationType === 'new') {
          success = await submitAddPlot();
        } else if (props.operationType === 'edit') {
          success = await submitEditPlot();
        }
        if (success) {
          closeDialog();
          emit('submitSuccess'); // 通知父组件刷新列表
        }
      } catch (error) {
        console.error('提交操作失败:', error);
        ElMessage.error(error.message || `操作失败，请稍后再试。`);
      }
    } else {
      ElMessage.warning('请检查表单填写是否正确！');
    }
  });
};
</script>

<style scoped>
/* --- CSS 变量定义 (保持与主组件一致) --- */
:root {
  --primary-green: #556B2F;
  --light-green: #6C8145;
  --border-color: #E8E0D0;
  --background-color-card: #ffffff;
  --text-color-tip: #999;
}

/* --- 自定义按钮样式 (与主组件保持一致) --- */
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

/* --- 弹窗头部样式 (保持与主组件一致) --- */
:deep(.el-dialog__header) {
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 15px;
  text-align: left;
}

.custom-dialog-header {
  text-align: left;
  padding-bottom: 0;
}

.custom-dialog-title {
  font-weight: 600;
  font-size: large;
  color: var(--primary-green);
  margin: 0;
  display: block;
}

/* --- 表单项提示文本样式 --- */
.form-item-tip {
  font-size: small;
  color: var(--text-color-tip);
  margin-top: 5px;
  display: block;
}

.dialog-footer {
  padding: 0; /* 移除默认 padding，确保 Element Plus 默认样式或自定义样式生效 */
}
</style>
