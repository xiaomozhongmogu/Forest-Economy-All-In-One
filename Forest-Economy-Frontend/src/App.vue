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
  --text-color-dark: #2B3F2B; /* 深文本色 */
  --primary-green: #556B2F;
  --light-green: #6C8145;
  --border-color: #E8E0D0;
  --background-color-page: #FDFBF7;
  --background-color-card: #ffffff;
  --text-color-tip: #999;
}
</style>
