<template>
  <div>
    <!-- 个人信息 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-weight: bold; font-size: 18px">👤 个人信息</span>
          <div>
            <el-button type="primary" size="small" @click="editProfile">编辑信息</el-button>
            <el-button type="success" size="small" @click="changePwdVisible = true">修改密码</el-button>
          </div>
        </div>
      </template>
      <el-descriptions :column="2" border v-if="userStore.user">
        <el-descriptions-item label="用户名">{{ userStore.user.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ userStore.user.nickname || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userStore.user.phone || '未绑定' }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userStore.user.email || '未绑定' }}</el-descriptions-item>
        <el-descriptions-item label="角色">{{ roleLabel(userStore.user.role) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ userStore.user.status === 1 ? '正常' : '禁用' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">{{ userStore.user.createTime || '' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 偏好设置 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header><span style="font-weight: bold; font-size: 18px">⚙️ 偏好设置</span></template>
      <el-form :model="prefForm" label-width="100px">
        <el-form-item label="默认预算等级">
          <el-radio-group v-model="prefForm.budgetLevel">
            <el-radio value="economy">经济型</el-radio>
            <el-radio value="comfort">舒适型</el-radio>
            <el-radio value="luxury">豪华型</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="兴趣标签">
          <el-input v-model="prefForm.interestTags" placeholder="如：美食,摄影,文化" />
        </el-form-item>
        <el-form-item label="饮食偏好">
          <el-input v-model="prefForm.dietaryPreference" placeholder="如：清淡、无辣、素食" />
        </el-form-item>
        <el-form-item label="交通偏好">
          <el-select v-model="prefForm.transportPreference" placeholder="不限" clearable>
            <el-option label="飞机优先" value="plane" />
            <el-option label="高铁优先" value="train" />
            <el-option label="自驾" value="car" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="savePref">保存设置</el-button></el-form-item>
      </el-form>
    </el-card>

    <!-- 出行人员 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span style="font-weight: bold; font-size: 18px">👥 常用出行人员</span>
          <el-button type="primary" size="small" @click="addCompanion">+ 添加</el-button>
        </div>
      </template>
      <div v-if="companions.length">
        <div v-for="c in companions" :key="c.id" style="display:flex;align-items:center;gap:12px;padding:12px;background:#fafafa;border-radius:8px;margin-bottom:8px">
          <div style="flex:1"><strong>{{ c.name }}</strong>
            <el-tag size="small" style="margin-left:8px">{{ relLabel(c.relationship) }}</el-tag>
            <span v-if="c.phone" style="color:#999;margin-left:8px">📞 {{ c.phone }}</span>
          </div>
          <el-button size="small" @click="editCompanion(c)">编辑</el-button>
          <el-button type="danger" size="small" @click="deleteCompanion(c.id)">删除</el-button>
        </div>
      </div>
      <el-empty v-else description="暂无出行人员" />
    </el-card>

    <!-- 我的收藏 -->
    <el-card shadow="hover">
      <template #header><span style="font-weight: bold; font-size: 18px">❤️ 我的收藏</span></template>
      <div v-if="favorites.length">
        <div v-for="f in favorites" :key="f.id" style="display:flex;align-items:center;gap:12px;padding:12px;background:#fafafa;border-radius:8px;margin-bottom:8px">
          <span style="flex:1;font-weight:bold">{{ f.planTitle || '未命名行程' }}</span>
          <span style="color:#999;font-size:12px">{{ (f.createTime || '').substring(0, 10) }}</span>
          <el-button type="primary" size="small" @click="$router.push('/plan'); $emit('viewPlan', f.planId)">查看</el-button>
          <el-button type="danger" size="small" @click="unfav(f.planId)">取消收藏</el-button>
        </div>
      </div>
      <el-empty v-else description="暂无收藏" />
    </el-card>

    <!-- 编辑个人信息弹窗 -->
    <el-dialog v-model="editVisible" title="编辑个人信息" width="420px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="昵称"><el-input v-model="editForm.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="editForm.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="editForm.email" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEditProfile">保存</el-button>
      </template>
    </el-dialog>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="changePwdVisible" title="修改密码" width="400px">
      <el-form :model="pwdForm" label-width="100px">
        <el-form-item label="原密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password /></el-form-item>
        <el-form-item label="确认密码"><el-input v-model="pwdForm.confirmPassword" type="password" show-password /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changePwdVisible = false">取消</el-button>
        <el-button type="primary" @click="doChangePwd">确认</el-button>
      </template>
    </el-dialog>

    <!-- 出行人员弹窗 -->
    <el-dialog v-model="compVisible" :title="compEditId ? '编辑出行人员' : '添加出行人员'" width="400px">
      <el-form :model="compForm" label-width="80px">
        <el-form-item label="姓名"><el-input v-model="compForm.name" /></el-form-item>
        <el-form-item label="关系">
          <el-select v-model="compForm.relationship">
            <el-option label="家人" value="family" />
            <el-option label="朋友" value="friend" />
            <el-option label="伴侣" value="couple" />
            <el-option label="同事" value="colleague" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号"><el-input v-model="compForm.phone" /></el-form-item>
        <el-form-item label="身份证"><el-input v-model="compForm.idCard" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="compVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCompanion">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { userApi, preferenceApi, companionApi, planApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()

const editVisible = ref(false)
const changePwdVisible = ref(false)
const compVisible = ref(false)
const compEditId = ref(null)
const favorites = ref([])
const companions = ref([])

const editForm = reactive({ nickname: '', phone: '', email: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const compForm = reactive({ name: '', relationship: 'family', phone: '', idCard: '' })
const prefForm = reactive({ budgetLevel: 'comfort', interestTags: '', dietaryPreference: '', transportPreference: '' })

function roleLabel(r) { return { 0: '普通用户', 1: '旅行社', 2: '管理员' }[r] || '未知' }
function relLabel(r) { return { family: '家人', friend: '朋友', couple: '伴侣', colleague: '同事' }[r] || r }

function editProfile() {
  editForm.nickname = userStore.user?.nickname || ''
  editForm.phone = userStore.user?.phone || ''
  editForm.email = userStore.user?.email || ''
  editVisible.value = true
}

async function saveEditProfile() {
  try {
    const res = await userApi.updateInfo({ id: userStore.user.id, ...editForm })
    if (res.code === 200) {
      userStore.user.nickname = editForm.nickname
      userStore.user.phone = editForm.phone
      userStore.user.email = editForm.email
      localStorage.setItem('user', JSON.stringify(userStore.user))
      ElMessage.success('修改成功')
      editVisible.value = false
    }
  } catch (e) { /* handled */ }
}

async function doChangePwd() {
  if (!pwdForm.oldPassword || !pwdForm.newPassword) { ElMessage.warning('请填写原密码和新密码'); return }
  if (pwdForm.newPassword.length < 6) { ElMessage.warning('新密码至少6位'); return }
  if (pwdForm.newPassword !== pwdForm.confirmPassword) { ElMessage.warning('两次密码不一致'); return }
  try {
    const res = await userApi.changePwd({ oldPassword: pwdForm.oldPassword, newPassword: pwdForm.newPassword })
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      userStore.logout()
      window.location.href = '/login'
    }
  } catch (e) { /* handled */ }
}

async function loadFavorites() {
  try {
    const res = await planApi.favorites()
    if (res.code === 200) favorites.value = res.data || []
  } catch (e) { /* handled */ }
}

async function unfav(planId) {
  try {
    const res = await planApi.unfavorite(planId)
    if (res.code === 200) { ElMessage.success('已取消收藏'); loadFavorites() }
  } catch (e) { /* handled */ }
}

async function loadCompanions() {
  try {
    const res = await companionApi.list()
    if (res.code === 200) companions.value = res.data || []
  } catch (e) { /* handled */ }
}

function addCompanion() {
  compEditId.value = null
  compForm.name = ''; compForm.relationship = 'family'; compForm.phone = ''; compForm.idCard = ''
  compVisible.value = true
}

function editCompanion(c) {
  compEditId.value = c.id
  compForm.name = c.name; compForm.relationship = c.relationship
  compForm.phone = c.phone || ''; compForm.idCard = c.idCard || ''
  compVisible.value = true
}

async function saveCompanion() {
  if (!compForm.name.trim()) { ElMessage.warning('请输入姓名'); return }
  try {
    let res
    if (compEditId.value) {
      res = await companionApi.update({ id: compEditId.value, ...compForm })
    } else {
      res = await companionApi.add(compForm)
    }
    if (res.code === 200) { ElMessage.success('保存成功'); compVisible.value = false; loadCompanions() }
  } catch (e) { /* handled */ }
}

async function deleteCompanion(id) {
  try {
    await ElMessageBox.confirm('确定删除？', '警告', { type: 'warning' })
    const res = await companionApi.remove(id)
    if (res.code === 200) { ElMessage.success('已删除'); loadCompanions() }
  } catch (e) { /* cancelled */ }
}

async function loadPref() {
  try {
    const res = await preferenceApi.get()
    if (res.code === 200 && res.data) {
      prefForm.budgetLevel = res.data.budgetLevel || 'comfort'
      prefForm.interestTags = res.data.interestTags || ''
      prefForm.dietaryPreference = res.data.dietaryPreference || ''
      prefForm.transportPreference = res.data.transportPreference || ''
    }
  } catch (e) { /* handled */ }
}

async function savePref() {
  try {
    const res = await preferenceApi.save(prefForm)
    if (res.code === 200) ElMessage.success('偏好已保存')
  } catch (e) { /* handled */ }
}

onMounted(() => { loadFavorites(); loadCompanions(); loadPref() })
</script>
