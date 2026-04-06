import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../components/Layout.vue'

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/login/index.vue'),
    meta: {
      title: '登录',
      noAuth: true
    }
  },
  {
    path: '/',
    component: Layout,
    meta: {
      title: '首页'
    },
    children: [
      {
        path: '',
        name: 'home',
        component: () => import('../views/Home.vue'),
        meta: {
          title: '首页'
        }
      },
      {
        path: 'system',
        name: 'system',
        meta: {
          title: '系统管理'
        },
        children: [
          {
            path: 'user',
            name: 'user',
            component: () => import('../views/system/user/index.vue'),
            meta: {
              title: '用户管理',
              permissions: ['user:list']
            }
          },
          {
            path: 'role',
            name: 'role',
            component: () => import('../views/system/role/index.vue'),
            meta: {
              title: '角色管理',
              permissions: ['role:list']
            }
          },
          {
            path: 'menu',
            name: 'menu',
            component: () => import('../views/system/menu/index.vue'),
            meta: {
              title: '菜单管理',
              permissions: ['menu:list']
            }
          },
          {
            path: 'dept',
            name: 'dept',
            component: () => import('../views/system/dept/index.vue'),
            meta: {
              title: '部门管理',
              permissions: ['dept:list']
            }
          }
        ]
      }
    ]
  },
  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: () => import('../views/404.vue'),
    meta: {
      title: '404',
      noAuth: true
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 管理系统` : '管理系统'
  
  // 不需要认证的页面
  if (to.meta.noAuth) {
    next()
    return
  }
  
  // 检查是否登录
  const token = localStorage.getItem('token')
  if (!token) {
    next({ path: '/login' })
    return
  }
  
  // 检查权限（这里只是简单示例，实际项目中需要根据后端返回的权限进行判断）
  const permissions = to.meta.permissions
  if (permissions && permissions.length > 0) {
    // 模拟权限检查
    const userPermissions = ['user:list', 'role:list', 'menu:list', 'dept:list']
    const hasPermission = permissions.every(perm => userPermissions.includes(perm))
    if (!hasPermission) {
      next({ path: '/' })
      return
    }
  }
  
  next()
})

export default router
