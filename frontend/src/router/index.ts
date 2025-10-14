import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/Register.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/home',
      redirect: '/'
    },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/Home.vue')
        },
        {
          path: 'enterprise',
          name: 'enterprise',
          component: () => import('@/views/Enterprise.vue'),
          meta: { roles: [0] } // 只有系统管理员可以访问
        },
        {
          path: 'certificate',
          name: 'certificate',
          component: () => import('@/views/Certificate.vue'),
          meta: { roles: [1, 2, 3, 4] } // 企业用户可以访问
        },
        {
          path: 'batch',
          name: 'batch',
          component: () => import('@/views/Batch.vue'),
          meta: { roles: [1, 2, 3, 4] }
        },
        {
          path: 'confirmation',
          name: 'confirmation',
          component: () => import('@/views/Confirmation.vue'),
          meta: { roles: [2, 3, 4] } // 屠宰、批发、零售可以访问
        },
        {
          path: 'trace-code',
          name: 'traceCode',
          component: () => import('@/views/TraceCode.vue'),
          meta: { roles: [4] } // 只有零售商可以访问
        },
        {
          path: 'trace-query',
          name: 'traceQuery',
          component: () => import('@/views/TraceQuery.vue')
        },
        {
          path: 'statistics',
          name: 'statistics',
          component: () => import('@/views/Statistics.vue'),
          meta: { roles: [0] } // 只有系统管理员可以访问
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token
  const userInfo = userStore.userInfo

  // 如果去登录页且已登录，重定向到首页
  if (to.path === '/login' && token) {
    next('/')
    return
  }

  if (to.meta.requiresAuth !== false && !token) {
    // 需要登录但未登录，跳转到登录页
    next('/login')
  } else if (to.meta.roles && Array.isArray(to.meta.roles)) {
    // 有权限要求，检查用户权限
    if (userInfo && to.meta.roles.includes(userInfo.userType)) {
      next()
    } else {
      // 权限不足，跳转到首页
      next('/')
    }
  } else {
    next()
  }
})

export default router
