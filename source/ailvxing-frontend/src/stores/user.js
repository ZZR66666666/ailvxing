import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '../api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value && (user.value.role === 1 || user.value.role === 2))

  function parseToken(t) {
    try {
      const parts = t.split('.')
      if (parts.length === 3) {
        return JSON.parse(atob(parts[1]))
      }
    } catch (e) { /* ignore */ }
    return null
  }

  async function login(username, password) {
    const res = await userApi.login({ username, password })
    if (res.code === 200) {
      token.value = res.data
      localStorage.setItem('token', res.data)
      const payload = parseToken(res.data)
      const userId = payload ? parseInt(payload.sub) : null
      const userRes = await userApi.getInfo(userId)
      if (userRes.code === 200) {
        user.value = userRes.data
        localStorage.setItem('user', JSON.stringify(userRes.data))
      }
      return { success: true, userId }
    }
    return { success: false, msg: res.msg }
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, isLoggedIn, isAdmin, login, logout }
})
