<template>
  <div class="menu-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <h2>菜单管理</h2>
          <el-button type="primary" @click="handleAddMenu">
            <el-icon><Plus /></el-icon>
            <span>添加菜单</span>
          </el-button>
        </div>
      </template>
      
      <!-- 菜单树形结构 -->
      <div class="menu-tree">
        <el-tree
          :data="menuList"
          node-key="id"
          default-expand-all
          :expand-on-click-node="false"
        >
          <template #default="{ node, data }">
            <div class="tree-node">
              <span>{{ node.label }}</span>
              <div class="tree-node-actions">
                <el-button type="primary" size="small" @click="handleAddSubMenu(data)">
                  <el-icon><Plus /></el-icon>
                </el-button>
                <el-button type="success" size="small" @click="handleEditMenu(data)">
                  <el-icon><Edit /></el-icon>
                </el-button>
                <el-button type="danger" size="small" @click="handleDeleteMenu(data.id)">
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </template>
        </el-tree>
      </div>
    </el-card>
    
    <!-- 添加/编辑菜单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入菜单名称"></el-input>
        </el-form-item>
        <el-form-item label="父菜单" prop="parentId">
          <el-select v-model="formData.parentId" placeholder="请选择父菜单">
            <el-option label="顶级菜单" value="0"></el-option>
            <el-option
              v-for="menu in menuOptions"
              :key="menu.id"
              :label="menu.label"
              :value="menu.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="菜单路径" prop="path">
          <el-input v-model="formData.path" placeholder="请输入菜单路径"></el-input>
        </el-form-item>
        <el-form-item label="组件路径" prop="component">
          <el-input v-model="formData.component" placeholder="请输入组件路径"></el-input>
        </el-form-item>
        <el-form-item label="菜单图标" prop="icon">
          <el-input v-model="formData.icon" placeholder="请输入菜单图标"></el-input>
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

// 菜单列表
const menuList = ref([])

// 对话框状态
const dialogVisible = ref(false)
const dialogTitle = ref('添加菜单')

// 表单数据
const formData = reactive({
  id: '',
  name: '',
  parentId: '0',
  path: '',
  component: '',
  icon: '',
  sort: 0,
  status: '1'
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  path: [
    { required: true, message: '请输入菜单路径', trigger: 'blur' }
  ]
}

const formRef = ref(null)

// 菜单选项（用于父菜单选择）
const menuOptions = computed(() => {
  const options = []
  
  const generateOptions = (menus, level = 0) => {
    menus.forEach(menu => {
      options.push({
        id: menu.id,
        label: ' '.repeat(level * 2) + menu.name
      })
      if (menu.children && menu.children.length > 0) {
        generateOptions(menu.children, level + 1)
      }
    })
  }
  
  generateOptions(menuList.value)
  return options
})

// 初始化数据
onMounted(() => {
  fetchMenuList()
})

// 获取菜单列表
const fetchMenuList = () => {
  // 模拟数据
  menuList.value = [
    {
      id: 1,
      name: '系统管理',
      parentId: '0',
      path: '/system',
      component: 'Layout',
      icon: 'Setting',
      sort: 1,
      status: '1',
      children: [
        {
          id: 2,
          name: '用户管理',
          parentId: '1',
          path: '/system/user',
          component: 'system/user/index.vue',
          icon: 'User',
          sort: 1,
          status: '1'
        },
        {
          id: 3,
          name: '角色管理',
          parentId: '1',
          path: '/system/role',
          component: 'system/role/index.vue',
          icon: 'Avatar',
          sort: 2,
          status: '1'
        },
        {
          id: 4,
          name: '菜单管理',
          parentId: '1',
          path: '/system/menu',
          component: 'system/menu/index.vue',
          icon: 'Menu',
          sort: 3,
          status: '1'
        },
        {
          id: 5,
          name: '部门管理',
          parentId: '1',
          path: '/system/dept',
          component: 'system/dept/index.vue',
          icon: 'OfficeBuilding',
          sort: 4,
          status: '1'
        }
      ]
    },
    {
      id: 6,
      name: '业务管理',
      parentId: '0',
      path: '/business',
      component: 'Layout',
      icon: 'Management',
      sort: 2,
      status: '1',
      children: [
        {
          id: 7,
          name: '订单管理',
          parentId: '6',
          path: '/business/order',
          component: 'business/order/index.vue',
          icon: 'ShoppingCart',
          sort: 1,
          status: '1'
        },
        {
          id: 8,
          name: '产品管理',
          parentId: '6',
          path: '/business/product',
          component: 'business/product/index.vue',
          icon: 'Goods',
          sort: 2,
          status: '1'
        }
      ]
    }
  ]
}

// 添加菜单
const handleAddMenu = () => {
  dialogTitle.value = '添加菜单'
  Object.assign(formData, {
    id: '',
    name: '',
    parentId: '0',
    path: '',
    component: '',
    icon: '',
    sort: 0,
    status: '1'
  })
  dialogVisible.value = true
}

// 添加子菜单
const handleAddSubMenu = (data) => {
  dialogTitle.value = '添加子菜单'
  Object.assign(formData, {
    id: '',
    name: '',
    parentId: data.id,
    path: '',
    component: '',
    icon: '',
    sort: 0,
    status: '1'
  })
  dialogVisible.value = true
}

// 编辑菜单
const handleEditMenu = (data) => {
  dialogTitle.value = '编辑菜单'
  Object.assign(formData, data)
  dialogVisible.value = true
}

// 删除菜单
const handleDeleteMenu = (id) => {
  // 检查是否有子菜单
  const hasChildren = checkHasChildren(id)
  if (hasChildren) {
    ElMessage.warning('该菜单下有子菜单，无法删除')
    return
  }
  
  ElMessageBox.confirm('确定要删除该菜单吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟删除
    deleteMenuById(id, menuList.value)
    ElMessage.success('删除成功')
  }).catch(() => {
    // 取消删除
  })
}

// 检查是否有子菜单
const checkHasChildren = (id) => {
  let hasChildren = false
  
  const check = (menus) => {
    for (const menu of menus) {
      if (menu.id === id && menu.children && menu.children.length > 0) {
        hasChildren = true
        return
      }
      if (menu.children && menu.children.length > 0) {
        check(menu.children)
      }
    }
  }
  
  check(menuList.value)
  return hasChildren
}

// 根据ID删除菜单
const deleteMenuById = (id, menus) => {
  for (let i = 0; i < menus.length; i++) {
    if (menus[i].id === id) {
      menus.splice(i, 1)
      return true
    }
    if (menus[i].children && menus[i].children.length > 0) {
      if (deleteMenuById(id, menus[i].children)) {
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
      updateMenuById(formData.id, formData, menuList.value)
      ElMessage.success('编辑成功')
    } else {
      // 添加
      const newMenu = {
        ...formData,
        id: Date.now()
      }
      
      if (newMenu.parentId === '0') {
        // 顶级菜单
        menuList.value.push(newMenu)
      } else {
        // 子菜单
        addSubMenuById(newMenu.parentId, newMenu, menuList.value)
      }
      
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 根据ID更新菜单
const updateMenuById = (id, data, menus) => {
  for (const menu of menus) {
    if (menu.id === id) {
      Object.assign(menu, data)
      return true
    }
    if (menu.children && menu.children.length > 0) {
      if (updateMenuById(id, data, menu.children)) {
        return true
      }
    }
  }
  return false
}

// 根据父ID添加子菜单
const addSubMenuById = (parentId, newMenu, menus) => {
  for (const menu of menus) {
    if (menu.id === parentId) {
      if (!menu.children) {
        menu.children = []
      }
      menu.children.push(newMenu)
      return true
    }
    if (menu.children && menu.children.length > 0) {
      if (addSubMenuById(parentId, newMenu, menu.children)) {
        return true
      }
    }
  }
  return false
}
</script>

<style scoped>
.menu-management {
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

.menu-tree {
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