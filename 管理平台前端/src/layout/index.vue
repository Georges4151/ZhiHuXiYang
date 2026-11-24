<template>
  <div class="layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '72px' : '260px'" class="aside">
        <div class="logo-section">
          <div class="logo-glow"></div>
          <div class="logo">
            <img src="@/assets/logo.svg" alt="logo" />
            <span v-show="!isCollapse" class="logo-text">智慧养老平台</span>
          </div>
          <div v-show="!isCollapse" class="logo-subtitle">Elderly Care System</div>
        </div>
        
        <div class="menu-wrapper">
          <el-menu
            :default-active="activeMenu"
            :collapse="isCollapse"
            :collapse-transition="false"
            class="menu"
          >
            <el-menu-item index="/dashboard" class="menu-item" @click="router.push('/dashboard')">
              <div class="menu-icon-wrapper" style="--icon-color: #667eea">
                <el-icon><DataAnalysis /></el-icon>
              </div>
              <span class="menu-text">数据概览</span>
            </el-menu-item>
            
            <el-menu-item index="/users" class="menu-item" @click="router.push('/users')">
              <div class="menu-icon-wrapper" style="--icon-color: #f093fb">
                <el-icon><User /></el-icon>
              </div>
              <span class="menu-text">用户管理</span>
            </el-menu-item>
            
            <el-menu-item index="/services" class="menu-item" @click="router.push('/services')">
              <div class="menu-icon-wrapper" style="--icon-color: #4facfe">
                <el-icon><Service /></el-icon>
              </div>
              <span class="menu-text">服务管理</span>
            </el-menu-item>
            
            <el-menu-item index="/orders" class="menu-item" @click="router.push('/orders')">
              <div class="menu-icon-wrapper" style="--icon-color: #43e97b">
                <el-icon><List /></el-icon>
              </div>
              <span class="menu-text">订单管理</span>
            </el-menu-item>
            
            <el-menu-item index="/activities" class="menu-item" @click="router.push('/activities')">
              <div class="menu-icon-wrapper" style="--icon-color: #fa709a">
                <el-icon><Calendar /></el-icon>
              </div>
              <span class="menu-text">活动管理</span>
            </el-menu-item>
            
            <el-menu-item index="/news" class="menu-item" @click="router.push('/news')">
              <div class="menu-icon-wrapper" style="--icon-color: #fee140">
                <el-icon><Document /></el-icon>
              </div>
              <span class="menu-text">新闻管理</span>
            </el-menu-item>
            
            <el-menu-item index="/health" class="menu-item" @click="router.push('/health')">
              <div class="menu-icon-wrapper" style="--icon-color: #30cfd0">
                <el-icon><FirstAidKit /></el-icon>
              </div>
              <span class="menu-text">健康数据</span>
            </el-menu-item>
            
            <el-menu-item index="/emergency" class="menu-item" @click="router.push('/emergency')">
              <div class="menu-icon-wrapper" style="--icon-color: #ff6b6b">
                <el-icon><PhoneFilled /></el-icon>
              </div>
              <span class="menu-text">紧急呼叫</span>
            </el-menu-item>
            
            <el-menu-item index="/feedback" class="menu-item" @click="router.push('/feedback')">
              <div class="menu-icon-wrapper" style="--icon-color: #a8edea">
                <el-icon><ChatDotSquare /></el-icon>
              </div>
              <span class="menu-text">意见反馈</span>
            </el-menu-item>
          </el-menu>
        </div>
        
        <!-- 侧边栏底部 -->
        <div class="aside-footer" v-show="!isCollapse">
          <div class="system-status">
            <div class="status-dot online"></div>
            <span>系统运行正常</span>
          </div>
          <div class="version">v2.0.0</div>
        </div>
      </el-aside>
      
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <div class="collapse-btn" @click="toggleCollapse">
              <el-icon :size="20">
                <Fold v-if="!isCollapse" />
                <Expand v-else />
              </el-icon>
            </div>
            <el-breadcrumb separator="/" class="breadcrumb">
              <el-breadcrumb-item :to="{ path: '/' }">
                <el-icon><HomeFilled /></el-icon>
                <span>首页</span>
              </el-breadcrumb-item>
              <el-breadcrumb-item v-if="currentRoute?.meta?.title">
                {{ currentRoute.meta.title }}
              </el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <!-- 通知图标 -->
            <div class="header-icon-btn">
              <el-badge :value="3" class="notification-badge">
                <el-icon :size="20"><Bell /></el-icon>
              </el-badge>
            </div>
            
            <!-- 全屏按钮 -->
            <div class="header-icon-btn" @click="toggleFullscreen">
              <el-icon :size="20"><FullScreen /></el-icon>
            </div>
            
            <!-- 用户菜单 -->
            <el-dropdown @command="handleCommand" class="user-dropdown">
              <div class="user-info">
                <div class="avatar-wrapper">
                  <el-avatar :size="36" :src="userAvatar" class="user-avatar">
                    <el-icon><UserFilled /></el-icon>
                  </el-avatar>
                  <div class="online-status"></div>
                </div>
                <div class="user-meta" v-show="!isCollapse">
                  <span class="username">{{ userStore.userInfo?.name || '管理员' }}</span>
                  <span class="user-role">超级管理员</span>
                </div>
                <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu class="user-dropdown-menu">
                  <div class="dropdown-header">
                    <el-avatar :size="48" :src="userAvatar">
                      <el-icon><UserFilled /></el-icon>
                    </el-avatar>
                    <div class="dropdown-user-info">
                      <span class="dropdown-username">{{ userStore.userInfo?.name || '管理员' }}</span>
                      <span class="dropdown-email">admin@elderly.com</span>
                    </div>
                  </div>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    <span>个人中心</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="settings">
                    <el-icon><Setting /></el-icon>
                    <span>系统设置</span>
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout" class="logout-item">
                    <el-icon><SwitchButton /></el-icon>
                    <span>退出登录</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <!-- 主内容区 -->
        <el-main class="main">
          <div class="main-content">
            <router-view v-slot="{ Component }">
              <transition name="page" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'
import { 
  DataAnalysis, User, Service, List, Calendar, Document, 
  FirstAidKit, PhoneFilled, ChatDotSquare, Fold, Expand,
  ArrowDown, UserFilled, HomeFilled, Bell, FullScreen, 
  Lock, Setting, SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const activeMenu = computed(() => route.path)
const currentRoute = computed(() => route)
const userAvatar = computed(() => userStore.userInfo?.avatar || '')

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        await userStore.logoutAction()
        router.push('/login')
      }).catch(() => {})
      break
  }
}
</script>

<style lang="scss" scoped>
.layout {
  width: 100%;
  height: 100vh;
  background: #f8fafc;
}

// 侧边栏
.aside {
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
      radial-gradient(circle at 20% 50%, rgba(102, 126, 234, 0.1) 0%, transparent 50%),
      radial-gradient(circle at 80% 80%, rgba(118, 75, 162, 0.1) 0%, transparent 50%);
    pointer-events: none;
  }
}

// Logo区域
.logo-section {
  padding: 24px 20px;
  position: relative;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.logo-glow {
  position: absolute;
  top: 50%;
  left: 36px;
  transform: translate(-50%, -50%);
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  filter: blur(20px);
  opacity: 0.3;
}

.logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  position: relative;
  z-index: 1;
  
  img {
    width: 40px;
    height: 40px;
    filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
  }
}

.logo-text {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(135deg, #fff 0%, #94a3b8 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  white-space: nowrap;
}

.logo-subtitle {
  text-align: center;
  font-size: 11px;
  color: rgba(148, 163, 184, 0.6);
  margin-top: 6px;
  letter-spacing: 1px;
  text-transform: uppercase;
}

// 菜单区域
.menu-wrapper {
  flex: 1;
  padding: 16px 12px;
  overflow-y: auto;
  overflow-x: hidden;
}

.menu {
  border-right: none;
  background: transparent;
  
  :deep(.el-menu-item) {
    height: 52px;
    margin: 4px 0;
    border-radius: 12px;
    color: rgba(255, 255, 255, 0.6);
    transition: all 0.3s ease;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      left: 0;
      top: 0;
      bottom: 0;
      width: 3px;
      background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
      transform: scaleY(0);
      transition: transform 0.3s ease;
    }
    
    &:hover {
      background: rgba(255, 255, 255, 0.05);
      color: #fff;
      
      .menu-icon-wrapper {
        transform: scale(1.1);
      }
    }
    
    &.is-active {
      background: linear-gradient(90deg, rgba(102, 126, 234, 0.2) 0%, rgba(118, 75, 162, 0.1) 100%);
      color: #fff;
      
      &::before {
        transform: scaleY(1);
      }
      
      .menu-icon-wrapper {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: #fff;
        box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
      }
    }
  }
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-icon-wrapper {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.08);
  color: var(--icon-color, #fff);
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.menu-text {
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
}

// 侧边栏底部
.aside-footer {
  padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.system-status {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  margin-bottom: 8px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  
  &.online {
    background: #10b981;
    box-shadow: 0 0 8px #10b981;
    animation: pulse 2s infinite;
  }
}

.version {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.3);
  text-align: center;
}

// 顶部导航
.header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(226, 232, 240, 0.6);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #64748b;
  transition: all 0.3s ease;
  background: #f1f5f9;
  
  &:hover {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    transform: scale(1.05);
  }
}

.breadcrumb {
  :deep(.el-breadcrumb__item) {
    .el-breadcrumb__inner {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #64748b;
      font-weight: 500;
      
      &.is-link {
        color: #667eea;
        
        &:hover {
          color: #764ba2;
        }
      }
    }
    
    &:last-child .el-breadcrumb__inner {
      color: #1e293b;
      font-weight: 600;
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon-btn {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #64748b;
  transition: all 0.3s ease;
  background: #f8fafc;
  
  &:hover {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  }
}

.notification-badge {
  :deep(.el-badge__content) {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    border: none;
  }
}

// 用户下拉菜单
.user-dropdown {
  margin-left: 8px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 6px 12px 6px 6px;
  border-radius: 12px;
  transition: all 0.3s ease;
  
  &:hover {
    background: #f1f5f9;
  }
}

.avatar-wrapper {
  position: relative;
}

.user-avatar {
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
}

.online-status {
  position: absolute;
  bottom: 2px;
  right: 2px;
  width: 10px;
  height: 10px;
  background: #10b981;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 0 0 2px #10b981;
  animation: pulse 2s infinite;
}

.user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.username {
  font-size: 14px;
  font-weight: 600;
  color: #1e293b;
}

.user-role {
  font-size: 11px;
  color: #64748b;
}

.dropdown-arrow {
  color: #94a3b8;
  transition: transform 0.3s ease;
}

.user-info:hover .dropdown-arrow {
  transform: rotate(180deg);
}

// 下拉菜单样式
:deep(.user-dropdown-menu) {
  padding: 8px;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(226, 232, 240, 0.6);
  min-width: 240px;
}

.dropdown-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  margin-bottom: 8px;
  border-bottom: 1px solid #f1f5f9;
}

.dropdown-user-info {
  display: flex;
  flex-direction: column;
}

.dropdown-username {
  font-weight: 600;
  color: #1e293b;
  font-size: 14px;
}

.dropdown-email {
  font-size: 12px;
  color: #64748b;
}

:deep(.el-dropdown-menu__item) {
  padding: 10px 12px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: #475569;
  transition: all 0.2s ease;
  
  &:hover {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
    color: #667eea;
  }
  
  .el-icon {
    font-size: 16px;
  }
  
  &.logout-item {
    color: #ef4444;
    
    &:hover {
      background: rgba(239, 68, 68, 0.1);
      color: #ef4444;
    }
  }
}

// 主内容区
.main {
  background: #f8fafc;
  padding: 24px;
  overflow-y: auto;
}

.main-content {
  min-height: calc(100vh - 112px);
}

// 页面过渡动画
.page-enter-active,
.page-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.page-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.page-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

// 动画
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

// 滚动条美化
.menu-wrapper::-webkit-scrollbar {
  width: 4px;
}

.menu-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.menu-wrapper::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}

.menu-wrapper::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.2);
}
</style>