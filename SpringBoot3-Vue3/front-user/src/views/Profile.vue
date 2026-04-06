<template>
  <div class="profile-container">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-left">
        <h1 class="logo">用户中心</h1>
      </div>
      <div class="header-right">
        <span class="user-info">{{ user.username }}</span>
        <el-button type="text" @click="handleLogout">退出登录</el-button>
      </div>
    </header>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 侧边栏 -->
      <aside class="sidebar">
        <ul class="sidebar-menu">
          <li class="menu-item active">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </li>
          <li class="menu-item">
            <el-icon><Setting /></el-icon>
            <span>账户设置</span>
          </li>
          <li class="menu-item">
            <el-icon><Bell /></el-icon>
            <span>消息通知</span>
          </li>
          <li class="menu-item">
            <el-icon><Help /></el-icon>
            <span>帮助中心</span>
          </li>
        </ul>
      </aside>

      <!-- 内容区 -->
      <main class="content">
        <div class="content-header">
          <h2>个人信息</h2>
        </div>
        <div class="profile-card">
          <div class="profile-avatar">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20profile%20picture&image_size=square" alt="头像" />
          </div>
          <div class="profile-info">
            <el-form :model="userInfo" label-width="120px">
              <el-form-item label="用户名">
                <el-input v-model="userInfo.username" disabled />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userInfo.email" />
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userInfo.phone" />
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group v-model="userInfo.gender">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="生日">
                <el-date-picker v-model="userInfo.birthday" type="date" placeholder="选择生日" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdate">保存修改</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Setting, Bell, Help } from '@element-plus/icons-vue'

const router = useRouter()
const user = ref(JSON.parse(localStorage.getItem('user') || '{"username": "用户"}'))

const userInfo = reactive({
  username: user.value.username,
  email: '',
  phone: '',
  gender: '男',
  birthday: ''
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  ElMessage.success('退出登录成功')
  router.push('/login')
}

const handleUpdate = () => {
  ElMessage.success('保存成功')
}

onMounted(() => {
  // 模拟获取用户信息
  setTimeout(() => {
    userInfo.email = 'user@example.com'
    userInfo.phone = '13800138000'
    userInfo.birthday = '1990-01-01'
  }, 500)
})
</script>

<style scoped>
.profile-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.header {
  height: 60px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  font-size: 20px;
  font-weight: 600;
  color: #667eea;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info {
  font-size: 14px;
  color: #333;
}

.main-content {
  flex: 1;
  display: flex;
  margin: 20px;
  gap: 20px;
}

.sidebar {
  width: 200px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px 0;
}

.sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.menu-item:hover {
  background-color: #f5f7fa;
  color: #667eea;
}

.menu-item.active {
  background-color: #f0f2ff;
  color: #667eea;
  border-left-color: #667eea;
}

.content {
  flex: 1;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.content-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
}

.content-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.profile-card {
  display: flex;
  gap: 40px;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.profile-info {
  flex: 1;
}

@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    order: 2;
  }

  .sidebar-menu {
    display: flex;
    overflow-x: auto;
  }

  .menu-item {
    white-space: nowrap;
    border-left: none;
    border-bottom: 3px solid transparent;
  }

  .menu-item.active {
    border-left-color: transparent;
    border-bottom-color: #667eea;
  }

  .profile-card {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
}
</style>