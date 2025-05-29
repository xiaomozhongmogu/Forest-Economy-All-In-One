<template>
  <div class="chart-container">
    <div v-if="selectedCrops.length === 0" class="chart-placeholder">
      请在上方表格中勾选作物以显示其价格走势
    </div>
    <div v-else ref="chartDom" class="market-chart"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue';
import * as echarts from 'echarts';

// 定义组件接收的 props
const props = defineProps({
  selectedCrops: {
    type: Array,
    default: () => []
  }
});

const chartDom = ref(null); // 图表容器的DOM引用
let myChart = null; // Echarts 实例

// 直接使用十六进制值，或者从外部传入作为 prop
const themeColor = '#556B2F';

// --- 生命周期钩子 ---
onMounted(() => {
  // onMounted时，如果selectedCrops不为空，则直接初始化并渲染
  // 如果selectedCrops为空，DOM元素不存在，无需初始化
  if (props.selectedCrops.length > 0) {
    initChart(); // 尝试初始化图表
  }
});

onBeforeUnmount(() => {
  if (myChart) {
    myChart.dispose(); // 销毁图表实例，防止内存泄漏
    myChart = null;
  }
  window.removeEventListener('resize', handleChartResize);
});

// --- 侦听器 ---
watch(() => props.selectedCrops, (newVal) => {
  // 当 selectedCrops 变化时
  if (newVal.length > 0) {
    // 如果有数据，确保 chartDom 已经渲染，然后初始化并渲染图表
    nextTick(() => {
      if (!myChart) { // 只有在myChart未初始化时才初始化
        initChart();
      } else {
        renderChart(); // 否则直接渲染
      }
    });
  } else {
    // 如果没有数据，销毁图表并清空
    if (myChart) {
      myChart.dispose();
      myChart = null;
    }
  }
}, { deep: true, immediate: true }); // 添加 immediate: true，确保首次加载时如果props.selectedCrops有值也能触发

// --- 方法 ---
const handleChartResize = () => {
  myChart?.resize();
};

const initChart = () => {
  if (chartDom.value && !myChart) { // 确保DOM元素存在且Echarts实例未初始化
    myChart = echarts.init(chartDom.value);
    window.addEventListener('resize', handleChartResize);
    renderChart(); // 初始化后立即渲染
  }
};


const renderChart = () => {
  if (!myChart) {
    console.warn('Echarts 实例未初始化，无法渲染图表。');
    return;
  }

  // 这里的 selectedCrops.length === 0 的判断可以移除，因为在外层 watch 已经处理
  // 如果执行到这里，说明 selectedCrops 已经有数据

  const allDates = new Set();
  props.selectedCrops.forEach(crop => {
    crop.historyPrices?.forEach(hp => allDates.add(hp.date));
  });
  const xAxisData = Array.from(allDates).sort();

  const seriesData = props.selectedCrops.map(crop => {
    const dataPoints = xAxisData.map(date => {
      const historyPrice = crop.historyPrices?.find(hp => hp.date === date);
      return historyPrice ? historyPrice.price : null;
    });

    return {
      name: crop.cropName,
      type: 'line',
      connectNulls: true,
      smooth: true,
      data: dataPoints,
      areaStyle: {},
      emphasis: {
        focus: 'series'
      },
      tooltip: {
        valueFormatter: (value) => value === null ? '-' : `${value}元/${crop.unit}`
      }
    };
  });

  const option = {
    title: {
      text: '作物市场价格走势',
      left: 'center',
      textStyle: {
        color: themeColor, // 使用 JavaScript 变量
        fontSize: 18,
        fontWeight: 'bold'
      }
    },
    tooltip: {
      trigger: 'axis',
      formatter: function (params) {
        let res = `<div style="font-weight: bold;">日期: ${params[0].name}</div>`;
        params.forEach(item => {
          if (item.value !== undefined) {
            const originalCrop = props.selectedCrops.find(crop => crop.cropName === item.seriesName);
            const unit = originalCrop ? originalCrop.unit : '单位';
            res += `
              <div style="display:flex; align-items: center; justify-content: space-between; padding-top: 4px;">
                <span style="display:inline-block;margin-right:4px;border-radius:10px;width:10px;height:10px;background-color:${item.color};"></span>
                <span>${item.seriesName}: </span>
                <span style="font-weight: bold; margin-left: auto;">${item.value.toFixed(2)}元/${unit}</span>
              </div>`;
          }
        });
        return res;
      },
      extraCssText: 'box-shadow: 0 0 8px rgba(0, 0, 0, 0.2); border-radius: 4px; padding: 10px;',
      confine: true
    },
    legend: {
      data: props.selectedCrops.map(crop => crop.cropName),
      top: 'bottom',
      type: 'scroll',
      pageButtonGap: 5,
      padding: [10, 0, 0, 0],
      textStyle: {
        color: '#333'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: xAxisData,
      axisLabel: {
        rotate: 45,
        formatter: '{value}',
        color: '#555'
      },
      axisLine: {
        lineStyle: {
          color: '#ccc'
        }
      }
    },
    yAxis: {
      type: 'value',
      name: '价格 (元)',
      axisLabel: {
        formatter: '{value} 元',
        color: '#555'
      },
      splitLine: {
        lineStyle: {
          type: 'dashed',
          color: '#f0f0f0'
        }
      },
      nameTextStyle: {
        color: '#555',
        fontSize: 14
      }
    },
    series: seriesData,
    dataZoom: [
      {
        type: 'inside',
        xAxisIndex: 0,
        start: 0,
        end: 100
      },
      {
        type: 'slider',
        xAxisIndex: 0,
        start: 0,
        end: 100,
        bottom: 50,
        height: 20,
        handleIcon: 'path://M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z',
        handleSize: '80%',
        handleStyle: {
          color: themeColor, // 使用 JavaScript 变量
        },
        textStyle: {
          color: '#333'
        }
      }
    ]
  };

  myChart.setOption(option, true);
};
</script>

<style scoped>
.chart-container {
  min-height: 450px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.market-chart {
  width: 100%;
  height: 450px;
}

.chart-placeholder {
  color: var(--text-color-tip); /* 使用您的提示文本色 */
  font-size: 16px;
  text-align: center;
  padding: 20px;
}
</style>
