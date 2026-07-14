<template>
  <div class="layout">
    <el-container style="height: 100vh">
      <el-aside width="220px" style="background: #fff; border-right: 1px solid #e8e8e8">
        <div class="logo">
          <span style="font-size: 20px; margin-right: 8px">🌍</span> 爱出行
        </div>
        <el-menu
          :default-active="currentRoute"
          router
          style="border-right: none"
        >
          <el-menu-item index="/plan">
            <el-icon><MapLocation /></el-icon>
            <span>行程规划</span>
          </el-menu-item>
          <el-menu-item index="/product">
            <el-icon><Shop /></el-icon>
            <span>旅游产品</span>
          </el-menu-item>
          <el-menu-item index="/order">
            <el-icon><Document /></el-icon>
            <span>我的订单</span>
          </el-menu-item>
          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
          <el-menu-item v-if="userStore.isAdmin" index="/admin">
            <el-icon><DataAnalysis /></el-icon>
            <span>后台管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header style="display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #e8e8e8">
          <h3 style="margin: 0; color: #1890ff">{{ route.meta.title }}</h3>
          <div>
            <span style="color: #666; margin-right: 12px">欢迎，{{ userStore.user?.username || '游客' }}</span>
            <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
          </div>
        </el-header>
        <el-main style="background: #f0f2f5">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const currentRoute = computed(() => route.path)

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  color: #1890ff;
  border-bottom: 1px solid #e8e8e8;
}
</style>
