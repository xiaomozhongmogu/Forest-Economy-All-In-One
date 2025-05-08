<template>
  <el-container class="layout-container" :class="{ 'minimized': isMinimized }">
    <el-aside :width="isMinimized ? '64px' : '230px'" class="aside-container">
      <el-menu :default-active="activeMenu" class="sidebar" :class="{ 'minimized': isMinimized }"
        :collapse="isMinimized" :collapse-transition="true" background-color="#FDFBF7" text-color="#2B3F2B"
        active-text-color="#556B2F" router>
        <div class="logo-container">
          <div class="logo" v-if="!isMinimized">
            <span class="logo-text">🌿林下经济</span>
          </div>
          <el-button v-else type="text" circle class="minimize-button centered-button" @click="toggleMinimize">
            <el-icon>
              <ArrowRight />
            </el-icon>
          </el-button>
          <el-button v-if="!isMinimized" type="text" circle class="minimize-button" @click="toggleMinimize">
            <el-icon>
              <ArrowLeft />
            </el-icon>
          </el-button>
        </div>

        <div class="new-project-container">
          <el-button class="new-project-button" type="primary" plain @click="createNewProject">
            <el-icon>
              <Plus />
            </el-icon>
            <span v-if="!isMinimized">新建项目</span>
            <span v-if="!isMinimized" class="shortcut-hint"></span>
          </el-button>
        </div>

        <el-menu-item index="/home">
          <el-icon>
            <House />
          </el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <el-menu-item index="/community">
          <el-icon>
            <ChatDotRound />
          </el-icon>
          <template #title>社区</template>
        </el-menu-item>

        <el-menu-item index="/data_dashboard">
          <el-icon>
            <DataAnalysis />
          </el-icon>
          <template #title>数据看板</template>
        </el-menu-item>

        <el-sub-menu index="4">
          <template #title>
            <el-icon>
              <Collection />
            </el-icon>
            <span v-if="!isMinimized">资源库</span>
          </template>
          <el-menu-item-group>
            <el-menu-item index="/resources/mushroom">林下食用菌培育技术</el-menu-item>
            <el-menu-item index="/resources/economy">中国各省林下经济产值</el-menu-item>
            <el-menu-item index="/resources/breeding">林下养殖最佳实践案例</el-menu-item>
            <el-menu-item index="/resources/medicine">林下药材种植指南</el-menu-item>
            <el-menu-item index="/resources/policy">林下经济政策扶持</el-menu-item>
          </el-menu-item-group>
        </el-sub-menu>

        <el-menu-item index="/data_management">
          <el-icon>
            <Search />
          </el-icon>
          <template #title>数据管理与分析</template>
        </el-menu-item>

        <div class="history-section" v-if="!isMinimized && historyStore.chatHistory.length > 0">
          <div class="history-title">历史记录</div>
          <div class="history-list">
            <div v-for="(history, index) in historyStore.chatHistory" :key="index" class="history-item">
              <div class="history-item-content" @click="loadHistory(history)">
                <el-icon>
                  <Clock />
                </el-icon>
                <span class="history-item-title">{{ history.title }}</span>
              </div>
              <el-icon class="delete-icon" @click.stop="confirmDelete(history)">
                <Close />
              </el-icon>
            </div>
          </div>
        </div>

        <div class="bottom-section" v-if="!isMinimized">
          <el-card class="upgrade-section" shadow="never">
            <template #header>
              <div class="upgrade-title">不知道如何使用？</div>
            </template>
            <div class="upgrade-desc">
              点击用户手册
              <br />
              开始使用先进分析工具和模型。
            </div>
            <el-button type="success" class="upgrade-button" @click="$router.push('/user-manual')">
              <el-icon>
                <TopRight />
              </el-icon> 用户手册
            </el-button>
          </el-card>

          <div class="auth-buttons" v-if="!isLoggedIn">
            <el-button type="primary" class="auth-button login-button" size="large" @click="$router.push('/login')">
              登录
            </el-button>
          </div>

          <div class="user-buttons" v-if="isLoggedIn">
            <el-dropdown @command="handleUserCommand" trigger="click" placement="top-end">
              <el-button type="default" class="user-avatar-button" :class="{ 'minimized': isMinimized }">
                <el-icon><User /></el-icon>
                <span v-if="!isMinimized" class="username-display">{{ userStore.userInfo.username || '用户' }}</span>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <div class="user-greeting">
                    {{ userStore.userInfo.username || '用户' }},您好
                  </div>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>退出登录
                  </el-dropdown-item>
                  <el-dropdown-item command="deleteAccount" class="delete-account-item">
                    <el-icon><Delete /></el-icon>注销帐号
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </el-menu>
    </el-aside>

    <el-container class="main-container">
      <div class="content-wrapper">
        <router-view />
      </div>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useHistoryStore } from '@/stores/historyStore'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  House,
  ChatDotRound,
  DataAnalysis,
  Collection,
  Search,
  ArrowLeft,
  ArrowRight,
  Plus,
  TopRight,
  Clock,
  Close,
  User, // <-- Added
  SwitchButton, // <-- Added
  Delete // <-- Added
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'; // 假设你的 store 路径
import { deleteByIdApi } from '@/api/user'

// 侧边栏状态
const isMinimized = ref(false)

// Placeholder user data - replace with actual data from your auth system
const userStore = useUserStore();

const isLoggedIn = computed(() => userStore.isLoggedIn)

console.log('登录成功，用户信息:', userStore.userInfo);
console.log('是否登录', userStore.isLoggedIn);

// 获取当前路由和路由器
const route = useRoute()
const router = useRouter()

// 获取历史记录存储
const historyStore = useHistoryStore()

// 根据当前路由路径动态设置激活的菜单项
const activeMenu = computed(() => {
  if (route.path.includes('/resources/')) {
    return route.path
  }
  return route.path
})

// 切换侧边栏展开/收起状态
const toggleMinimize = () => {
  isMinimized.value = !isMinimized.value
  localStorage.setItem('sidebarMinimized', isMinimized.value)
}

// 创建新项目
const createNewProject = () => {
  router.push({ path: '/home', query: { new: Date.now() } })
}

// 加载历史记录
const loadHistory = (history) => {
  router.push({ path: '/home', query: { historyId: history.id } })
}

// 确认删除历史记录
const confirmDelete = (history) => {
  ElMessageBox.confirm(
    '确定要删除此历史记录吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      historyStore.removeHistory(history.id)
      ElMessage({
        type: 'success',
        message: '历史记录已删除',
      })
    })
    .catch(() => {
      // 用户取消删除
    })
}

// 处理用户下拉菜单命令
const handleUserCommand = (command) => {
  if (command === 'logout') {
    logoutUser()
  } else if (command === 'deleteAccount') {
    confirmDeleteAccount()
  }
}

// 退出登录逻辑
const logoutUser = () => {
  userStore.clearUserInfo()
  ElMessage.success('已成功退出登录')
}

// 确认注销帐号逻辑
const confirmDeleteAccount = () => {
  ElMessageBox.confirm(
    '您确定要注销您的帐号吗？此操作不可逆，您的所有数据将被永久删除。',
    '注销帐号确认',
    {
      confirmButtonText: '确定注销',
      cancelButtonText: '取消',
      type: 'error',
      // confirmButtonClass: 'el-button--danger', // Element Plus might handle this by type 'error'
    }
  )
    .then(() => {
      deleteUserAccount()
    })
    .catch(() => {
      ElMessage.info('已取消注销帐号操作')
    })
}

// 注销帐号逻辑
const deleteUserAccount = async() => {
  // Replace with your actual account deletion logic (e.g., API call to backend)
  const result = await deleteByIdApi(userStore.userInfo.id);
  if (result.code === 1 || result.success) {
    ElMessage.success('您的帐号已成功注销');
    userStore.clearUserInfo();
  } else {
    ElMessage.error('注销帐号失败，请重试');
  }
  console.log('Deleting account for:', userStore.userInfo.username)
}


// 在组件挂载时从localStorage恢复侧边栏状态
onMounted(() => {
  const savedState = localStorage.getItem('sidebarMinimized')
  if (savedState !== null) {
    isMinimized.value = savedState === 'true'
  }
  historyStore.loadFromLocalStorage()

  // ** Example: Check if user is logged in (replace with your actual auth check) **
  // For instance, you might check for a token in localStorage
  // const authToken = localStorage.getItem('authToken');
  // if (authToken) {
  //   isLoggedIn.value = true;
  //   // Fetch user details if needed, e.g.,
  //   // user.value.name = await fetchUserNameAPI();
  // } else {
  //   isLoggedIn.value = false;
  // }
})
</script>

<style scoped>
/* 页面整体布局 */
.layout-container {
  display: flex;
  min-height: 100vh;
  width: 100%;
  position: relative;
  overflow: visible;
}

/* 侧边栏容器 */
.aside-container {
  height: 100vh;
  overflow: hidden;
  padding: 0;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 1000;
  transition: width 0.3s ease-in-out;
}

/* 侧边栏菜单 */
.sidebar {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100vh;
  background-color: #FDFBF7; /* Existing background */
  border-right: 1px solid #E8E0D0; /* Softer border */
  padding: 15px 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', sans-serif;
  transition: width 0.3s ease-in-out;
}

.sidebar.minimized {
  width: 64px;
}

/* 主内容区域 */
.main-container {
  flex: 1;
  margin-left: 230px;
  width: calc(100% - 230px);
  min-height: 100vh;
  overflow-y: auto;
  overflow-x: visible;
  transition: margin-left 0.3s ease-in-out, width 0.3s ease-in-out;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #F4F5F7; /* Light background for main content area */
}

/* 当侧边栏折叠时调整主容器 */
.layout-container.minimized .main-container {
  margin-left: 64px;
  width: calc(100% - 64px);
}

/* 内容包装器 */
.content-wrapper {
  width: 100%;
  max-width: 1200px;
  padding: 20px;
  margin: 0 auto;
  transition: all 0.3s ease-in-out;
}

/* Logo区域 */
.logo-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 0 15px;
  position: relative;
  height: 50px;
  transition: all 0.3s ease-in-out;
  overflow: hidden;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 18px;
  color: #556B2F;
  font-weight: 600;
  transition: opacity 0.3s ease-in-out;
}

.logo-icon {
  font-size: 22px;
  margin-right: 5px;
  color: #556B2F;
}

.logo-text {
  color: #556B2F;
}

.minimize-button {
  position: absolute;
  right: 5px;
  color: #556B2F;
}

.centered-button {
  left: 50%;
  transform: translateX(-50%);
}

/* 新建项目按钮 */
.new-project-container {
  padding: 0 15px;
  margin-bottom: 20px;
  transition: padding 0.3s ease-in-out;
}

.new-project-button {
  width: 100%;
  background-color: #ffffff;
  border-color: #E8E0D0;
  color: #2B3F2B;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease-in-out;
}

.new-project-button:hover {
  background-color: #EFF4ED;
  border-color: #D9E5D6;
}

.shortcut-hint {
  margin-left: auto;
  font-size: 12px;
  color: #7D7060;
  display: flex;
  align-items: center;
}

.shortcut-hint:before {
  content: "⌘";
  background-color: #F0EEE6;
  border-radius: 3px;
  padding: 1px 4px;
  margin-right: 3px;
}

.shortcut-hint:after {
  content: "K";
  background-color: #F0EEE6;
  border-radius: 3px;
  padding: 1px 4px;
  margin-left: 3px;
}

/* 底部区域 */
.bottom-section {
  margin-top: auto;
  padding: 0 15px;
  margin-bottom: 15px;
  transition: opacity 0.3s ease-in-out, transform 0.3s ease-in-out;
}

.upgrade-section {
  background-color: #EFF4ED;
  border-radius: 10px;
  margin-bottom: 15px;
  border: 1px solid #D9E5D6;
}

.upgrade-title {
  font-weight: 600;
  font-size: 14px;
  color: #556B2F;
}

.upgrade-desc {
  font-size: 12px;
  color: #3C2A1A;
  line-height: 1.4;
  margin-bottom: 12px;
}

.upgrade-button {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #556B2F;
  border-color: #455A20;
}

.upgrade-button:hover {
  background-color: #455A20;
}

.auth-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 10px; /* Added margin */
}

.auth-button {
  width: 100%;
  border-radius: 8px;
  font-size: 14px;
  height: 40px;
}

.login-button {
  background-color: #556B2F;
  border-color: #455A20;
}

.login-button:hover {
  background-color: #455A20;
}

/* 用户按钮区域 - NEW STYLES */
.user-buttons {
  margin-top: 10px; /* Space above user button */
}

.user-avatar-button {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-start; /* Align icon and text to the start */
  padding: 0 12px;
  height: 44px; /* Slightly larger */
  border-radius: 8px;
  background-color: #f7f7f7; /* Light background for the button */
  border: 1px solid #e0e0e0; /* Soft border */
  color: #333; /* Text color */
  transition: background-color 0.2s ease, border-color 0.2s ease;
}

.user-avatar-button:hover,
.user-avatar-button:focus {
  background-color: #e9e9e9; /* Darker on hover/focus */
  border-color: #d0d0d0;
  color: #2B3F2B;
}

.user-avatar-button .el-icon {
  font-size: 18px; /* Icon size */
  margin-right: 10px; /* Space between icon and username */
  color: #556B2F; /* Icon color to match theme */
}

.user-avatar-button.minimized .el-icon {
  margin-right: 0;
}

.user-avatar-button.minimized {
  justify-content: center;
  padding: 0;
  width: 44px; /* Square button when minimized */
}


.username-display {
  font-weight: 500;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex-grow: 1; /* Allow username to take available space */
  text-align: left;
}

/* Styles for the dropdown menu */
.el-dropdown-menu {
  padding: 0 !important; /* Remove default padding */
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  border: 1px solid #EBEEF5;
}

.user-greeting {
  padding: 12px 16px;
  font-size: 14px;
  color: #606266; /* Standard text color */
  background-color: #FAFAFA; /* Light background for greeting */
  border-bottom: 1px solid #EBEEF5; /* Separator */
  font-weight: 500;
}

:deep(.el-dropdown-menu__item) {
  padding: 10px 16px !important;
  font-size: 14px !important;
  line-height: 1.5 !important;
  display: flex !important;
  align-items: center !important;
}

:deep(.el-dropdown-menu__item .el-icon) {
  margin-right: 8px;
  font-size: 16px; /* Consistent icon size in dropdown */
  color: #556B2F; /* Match icon color */
}

:deep(.el-dropdown-menu__item.delete-account-item) {
  color: #F56C6C !important; /* Element Plus danger color for text */
}

:deep(.el-dropdown-menu__item.delete-account-item .el-icon) {
  color: #F56C6C !important; /* Danger color for icon */
}

:deep(.el-dropdown-menu__item.delete-account-item:hover) {
  background-color: #FEF0F0 !important; /* Light red background on hover */
  color: #F56C6C !important;
}

:deep(.el-dropdown-menu__item--divided) {
  margin-top: 6px !important;
  border-top: 1px solid #EBEEF5 !important; /* Softer divider */
}
/* END OF NEW USER BUTTON STYLES */


/* Element Plus覆盖样式 */
:deep(.el-menu) {
  border-right: none;
  /* background-color: #82ae89; */ /* Removed to use parent's #FDFBF7 */
}

/* 历史记录区域 */
.history-section {
  margin: 15px 0;
  padding: 0 15px;
  max-height: 200px; /* Limit height and make it scrollable if needed */
  overflow-y: auto;
}

.history-title {
  font-size: 13px; /* Slightly smaller */
  font-weight: 600;
  color: #556B2F; /* Dark green to match theme */
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid rgba(85, 107, 47, 0.2); /* Themed border */
  text-transform: uppercase; /* Optional: for style */
  letter-spacing: 0.5px; /* Optional */
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 6px; /* Slightly increased gap */
}

.history-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 10px;
  border-radius: 6px;
  background-color: rgba(85, 107, 47, 0.05); /* Very light green tint */
  transition: all 0.2s ease-in-out;
}

.history-item-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  cursor: pointer;
  color: #2B3F2B; /* Dark text for readability */
}

.history-item:hover {
  background-color: rgba(85, 107, 47, 0.1); /* Slightly darker on hover */
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.delete-icon {
  color: #A8B3A8; /* Softer delete icon color */
  opacity: 0.7;
  cursor: pointer;
  font-size: 14px;
  margin-left: 5px;
  transition: all 0.2s ease-in-out;
}

.delete-icon:hover {
  opacity: 1;
  color: #ff6b6b; /* Brighter red on hover */
}

.history-item-title {
  font-size: 13px;
  color: #2B3F2B; /* Ensuring readability */
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 150px; /* Adjust if needed based on sidebar width */
}

.empty-history {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #7D7060;
  font-size: 12px;
  padding: 10px 15px;
  font-style: italic;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 48px; /* Slightly reduced height */
  line-height: 48px; /* Match height */
  color: #2B3F2B !important;
  font-weight: 500; /* Slightly bolder */
  transition: all 0.3s ease-in-out;
  border-radius: 6px; /* Rounded corners for menu items */
  margin: 0 10px 4px 10px; /* Add horizontal margin and small bottom margin */
  padding-left: 15px !important; /* Adjust padding */
}
:deep(.el-sub-menu .el-menu-item) {
  height: 40px; /* Sub-menu items even smaller */
  line-height: 40px;
  font-size: 13px;
  background-color: transparent !important; /* Ensure sub-items don't have strange bg */
  margin: 0 0 2px 0;
  padding-left: 30px !important; /* Indent sub-menu items */
}


:deep(.el-menu-item.is-active) {
  background-color: #556B2F !important;
  color: #FFFFFF !important;
  box-shadow: 0 2px 5px rgba(85, 107, 47, 0.3);
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #EFF4ED !important; /* Lighter green on hover */
  color: #556B2F !important;
}


:deep(.el-card__header) {
  padding: 10px 15px;
  border-bottom: none;
}

:deep(.el-card__body) {
  padding: 0 15px 15px;
}
</style>
