<template>
  <router-view />
</template>

<script>

export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', sans-serif;
  background-color: #FDFBF7;
  min-height: 100vh;
  overflow-y: auto;
  overflow-x: auto;
}

/* 重置Element Plus的默认样式 */
:deep(.el-main) {
  padding: 0 !important;
  overflow: visible !important;
  min-height: 100% !important;
}

:deep(.el-container) {
  height: 100% !important;
  width: 100% !important;
}

.app-container.sidebar-minimized el-main {
  width: calc(100% - 60px);
}
/* 定义 CSS 变量，严格遵循您原有的配色 */
:root {
  --primary-color: #556B2F; /* 主色调：墨绿 */
  --light-bg-color: #FDFBF7; /* 浅背景色 */
  --border-color-light: #E8E0D0; /* 浅边框色 */
  --text-color-dark: #2B3F2B; /* 深文本色 */
}
</style>
