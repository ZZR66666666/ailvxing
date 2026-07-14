import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: () => import('../layout/MainLayout.vue'),
    redirect: '/plan',
    children: [
      { path: 'plan', name: 'Plan', component: () => import('../views/Plan.vue'), meta: { title: '行程规划' } },
      { path: 'product', name: 'Product', component: () => import('../views/Product.vue'), meta: { title: '旅游产品' } },
      { path: 'order', name: 'Order', component: () => import('../views/Order.vue'), meta: { title: '我的订单' } },
      { path: 'profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { title: '个人中心' } },
      { path: 'admin', name: 'Admin', component: () => import('../views/Admin.vue'), meta: { title: '后台管理', auth: 'admin' } }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.meta.auth === 'admin') {
    const user = JSON.parse(localStorage.getItem('user') || 'null')
    if (user && (user.role === 1 || user.role === 2)) {
      next()
    } else {
      next('/plan')
    }
  } else {
    next()
  }
  document.title = to.meta.title ? `爱出行 - ${to.meta.title}` : '爱出行'
})

export default router
