<template>
  <div class="market-info-container">
    <el-card class="market-info-card" shadow="never">
      <template #header>
        <div class="market-info-title">林下经济作物市场价格</div>
      </template>

      <el-form :model="queryForm" :inline="true" class="market-info-query-form">
        <el-form-item label="作物名称">
          <el-input v-model="queryForm.cropName" placeholder="请输入作物名称" clearable></el-input>
        </el-form-item>
        <el-form-item label="地区">
          <el-input v-model="queryForm.region" placeholder="请输入地区" clearable></el-input>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="queryForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            unlink-panels
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" color="#556B2F" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
        :data="paginatedMarketPriceList"
        style="width: 100%"
        v-loading="loading"
        ref="marketTableRef"
        @selection-change="handleSelectionChange"
        border
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="cropName" label="作物名称" width="150" show-overflow-tooltip></el-table-column>
        <el-table-column prop="region" label="地区" width="120" show-overflow-tooltip></el-table-column>
        <el-table-column prop="market" label="市场" width="150" show-overflow-tooltip></el-table-column>
        <el-table-column prop="price" label="价格(元/斤)" width="120">
          <template #default="{ row }">
            <span>{{ row.price.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="100"></el-table-column>
        <el-table-column prop="publishDate" label="发布日期" width="180"></el-table-column>
        <el-table-column prop="source" label="数据来源" min-width="150" show-overflow-tooltip></el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        class="market-info-pagination"
      ></el-pagination>

      <el-divider />

      <div class="chart-section">
        <h3 class="chart-title">价格走势图</h3>
        <p class="chart-description">（请在上方表格中勾选作物以显示其价格走势）</p>
        <MarketPriceChart :selectedCrops="selectedCropsForChart"  />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import MarketPriceChart from './components/marketPriceChart.vue';



// --- 状态管理 ---
const queryForm = reactive({
  cropName: '',
  region: '',
  dateRange: [],
});

const selectedCropsForChart = ref([]);

const allMarketPriceList = ref([]);
const paginatedMarketPriceList = computed(() => {
  const startIndex = (pagination.currentPage - 1) * pagination.pageSize; // 修正这里
  const endIndex = startIndex + pagination.pageSize; // 修正这里
  return allMarketPriceList.value.slice(startIndex, endIndex);
});

const loading = ref(false);

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0,
});

const marketTableRef = ref(null);

// --- 模拟数据 (用于演示) ---
// --- 辅助函数：生成历史价格数据 ---
const generateHistoryPrices = (startDateStr, endDateStr, basePrice, fluctuation) => {
    const prices = [];
    let currentDate = new Date(startDateStr);
    const endDate = new Date(endDateStr);

    while (currentDate <= endDate) {
        // 模拟价格波动，确保价格不会低于0
        const price = Math.max(0.5, parseFloat((basePrice + (Math.random() - 0.5) * fluctuation * 2).toFixed(2)));
        prices.push({
            date: currentDate.toISOString().slice(0, 10), // 格式化为 YYYY-MM-DD
            price: price
        });
        currentDate.setDate(currentDate.getDate() + 7); // 每周一个数据点
    }
    return prices;
};

// --- 生成 mockMarketData ---
const mockMarketData = [
  {
    id: 1, cropName: '黑木耳', region: '吉林长白山', market: '长白山农贸市场', price: 35.00, unit: '斤', publishDate: '2024-03-01', source: '市场监测中心',
    historyPrices: generateHistoryPrices('2023-01-01', '2025-05-29', 35, 5) // 从2023年1月1日开始，基准价35，波动幅度5
  },
  {
    id: 2, cropName: '松茸', region: '云南香格里拉', market: '香格里拉特产店', price: 280.00, unit: '斤', publishDate: '2024-03-05', source: '电商平台数据',
    historyPrices: generateHistoryPrices('2023-01-01', '2025-05-29', 280, 20) // 基准价280，波动幅度20
  },
  {
    id: 3, cropName: '核桃', region: '新疆阿克苏', market: '阿克苏干果市场', price: 18.50, unit: '斤', publishDate: '2024-02-28', source: '农业信息网',
    historyPrices: generateHistoryPrices('2023-01-01', '2025-05-29', 18.5, 3)
  },
  {
    id: 4, cropName: '灵芝', region: '安徽大别山', market: '药材批发市场', price: 120.00, unit: '斤', publishDate: '2024-03-10', source: '行业协会报告',
    historyPrices: generateHistoryPrices('2023-01-01', '2025-05-29', 120, 15)
  },
  {
    id: 5, cropName: '人参', region: '吉林集安', market: '集安药材市场', price: 600.00, unit: '克', publishDate: '2024-03-08', source: '中药材天地网',
    historyPrices: generateHistoryPrices('2023-01-01', '2025-05-29', 600, 50)
  },
  {
    id: 6, cropName: '蓝莓', region: '黑龙江大兴安岭', market: '本地果蔬超市', price: 25.00, unit: '斤', publishDate: '2024-03-03', source: '消费者反馈',
    historyPrices: generateHistoryPrices('2023-01-01', '2025-05-29', 25, 4)
  },
  {
    id: 7, cropName: '榛子', region: '辽宁清原', market: '清原农产品市场', price: 22.00, unit: '斤', publishDate: '2024-02-25', source: '地方新闻报道',
    historyPrices: generateHistoryPrices('2023-01-01', '2025-05-29', 22, 3)
  },
  {
    id: 8, cropName: '五味子', region: '辽宁本溪', market: '本溪特产商店', price: 45.00, unit: '斤', publishDate: '2024-03-02', source: '网络爬取',
    historyPrices: generateHistoryPrices('2023-01-01', '2025-05-29', 45, 6)
  },
  {
    id: 9, cropName: '枸杞', region: '宁夏中宁', market: '中宁枸杞市场', price: 55.00, unit: '斤', publishDate: '2024-04-10', source: '电商平台',
    historyPrices: generateHistoryPrices('2023-03-01', '2025-05-29', 55, 7)
  },
  {
    id: 10, cropName: '藏红花', region: '西藏拉萨', market: '拉萨药材市场', price: 800.00, unit: '克', publishDate: '2024-04-15', source: '行业报告',
    historyPrices: generateHistoryPrices('2023-03-01', '2025-05-29', 800, 80)
  },
];

// --- 生命周期钩子 ---
onMounted(() => {
  fetchMarketData();
});

// --- 数据获取与过滤 ---
const fetchMarketData = () => {
  loading.value = true;
  console.log('模拟查询参数:', queryForm);

  setTimeout(() => {
    let filteredData = mockMarketData.filter(item => {
      const matchesCropName = queryForm.cropName
        ? item.cropName.includes(queryForm.cropName)
        : true;
      const matchesRegion = queryForm.region
        ? item.region.includes(queryForm.region)
        : true;

      let matchesDate = true;
      if (queryForm.dateRange && queryForm.dateRange.length === 2) {
        const startDate = new Date(queryForm.dateRange[0]);
        const endDate = new Date(queryForm.dateRange[1]);
        const itemPublishDate = new Date(item.publishDate);
        matchesDate = itemPublishDate >= startDate && itemPublishDate <= endDate;
      }

      return matchesCropName && matchesRegion && matchesDate;
    });

    allMarketPriceList.value = filteredData;
    pagination.total = filteredData.length;
    loading.value = false;
    ElMessage.success('市场数据加载成功！');

    marketTableRef.value?.clearSelection();
    selectedCropsForChart.value = [];
  }, 500);
};

// --- 事件处理函数 ---
const handleQuery = () => {
  pagination.currentPage = 1;
  fetchMarketData();
};

const handleReset = () => {
  queryForm.cropName = '';
  queryForm.region = '';
  queryForm.dateRange = [];
  pagination.currentPage = 1;
  pagination.pageSize = 10;
  marketTableRef.value?.clearSelection();
  selectedCropsForChart.value = [];
  fetchMarketData();
};

const handleSizeChange = (val) => {
  pagination.pageSize = val;
  pagination.currentPage = 1;
};

const handleCurrentChange = (val) => {
  pagination.currentPage = val;
};

const handleSelectionChange = (selection) => {
  selectedCropsForChart.value = selection;
  console.log('selectedCropsForChart:', selectedCropsForChart.value);

};
</script>

<style scoped>



.market-info-container {
  background-color: var(--background-color-page);
  padding: 20px;
}

.market-info-card {
  background-color: var(--background-color-card);
  border-radius: 10px;
  border: var(--border-color) solid 1px;
  box-shadow: 4px 4px 8px rgba(0, 0, 0, 0.05), 2px 2px 6px rgba(0, 0, 0, 0.03);
  padding: 20px;
}

.market-info-title {
  font-weight: 600;
  font-size: 20px;
  color: var(--primary-green);
  text-align: center;
  padding-bottom: 10px;
}

.market-info-query-form {
  margin-bottom: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.market-info-pagination {
  margin-top: 20px;
  text-align: right;
  padding: 10px 0;
}

.chart-section {
  margin-top: 30px;
  padding: 25px;
  border: 1px solid var(--border-color); /* 使用边框色 */
  border-radius: 10px;
  background-color: var(--background-color-page); /* 可以考虑用更浅的页面背景色或自定义色 */
  box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.05);
}

.chart-title {
  text-align: center;
  color: var(--primary-green);
  font-size: 18px;
  margin-bottom: 5px;
}

.chart-description {
  text-align: center;
  color: var(--text-color-tip); /* 使用提示文本色 */
  font-size: 13px;
  margin-bottom: 20px;
}

/* Element Plus 样式覆盖 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: var(--text-color-dark); /* 使用深文本色 */
}

:deep(.el-input__inner),
:deep(.el-date-editor .el-range-input) {
  border-color: var(--border-color); /* 使用边框色 */
}

:deep(.el-button--primary) {
  background-color: var(--primary-green) !important;
  border-color: var(--primary-green) !important;
}

:deep(.el-button--primary:hover) {
  background-color: var(--light-green) !important; /* 悬停时使用浅绿色 */
  border-color: var(--light-green) !important;
}

:deep(.el-button:hover) {
  color: var(--primary-green);
  border-color: var(--primary-green);
}

:deep(.el-table--border .el-table__cell) {
  border-right: 1px solid var(--border-color); /* 使用边框色 */
}
</style>
