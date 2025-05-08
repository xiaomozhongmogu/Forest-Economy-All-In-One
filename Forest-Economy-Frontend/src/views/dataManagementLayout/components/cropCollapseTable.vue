<template>
  <el-table
    :data="cropList"
    style="width: 100%;"
  >
    <el-table-column type="expand"  >
      <template #default="props">
        <div m="4">
          <h3 style="font-size: large; color: #556B2F; " >农田信息</h3>
          <el-table
            :data="props.row.farmland"
            :border="true"
             style="margin: 0 auto; width: 80%; border: 5px solid #E8E0D0">
            <el-table-column label="农田名称" prop="farmername" align="center" />
            <el-table-column label="联系电话" prop="phonenum" align="center"/>
            <el-table-column label="数量" prop="number" align="center"/>
          </el-table>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="作物名称" prop="cropName" align="center" />
    <el-table-column label="别名" prop="alias" align="center" />
    <el-table-column label="总数" prop="totalNumber" align="center" />
  </el-table>
</template>

<script setup>

// 定义组件的props
const props = defineProps({
  cropList:{
    type : Object,
    requited : true
  }
})
console.log(props.cropList)

</script>

<style scoped>
/* 可以根据需要添加样式 */
</style>
