<template>
  <div class="market-info-container">
    <el-card class="market-info-card" shadow="never">
      <template #header>
        <div class="market-info-title" style="font-size: large;">林下经济作物市场价格</div>
      </template>

      <el-form :model="queryForm" :inline="true" class="market-info-query-form">
        <el-form-item label="作物名称">
          <el-input v-model="queryForm.cropName" placeholder="请输入作物名称"></el-input>
        </el-form-item>
        <el-form-item label="地区">
          <el-input v-model="queryForm.region" placeholder="请输入地区"></el-input>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button color="#556B2F" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
        :data="marketPriceList"
        style="width: 100%"
        v-loading="loading"
        ref="multipleTableRef"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="cropName" label="作物名称" width="150"></el-table-column>
        <el-table-column prop="region" label="地区" width="120"></el-table-column>
        <el-table-column prop="market" label="市场" width="150"></el-table-column>
        <el-table-column prop="price" label="价格(元/斤)" width="120"></el-table-column>
        <el-table-column prop="unit" label="单位" width="100"></el-table-column>
        <el-table-column prop="publishDate" label="发布日期" width="180"></el-table-column>
        <el-table-column prop="source" label="数据来源" min-width="150"></el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        class="market-info-pagination"
      ></el-pagination>

      <el-divider />

      <div class="chart-section">
        <h3 style="text-align: center; color: #556B2F;">价格走势图</h3>
        <p style="text-align: center; color: #888; font-size: small;">（请在上方表格中勾选作物以显示其价格走势）</p>
        <div ref="chartRef" class="market-chart"></div>
      </div>

    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick, onBeforeUnmount } from 'vue';
import { ElMessage } from 'element-plus';
import * as echarts from 'echarts'; // **引入 Echarts**

// 查询表单数据
const queryForm = ref({
  cropName: '',
  region: '',
  dateRange: [],
  selectedCrops: [] // **新增：用于存储勾选的作物对象 (行数据)**
});

// 市场价格列表数据
const marketPriceList = ref([]);

// 加载状态
const loading = ref(false);

// 当前页码
const currentPage = ref(1);

// 每页显示数量
const pageSize = ref(10);

// 数据总数
const total = ref(0);

// Echarts 图表实例引用
const chartRef = ref(null); // 图表容器的DOM引用
let myChart = null; // Echarts 实例

// ElTable 的引用，用于操作表格（如清空选择）
const multipleTableRef = ref(null);

// **模拟数据** (为演示图表，每个作物现在会包含多个历史价格点)
const mockMarketData = [
  { id: 1, cropName: '黑木耳', region: '吉林长白山', market: '长白山农贸市场', price: 35.00, unit: '斤', publishDate: '2024-03-01', source: '市场监测中心', historyPrices: [{ date: '2024-02-01', price: 32 }, { date: '2024-02-15', price: 33 }, { date: '2024-03-01', price: 35 }, { date: '2024-03-15', price: 36 }, { date: '2024-04-01', price: 34 }] },
  { id: 2, cropName: '松茸', region: '云南香格里拉', market: '香格里拉特产店', price: 280.00, unit: '斤', publishDate: '2024-03-05', source: '电商平台数据', historyPrices: [{ date: '2024-02-05', price: 270 }, { date: '2024-02-20', price: 275 }, { date: '2024-03-05', price: 280 }, { date: '2024-03-20', price: 290 }, { date: '2024-04-05', price: 285 }] },
  { id: 3, cropName: '核桃', region: '新疆阿克苏', market: '阿克苏干果市场', price: 18.50, unit: '斤', publishDate: '2024-02-28', source: '农业信息网', historyPrices: [{ date: '2024-01-28', price: 17 }, { date: '2024-02-10', price: 18 }, { date: '2024-02-28', price: 18.5 }, { date: '2024-03-10', price: 19 }, { date: '2024-04-01', price: 18 }] },
  { id: 4, cropName: '灵芝', region: '安徽大别山', market: '药材批发市场', price: 120.00, unit: '斤', publishDate: '2024-03-10', source: '行业协会报告', historyPrices: [{ date: '2024-02-10', price: 110 }, { date: '2024-02-25', price: 115 }, { date: '2024-03-10', price: 120 }, { date: '2024-03-25', price: 125 }, { date: '2024-04-10', price: 122 }] },
  { id: 5, cropName: '人参', region: '吉林集安', market: '集安药材市场', price: 600.00, unit: '克', publishDate: '2024-03-08', source: '中药材天地网', historyPrices: [{ date: '2024-02-08', price: 580 }, { date: '2024-02-22', price: 590 }, { date: '2024-03-08', price: 600 }, { date: '2024-03-22', price: 610 }, { date: '2024-04-08', price: 605 }] },
  { id: 6, cropName: '蓝莓', region: '黑龙江大兴安岭', market: '本地果蔬超市', price: 25.00, unit: '斤', publishDate: '2024-03-03', source: '消费者反馈', historyPrices: [{ date: '2024-02-03', price: 24 }, { date: '2024-02-18', price: 24.5 }, { date: '2024-03-03', price: 25 }, { date: '2024-03-18', price: 25.5 }, { date: '2024-04-03', price: 25 }] },
  { id: 7, cropName: '榛子', region: '辽宁清原', market: '清原农产品市场', price: 22.00, unit: '斤', publishDate: '2024-02-25', source: '地方新闻报道', historyPrices: [{ date: '2024-01-25', price: 21 }, { date: '2024-02-10', price: 21.5 }, { date: '2024-02-25', price: 22 }, { date: '2024-03-10', price: 22.5 }, { date: '2024-04-01', price: 22 }] },
  { id: 8, cropName: '五味子', region: '辽宁本溪', market: '本溪特产商店', price: 45.00, unit: '斤', publishDate: '2024-03-02', source: '网络爬取', historyPrices: [{ date: '2024-02-02', price: 43 }, { date: '2024-02-17', price: 44 }, { date: '2024-03-02', price: 45 }, { date: '2024-03-17', price: 46 }, { date: '2024-04-02', price: 45 }] },
];


// 组件挂载时，执行一次数据加载和图表初始化
onMounted(() => {
  // 首次加载模拟数据
  simulateFetchMarketData();
  // 初始化 Echarts 实例
  initChart();
});

// 组件卸载前销毁图表实例，防止内存泄漏
onBeforeUnmount(() => {
  if (myChart) {
    myChart.dispose();
    myChart = null;
  }
});

// **监听 selectedCrops 变化，重新渲染图表**
watch(() => queryForm.value.selectedCrops, (newVal) => {
  renderChart();
}, { deep: true }); // 深度监听，确保数组内部对象变化也能触发

/**
 * 模拟获取市场数据
 */
const simulateFetchMarketData = () => {
  loading.value = true;
  console.log('模拟查询参数:', queryForm.value);

  let filteredData = mockMarketData.filter(item => {
    const matchesCropName = queryForm.value.cropName
      ? item.cropName.includes(queryForm.value.cropName)
      : true;
    const matchesRegion = queryForm.value.region
      ? item.region.includes(queryForm.value.region)
      : true;

    let matchesDate = true;
    if (queryForm.value.dateRange && queryForm.value.dateRange.length === 2) {
      const startDate = new Date(queryForm.value.dateRange[0]);
      const endDate = new Date(queryForm.value.dateRange[1]);
      // 对于模拟数据，这里简单判断publishDate是否在范围内，实际可能需要更复杂的历史数据匹配
      const itemPublishDate = new Date(item.publishDate);
      matchesDate = itemPublishDate >= startDate && itemPublishDate <= endDate;
    }

    return matchesCropName && matchesRegion && matchesDate;
  });

  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  const paginatedData = filteredData.slice(startIndex, endIndex);

  setTimeout(() => {
    marketPriceList.value = paginatedData;
    total.value = filteredData.length;
    loading.value = false;
    ElMessage.success('市场数据加载成功！');

    // **清除并更新表格选择，因为数据可能已变化**
    if (multipleTableRef.value) {
      multipleTableRef.value.clearSelection();
    }
    queryForm.value.selectedCrops = []; // 清空已选作物，确保图表重新绘制
    renderChart(); // 重新渲染图表（此时应为空或显示提示）
  }, 800);
};

/**
 * 处理查询按钮点击
 */
const handleQuery = () => {
  currentPage.value = 1;
  simulateFetchMarketData();
};

/**
 * 处理重置按钮点击
 */
const handleReset = () => {
  queryForm.value = {
    cropName: '',
    region: '',
    dateRange: [],
    selectedCrops: [] // 重置时也清空已选作物
  };
  currentPage.value = 1;
  pageSize.value = 10;
  multipleTableRef.value.clearSelection(); // 清除表格选择
  simulateFetchMarketData();
};

/**
 * 每页显示数量改变
 */
const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
  simulateFetchMarketData();
};

/**
 * 当前页码改变
 */
const handleCurrentChange = (val) => {
  currentPage.value = val;
  simulateFetchMarketData();
};

/**
 * 表格选择项改变时触发
 * @param {Array} selection 当前所有勾选的行数据
 */
const handleSelectionChange = (selection) => {
  queryForm.value.selectedCrops = selection;
};

/**
 * 初始化 Echarts 实例
 */
const initChart = () => {
  // 确保 DOM 元素已经渲染
  nextTick(() => {
    if (chartRef.value) {
      myChart = echarts.init(chartRef.value);
      renderChart(); // 首次加载时也渲染图表
    }
  });
};

/**
 * 渲染 Echarts 图表
 */
const renderChart = () => {
  if (!myChart) {
    console.warn('Echarts instance not initialized.');
    return;
  }

  const selectedCrops = queryForm.value.selectedCrops;

  if (selectedCrops.length === 0) {
    // 如果没有选择作物，显示一个空图表或提示
    myChart.setOption({
      title: {
        text: '请勾选作物以查看价格走势',
        left: 'center',
        top: 'middle',
        textStyle: {
          color: '#aaa',
          fontSize: 16
        }
      },
      xAxis: { show: false },
      yAxis: { show: false },
      series: [],
      graphic: {
        type: 'text',
        left: 'center',
        top: 'center',
        style: {
          text: selectedCrops.length === 0 ? '' : '',
          fill: '#888',
          fontSize: 16
        }
      }
    }, true); // `true` 表示不合并配置，确保提示信息能显示
    return;
  }

  // 提取所有选定作物的历史日期作为X轴
  let allDates = new Set();
  selectedCrops.forEach(crop => {
    crop.historyPrices && crop.historyPrices.forEach(hp => allDates.add(hp.date));
  });
  const xAxisData = Array.from(allDates).sort(); // 对日期进行排序

  const seriesData = selectedCrops.map(crop => {
    // 为每个作物创建一个系列
    const dataPoints = xAxisData.map(date => {
      const historyPrice = crop.historyPrices.find(hp => hp.date === date);
      return historyPrice ? historyPrice.price : null; // 如果某天没有数据则显示为null
    });

    return {
      name: crop.cropName,
      type: 'line',
      connectNulls: true, // 允许数据中的 null 值连接起来
      smooth: true, // 平滑曲线
      data: dataPoints,
      // symbol: 'none', // 不显示数据点
      areaStyle: {}, // 填充面积
      tooltip: {
        valueFormatter: (value) => value === null ? '-' : `${value}元/${crop.unit}` // 格式化tooltip
      }
    };
  });

  const option = {
    title: {
      text: '作物市场价格走势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      formatter: function (params) {
        let res = `日期: ${params[0].name}<br/>`;
        params.forEach(item => {
          if (item.value !== undefined) {
            res += `<span style="display:inline-block;margin-right:4px;border-radius:10px;width:10px;height:10px;background-color:${item.color};"></span>
                  ${item.seriesName}: ${item.value + '元' }<br/>`;
          }
        });
        return res;
      }
    },
    legend: {
      data: selectedCrops.map(crop => crop.cropName),
      top: 'bottom' // 图例放底部
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%', // 留出空间给 legend
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xAxisData,
      axisLabel: {
        rotate: 30 // 日期标签倾斜，避免重叠
      }
    },
    yAxis: {
      type: 'value',
      name: '价格 (元)',
      axisLabel: {
        formatter: '{value} 元'
      }
    },
    series: seriesData
  };

  myChart.setOption(option, true); // `true` 表示不合并配置，确保每次更新都是完整的
};
</script>

<style scoped>
.market-info-container {
  background-color: #FDFBF7;
  padding: 0px;
}

.market-info-card {
  background-color: #ffffff;
  border-radius: 10px;
  border: #E8E0D0 solid 1px;
  box-shadow: 4px 4px 8px rgba(0, 0, 0, 0.1), 4px 4px 12px rgba(0, 0, 0, 0.08);
}

.market-info-title {
  font-weight: 600;
  font-size: 14px;
  color: #556B2F;
}

.market-info-query-form {
  margin-bottom: 20px;
}

.market-info-pagination {
  margin-top: 20px;
  text-align: right;
}

.chart-section {
  margin-top: 30px;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  background-color: #e8e0d09e;
}

.market-chart {
  width: 100%;
  height: 400px; /* 设置图表高度 */
}
</style>
