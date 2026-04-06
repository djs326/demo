<template>
  <div class="role-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>角色管理</h2>
          <el-button type="primary" @click="handleAddRole">
            <el-icon><Plus /></el-icon>
            <span>添加角色</span>
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.name" placeholder="请输入角色名称" clearable></el-input>
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
      
      <!-- 角色列表 -->
      <el-table :data="roleList" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="角色名称"></el-table-column>
        <el-table-column prop="code" label="角色代码"></el-table-column>
        <el-table-column prop="description" label="角色描述"></el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-switch v-model="scope.row.status" @change="handleStatusChange(scope.row)" active-value="1" inactive-value="0"></el-switch>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180"></el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEditRole(scope.row)">
              <el-icon><Edit /></el-icon>
              <span>编辑</span>
            </el-button>
            <el-button type="success" size="small" @click="handlePermission(scope.row)">
              <el-icon><Lock /></el-icon>
              <span>权限</span>
            </el-button>
            <el-button type="danger" size="small" @click="handleDeleteRole(scope.row.id)">
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
    
    <!-- 添加/编辑角色对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入角色名称"></el-input>
        </el-form-item>
        <el-form-item label="角色代码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入角色代码"></el-input>
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="formData.description" type="textarea" placeholder="请输入角色描述"></el-input>
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
    
    <!-- 权限配置对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="权限配置"
      width="600px"
    >
      <el-tree
        :data="menuList"
        show-checkbox
        node-key="id"
        default-expand-all
        :default-checked-keys="checkedMenuIds"
        @check-change="handleCheckChange"
      >
        <template #default="{ node }">
          <span class="menu-label">{{ node.label }}</span>
        </template>
      </el-tree>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSavePermission">保存权限</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Lock, Delete } from '@element-plus/icons-vue'

// 搜索表单
const searchForm = reactive({
  name: '',
  status: ''
})

// 分页数据
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 角色列表
const roleList = ref([])

// 菜单列表（用于权限配置）
const menuList = ref([
  {
    id: 1,
    label: '系统管理',
    children: [
      { id: 2, label: '用户管理' },
      { id: 3, label: '角色管理' },
      { id: 4, label: '菜单管理' },
      { id: 5, label: '部门管理' }
    ]
  },
  {
    id: 6,
    label: '业务管理',
    children: [
      { id: 7, label: '订单管理' },
      { id: 8, label: '产品管理' }
    ]
  }
])

// 对话框状态
const dialogVisible = ref(false)
const dialogTitle = ref('添加角色')

// 权限配置对话框
const permissionDialogVisible = ref(false)
const currentRole = ref(null)
const checkedMenuIds = ref([])

// 表单数据
const formData = reactive({
  id: '',
  name: '',
  code: '',
  description: '',
  status: '1'
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色代码', trigger: 'blur' }
  ]
}

const formRef = ref(null)

// 初始化数据
onMounted(() => {
  fetchRoleList()
})

// 获取角色列表
const fetchRoleList = () => {
  // 模拟数据
  roleList.value = [
    {
      id: 1,
      name: '管理员',
      code: 'admin',
      description: '系统管理员',
      status: '1',
      createTime: '2024-01-01 00:00:00'
    },
    {
      id: 2,
      name: '普通用户',
      code: 'user',
      description: '普通用户',
      status: '1',
      createTime: '2024-01-02 00:00:00'
    },
    {
      id: 3,
      name: '访客',
      code: 'guest',
      description: '访客',
      status: '0',
      createTime: '2024-01-03 00:00:00'
    }
  ]
  pagination.total = roleList.value.length
}

// 搜索
const handleSearch = () => {
  pagination.currentPage = 1
  fetchRoleList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.status = ''
  pagination.currentPage = 1
  fetchRoleList()
}

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchRoleList()
}

// 页码变化
const handleCurrentChange = (current) => {
  pagination.currentPage = current
  fetchRoleList()
}

// 添加角色
const handleAddRole = () => {
  dialogTitle.value = '添加角色'
  Object.assign(formData, {
    id: '',
    name: '',
    code: '',
    description: '',
    status: '1'
  })
  dialogVisible.value = true
}

// 编辑角色
const handleEditRole = (row) => {
  dialogTitle.value = '编辑角色'
  Object.assign(formData, row)
  dialogVisible.value = true
}

// 删除角色
const handleDeleteRole = (id) => {
  ElMessageBox.confirm('确定要删除该角色吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟删除
    roleList.value = roleList.value.filter(item => item.id !== id)
    pagination.total = roleList.value.length
    ElMessage.success('删除成功')
  }).catch(() => {
    // 取消删除
  })
}

// 状态变更
const handleStatusChange = (row) => {
  ElMessage.success(`角色 ${row.name} 状态已更新为 ${row.status === '1' ? '启用' : '禁用'}`)
}

// 权限配置
const handlePermission = (row) => {
  currentRole.value = row
  // 模拟已选权限
  checkedMenuIds.value = [1, 2, 3, 4, 5]
  permissionDialogVisible.value = true
}

// 处理权限选择变化
const handleCheckChange = (data, checked, indeterminate) => {
  console.log('权限变化:', data, checked, indeterminate)
}

// 保存权限
const handleSavePermission = () => {
  ElMessage.success(`角色 ${currentRole.value.name} 权限配置成功`)
  permissionDialogVisible.value = false
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (formData.id) {
      // 编辑
      const index = roleList.value.findIndex(item => item.id === formData.id)
      if (index !== -1) {
        roleList.value[index] = { ...formData }
        ElMessage.success('编辑成功')
      }
    } else {
      // 添加
      const newRole = {
        ...formData,
        id: Date.now(),
        createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
      }
      roleList.value.unshift(newRole)
      pagination.total = roleList.value.length
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}
</script>

<style scoped>
.role-management {
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

.menu-label {
  font-size: 14px;
}
</style>