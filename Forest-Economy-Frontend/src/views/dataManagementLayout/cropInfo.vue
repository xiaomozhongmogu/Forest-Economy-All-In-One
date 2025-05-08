<template>
  <div class="crop-info-container">
    <el-card class="crop-info-card" shadow="never">
      <template #header>
        <div class="crop-info-title" style="font-size: large;">作物信息查询</div>
      </template>
      <el-form :model="queryForm" :inline="true" class="crop-info-query-form">
        <el-form-item label="作物名称">
          <el-input v-model="queryForm.cropName" placeholder="请输入作物名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button color="#556B2F" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <crop-collapse-table :cropList="cropList"/>

    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CropCollapseTable from './components/cropCollapseTable.vue'

// 查询表单数据
const queryForm = ref({
  cropName: ''
})

// 作物列表数据
const cropList = ref([])

// 当前页码
//const currentPage = ref(1)

// 每页显示数量
//const pageSize = ref(10)

// 数据总数
//const total = ref(0)

// 查询方法
const handleQuery = () => {
  // 模拟查询逻辑
  console.log('执行查询操作', queryForm.value)

  // 假设查询成功后，更新 cropList 和 total
  cropList.value = [
    { cropName: '小麦',
      alias: '麦子',
      totalNumber: 1000,
      farmland: [
        { farmername: '小麦田1', phonenum: '123123123' ,number: 500  },
        { farmername: '小麦田2', phonenum: '123123123',number: 500  }
      ]
    },
    { cropName: '玉米',
      alias: '玉米',
      totalNumber: 1000,
      farmland: [
        { farmername: '小麦田1', phonenum: '123123123',number: 500  },
        { farmername: '小麦田2', phonenum: '123123123',number: 500  }
      ]
   }
  ]
}


// 重置方法
const handleReset = () => {
  queryForm.value = {
    cropName: ''
  }
}

// 分页切换方法
// const handleCurrentChange = (val) => {
//   currentPage.value = val
//   handleQuery()
// }

</script>

<style scoped>
.crop-info-container {
  background-color: #FDFBF7;
  padding: 0px;
}

.crop-info-card {
  background-color: #ffffff;
  border-radius: 10px;
  border: #E8E0D0 solid 1px;
  box-shadow: 4px 4px 8px rgba(0, 0, 0, 0.1), 4px 4px 12px rgba(0, 0, 0, 0.08);
}

.crop-info-title {
  font-weight: 600;
  font-size: 14px;
  color: #556B2F;
}

.crop-info-query-form {
  margin-bottom: 20px;
}

.crop-info-pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
