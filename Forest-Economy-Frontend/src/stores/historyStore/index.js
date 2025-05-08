// 历史记录存储
import { ref } from 'vue';
import { defineStore } from 'pinia';

export const useHistoryStore = defineStore('history', () => {
  // 历史记录，最多存储5条
  const chatHistory = ref([]);

  // 添加历史记录
  const addHistory = (history) => {
    // 检查是否已经存在相同ID的历史记录
    const existingIndex = chatHistory.value.findIndex(item => item.id === history.id);

    if (existingIndex >= 0) {
      // 更新现有记录
      chatHistory.value[existingIndex] = history;
    } else {
      // 如果超过5条，删除最早的一条
      if (chatHistory.value.length >= 5) {
        chatHistory.value.shift();
      }

      // 添加新的历史记录
      chatHistory.value.push(history);
    }

    // 保存到本地存储
    // saveToLocalStorage();
  };

  // 删除历史记录
  const removeHistory = (id) => {
    chatHistory.value = chatHistory.value.filter(item => item.id !== id);
    saveToLocalStorage();
  };

  // 清空历史记录
  const clearHistory = () => {
    chatHistory.value = [];
    saveToLocalStorage();
  };

  // 保存到本地存储
  const saveToLocalStorage = () => {
    localStorage.setItem('forest_economy_history', JSON.stringify(chatHistory.value));
  };

  // 从本地存储加载
  const loadFromLocalStorage = () => {
    const saved = localStorage.getItem('forest_economy_history');
    if (saved) {
      try {
        chatHistory.value = JSON.parse(saved);
      } catch (e) {
        console.error('加载历史记录失败:', e);
        chatHistory.value = [];
      }
    }
  };

  // 初始加载
  loadFromLocalStorage();

  return {
    chatHistory,
    addHistory,
    removeHistory,
    clearHistory,
    loadFromLocalStorage
  };
});
