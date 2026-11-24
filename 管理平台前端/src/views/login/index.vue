<template>
  <div class="login-container">
    <!-- 动态背景 -->
    <div class="animated-bg">
      <div class="gradient-orb orb-1"></div>
      <div class="gradient-orb orb-2"></div>
      <div class="gradient-orb orb-3"></div>
      <div class="particles">
        <span v-for="n in 20" :key="n" class="particle"></span>
      </div>
    </div>

    <div class="login-wrapper">
      <!-- 左侧品牌展示 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="logo-wrapper">
            <div class="logo-glow"></div>
            <img src="@/assets/logo.svg" alt="logo" class="logo" />
          </div>
          <h1 class="brand-title">
            <span class="gradient-text">智慧养老</span>
            <span class="brand-subtitle">管理平台</span>
          </h1>
          <p class="brand-desc">Elderly Care Management System</p>
          
          <div class="feature-cards">
            <div class="feature-card" v-for="(feature, index) in features" :key="index" :style="{ animationDelay: `${index * 0.1}s` }">
              <div class="feature-icon" :style="{ background: feature.gradient }">
                <el-icon :size="24">
                  <component :is="feature.icon" />
                </el-icon>
              </div>
              <div class="feature-info">
                <span class="feature-name">{{ feature.name }}</span>
                <span class="feature-desc">{{ feature.desc }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单 -->
      <div class="form-section">
        <div class="form-card">
          <!-- 登录表单 -->
          <template v-if="!showRegister">
            <div class="form-header">
              <h2>欢迎回来</h2>
              <p>请登录您的账户以继续</p>
            </div>

            <el-form
              ref="loginFormRef"
              :model="loginForm"
              :rules="loginRules"
              class="login-form"
              @keyup.enter="handleLogin"
            >
              <el-form-item prop="username">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><User /></el-icon>
                  <el-input
                    v-model="loginForm.username"
                    placeholder="请输入用户名"
                    size="large"
                    class="modern-input"
                  />
                </div>
              </el-form-item>

              <el-form-item prop="password">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><Lock /></el-icon>
                  <el-input
                    v-model="loginForm.password"
                    type="password"
                    placeholder="请输入密码"
                    size="large"
                    show-password
                    class="modern-input"
                  />
                </div>
              </el-form-item>

              <div class="form-options">
                <el-checkbox v-model="rememberMe" class="remember-checkbox">
                  <span class="checkbox-label">记住我</span>
                </el-checkbox>
              </div>

              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  :loading="loading"
                  class="login-btn"
                  @click="handleLogin"
                >
                  <span class="btn-text">登 录</span>
                  <el-icon class="btn-icon"><ArrowRight /></el-icon>
                </el-button>
              </el-form-item>
            </el-form>

            <div class="form-footer">
              <div class="register-link">
                <span>还没有账号？</span>
                <a href="#" @click.prevent="showRegister = true">立即注册</a>
              </div>
            </div>
          </template>

          <!-- 注册表单 -->
          <template v-else>
            <div class="form-header">
              <h2>注册账号</h2>
              <p>创建您的管理账户</p>
            </div>

            <el-form
              ref="registerFormRef"
              :model="registerForm"
              :rules="registerRules"
              class="login-form"
            >
              <el-form-item prop="username">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><User /></el-icon>
                  <el-input
                    v-model="registerForm.username"
                    placeholder="请输入用户名"
                    size="large"
                    class="modern-input"
                  />
                </div>
              </el-form-item>

              <el-form-item prop="name">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><UserFilled /></el-icon>
                  <el-input
                    v-model="registerForm.name"
                    placeholder="请输入姓名"
                    size="large"
                    class="modern-input"
                  />
                </div>
              </el-form-item>

              <el-form-item prop="password">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><Lock /></el-icon>
                  <el-input
                    v-model="registerForm.password"
                    type="password"
                    placeholder="请输入密码"
                    size="large"
                    show-password
                    class="modern-input"
                  />
                </div>
              </el-form-item>

              <el-form-item prop="confirmPassword">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><Lock /></el-icon>
                  <el-input
                    v-model="registerForm.confirmPassword"
                    type="password"
                    placeholder="请确认密码"
                    size="large"
                    show-password
                    class="modern-input"
                  />
                </div>
              </el-form-item>

              <el-form-item prop="role">
                <div class="input-wrapper">
                  <el-icon class="input-icon"><UserFilled /></el-icon>
                  <el-select
                    v-model="registerForm.role"
                    placeholder="请选择角色"
                    size="large"
                    class="modern-input role-select"
                  >
                    <el-option label="管理人员" value="admin" />
                    <el-option label="运营成员" value="operator" />
                  </el-select>
                </div>
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  size="large"
                  :loading="registerLoading"
                  class="login-btn"
                  @click="handleRegister"
                >
                  <span class="btn-text">注 册</span>
                  <el-icon class="btn-icon"><ArrowRight /></el-icon>
                </el-button>
              </el-form-item>
            </el-form>

            <div class="form-footer">
              <div class="register-link">
                <span>已有账号？</span>
                <a href="#" @click.prevent="showRegister = false">立即登录</a>
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>

    <!-- 底部版权 -->
    <div class="login-footer">
      <p>© 2024 智慧养老服务平台. All rights reserved.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock, ArrowRight, UserFilled, DataAnalysis, Service, FirstAidKit, ChatDotRound } from '@element-plus/icons-vue'
import { login, register } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const registerFormRef = ref(null)
const loading = ref(false)
const registerLoading = ref(false)
const rememberMe = ref(false)
const showRegister = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  name: '',
  password: '',
  confirmPassword: '',
  role: 'operator'
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名长度不能小于3位', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const features = [
  { 
    icon: 'UserFilled', 
    name: '用户管理', 
    desc: '智能化用户档案管理',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  { 
    icon: 'Service', 
    name: '服务管理', 
    desc: '全方位服务跟踪',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  { 
    icon: 'DataAnalysis', 
    name: '数据分析', 
    desc: '可视化数据报表',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  { 
    icon: 'FirstAidKit', 
    name: '健康监测', 
    desc: '实时健康数据追踪',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
]

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.loginAction(loginForm)
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } catch (error) {
        console.error('登录失败', error)
      } finally {
        loading.value = false
      }
    }
  })
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      registerLoading.value = true
      try {
        const { confirmPassword, ...registerData } = registerForm
        console.log('注册数据:', registerData)
        const result = await register(registerData)
        console.log('注册结果:', result)
        if (result === true) {
          ElMessage.success('注册成功，请登录')
          showRegister.value = false
          // 清空表单
          registerForm.username = ''
          registerForm.name = ''
          registerForm.password = ''
          registerForm.confirmPassword = ''
          registerForm.role = 'operator'
        } else {
          ElMessage.error('注册失败，请重试')
        }
      } catch (error) {
        console.error('注册失败', error)
        ElMessage.error(error.message || '注册失败，请检查网络连接')
      } finally {
        registerLoading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ec 100%);
}

// 动态背景
.animated-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 0;
  pointer-events: none;
}

.gradient-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.5;
  animation: float 20s infinite ease-in-out;
}

.orb-1 {
  width: 600px;
  height: 600px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  top: -200px;
  right: -100px;
  animation-delay: 0s;
}

.orb-2 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  bottom: -150px;
  left: -100px;
  animation-delay: -5s;
}

.orb-3 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  top: 50%;
  left: 30%;
  animation-delay: -10s;
}

.particles {
  position: absolute;
  width: 100%;
  height: 100%;
}

.particle {
  position: absolute;
  width: 4px;
  height: 4px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  animation: particle-float 15s infinite;
}

@for $i from 1 through 20 {
  .particle:nth-child(#{$i}) {
    left: random(100) * 1%;
    top: random(100) * 1%;
    animation-delay: random(15) * -1s;
    animation-duration: 15s + random(10);
  }
}

@keyframes particle-float {
  0%, 100% {
    transform: translateY(0) translateX(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 1;
  }
  100% {
    transform: translateY(-100vh) translateX(50px);
    opacity: 0;
  }
}

// 主内容区
.login-wrapper {
  flex: 1;
  display: flex;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  padding: 40px;
  gap: 60px;
  align-items: center;
}

// 品牌区域
.brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.brand-content {
  text-align: center;
  animation: fadeInUp 0.8s ease;
}

.logo-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 30px;
}

.logo-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  filter: blur(30px);
  opacity: 0.3;
  animation: pulse 3s infinite;
}

.logo {
  width: 80px;
  height: 80px;
  position: relative;
  z-index: 1;
  filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.1));
}

.brand-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-subtitle {
  color: #1e293b;
  font-size: 36px;
}

.brand-desc {
  font-size: 16px;
  color: #64748b;
  margin: 0 0 40px;
  letter-spacing: 2px;
}

// 功能卡片
.feature-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  max-width: 500px;
  margin: 0 auto;
}

.feature-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  animation: fadeInUp 0.6s ease backwards;
  border: 1px solid rgba(255, 255, 255, 0.5);

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
  }
}

.feature-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.feature-info {
  text-align: left;
  display: flex;
  flex-direction: column;
}

.feature-name {
  font-weight: 600;
  color: #1e293b;
  font-size: 15px;
}

.feature-desc {
  font-size: 12px;
  color: #64748b;
  margin-top: 3px;
}

// 表单区域
.form-section {
  width: 420px;
  animation: fadeInUp 0.8s ease 0.2s backwards;
}

.form-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.form-header {
  text-align: center;
  margin-bottom: 30px;

  h2 {
    font-size: 28px;
    font-weight: 700;
    color: #1e293b;
    margin: 0 0 8px;
  }

  p {
    font-size: 14px;
    color: #64748b;
    margin: 0;
  }
}

// 输入框样式
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 15px;
  color: #94a3b8;
  font-size: 18px;
  z-index: 1;
}

.modern-input {
  :deep(.el-input__wrapper) {
    padding-left: 45px;
    border-radius: 12px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    border: 2px solid #e2e8f0;
    transition: all 0.3s ease;

    &:hover, &:focus {
      border-color: #667eea;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }

    &.is-focus {
      border-color: #667eea;
      box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }
  }

  :deep(.el-input__inner) {
    height: 50px;
    font-size: 15px;

    &::placeholder {
      color: #94a3b8;
    }
  }
}

.role-select {
  width: 100%;
}

// 表单选项
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
}

.remember-checkbox {
  :deep(.el-checkbox__label) {
    padding-left: 8px;
  }

  :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    background-color: #667eea;
    border-color: #667eea;
  }
}

.checkbox-label {
  font-size: 14px;
  color: #64748b;
}

// 登录按钮
.login-btn {
  width: 100%;
  height: 52px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 14px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(102, 126, 234, 0.5);
  }

  &:active {
    transform: translateY(0);
  }

  .btn-icon {
    transition: transform 0.3s ease;
  }

  &:hover .btn-icon {
    transform: translateX(4px);
  }
}

// 表单底部
.form-footer {
  margin-top: 30px;
}

.divider {
  display: flex;
  align-items: center;
  margin-bottom: 20px;

  &::before,
  &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: linear-gradient(90deg, transparent, #e2e8f0, transparent);
  }

  span {
    padding: 0 15px;
    font-size: 12px;
    color: #94a3b8;
  }
}

.demo-accounts {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 20px;
}

.demo-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 25px;
  font-size: 13px;
  border: 1px solid #e2e8f0;

  &.admin {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
  }

  &.operator {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
    color: #fff;
  }

  .el-icon {
    color: inherit;
  }
}

.register-link {
  text-align: center;
  font-size: 14px;
  color: #64748b;

  a {
    color: #667eea;
    text-decoration: none;
    font-weight: 600;
    margin-left: 5px;

    &:hover {
      color: #764ba2;
    }
  }
}

// 页面底部
.login-footer {
  text-align: center;
  padding: 20px;
  z-index: 1;

  p {
    font-size: 12px;
    color: #94a3b8;
    margin: 0;
  }
}

// 动画
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.05);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.95);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.3;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 0.5;
    transform: translate(-50%, -50%) scale(1.1);
  }
}

// 响应式
@media (max-width: 1024px) {
  .login-wrapper {
    flex-direction: column;
    gap: 40px;
    padding: 20px;
  }

  .brand-section {
    order: -1;
  }

  .form-section {
    width: 100%;
    max-width: 420px;
  }

  .feature-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .brand-title {
    font-size: 36px;
  }

  .brand-subtitle {
    font-size: 28px;
  }
}

@media (max-width: 640px) {
  .form-card {
    padding: 30px 20px;
  }

  .feature-cards {
    grid-template-columns: 1fr;
  }

  .gradient-orb {
    display: none;
  }

  .demo-accounts {
    flex-direction: column;
  }
}
</style>
