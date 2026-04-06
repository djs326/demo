<template>
  <div class="dept-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>部门管理</h2>
          <el-button type="primary" @click="handleAddDept">
            <el-icon><Plus /></el-icon>
            <span>添加部门</span>
          </el-button>
        </div>
      </template>
      
      <!-- 部门树形结构 -->
      <div class="dept-tree">
        <el-tree
          :data="deptList"
          node-key="id"
          default-expand-all
          :expand-on-click-node="false"
        >
          <template #default="{ node, data }">
            <div class="tree-node">
              <span>{{ node.label }}</span>
              <div class="tree-node-actions">
                <el-button type="primary" size="small" @click="handleAddSubDept(data)">
                  <el-icon><Plus /></el-icon>
                </el-button>
                <el-button type="success" size="small" @click="handleEditDept(data)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button type="danger" size="small" @click="handleDeleteDept(data.id)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </template>
        </el-tree>
      </div>
    </el-card>
    
    <!-- 添加/编辑部门对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入部门名称"></el-input>
        </el-form-item>
        <el-form-item label="父部门" prop="parentId">
          <el-select v-model="formData.parentId" placeholder="请选择父部门">
            <el-option label="顶级部门" value="0"></el-option>
            <el-option
              v-for="dept in deptOptions"
              :key="dept.id"
              :label="dept.label"
              :value="dept.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="部门编码" prop="code">
          <el-input v-model="formData.code" placeholder="请输入部门编码"></el-input>
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="formData.leader" placeholder="请输入负责人"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="formData.sort" :min="0" placeholder="请输入排序"></el-input-number>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'

// 部门列表
const deptList = ref([])

// 对话框状态
const dialogVisible = ref(false)
const dialogTitle = ref('添加部门')

// 表单数据
const formData = reactive({
  id: '',
  name: '',
  parentId: '0',
  code: '',
  leader: '',
  phone: '',
  sort: 0,
  status: '1'
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入部门编码', trigger: 'blur' }
  ]
}

const formRef = ref(null)

// 部门选项（用于父部门选择）
const deptOptions = computed(() => {
  const options = []
  
  const generateOptions = (depts, level = 0) => {
    depts.forEach(dept => {
      options.push({
        id: dept.id,
        label: ' '.repeat(level * 2) + dept.name
      })
      if (dept.children && dept.children.length > 0) {
        generateOptions(dept.children, level + 1)
      }
    })
  }
  
  generateOptions(deptList.value)
  return options
})

// 初始化数据
onMounted(() => {
  fetchDeptList()
})

// 获取部门列表
const fetchDeptList = () => {
  // 模拟数据
  deptList.value = [
    {
      id: 1,
      name: '总公司',
      parentId: '0',
      code: 'COMPANY',
      leader: '张三',
      phone: '13800138000',
      sort: 1,
      status: '1',
      children: [
        {
          id: 2,
          name: '技术部',
          parentId: '1',
          code: 'TECH',
          leader: '李四',
          phone: '13800138001',
          sort: 1,
          status: '1',
          children: [
            {
              id: 3,
              name: '前端开发组',
              parentId: '2',
              code: 'FRONTEND',
              leader: '王五',
              phone: '13800138002',
              sort: 1,
              status: '1'
            },
            {
              id: 4,
              name: '后端开发组',
              parentId: '2',
              code: 'BACKEND',
              leader: '赵六',
              phone: '13800138003',
              sort: 2,
              status: '1'
            }
          ]
        },
        {
          id: 5,
          name: '产品部',
          parentId: '1',
          code: 'PRODUCT',
          leader: '孙七',
          phone: '13800138004',
          sort: 2,
          status: '1'
        },
        {
          id: 6,
          name: '销售部',
          parentId: '1',
          code: 'SALES',
          leader: '周八',
          phone: '13800138005',
          sort: 3,
          status: '1'
        }
      ]
    }
  ]
}

// 添加部门
const handleAddDept = () => {
  dialogTitle.value = '添加部门'
  Object.assign(formData, {
    id: '',
    name: '',
    parentId: '0',
    code: '',
    leader: '',
    phone: '',
    sort: 0,
    status: '1'
  })
  dialogVisible.value = true
}

// 添加子部门
const handleAddSubDept = (data) => {
  dialogTitle.value = '添加子部门'
  Object.assign(formData, {
    id: '',
    name: '',
    parentId: data.id,
    code: '',
    leader: '',
    phone: '',
    sort: 0,
    status: '1'
  })
  dialogVisible.value = true
}

// 编辑部门
const handleEditDept = (data) => {
  dialogTitle.value = '编辑部门'
  Object.assign(formData, data)
  dialogVisible.value = true
}

// 删除部门
const handleDeleteDept = (id) => {
  // 检查是否有子部门
  const hasChildren = checkHasChildren(id)
  if (hasChildren) {
    ElMessage.warning('该部门下有子部门，无法删除')
    return
  }
  
  ElMessageBox.confirm('确定要删除该部门吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟删除
    deleteDeptById(id, deptList.value)
    ElMessage.success('删除成功')
  }).catch(() => {
    // 取消删除
  })
}

// 检查是否有子部门
const checkHasChildren = (id) => {
  let hasChildren = false
  
  const check = (depts) => {
    for (const dept of depts) {
      if (dept.id === id && dept.children && dept.children.length > 0) {
        hasChildren = true
        return
      }
      if (dept.children && dept.children.length > 0) {
        check(dept.children)
      }
    }
  }
  
  check(deptList.value)
  return hasChildren
}

// 根据ID删除部门
const deleteDeptById = (id, depts) => {
  for (let i = 0; i < depts.length; i++) {
    if (depts[i].id === id) {
      depts.splice(i, 1)
      return true
    }
    if (depts[i].children && depts[i].children.length > 0) {
      if (deleteDeptById(id, depts[i].children)) {
        return true
      }
    }
  }
  return false
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    if (formData.id) {
      // 编辑
      updateDeptById(formData.id, formData, deptList.value)
      ElMessage.success('编辑成功')
    } else {
      // 添加
      const newDept = {
        ...formData,
        id: Date.now()
      }
      
      if (newDept.parentId === '0') {
        // 顶级部门
        deptList.value.push(newDept)
      } else {
        // 子部门
        addSubDeptById(newDept.parentId, newDept, deptList.value)
      }
      
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 根据ID更新部门
const updateDeptById = (id, data, depts) => {
  for (const dept of depts) {
    if (dept.id === id) {
      Object.assign(dept, data)
      return true
    }
    if (dept.children && dept.children.length > 0) {
      if (updateDeptById(id, data, dept.children)) {
        return true
      }
    }
  }
  return false
}

// 根据父ID添加子部门
const addSubDeptById = (parentId, newDept, depts) => {
  for (const dept of depts) {
    if (dept.id === parentId) {
      if (!dept.children) {
        dept.children = []
      }
      dept.children.push(newDept)
      return true
    }
    if (dept.children && dept.children.length > 0) {
      if (addSubDeptById(parentId, newDept, dept.children)) {
        return true
      }
    }
  }
  return false
}
</script>

<style scoped>
.dept-management {
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

.dept-tree {
  margin-top: 20px;
}

.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.tree-node-actions {
  display: flex;
  gap: 5px;
}
</style>