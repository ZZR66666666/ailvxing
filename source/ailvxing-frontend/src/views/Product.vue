<template>
  <div>
    <el-card shadow="hover" style="margin-bottom: 20px">
      <div style="display: flex; gap: 12px; align-items: center">
        <span style="font-weight: bold">🔍 搜索旅游产品</span>
        <div style="flex: 1; max-width: 300px">
          <el-input v-model="searchDest" placeholder="按目的地搜索" clearable @clear="loadProducts" @keyup.enter="searchProducts" />
        </div>
        <el-button type="primary" @click="searchProducts">搜索</el-button>
        <el-button @click="loadProducts">查看全部</el-button>
      </div>
      <div style="margin-top: 12px">
        <el-tag v-for="t in tags" :key="t.id" size="small" style="margin: 0 6px 6px 0; cursor: pointer" @click="searchByTag(t.name)">{{ t.name }}</el-tag>
      </div>
    </el-card>

    <el-row :gutter="16">
      <el-col :span="8" v-for="p in products" :key="p.id" style="margin-bottom: 16px">
        <el-card shadow="hover" class="product-item">
          <h3 style="color: #fa8c16; margin: 0 0 8px">{{ p.name }}</h3>
          <p>📍 {{ p.destination }} | 🏷️ {{ typeLabel(p.type) }} | 📅 {{ p.days }}天</p>
          <p style="font-size: 13px; color: #999">{{ p.includes || '' }}</p>
          <p>{{ p.description || '' }}</p>
          <p class="product-price">¥{{ p.price }}<span style="font-size: 12px; font-weight: normal; color: #999">/人</span>
            <el-tag v-if="p.stock > 0" type="success" size="small">库存:{{ p.stock }}</el-tag>
            <el-tag v-else type="danger" size="small">已售罄</el-tag>
          </p>
          <div style="margin-top: 12px">
            <el-button type="primary" size="small" @click="viewDetail(p)">详情</el-button>
            <el-button type="danger" size="small" @click="bookProduct(p)" :disabled="p.stock <= 0">预订</el-button>
            <el-button size="small" @click="viewReviews(p.id)">评价</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!products.length" description="暂无产品，换个目的地试试" />

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" :title="detailProduct?.name" width="550px">
      <el-descriptions v-if="detailProduct" :column="2" border>
        <el-descriptions-item label="目的地">{{ detailProduct.destination }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ typeLabel(detailProduct.type) }}</el-descriptions-item>
        <el-descriptions-item label="天数">{{ detailProduct.days }}天</el-descriptions-item>
        <el-descriptions-item label="价格"><span style="color:#ff4d4f;font-size:18px;font-weight:bold">¥{{ detailProduct.price }}/人</span></el-descriptions-item>
        <el-descriptions-item label="包含项目">{{ detailProduct.includes || '无' }}</el-descriptions-item>
        <el-descriptions-item label="库存">{{ detailProduct.stock > 0 ? '剩余' + detailProduct.stock + '份' : '已售罄' }}</el-descriptions-item>
        <el-descriptions-item label="有效期" :span="2">{{ detailProduct.startDate || '' }} ~ {{ detailProduct.endDate || '' }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ detailProduct.description || '暂无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="danger" @click="detailVisible = false; bookProduct(detailProduct)" :disabled="detailProduct?.stock <= 0">立即预订</el-button>
      </template>
    </el-dialog>

    <!-- 预订弹窗 -->
    <el-dialog v-model="bookVisible" title="预订产品" width="480px">
      <p style="color:#666;margin-bottom:16px">{{ bookTarget?.name }}</p>
      <el-form :model="bookForm" label-width="80px">
        <el-form-item label="联系人">
          <el-input v-model="bookForm.contactName" placeholder="姓名" />
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="bookForm.contactPhone" placeholder="手机号" />
        </el-form-item>
        <el-form-item label="人数">
          <el-input-number v-model="bookForm.quantity" :min="1" :max="bookTarget?.stock || 1" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="bookForm.remark" type="textarea" :rows="2" placeholder="特殊需求（选填）" />
        </el-form-item>
        <p style="color:#ff4d4f;font-size:16px">💰 预估价格：¥{{ (bookTarget?.price || 0) * bookForm.quantity }}</p>
      </el-form>
      <template #footer>
        <el-button @click="bookVisible = false">取消</el-button>
        <el-button type="danger" @click="submitOrder">确认下单</el-button>
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

    <!-- 评价弹窗 -->
    <el-dialog v-model="reviewVisible" title="产品评价" width="500px">
      <div v-if="reviews.length">
        <div v-for="r in reviews" :key="r.id" style="background:#fafafa;padding:12px;border-radius:8px;margin-bottom:8px">
          <div style="display:flex;justify-content:space-between;margin-bottom:6px">
            <span style="color:#faad14">{{ '★'.repeat(r.rating) }}{{ '☆'.repeat(5 - r.rating) }}</span>
            <span style="color:#999;font-size:12px">{{ (r.createTime || '').substring(0, 10) }}</span>
          </div>
          <p style="font-size:14px">{{ r.content }}</p>
        </div>
      </div>
      <el-empty v-else description="暂无评价" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { productApi, orderApi, reviewApi } from '../api'
import { ElMessage } from 'element-plus'

const products = ref([])
const tags = ref([])
const searchDest = ref('')
const detailVisible = ref(false)
const detailProduct = ref(null)
const bookVisible = ref(false)
const bookTarget = ref(null)
const reviewVisible = ref(false)
const reviews = ref([])

const bookForm = reactive({ contactName: '', contactPhone: '', quantity: 1, remark: '' })

function typeLabel(t) {
  return { group: '跟团游', free: '自由行', ticket: '门票', hotel: '酒店', custom: '定制游', semi: '半自助' }[t] || t
}

async function loadProducts() {
  try {
    const res = await productApi.list()
    if (res.code === 200) products.value = res.data || []
  } catch (e) { /* handled */ }
}

async function searchProducts() {
  if (!searchDest.value) { loadProducts(); return }
  try {
    const res = await productApi.search(searchDest.value)
    if (res.code === 200) products.value = res.data || []
  } catch (e) { /* handled */ }
}

function searchByTag(tag) {
  searchDest.value = tag
  searchProducts()
}

async function viewDetail(p) {
  try {
    const res = await productApi.detail(p.id)
    if (res.code === 200) { detailProduct.value = res.data; detailVisible.value = true }
  } catch (e) { /* handled */ }
}

function bookProduct(p) {
  bookTarget.value = p
  bookForm.contactName = ''; bookForm.contactPhone = ''; bookForm.quantity = 1; bookForm.remark = ''
  bookVisible.value = true
}

const payVisible = ref(false)
const payOrderId = ref(null)
const payAmount = ref(0)

async function submitOrder() {
  if (!bookForm.contactName || !bookForm.contactPhone) {
    ElMessage.warning('请填写联系人和电话')
    return
  }
  try {
    const res = await orderApi.create({
      productId: bookTarget.value.id,
      contactName: bookForm.contactName,
      contactPhone: bookForm.contactPhone,
      remark: bookForm.remark
    })
    if (res.code === 200) {
      payOrderId.value = res.data
      payAmount.value = (bookTarget.value?.price || 0) * bookForm.quantity
      bookVisible.value = false
      payVisible.value = true
    }
  } catch (e) { /* handled */ }
}

async function doPayNow(method) {
  console.log('paying', payOrderId.value, method)
  try {
    const res = await orderApi.pay(payOrderId.value, method)
    console.log('pay result', res)
    if (res && res.code === 200) {
      ElMessage.success('支付成功！')
      payVisible.value = false
      loadProducts()
    } else {
      ElMessage.error(res?.msg || '支付失败')
    }
  } catch (e) {
    console.error('pay error', e)
    ElMessage.error('支付请求失败')
  }
}

async function viewReviews(productId) {
  try {
    const res = await reviewApi.ofProduct(productId)
    if (res.code === 200) reviews.value = res.data || []
    reviewVisible.value = true
  } catch (e) { /* handled */ }
}

onMounted(async () => {
  loadProducts()
  try {
    const res = await productApi.tags()
    if (res.code === 200) tags.value = res.data || []
  } catch (e) { /* handled */ }
})
</script>

<style scoped>
.product-item { transition: all 0.3s }
.product-item:hover { transform: translateY(-2px) }
.product-price { color: #ff4d4f; font-size: 18px; font-weight: bold; margin: 8px 0 }
</style>
