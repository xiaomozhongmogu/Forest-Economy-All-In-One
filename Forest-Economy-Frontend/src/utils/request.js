import axios from 'axios'
import { useUserStore } from '@/stores/userStore';

const request = axios.create({
  baseURL: '/api',
  // baseURL: 'http://localhost:8080',
  timeout: 1000,
})

request.interceptors.request.use(config => {
  const loginUser = JSON.parse(localStorage.getItem('loginUser'));
  if (loginUser && loginUser.token) {
    config.headers.token = loginUser.token;
  }
  return config;
}, error => {
  return Promise.reject(error);
});


request.interceptors.response.use(response => {
  return response.data;
}, error => {
  if (error.response.status === 401) {
    // 401 Unauthorized
    useUserStore().clearUserInfo();
  }
  return Promise.reject(error);
});

export default request;
