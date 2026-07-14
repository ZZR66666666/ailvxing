<template>
  <div class="login-page">
    <div class="login-card">
      <h1 style="text-align: center; color: #1890ff; margin-bottom: 8px">🌍 爱出行</h1>
      <p style="text-align: center; color: #999; margin-bottom: 32px">自助旅行规划系统</p>

      <el-tabs v-model="activeTab" style="width: 100%">
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef">
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" size="large" @keyup.enter="handleLogin" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password @keyup.enter="handleLogin" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" style="width: 100%" @click="handleLogin" :loading="loading">登 录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form :model="regForm" :rules="regRules" ref="regFormRef">
            <el-form-item prop="username">
              <el-input v-model="regForm.username" placeholder="用户名" prefix-icon="User" size="large" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="regForm.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input v-model="regForm.phone" placeholder="手机号" prefix-icon="Phone" size="large" />
            </el-form-item>
            <el-form-item>
              <el-button type="success" size="large" style="width: 100%" @click="handleRegister" :loading="regLoading">注 册</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { userApi } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const loading = ref(false)
const regLoading = ref(false)

const loginForm = reactive({ username: '', password: '' })
const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const regForm = reactive({ username: '', password: '', phone: '' })
const regRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, min: 6, message: '密码至少6位', trigger: 'blur' }],
  phone: [{ pattern: /^1\d{10}$/, message: '请输入正确的手机号', trigger: 'blur' }]
}

async function handleLogin() {
  loading.value = true
  const result = await userStore.login(loginForm.username, loginForm.password)
  loading.value = false
  if (result.success) {
    ElMessage.success('登录成功')
    router.push('/plan')
  } else {
    ElMessage.error(result.msg || '登录失败')
  }
}

async function handleRegister() {
  regLoading.value = true
  try {
    const res = await userApi.register(regForm)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      activeTab.value = 'login'
      loginForm.username = regForm.username
    } else {
      ElMessage.error(res.msg || '注册失败')
    }
  } catch (e) { /* handled by interceptor */ }
  regLoading.value = false
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1890ff30, #36cfc930);
}
.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.12);
}
</style>
