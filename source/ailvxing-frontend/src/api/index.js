import axios from 'axios'
import { ElMessage } from 'element-plus'

const api = axios.create({
  baseURL: window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1' ? '/api' : 'http://192.168.70.196:8080/api',
  timeout: 120000
})

// 请求拦截器：自动带 token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：统一错误处理
api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    ElMessage.error(error.response?.data?.msg || '请求失败')
    return Promise.reject(error)
  }
)

// ========== 用户 ==========
export const userApi = {
  login: (data) => api.post('/user/login', data),
  register: (data) => api.post('/user/register', data),
  getInfo: (id) => api.get(`/user/${id}`),
  updateInfo: (data) => api.put('/user', data),
  changePwd: (params) => api.post('/user/change-password', null, { params })
}

// ========== 偏好 ==========
export const preferenceApi = {
  get: () => api.get('/preference'),
  save: (data) => api.post('/preference', data)
}

// ========== 出行人员 ==========
export const companionApi = {
  list: () => api.get('/companion'),
  add: (data) => api.post('/companion', data),
  update: (data) => api.put('/companion', data),
  remove: (id) => api.delete(`/companion/${id}`)
}

// ========== 行程规划 ==========
export const planApi = {
  generate: (data) => api.post('/plan/generate', data),
  list: () => api.get('/plan/my'),
  detail: (id) => api.get(`/plan/${id}`),
  delete: (id) => api.delete(`/plan/${id}`),
  update: (data) => api.put('/plan', data),
  favorite: (id) => api.post(`/plan/favorite/${id}`),
  unfavorite: (id) => api.delete(`/plan/favorite/${id}`),
  favorites: () => api.get('/plan/favorites'),
  share: (id) => api.post(`/plan/share/${id}`),
  shared: (code) => api.get(`/plan/shared/${code}`),
  regenerate: (id, budgetLevel) => api.post(`/plan/${id}/regenerate?budgetLevel=${budgetLevel}`),
  recommendations: (id) => api.get(`/plan/${id}/recommendations`),
  updateRecommendStatus: (id, status) => api.put(`/plan/recommend/${id}/status?status=${status}`)
}

// ========== 产品 ==========
export const productApi = {
  list: () => api.get('/product/list'),
  search: (dest) => api.get(`/product/search/destination?destination=${encodeURIComponent(dest)}`),
  detail: (id) => api.get(`/product/${id}`),
  tags: () => api.get('/product/tags'),
  add: (data) => api.post('/product', data)
}

// ========== 订单 ==========
export const orderApi = {
  create: (data) => api.post('/order', data),
  my: () => api.get('/order/my'),
  pay: (id, method) => api.post(`/order/pay/${id}?payMethod=${method}`),
  cancel: (id) => api.post(`/order/cancel/${id}`),
  refund: (id) => api.post(`/order/refund/${id}`),
  complete: (id) => api.post(`/order/complete/${id}`),
  delete: (id) => api.delete(`/order/${id}`)
}

// ========== 评价 ==========
export const reviewApi = {
  add: (data) => api.post('/review', data),
  my: () => api.get('/review/my'),
  ofProduct: (id) => api.get(`/review/product/${id}`),
  update: (data) => api.put('/review', data),
  delete: (id) => api.delete(`/review/${id}`)
}

// ========== 管理员 ==========
export const adminApi = {
  overview: () => api.get('/admin/stats/overview'),
  destinations: () => api.get('/admin/stats/destinations'),
  productRanking: () => api.get('/admin/stats/product-ranking'),
  conversion: () => api.get('/admin/stats/recommend-conversion'),
  aiStats: () => api.get('/admin/stats/ai'),
  aiLogs: (limit = 10) => api.get(`/admin/stats/ai/logs?limit=${limit}`),
  allOrders: () => api.get('/admin/orders'),
  allUsers: () => api.get('/admin/users'),
  allReviews: () => api.get('/admin/reviews'),
  updateProduct: (data) => api.put('/admin/product', data),
  offlineProduct: (id) => api.put(`/admin/product/${id}/offline`),
  onlineProduct: (id) => api.put(`/admin/product/${id}/online`),
  toggleUser: (id, status) => api.put(`/admin/user/${id}/status?status=${status}`),
  deleteReview: (id) => api.delete(`/admin/review/${id}`),
  deleteOrder: (id) => api.delete(`/admin/order/${id}`),
  confirmOrder: (id) => api.post(`/order/confirm/${id}`),
  departOrder: (id) => api.post(`/order/depart/${id}`)
}

export default api
