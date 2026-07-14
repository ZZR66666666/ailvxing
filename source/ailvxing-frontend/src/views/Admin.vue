<template>
  <div>
    <el-tabs type="border-card">
      <!-- 概览 -->
      <el-tab-pane label="📊 系统概览">
        <el-row :gutter="16" v-if="overview">
          <el-col :span="8" v-for="s in overviewCards" :key="s.label" style="margin-bottom:16px">
            <el-card shadow="hover" :style="{ background: s.bg, color: '#fff', textAlign: 'center' }">
              <h2 style="margin:0;font-size:28px">{{ s.value }}</h2>
              <p style="margin:4px 0 0;opacity:0.9">{{ s.label }}</p>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 统计 -->
      <el-tab-pane label="📈 数据统计">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-card shadow="hover" style="margin-bottom:16px">
              <template #header><b>🔥 热门目的地</b></template>
              <el-table :data="destinations" stripe size="small">
                <el-table-column type="index" label="排名" width="60" />
                <el-table-column prop="destination" label="目的地" />
                <el-table-column prop="productCount" label="产品数量" width="100" />
              </el-table>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover" style="margin-bottom:16px">
              <template #header><b>🏆 产品销售排行</b></template>
              <el-table :data="productRanking" stripe size="small">
                <el-table-column type="index" label="排名" width="60" />
                <el-table-column prop="name" label="产品名称" />
                <el-table-column prop="soldCount" label="已售" width="70" />
                <el-table-column prop="stock" label="库存" width="70" />
              </el-table>
            </el-card>
          </el-col>
        </el-row>
        <el-row :gutter="16" v-if="conversion">
          <el-col :span="6">
            <el-card shadow="hover" style="text-align:center;background:linear-gradient(135deg,#667eea,#764ba2);color:#fff">
              <h3 style="margin:0">{{ conversion.totalRecommends || 0 }}</h3><p>总推荐</p>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" style="text-align:center;background:linear-gradient(135deg,#4facfe,#00f2fe);color:#fff">
              <h3 style="margin:0">{{ conversion.viewedCount || 0 }}</h3><p>已查看</p>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" style="text-align:center;background:linear-gradient(135deg,#43e97b,#38f9d7);color:#fff">
              <h3 style="margin:0">{{ conversion.orderedCount || 0 }}</h3><p>已下单</p>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" style="text-align:center;background:linear-gradient(135deg,#f093fb,#f5576c);color:#fff">
              <h3 style="margin:0">{{ conversion.conversionRate || '0%' }}</h3><p>转化率</p>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- AI日志 -->
      <el-tab-pane label="🤖 AI调用日志">
        <el-row :gutter="16" v-if="aiStats" style="margin-bottom:16px">
          <el-col :span="6">
            <el-card shadow="hover" style="text-align:center;background:linear-gradient(135deg,#43e97b,#38f9d7);color:#fff">
              <h3>{{ aiStats.successCount || 0 }}</h3><p>成功</p>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" style="text-align:center;background:linear-gradient(135deg,#fa709a,#fee140);color:#fff">
              <h3>{{ aiStats.failedCount || 0 }}</h3><p>失败</p>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" style="text-align:center;background:linear-gradient(135deg,#667eea,#764ba2);color:#fff">
              <h3>{{ (aiStats.successCount || 0) + (aiStats.failedCount || 0) }}</h3><p>总计</p>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover" style="text-align:center;background:linear-gradient(135deg,#4facfe,#00f2fe);color:#fff">
              <h3>{{ successRate }}%</h3><p>成功率</p>
            </el-card>
          </el-col>
        </el-row>
        <el-table :data="aiLogs" stripe size="small">
          <el-table-column label="时间" width="180">
            <template #default="{ row }">{{ (row.createTime || '').substring(0, 19) }}</template>
          </el-table-column>
          <el-table-column prop="model" label="模型" width="120" />
          <el-table-column prop="durationMs" label="耗时" width="80">
            <template #default="{ row }">{{ row.durationMs }}ms</template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.isSuccess ? 'success' : 'danger'" size="small">{{ row.isSuccess ? '成功' : '失败' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="errorMsg" label="错误信息" />
        </el-table>
      </el-tab-pane>

      <!-- 产品管理 -->
      <el-tab-pane label="📦 产品管理">
        <div style="margin-bottom:12px">
          <el-button type="primary" size="small" @click="showAddProduct">+ 添加产品</el-button>
        </div>
        <el-table :data="adminProducts" stripe size="small">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="name" label="名称" width="160" />
          <el-table-column label="类型" width="80">
            <template #default="{ row }">{{ typeLabel(row.type) }}</template>
          </el-table-column>
          <el-table-column prop="destination" label="目的地" width="100" />
          <el-table-column label="价格" width="90">
            <template #default="{ row }"><span style="color:#ff4d4f">¥{{ row.price }}</span></template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" width="60" />
          <el-table-column prop="soldCount" label="已售" width="60" />
          <el-table-column label="状态" width="70">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" min-width="180">
            <template #default="{ row }">
              <el-button size="small" @click="editAdminProduct(row)">编辑</el-button>
              <el-button v-if="row.status === 1" type="danger" size="small" @click="offlineProduct(row.id)">下架</el-button>
              <el-button v-else type="success" size="small" @click="onlineProduct(row.id)">上架</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 订单管理 -->
      <el-tab-pane label="📋 订单管理">
        <el-table :data="adminOrders" stripe size="small">
          <el-table-column prop="orderNo" label="订单号" width="170" />
          <el-table-column label="产品" width="120">
            <template #default="{ row }">{{ row.items?.[0]?.productName || '-' }}</template>
          </el-table-column>
          <el-table-column label="金额" width="90">
            <template #default="{ row }"><span style="color:#ff4d4f">¥{{ row.totalAmount || row.payAmount }}</span></template>
          </el-table-column>
          <el-table-column label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="orderStatusType(row.status)" size="small">{{ orderStatusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="联系人" width="120">
            <template #default="{ row }">{{ row.contactName }} {{ row.contactPhone }}</template>
          </el-table-column>
          <el-table-column label="时间" width="150">
            <template #default="{ row }">{{ (row.createTime || '').substring(0, 16) }}</template>
          </el-table-column>
          <el-table-column label="操作" min-width="150">
            <template #default="{ row }">
              <el-button v-if="row.status === 1" type="primary" size="small" @click="confirmOrder(row.id)">确认</el-button>
              <el-button v-if="row.status === 2" type="primary" size="small" @click="departOrder(row.id)">出团</el-button>
              <el-button v-if="row.status === 3" type="success" size="small" @click="completeAdminOrder(row.id)">完成</el-button>
              <el-button v-if="row.status >= 4" type="danger" size="small" @click="deleteAdminOrder(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 用户管理 -->
      <el-tab-pane label="👥 用户管理">
        <el-table :data="adminUsers" stripe size="small">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="username" label="用户名" width="120" />
          <el-table-column prop="nickname" label="昵称" width="100" />
          <el-table-column prop="phone" label="手机号" width="120" />
          <el-table-column label="角色" width="90">
            <template #default="{ row }">{{ roleLabel(row.role) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="70">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{ row }">
              <el-button v-if="row.status === 1" type="danger" size="small" @click="toggleUser(row.id, 0)">禁用</el-button>
              <el-button v-else type="success" size="small" @click="toggleUser(row.id, 1)">启用</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 评价管理 -->
      <el-tab-pane label="⭐ 评价管理">
        <el-table :data="adminReviews" stripe size="small">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column label="用户" width="70">
            <template #default="{ row }">用户{{ row.userId }}</template>
          </el-table-column>
          <el-table-column label="评分" width="80">
            <template #default="{ row }"><span style="color:#faad14">{{ '★'.repeat(row.rating) }}</span></template>
          </el-table-column>
          <el-table-column prop="content" label="内容" min-width="200" />
          <el-table-column label="日期" width="100">
            <template #default="{ row }">{{ (row.createTime || '').substring(0, 10) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="70">
            <template #default="{ row }"><el-button type="danger" size="small" @click="delReview(row.id)">删除</el-button></template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 编辑产品弹窗 -->
    <el-dialog v-model="editProductVisible" title="编辑产品" width="480px">
      <el-form :model="editProductForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="editProductForm.name" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editProductForm.type">
            <el-option label="跟团游" value="group" />
            <el-option label="自由行" value="free" />
            <el-option label="门票" value="ticket" />
            <el-option label="酒店" value="hotel" />
            <el-option label="定制游" value="custom" />
            <el-option label="半自助" value="semi" />
          </el-select>
        </el-form-item>
        <el-form-item label="目的地"><el-input v-model="editProductForm.destination" /></el-form-item>
        <el-form-item label="天数"><el-input-number v-model="editProductForm.days" :min="1" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="editProductForm.price" :min="0" :step="100" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="editProductForm.stock" :min="0" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="editProductForm.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editProductVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProductEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 添加产品弹窗 -->
    <el-dialog v-model="addProductVisible" title="添加新产品" width="480px">
      <el-form :model="addProductForm" label-width="80px">
        <el-form-item label="名称"><el-input v-model="addProductForm.name" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="addProductForm.type">
            <el-option label="跟团游" value="group" />
            <el-option label="自由行" value="free" />
            <el-option label="门票" value="ticket" />
            <el-option label="酒店" value="hotel" />
            <el-option label="定制游" value="custom" />
            <el-option label="半自助" value="semi" />
          </el-select>
        </el-form-item>
        <el-form-item label="目的地"><el-input v-model="addProductForm.destination" /></el-form-item>
        <el-form-item label="天数"><el-input-number v-model="addProductForm.days" :min="1" /></el-form-item>
        <el-form-item label="价格(元)"><el-input-number v-model="addProductForm.price" :min="0" :step="100" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="addProductForm.stock" :min="0" /></el-form-item>
        <el-form-item label="包含项目"><el-input v-model="addProductForm.includes" placeholder="如：往返机票+酒店+门票" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="addProductForm.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addProductVisible = false">取消</el-button>
        <el-button type="primary" @click="addNewProduct">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { adminApi, productApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const overview = ref(null)
const destinations = ref([])
const productRanking = ref([])
const conversion = ref(null)
const aiStats = ref(null)
const aiLogs = ref([])
const adminProducts = ref([])
const adminOrders = ref([])
const adminUsers = ref([])
const adminReviews = ref([])
const editProductVisible = ref(false)
const addProductVisible = ref(false)

const editProductForm = reactive({ id: null, name: '', type: 'group', destination: '', days: 1, price: 0, stock: 0, description: '' })
const addProductForm = reactive({ name: '', type: 'group', destination: '', days: 1, price: 0, stock: 50, includes: '', description: '' })

const successRate = computed(() => {
  if (!aiStats.value) return '0'
  const t = (aiStats.value.successCount || 0) + (aiStats.value.failedCount || 0)
  return t > 0 ? ((aiStats.value.successCount || 0) / t * 100).toFixed(1) : '0'
})

const overviewCards = computed(() => {
  if (!overview.value) return []
  return [
    { label: '在售产品', value: overview.value.productCount || 0, bg: 'linear-gradient(135deg,#667eea,#764ba2)' },
    { label: '行程规划', value: overview.value.planCount || 0, bg: 'linear-gradient(135deg,#f093fb,#f5576c)' },
    { label: '总订单', value: overview.value.orderCount || 0, bg: 'linear-gradient(135deg,#4facfe,#00f2fe)' },
    { label: 'AI成功', value: overview.value.aiSuccessCount || 0, bg: 'linear-gradient(135deg,#43e97b,#38f9d7)' },
    { label: 'AI失败', value: overview.value.aiFailedCount || 0, bg: 'linear-gradient(135deg,#fa709a,#fee140)' },
    { label: '评价数', value: overview.value.reviewCount || 0, bg: 'linear-gradient(135deg,#a18cd1,#fbc2eb)' }
  ]
})

function typeLabel(t) {
  return { group: '跟团游', free: '自由行', ticket: '门票', hotel: '酒店', custom: '定制游', semi: '半自助' }[t] || t
}
function roleLabel(r) { return { 0: '普通用户', 1: '旅行社', 2: '管理员' }[r] || '未知' }
function orderStatusLabel(s) {
  return { 0: '待支付', 1: '已支付', 2: '已确认', 3: '已出团', 4: '已完成', 5: '已退款', 6: '已取消' }[s] || '未知'
}
function orderStatusType(s) {
  return { 0: 'warning', 1: 'info', 2: '', 3: 'info', 4: 'success', 5: '', 6: 'info' }[s] || ''
}

async function loadAll() {
  try { const r = await adminApi.overview(); if (r.code === 200) overview.value = r.data } catch (e) { /* handled */ }
  try { const r = await adminApi.destinations(); if (r.code === 200) destinations.value = r.data || [] } catch (e) { /* handled */ }
  try { const r = await adminApi.productRanking(); if (r.code === 200) productRanking.value = r.data || [] } catch (e) { /* handled */ }
  try { const r = await adminApi.conversion(); if (r.code === 200) conversion.value = r.data } catch (e) { /* handled */ }
  try { const r = await adminApi.aiStats(); if (r.code === 200) aiStats.value = r.data } catch (e) { /* handled */ }
  try { const r = await adminApi.aiLogs(); if (r.code === 200) aiLogs.value = r.data || [] } catch (e) { /* handled */ }
  try { const r = await productApi.list(); if (r.code === 200) adminProducts.value = r.data || [] } catch (e) { /* handled */ }
  try { const r = await adminApi.allOrders(); if (r.code === 200) adminOrders.value = r.data || [] } catch (e) { /* handled */ }
  try { const r = await adminApi.allUsers(); if (r.code === 200) adminUsers.value = r.data || [] } catch (e) { /* handled */ }
  try { const r = await adminApi.allReviews(); if (r.code === 200) adminReviews.value = r.data || [] } catch (e) { /* handled */ }
}

async function offlineProduct(id) {
  try { await ElMessageBox.confirm('确定下架？'); const r = await adminApi.offlineProduct(id); if (r.code === 200) { ElMessage.success('已下架'); loadAll() } } catch (e) { /* cancelled */ }
}
async function onlineProduct(id) {
  try { const r = await adminApi.onlineProduct(id); if (r.code === 200) { ElMessage.success('已上架'); loadAll() } } catch (e) { /* handled */ }
}
async function toggleUser(id, status) {
  const action = status === 1 ? '启用' : '禁用'
  try { await ElMessageBox.confirm(`确定${action}该用户？`); const r = await adminApi.toggleUser(id, status); if (r.code === 200) { ElMessage.success('操作成功'); loadAll() } } catch (e) { /* cancelled */ }
}
async function delReview(id) {
  try { await ElMessageBox.confirm('确定删除该评价？'); const r = await adminApi.deleteReview(id); if (r.code === 200) { ElMessage.success('已删除'); loadAll() } } catch (e) { /* cancelled */ }
}
async function confirmOrder(id) {
  try { const r = await adminApi.confirmOrder(id); if (r.code === 200) { ElMessage.success('已确认'); loadAll() } } catch (e) { /* handled */ }
}
async function departOrder(id) {
  try { await ElMessageBox.confirm('确认出团？'); const r = await adminApi.departOrder(id); if (r.code === 200) { ElMessage.success('已出团'); loadAll() } } catch (e) { /* cancelled */ }
}
async function completeAdminOrder(id) {
  try { const r = await orderApi_complete(id); if (r.code === 200) { ElMessage.success('已完成'); loadAll() } } catch (e) { /* handled */ }
}
async function deleteAdminOrder(id) {
  try { await ElMessageBox.confirm('确定删除该订单？删除不可恢复！'); const r = await adminApi.deleteOrder(id); if (r.code === 200) { ElMessage.success('已删除'); loadAll() } } catch (e) { /* cancelled */ }
}

function editAdminProduct(p) {
  editProductForm.id = p.id; editProductForm.name = p.name; editProductForm.type = p.type
  editProductForm.destination = p.destination; editProductForm.days = p.days
  editProductForm.price = p.price; editProductForm.stock = p.stock; editProductForm.description = p.description || ''
  editProductVisible.value = true
}

async function saveProductEdit() {
  try {
    const r = await adminApi.updateProduct(editProductForm)
    if (r.code === 200) { ElMessage.success('修改成功'); editProductVisible.value = false; loadAll() }
  } catch (e) { /* handled */ }
}

function showAddProduct() {
  addProductForm.name = ''; addProductForm.type = 'group'; addProductForm.destination = ''
  addProductForm.days = 1; addProductForm.price = 0; addProductForm.stock = 50
  addProductForm.includes = ''; addProductForm.description = ''
  addProductVisible.value = true
}

async function addNewProduct() {
  if (!addProductForm.name || !addProductForm.destination) {
    ElMessage.warning('请填写产品名称和目的地')
    return
  }
  try {
    const r = await productApi.add(addProductForm)
    if (r && r.code === 200) {
      ElMessage.success('添加成功')
      addProductVisible.value = false
      loadAll()
    }
  } catch (e) { /* handled */ }
}

import { orderApi } from '../api'
const orderApi_complete = orderApi.complete

onMounted(loadAll)
</script>
