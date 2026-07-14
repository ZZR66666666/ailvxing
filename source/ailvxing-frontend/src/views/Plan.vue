<template>
  <div class="plan-page">
    <!-- 生成规划卡片 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header>
        <span style="font-size: 18px; font-weight: bold">✨ 智能生成旅行规划</span>
      </template>
      <el-form :model="planForm" inline>
        <el-form-item label="目的地">
          <el-input v-model="planForm.destination" placeholder="如：三亚、成都、丽江" style="width: 180px" />
        </el-form-item>
        <el-form-item label="天数">
          <el-input-number v-model="planForm.days" :min="1" :max="30" style="width: 120px" />
        </el-form-item>
        <el-form-item label="预算(元)">
          <el-input-number v-model="planForm.budget" :min="0" :step="1000" style="width: 150px" placeholder="可选" />
        </el-form-item>
        <el-form-item label="预算等级">
          <el-radio-group v-model="planForm.budgetLevel">
            <el-radio-button value="economy">经济型</el-radio-button>
            <el-radio-button value="comfort">舒适型</el-radio-button>
            <el-radio-button value="luxury">豪华型</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="兴趣标签">
          <el-input v-model="planForm.interestTags" placeholder="如：海滩,美食,历史" style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="generatePlan" :loading="generating">
            🚀 生成规划
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 规划结果 -->
    <el-card v-if="planResult" shadow="hover" style="margin-bottom: 20px">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center">
          <span style="font-size: 18px; font-weight: bold; color: #1890ff">✅ {{ planResult.title }}</span>
          <div>
            <el-button-group>
              <el-button :type="planResult.budgetLevel === 'economy' ? 'primary' : ''" size="small" @click="regenPlan('economy')">经济型</el-button>
              <el-button :type="planResult.budgetLevel === 'comfort' ? 'primary' : ''" size="small" @click="regenPlan('comfort')">舒适型</el-button>
              <el-button :type="planResult.budgetLevel === 'luxury' ? 'primary' : ''" size="small" @click="regenPlan('luxury')">豪华型</el-button>
            </el-button-group>
          </div>
        </div>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="目的地">{{ planResult.destination }}</el-descriptions-item>
        <el-descriptions-item label="天数">{{ planResult.days }} 天</el-descriptions-item>
        <el-descriptions-item label="预算等级">{{ budgetLabel(planResult.budgetLevel) }}</el-descriptions-item>
        <el-descriptions-item label="预算金额">¥{{ planResult.budget || '未设定' }}</el-descriptions-item>
        <el-descriptions-item label="兴趣标签" :span="2">
          <el-tag v-for="tag in (planResult.interestTags || '').split(',').filter(Boolean)" :key="tag" size="small" style="margin-right: 6px">{{ tag }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 每日行程 -->
      <div v-if="planDetail && planDetail.days" style="margin-top: 20px">
        <div v-if="planDetail.summary" style="color: #666; margin-bottom: 12px">{{ planDetail.summary }}</div>
        <div v-if="planDetail.tips" style="color: #fa8c16; margin-bottom: 12px; font-size: 13px">💡 {{ planDetail.tips }}</div>
        <div v-if="planDetail.totalBudget" style="color: #ff4d4f; font-weight: bold; margin-bottom: 8px; font-size: 18px">💰 预估总费用：¥{{ planDetail.totalBudget }}</div>
        <div v-if="planDetail.budgetDetail" style="color: #999; font-size: 13px; margin-bottom: 16px">📊 {{ planDetail.budgetDetail }}</div>

        <el-timeline>
          <el-timeline-item
            v-for="day in planDetail.days"
            :key="day.day"
            :timestamp="(day.title || '第' + day.day + '天') + (day.dayBudget ? ' · ¥' + day.dayBudget : '')"
            placement="top"
            color="#1890ff"
          >
            <el-card v-for="act in day.activities" :key="act.time + act.activity" shadow="hover" size="small" style="margin-bottom: 6px">
              <div style="display: flex; gap: 12px; align-items: center">
                <el-tag size="small" style="min-width: 50px; text-align: center">{{ act.time }}</el-tag>
                <span style="flex: 1"><strong>{{ act.activity }}</strong><span v-if="act.location" style="color: #999"> 📍 {{ act.location }}</span></span>
                <span v-if="act.duration" style="color: #999; font-size: 13px">{{ act.duration }}</span>
                <span v-if="act.cost" style="color: #ff4d4f; font-weight: bold">¥{{ act.cost }}</span>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>

      <div style="margin-top: 20px; text-align: center" v-if="!planDetail || !planDetail.days">
        <pre style="white-space: pre-wrap; text-align: left; background: #f6f8fa; padding: 16px; border-radius: 8px">{{ planResult.planDetail }}</pre>
      </div>

      <!-- 推荐产品 -->
      <div v-if="recommendations.length" style="margin-top: 20px">
        <h4 style="color: #fa8c16">🛒 智能推荐产品（共{{ recommendations.length }}个）</h4>
        <el-row :gutter="12">
          <el-col :span="6" v-for="r in recommendations" :key="r.id">
            <el-card shadow="hover" class="recommend-card" @click="bookProduct(r.productId)">
              <p style="font-weight: bold; color: #fa8c16; margin: 0">{{ getProductName(r.productId) }}</p>
              <p style="font-size: 12px; color: #666; margin: 4px 0">{{ r.matchReason || '智能匹配' }}</p>
              <p style="color: #ff4d4f; font-weight: bold; margin: 4px 0">立即预订 →</p>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <div style="margin-top: 20px">
        <el-button type="success" size="small" @click="favoritePlan(planResult.id)">❤️ 收藏</el-button>
        <el-button type="primary" size="small" @click="sharePlan(planResult.id)">📤 分享</el-button>
      </div>
    </el-card>

    <!-- 我的行程列表 -->
    <el-card shadow="hover">
      <template #header><span style="font-size: 18px; font-weight: bold">📋 我的行程</span></template>
      <el-row :gutter="16" v-if="plans.length">
        <el-col :span="8" v-for="p in plans" :key="p.id" style="margin-bottom: 16px">
          <el-card shadow="hover" class="plan-item">
            <h3 style="color: #1890ff; margin: 0 0 8px">{{ p.title || '未命名' }}</h3>
            <p>📍 {{ p.destination }} | 📅 {{ p.days }}天 | 💰 {{ budgetLabel(p.budgetLevel) }}</p>
            <p>¥{{ p.budget || '未设定' }}</p>
            <el-tag v-for="tag in (p.interestTags || '').split(',').filter(Boolean)" :key="tag" size="small" style="margin-right: 4px">{{ tag }}</el-tag>
            <div style="margin-top: 12px">
              <el-button type="primary" size="small" @click="viewPlan(p.id)">详情</el-button>
              <el-button size="small" @click="editPlan(p)">编辑</el-button>
              <el-button type="success" size="small" @click="sharePlan(p.id)">分享</el-button>
              <el-button type="danger" size="small" @click="deletePlan(p.id)">删除</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
      <el-empty v-else description="暂无行程规划，快去生成一个吧！" />
    </el-card>

    <!-- 编辑行程弹窗 -->
    <el-dialog v-model="editVisible" title="编辑行程规划" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" placeholder="行程标题" />
        </el-form-item>
        <el-form-item label="预算等级">
          <el-select v-model="editForm.budgetLevel">
            <el-option label="经济型" value="economy" />
            <el-option label="舒适型" value="comfort" />
            <el-option label="豪华型" value="luxury" />
          </el-select>
        </el-form-item>
        <el-form-item label="详情">
          <el-input v-model="editForm.planDetail" type="textarea" :rows="8" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 预订弹窗 -->
    <el-dialog v-model="bookVisible" title="预订产品" width="450px">
      <p style="color:#666;margin-bottom:12px">{{ bookTargetName }}</p>
      <el-form :model="bookForm" label-width="80px">
        <el-form-item label="联系人"><el-input v-model="bookForm.contactName" placeholder="姓名" /></el-form-item>
        <el-form-item label="电话"><el-input v-model="bookForm.contactPhone" placeholder="手机号" /></el-form-item>
        <el-form-item label="人数"><el-input-number v-model="bookForm.quantity" :min="1" :max="10" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="bookForm.remark" type="textarea" :rows="2" /></el-form-item>
        <p style="color:#ff4d4f;font-size:16px">💰 预估价格：¥{{ bookTargetPrice * bookForm.quantity }}</p>
      </el-form>
      <template #footer>
        <el-button @click="bookVisible = false">取消</el-button>
        <el-button type="danger" @click="submitBook" :loading="booking">确认下单</el-button>
      </template>
    </el-dialog>

    <!-- 支付弹窗 -->
    <el-dialog v-model="payVisible" title="选择支付方式" width="400px">
      <p style="text-align:center;color:#666;margin-bottom:16px">订单号：{{ payOrderId }}</p>
      <p style="text-align:center;color:#ff4d4f;font-size:20px;margin-bottom:24px">¥{{ payAmount }}</p>
      <div style="display:flex;gap:12px;justify-content:center">
        <el-button type="primary" size="large" @click="doPayNow('wechat')">微信支付</el-button>
        <el-button type="primary" size="large" @click="doPayNow('alipay')">支付宝</el-button>
        <el-button type="success" size="large" @click="doPayNow('bank')">银行卡</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { planApi, productApi, orderApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

// 产品名缓存
const productNames = ref({})
async function loadProductNames() {
  try {
    const res = await productApi.list()
    if (res.code === 200 && res.data) {
      res.data.forEach(p => { productNames.value[p.id] = p.name })
    }
  } catch (e) { /* ignore */ }
}
function getProductName(pid) { return productNames.value[pid] || '产品#' + pid }

// 预订相关
const bookVisible = ref(false)
const booking = ref(false)
const bookTargetId = ref(null)
const bookTargetName = ref('')
const bookTargetPrice = ref(0)
const bookForm = reactive({ contactName: '', contactPhone: '', quantity: 1, remark: '' })

function bookProduct(pid) {
  bookTargetId.value = pid
  bookTargetName.value = productNames.value[pid] || '产品#' + pid
  bookTargetPrice.value = 0
  bookForm.contactName = ''; bookForm.contactPhone = ''; bookForm.quantity = 1; bookForm.remark = ''
  // 获取产品价格
  productApi.detail(pid).then(res => {
    if (res.code === 200) bookTargetPrice.value = res.data.price || 0
  }).catch(() => {})
  bookVisible.value = true
}

const payVisible = ref(false)
const payOrderId = ref(null)
const payAmount = ref(0)

async function submitBook() {
  if (!bookForm.contactName || !bookForm.contactPhone) {
    ElMessage.warning('请填写联系人和电话')
    return
  }
  booking.value = true
  try {
    const res = await orderApi.create({
      productId: bookTargetId.value,
      contactName: bookForm.contactName,
      contactPhone: bookForm.contactPhone,
      remark: bookForm.remark
    })
    if (res.code === 200) {
      payOrderId.value = res.data
      payAmount.value = bookTargetPrice.value * bookForm.quantity
      bookVisible.value = false
      payVisible.value = true
    }
  } catch (e) { /* handled */ }
  booking.value = false
}

async function doPayNow(method) {
  console.log('paying', payOrderId.value, method)
  try {
    const res = await orderApi.pay(payOrderId.value, method)
    console.log('pay result', res)
    if (res && res.code === 200) {
      ElMessage.success('支付成功！')
      payVisible.value = false
    } else {
      ElMessage.error(res?.msg || '支付失败')
    }
  } catch (e) {
    console.error('pay error', e)
    ElMessage.error('支付请求失败')
  }
}

const generating = ref(false)
const plans = ref([])
const planResult = ref(null)
const planDetail = ref(null)
const recommendations = ref([])
const editVisible = ref(false)

const planForm = reactive({
  destination: '', days: 5, budget: null, budgetLevel: 'comfort', interestTags: '', companionIds: ''
})
const editForm = reactive({ id: null, title: '', budgetLevel: '', planDetail: '' })

function budgetLabel(level) {
  return { economy: '经济型', comfort: '舒适型', luxury: '豪华型' }[level] || level
}

async function generatePlan() {
  if (!planForm.destination) {
    ElMessage.warning('请输入目的地')
    return
  }
  generating.value = true
  try {
    const res = await planApi.generate(planForm)
    if (res.code === 200) {
      planResult.value = res.data
      try { planDetail.value = typeof res.data.planDetail === 'string' ? JSON.parse(res.data.planDetail) : res.data.planDetail } catch (e) { planDetail.value = null }
      loadPlans()
      if (res.data.id) loadRecommendations(res.data.id)
      ElMessage.success('规划生成成功！')
    } else {
      ElMessage.error(res.msg || '生成失败')
    }
  } catch (e) { /* handled */ }
  generating.value = false
}

async function regenPlan(level) {
  if (!planResult.value) return
  generating.value = true
  try {
    const res = await planApi.regenerate(planResult.value.id, level)
    if (res.code === 200) {
      planResult.value = res.data
      try { planDetail.value = typeof res.data.planDetail === 'string' ? JSON.parse(res.data.planDetail) : res.data.planDetail } catch (e) { planDetail.value = null }
      if (res.data.id) loadRecommendations(res.data.id)
      ElMessage.success('已切换方案')
    }
  } catch (e) { /* handled */ }
  generating.value = false
}

async function loadPlans() {
  try {
    const res = await planApi.list()
    if (res.code === 200) plans.value = res.data || []
  } catch (e) { /* handled */ }
}

async function viewPlan(id) {
  try {
    const res = await planApi.detail(id)
    if (res.code === 200) {
      planResult.value = res.data
      try { planDetail.value = typeof res.data.planDetail === 'string' ? JSON.parse(res.data.planDetail) : res.data.planDetail } catch (e) { planDetail.value = null }
      loadRecommendations(id)
    }
  } catch (e) { /* handled */ }
}

async function loadRecommendations(planId) {
  try {
    const res = await planApi.recommendations(planId)
    if (res.code === 200) recommendations.value = res.data || []
  } catch (e) { recommendations.value = [] }
}

function editPlan(p) {
  editForm.id = p.id
  editForm.title = p.title || ''
  editForm.budgetLevel = p.budgetLevel || 'comfort'
  editForm.planDetail = p.planDetail || ''
  editVisible.value = true
}

async function saveEdit() {
  try {
    const res = await planApi.update({ id: editForm.id, title: editForm.title, budgetLevel: editForm.budgetLevel, planDetail: editForm.planDetail })
    if (res.code === 200) {
      ElMessage.success('修改成功')
      editVisible.value = false
      loadPlans()
    }
  } catch (e) { /* handled */ }
}

async function deletePlan(id) {
  try {
    await ElMessageBox.confirm('确定删除该行程？删除后不可恢复', '警告', { type: 'warning' })
    const res = await planApi.delete(id)
    if (res.code === 200) {
      ElMessage.success('已删除')
      loadPlans()
      if (planResult.value?.id === id) planResult.value = null
    }
  } catch (e) { /* cancelled */ }
}

async function favoritePlan(id) {
  try {
    const res = await planApi.favorite(id)
    if (res.code === 200) ElMessage.success('收藏成功！')
  } catch (e) { /* handled */ }
}

async function sharePlan(id) {
  try {
    const res = await planApi.share(id)
    if (res.code === 200) {
      const link = `${location.origin}/api/plan/shared/${res.data}`
      ElMessageBox.alert(`分享链接：${link}`, '分享行程', { confirmButtonText: '复制', callback: () => { navigator.clipboard?.writeText(link); ElMessage.success('已复制') } })
    }
  } catch (e) { /* handled */ }
}

function orderProduct(productId) {
  ElMessage.info('预订功能，请前往旅游产品页面操作')
}

onMounted(() => { loadPlans(); loadProductNames() })
</script>

<style scoped>
.plan-item { cursor: pointer; transition: all 0.3s }
.plan-item:hover { transform: translateY(-2px) }
.recommend-card { cursor: pointer; border: 1px solid #ffd591; background: linear-gradient(135deg, #fff7e6, #fffbe6); transition: all 0.3s }
.recommend-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(250,140,22,0.2) }
</style>
