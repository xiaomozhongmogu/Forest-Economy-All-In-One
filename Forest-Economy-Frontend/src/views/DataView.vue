<template>
  <div class="dashboard-content">
    <div class="title"><h1>数据看板</h1></div>
    <div class="charts-content">
      <div id = "area-chart" ref="areaChart">
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
import * as echarts from 'echarts';

// 响应式引用
const areaChart = ref(null);
let chart = null;

// 初始化图表
const initChart = () => {
  const chartDom = areaChart.value;
  if (!chartDom) {
    console.error('图表DOM元素未找到');
    return;
  }

  // 确保容器有尺寸
  if (chartDom.offsetHeight === 0) {
    chartDom.style.height = '500px';
  }

  chart = echarts.init(chartDom);

  const option = {
  title: {
    text: '林下农业年产量趋势'
  },
  tooltip: {
    show: true,
    trigger: 'axis',
    backgroundColor: 'rgba(255, 255, 255, 0.9)',
    borderColor: '#ccc',
    borderWidth: 1,
    padding: 10,
    textStyle: {
      color: '#333',
      fontSize: 14
    },
    extraCssText: 'z-index: 100',
    formatter: function(params) {
      let result = params[0].name + '年<br/>';
      params.forEach(item => {
        result +=
          '<span style="display:inline-block;width:10px;height:10px;background-color:' +
          item.color +
          '"></span> ' +
          item.seriesName + ': ' +
          item.value + '吨<br/>';
      });
      return result;
    },
    axisPointer: {
      type: 'cross',
      label: {
        backgroundColor: '#6a7985'
      }
    }
  },
  legend: {
    data: ['食用菌', '中药材', '山野菜', '林果', '养殖产品']
  },
  toolbox: {
    feature: {
      saveAsImage: {}
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: [{
    type: 'category',
    boundaryGap: false,
    name: '年份',
    data: ['2015', '2016', '2017', '2018', '2019', '2020', '2021', '2022', '2023', '2024']
  }],
  yAxis: [{
    type: 'value',
    name: '产量（吨）',
    axisLabel: {
      formatter: '{value} 吨'
    }
  }],
  series: [
    {
      name: '食用菌',
      type: 'line',
      stack: '总量',
      areaStyle: {},
      emphasis: {
        focus: 'series'
      },
      data: [120, 145, 160, 190, 220, 260, 290, 320, 370, 410]
    },
    {
      name: '中药材',
      type: 'line',
      stack: '总量',
      areaStyle: {},
      emphasis: {
        focus: 'series'
      },
      data: [80, 95, 110, 125, 155, 180, 210, 240, 265, 290]
    },
    {
      name: '山野菜',
      type: 'line',
      stack: '总量',
      areaStyle: {},
      emphasis: {
        focus: 'series'
      },
      data: [150, 165, 185, 210, 240, 270, 310, 350, 390, 430]
    },
    {
      name: '林果',
      type: 'line',
      stack: '总量',
      areaStyle: {},
      emphasis: {
        focus: 'series'
      },
      data: [320, 350, 390, 430, 480, 530, 590, 650, 720, 790]
    },
    {
      name: '养殖产品',
      type: 'line',
      stack: '总量',
      label: {
        show: true,
        position: 'top'
      },
      areaStyle: {},
      emphasis: {
        focus: 'series'
      },
      data: [220, 250, 290, 340, 400, 470, 550, 640, 730, 830]
    },
  ],
};

  chart.setOption(option);

  // 强制更新以确保渲染
  setTimeout(() => {
    chart.resize();
  }, 200);
};

// 重置图表大小
const resizeChart = () => {
  if (chart) {
    chart.resize();
  }
};

// 生命周期钩子
onMounted(() => {
  nextTick(() => {
    initChart();
  });
  window.addEventListener('resize', resizeChart);
});

onBeforeUnmount(() => {
  if (chart) {
    chart.dispose();
    chart = null;
  }
  window.removeEventListener('resize', resizeChart);
});
</script>

<style scoped>
.dashboard-content {
  padding: 20px;
  background-color: #FDFBF7;
  color: #2B3F2B;
  width: 100%;
}

.charts-content {
  margin-top: 20px;
  width: 100%;
}

#area-chart {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #E8E0D0;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  width: 100%;
  height: 500px; /* 设置一个固定高度 */
  background-color: #F0F0F0;
}
</style>
