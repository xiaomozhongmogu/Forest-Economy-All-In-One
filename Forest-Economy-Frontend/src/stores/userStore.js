import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useUserStore = defineStore('user', () => {
  const userInfo = ref({
    id: '',
    username: '',
    token: '',
  });

  const isLoggedIn = computed(() => !!userInfo.value.token && !!userInfo.value.id);
  const isRemembered = ref(false);

  // 设置记住状态的函数
  const setRememberStatus = (remember) => {
    isRemembered.value = remember;

    // 如果已登录，则更新存储位置
    if (isLoggedIn.value) {
      // 清除旧存储
      if (remember) {
        sessionStorage.removeItem('user');
      } else {
        localStorage.removeItem('user');
      }

      // 保存到新存储
      const storage = remember ? localStorage : sessionStorage;
      storage.setItem('user', JSON.stringify({
        userInfo: userInfo.value,
        isRemembered: remember
      }));
    }
  };

  // 设置用户信息
  const setUserInfo = (user) => {
    userInfo.value = {
      id: user.id || '',
      username: user.username || '',
      token: user.token || '',
    };

    // 登录成功后，根据记住状态保存数据
    const storage = isRemembered.value ? localStorage : sessionStorage;
    storage.setItem('user', JSON.stringify({
      userInfo: userInfo.value,
      isRemembered: isRemembered.value
    }));
  };

  // 清除用户信息
  const clearUserInfo = () => {
    userInfo.value = {
      id: '',
      username: '',
      token: '',
    };

    // 清除记住状态
    isRemembered.value = false;

    // 清除所有存储
    localStorage.removeItem('user');
    sessionStorage.removeItem('user');
  };

  // 初始化函数
  function init() {
    try {
      // 先检查localStorage
      let data = JSON.parse(localStorage.getItem('user') || 'null');
      if (data && data.userInfo && data.userInfo.token) {
        userInfo.value = data.userInfo;
        isRemembered.value = true;
        return;
      }

      // 再检查sessionStorage
      data = JSON.parse(sessionStorage.getItem('user') || 'null');
      if (data && data.userInfo && data.userInfo.token) {
        userInfo.value = data.userInfo;
        isRemembered.value = false;
      }
    } catch (error) {
      console.error('加载用户数据失败:', error);
    }
  }

  // 初始化
  init();

  return {
    userInfo,
    setUserInfo,
    clearUserInfo,
    isLoggedIn,
    setRememberStatus,
    isRemembered
  };
});
