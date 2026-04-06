<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="200px" class="layout-aside">
      <div class="logo">
        <h1>管理系统</h1>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="layout-menu"
        @select="handleMenuSelect"
        router
        :collapse-transition="false"
      >
        <el-menu-item index="/">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/system/role">
            <el-icon><Avatar /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/system/menu">
            <el-icon><Menu /></el-icon>
            <span>菜单管理</span>
          </el-menu-item>
          <el-menu-item index="/system/dept">
            <el-icon><OfficeBuilding /></el-icon>
            <span>部门管理</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-button
            type="text"
            class="menu-toggle"
            @click="toggleCollapse"
          >
            <el-icon><Menu /></el-icon>
          </el-button>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" :src="userAvatar"></el-avatar>
              <span>{{ userInfo.username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 内容区域 -->
      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, Setting, User, Avatar, Menu, OfficeBuilding, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const isCollapse = ref(false)

// 计算当前激活的菜单
const activeMenu = computed(() => {
  const path = route.path
  if (path === '/') return path
  return path.startsWith('/system') ? 'system' : path
})

// 处理菜单选择
const handleMenuSelect = (key, keyPath) => {
  console.log('菜单选择:', key, keyPath)
}

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 用户信息
const userInfo = ref({
  username: '管理员'
})

const userAvatar = ref('')

// 退出登录
const handleLogout = () => {
  // 清除本地存储
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('退出登录成功')
  router.push('/login')
}

// 初始化用户信息
onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    userInfo.value = JSON.parse(userStr)
  }
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.layout-aside {
  background-color: #001529;
  color: #fff;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #1f2d3d;
}

.logo h1 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.layout-menu {
  border-right: none;
  background-color: #001529;
  color: #fff;
}

.el-menu-item {
  color: rgba(255, 255, 255, 0.65);
}

.el-menu-item:hover {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.1);
}

.el-menu-item.is-active {
  color: #1890ff;
  background-color: rgba(24, 144, 255, 0.2);
}

.el-sub-menu__title {
  color: rgba(255, 255, 255, 0.65);
}

.el-sub-menu__title:hover {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.1);
}

.layout-header {
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.menu-toggle {
  font-size: 18px;
  margin-right: 20px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 10px;
  height: 40px;
  border-radius: 20px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.user-info span {
  margin-left: 10px;
  font-size: 14px;
}

.layout-main {
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>