<template>
  <div class="login-page">
    <div class="page-content">
      <!-- 左侧展示区 -->
      <div class="showcase-container">
        <div class="logo-section">
          <span class="logo-icon">🌿</span>
          <span class="logo-text">林下经济</span>
        </div>
        <div class="showcase-content">
          <h2 class="showcase-title">欢迎回到林下经济</h2>
          <p class="showcase-description">您的智能林下管理助手</p>
          <div class="showcase-features">
            <div class="feature-item">
              <div class="feature-icon">🌲</div>
              <div class="feature-text">科学规划种植面积</div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">💰</div>
              <div class="feature-text">提高经济产出效率</div>
            </div>
            <div class="feature-item">
              <div class="feature-icon">🔔</div>
              <div class="feature-text">智能预警与通知</div>
            </div>
          </div>
        </div>
        <div class="showcase-footer">
          <p>© 2025 林下经济管理系统 · 所有权利保留</p>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-container">
        <div class="back-button-container">
          <router-link to="/" class="back-button">
            <span>←</span> 返回主页
          </router-link>
        </div>

        <div class="form-header">
          <h1 class="title">账户登录</h1>
          <p class="subtitle">欢迎回来，请登录您的账户</p>
        </div>

        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label class="form-label">帐号信息</label>
            <input
              type="tel"
              class="form-input"
              placeholder="请输入 手机号码 / 用户名"
              v-model="formData.phoneNumber"
              :class="{ 'error-input': errors.phoneNumber }"
            >
            <p v-if="errors.phoneNumber" class="error-message">{{ errors.phoneNumber }}</p>
          </div>

          <div class="form-group">
            <label class="form-label">密码</label>
            <div class="password-input-container">
              <input
                :type="showPassword ? 'text' : 'password'"
                class="form-input"
                placeholder="请输入密码"
                v-model="formData.password"
                :class="{ 'error-input': errors.password }"
              >
              <span class="password-toggle" @click="togglePassword">
                {{ showPassword ? '👁️' : '👁️‍🗨️' }}
              </span>
            </div>
            <p v-if="errors.password" class="error-message">{{ errors.password }}</p>
          </div>

          <div class="login-options">
            <div class="remember-me">
  <input
    type="checkbox"
    id="remember"
    v-model="formData.remember"
    @change="handleRememberChange"
  >
  <label for="remember">记住我</label>
</div>
            <a href="#" class="forgot-password" @click.prevent="forgotPassword">忘记密码？</a>
          </div>

          <button type="submit" class="login-button" :disabled="isSubmitting">
            <span v-if="isSubmitting" class="spinner"></span>
            {{ isSubmitting ? '登录中...' : '登录' }}
          </button>

          <div class="other-login-methods">
            <p class="divider"><span>其他登录方式</span></p>
            <div class="login-methods">
              <button type="button" class="method-button" @click="loginWithMethod('wechat')">
                <span class="method-icon">微信</span>
              </button>
              <button type="button" class="method-button" @click="loginWithMethod('alipay')">
                <span class="method-icon">支付宝</span>
              </button>
              <button type="button" class="method-button" @click="loginWithMethod('sms')">
                <span class="method-icon">短信登录</span>
              </button>
            </div>
          </div>

          <div class="register-link">
            还没有账户？<a href="#" @click.prevent="goToRegister">立即注册</a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { loginApi } from '@/api/login';
import { useUserStore } from '@/stores/userStore';

const router = useRouter();
const userStore = useUserStore();

// 表单数据，初始化记住我状态
const formData = ref({
  phoneNumber: '',
  password: '',
  remember: userStore.isRemembered
});


// 错误信息
const errors = ref({
  phoneNumber: '',
  password: ''
});

// 状态管理
const isSubmitting = ref(false);
const showPassword = ref(false);

// 切换密码显示
const togglePassword = () => {
  showPassword.value = !showPassword.value;
};

// 表单验证
const validateForm = () => {
  let isValid = true;
  // 重置错误消息
  for (let key in errors.value) {
    errors.value[key] = '';
  }

  // 密码验证
  if (!formData.value.password) {
    errors.value.password = '请输入密码';
    isValid = false;
  }

  return isValid;
};

// 登录处理
const handleLogin = async() => {
  if (!validateForm()) return;

  isSubmitting.value = true;

  try {
    const result = await loginApi(formData.value);

    if (result.code === 1 || result.success) {
      // 先设置记住我状态，这影响后续的持久化行为
      userStore.setRememberStatus(formData.value.remember);
      console.log("记住我状态为:", formData.value.remember);


      // 设置用户信息 - 这会触发store的持久化逻辑
      userStore.setUserInfo({
        id: result.data.id,
        username: result.data.username,
        token: result.data.token,
      });

      ElMessage.success('登录成功');
      router.push('/');
    } else {
      ElMessage.error(result.message || '登录失败');
    }
  } catch (error) {
    console.error('Login error:', error);
    ElMessage.error(error.message || '登录失败，请检查网络连接');
  } finally {
    isSubmitting.value = false;
  }
};

// 添加到您的方法中
const handleRememberChange = () => {
  console.log("记住我状态变更为:", formData.value.remember);
  // 您可以在这里做一些其他操作，比如立即更新 store 中的状态
  userStore.setRememberStatus(formData.value.remember);
};

// 忘记密码
const forgotPassword = () => {
  router.push('/forgot-password');
};

// 第三方登录方式
const loginWithMethod = (method) => {
  console.log(`使用${method}登录`);

  // 根据不同的登录方式执行不同的操作
  switch(method) {
    case 'wechat':
      ElMessage({
        type: 'info',
        message: '正在跳转到微信登录...'
      });
      break;
    case 'alipay':
      ElMessage({
        type: 'info',
        message: '正在跳转到支付宝登录...'
      });
      break;
    case 'sms':
      router.push('/sms-login');
      break;
  }
};

// 前往注册
const goToRegister = () => {
  router.push('/register');
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background-color: #e5f8b9;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 30px;
}

.page-content {
  display: flex;
  width: 100%;
  max-width: 1200px;
  min-height: 700px;
  background-color: #FFFFFF;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

/* 左侧展示区样式 */
.showcase-container {
  flex: 1;
  background: linear-gradient(135deg, #556B2F, #7B904B);
  color: white;
  padding: 40px;
  display: flex;
  flex-direction: column;
  position: relative;
}

.logo-section {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
}

.logo-icon {
  font-size: 42px;
  margin-right: 16px;
}

.logo-text {
  font-size: 32px;
  font-weight: 600;
  letter-spacing: 1px;
}

.showcase-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.showcase-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 16px;
}

.showcase-description {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 40px;
  line-height: 1.5;
}

.showcase-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.feature-icon {
  font-size: 24px;
  background: rgba(255, 255, 255, 0.2);
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.feature-text {
  font-size: 16px;
  font-weight: 500;
}

.showcase-footer {
  margin-top: auto;
  font-size: 14px;
  opacity: 0.7;
}

/* 右侧登录表单样式 */
.login-container {
  flex: 1;
  padding: 40px 60px;
  overflow-y: auto;
  background-color: #FDFBF7;
  display: flex;
  flex-direction: column;
}

.back-button-container {
  text-align: left;
  margin-bottom: 30px;
}

.back-button {
  display: inline-flex;
  align-items: center;
  color: #556B2F;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.2s;
}

.back-button:hover {
  opacity: 0.8;
}

.back-button span {
  margin-right: 8px;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.title {
  font-size: 28px;
  color: #2B3F2B;
  margin-bottom: 10px;
  font-weight: 600;
}

.subtitle {
  font-size: 16px;
  color: #5A4A3A;
}

.form-group {
  margin-bottom: 25px;
  position: relative;
}

.form-label {
  display: block;
  font-size: 15px;
  color: #3C2A1A;
  margin-bottom: 10px;
  font-weight: 500;
}

.form-input {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #E8E0D0;
  border-radius: 10px;
  font-size: 15px;
  color: #2B3F2B;
  background-color: #ffffff;
  box-sizing: border-box;
  transition: all 0.3s;
}

.form-input:focus {
  border-color: #98C9A3;
  outline: none;
  box-shadow: 0 0 0 3px rgba(152, 201, 163, 0.2);
}

.error-input {
  border-color: #D75650;
}

.error-message {
  color: #D75650;
  font-size: 13px;
  margin-top: 6px;
  margin-bottom: 0;
}

.password-input-container {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  font-size: 18px;
  opacity: 0.6;
  transition: opacity 0.2s;
}

.password-toggle:hover {
  opacity: 1;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #5A4A3A;
  font-size: 14px;
}

.remember-me input[type="checkbox"] {
  width: 16px;
  height: 16px;
  accent-color: #556B2F;
}

.forgot-password {
  color: #556B2F;
  font-size: 14px;
  text-decoration: none;
  transition: all 0.2s;
}

.forgot-password:hover {
  text-decoration: underline;
}

.login-button {
  width: 100%;
  padding: 16px;
  background-color: #7B904B;
  border: none;
  border-radius: 10px;
  color: white;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
  box-shadow: 0 4px 12px rgba(123, 144, 75, 0.3);
  margin-bottom: 30px;
}

.login-button:hover:not(:disabled) {
  background-color: #556B2F;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(123, 144, 75, 0.4);
}

.login-button:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 4px 8px rgba(123, 144, 75, 0.3);
}

.login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.spinner {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid rgba(255,255,255,0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s linear infinite;
  margin-right: 10px;
  vertical-align: middle;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.other-login-methods {
  margin-bottom: 30px;
}

.divider {
  text-align: center;
  position: relative;
  color: #8A7A6A;
  font-size: 14px;
  margin: 20px 0;
}

.divider::before,
.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: calc(50% - 80px);
  height: 1px;
  background-color: #E8E0D0;
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.divider span {
  display: inline-block;
  padding: 0 15px;
  background-color: #FDFBF7;
  position: relative;
  z-index: 1;
}

.login-methods {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.method-button {
  flex: 1;
  max-width: 120px;
  padding: 10px 0;
  background-color: #F5F1E6;
  border: 1px solid #E8E0D0;
  border-radius: 8px;
  color: #5A4A3A;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.method-button:hover {
  background-color: #EEE9DD;
}

.method-icon {
  display: block;
  text-align: center;
}

.register-link {
  text-align: center;
  margin-top: auto;
  font-size: 15px;
  color: #5A4A3A;
  padding-top: 20px;
}

.register-link a {
  color: #556B2F;
  text-decoration: none;
  font-weight: 600;
}

.register-link a:hover {
  text-decoration: underline;
}

/* 响应式处理 */
@media (max-width: 1200px) {
  .page-content {
    max-width: 900px;
  }

  .showcase-container {
    padding: 30px;
  }

  .login-container {
    padding: 30px 40px;
  }
}

@media (max-width: 900px) {
  .page-content {
    flex-direction: column;
    max-width: 600px;
  }

  .showcase-container {
    padding: 30px;
    min-height: 300px;
  }

  .showcase-features {
    flex-direction: row;
    justify-content: space-between;
  }

  .feature-item {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .login-container {
    padding: 30px;
  }
}

@media (max-width: 600px) {
  .login-page {
    padding: 0;
  }

  .page-content {
    border-radius: 0;
    max-width: none;
    min-height: 100vh;
  }

  .showcase-container {
    display: none;
  }

  .login-methods {
    flex-direction: column;
    align-items: center;
  }

  .method-button {
    max-width: 100%;
    width: 100%;
  }
}
</style>
