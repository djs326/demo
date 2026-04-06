<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>用户管理</h2>
          <el-button type="primary" @click="handleAddUser">
            <el-icon><Plus /></el-icon>
            <span>添加用户</span>
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="启用" value="1"></el-option>
            <el-option label="禁用" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            <span>搜索</span>
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            <span>重置</span>
          </el-button>
        </el-form-item>
      </el-form>
      
      <!-- 用户列表 -->
      <el-table :data="userList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="nickname" label="昵称"></el-table-column>
        <el-table-column prop="email" label="邮箱"></el-table-column>
        <el-table-column prop="phone" label="手机号"></el-table-column>
        <el-table-column prop="deptName" label="部门"></el-table-column>
        <el-table-column prop="roleName" label="角色"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch v-model="scope.row.status" @change="handleStatusChange(scope.row)" active-value="1" inactive-value="0"></el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditUser(scope.row)">
              <el-icon><Edit /></el-icon>
              <span>编辑</span>
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteUser(scope.row.id)">
              <el-icon><Delete /></el-icon>
              <span>删除</span>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!formData.id">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="formData.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-select v-model="formData.deptId" placeholder="请选择部门">
            <el-option v-for="dept in deptList" :key="dept.id" :label="dept.name" :value="dept.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="formData.roleId" placeholder="请选择角色">
            <el-option v-for="role in roleList" :key="role.id" :label="role.name" :value="role.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="formData.status" active-value="1" inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete } from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  username: '',
  status: ''
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 用户列表
const userList = ref([])

// 部门列表
const deptList = ref([
  { id: 1, name: '技术部' },
  { id: 2, name: '产品部' },
  { id: 3, name: '销售部' }
])

// 角色列表
const roleList = ref([
  { id: 1, name: '管理员' },
  { id: 2, name: '普通用户' },
  { id: 3, name: '访客' }
])

// 对话框状态
const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')

// 表单数据
const formData = reactive({
  id: '',
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  deptId: '',
  roleId: '',
  status: '1'
})

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  deptId: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ],
  roleId: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const formRef = ref(null)

// 初始化数据
onMounted(() => {
  fetchUserList()
})

// 获取用户列表
const fetchUserList = () => {
  // 模拟数据
  userList.value = [
    {
      id: 1,
      username: 'admin',
      nickname: '管理员',
      email: 'admin@example.com',
      phone: '13800138000',
      deptName: '技术部',
      roleName: '管理员',
      status: '1',
      createTime: '2024-01-01 00:00:00'
    },
    {
      id: 2,
      username: 'user1',
      nickname: '用户1',
      email: 'user1@example.com',
      phone: '13800138001',
      deptName: '产品部',
      roleName: '普通用户',
      status: '1',
      createTime: '2024-01-02 00:00:00'
    },
    {
      id: 3,
      username: 'user2',
      nickname: '用户2',
      email: 'user2@example.com',
      phone: '13800138002',
      deptName: '销售部',
      roleName: '普通用户',
      status: '0',
      createTime: '2024-01-03 00:00:00'
    }
  ]
  pagination.total = userList.value.length
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchUserList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.username = ''
  searchForm.status = ''
  pagination.currentPage = 1
  fetchUserList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchUserList()
}

// 页码变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  fetchUserList()
}

// 添加用户
const handleAddUser = () => {
  dialogTitle.value = '添加用户'
  Object.assign(formData, {
    id: '',
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
    deptId: '',
    roleId: '',
    status: '1'
  })
  dialogVisible.value = true
}

// 编辑用户
const handleEditUser = (row) => {
  dialogTitle.value = '编辑用户'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 删除用户
const handleDeleteUser = (id) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟删除
    userList.value = userList.value.filter(item => item.id !== id)
    pagination.total = userList.value.length
    ElMessage.success('删除成功')
  }).catch(() => {
    // 取消删除
  })
}

// 状态变更
const handleStatusChange = (row) => {
  ElMessage.success(`用户 ${row.username} 状态已更新为 ${row.status === '1' ? '启用' : '禁用'}`)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (formData.id) {
      // 编辑
      const index = userList.value.findIndex(item => item.id === formData.id)
      if (index !== -1) {
        userList.value[index] = { ...formData }
        ElMessage.success('编辑成功')
      }
    } else {
      // 添加
      const newUser = {
        ...formData,
        id: Date.now(),
        deptName: deptList.value.find(d => d.id === formData.deptId)?.name || '',
        roleName: roleList.value.find(r => r.id === formData.roleId)?.name || '',
        createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
      }
      userList.value.unshift(newUser)
      pagination.total = userList.value.length
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}
</script>

<style scoped>
.user-management {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>