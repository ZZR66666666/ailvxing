<template>
  <div>
    <!-- 我的订单 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <template #header><span style="font-weight: bold; font-size: 18px">📋 我的订单</span></template>
      <el-table :data="orders" stripe v-if="orders.length" style="width: 100%">
        <el-table-column label="订单号" prop="orderNo" width="180" />
        <el-table-column label="产品" width="140">
          <template #default="{ row }">{{ row.items?.[0]?.productName || '-' }}</template>
        </el-table-column>
        <el-table-column label="金额" width="100">
          <template #default="{ row }"><span style="color:#ff4d4f;font-weight:bold">¥{{ row.totalAmount || row.payAmount || '0' }}</span></template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="联系人" prop="contactName" width="100" />
        <el-table-column label="时间" width="150">
          <template #default="{ row }">{{ (row.createTime || '').substring(0, 16) }}</template>
        </el-table-column>
        <el-table-column label="操作" min-width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="primary" size="small" @click="payOrder(row.id)">支付</el-button>
            <el-button v-if="row.status === 0" size="small" @click="cancelOrder(row.id)">取消</el-button>
            <el-button v-if="row.status === 1" size="small" @click="applyRefund(row.id)">退款</el-button>
            <el-button v-if="row.status === 2" size="small" @click="applyRefund(row.id)">退款</el-button>
            <el-button v-if="row.status === 3" type="primary" size="small" @click="completeOrder(row.id)">确认完成</el-button>
            <el-button v-if="row.status === 3" size="small" @click="applyRefund(row.id)">退款</el-button>
            <el-button v-if="row.status === 4" type="success" size="small" @click="addReview(row)">评价</el-button>
            <el-button v-if="row.status >= 4" type="danger" size="small" @click="deleteOrder(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-else description="暂无订单，快去预订旅游产品吧！" />
    </el-card>

    <!-- 我的评价 -->
    <el-card shadow="hover">
      <template #header><span style="font-weight: bold; font-size: 18px">⭐ 我的评价</span></template>
      <div v-if="myReviews.length">
        <div v-for="r in myReviews" :key="r.id" style="background:#fafafa;padding:12px;border-radius:8px;margin-bottom:8px">
          <div style="display:flex;justify-content:space-between;margin-bottom:6px">
            <span style="color:#faad14;font-size:18px">{{ '★'.repeat(r.rating) }}{{ '☆'.repeat(5 - r.rating) }}</span>
            <span style="color:#999;font-size:12px">{{ (r.createTime || '').substring(0, 10) }}</span>
          </div>
          <p>{{ r.content }}</p>
          <div style="text-align:right;margin-top:8px">
            <el-button size="small" @click="editReview(r)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteReview(r.id)">删除</el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无评价" />
    </el-card>

    <!-- 支付弹窗 -->
    <el-dialog v-model="payVisible" title="选择支付方式" width="400px">
      <div style="display:flex;gap:12px;justify-content:center">
        <el-button type="primary" @click="doPay('wechat')">微信支付</el-button>
        <el-button type="primary" @click="doPay('alipay')">支付宝</el-button>
        <el-button type="success" @click="doPay('bank')">银行卡</el-button>
      </div>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog v-model="reviewDialogVisible" :title="editingReviewId ? '编辑评价' : '提交评价'" width="450px">
      <el-form label-width="60px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="reviewForm.content" type="textarea" :rows="4" placeholder="分享您的体验..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { orderApi, reviewApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const myReviews = ref([])
const payVisible = ref(false)
const payOrderId = ref(null)
const reviewDialogVisible = ref(false)
const editingReviewId = ref(null)
const reviewForm = reactive({ orderId: null, productId: null, rating: 5, content: '' })

function statusLabel(s) {
  const map = { 0: '待支付', 1: '已支付', 2: '已确认', 3: '已出团', 4: '已完成', 5: '已退款', 6: '已取消' }
  return map[s] || '未知'
}
function statusType(s) {
  const map = { 0: 'warning', 1: 'info', 2: '', 3: 'info', 4: 'success', 5: '', 6: 'info' }
  return map[s] || ''
}

async function loadOrders() {
  try {
    const res = await orderApi.my()
    if (res.code === 200) orders.value = res.data || []
  } catch (e) { /* handled */ }
}

async function loadReviews() {
  try {
    const res = await reviewApi.my()
    if (res.code === 200) myReviews.value = res.data || []
  } catch (e) { /* handled */ }
}

async function payOrder(id) { payOrderId.value = id; payVisible.value = true }

async function doPay(method) {
  try {
    const res = await orderApi.pay(payOrderId.value, method)
    if (res.code === 200) { ElMessage.success('支付成功'); payVisible.value = false; loadOrders() }
  } catch (e) { /* handled */ }
}

async function cancelOrder(id) {
  try {
    await ElMessageBox.confirm('确定取消该订单？', '警告', { type: 'warning' })
    const res = await orderApi.cancel(id)
    if (res.code === 200) { ElMessage.success('已取消'); loadOrders() }
  } catch (e) { /* cancelled */ }
}

async function applyRefund(id) {
  try {
    await ElMessageBox.confirm('确定申请退款？', '退款', { type: 'warning' })
    const res = await orderApi.refund(id)
    if (res.code === 200) { ElMessage.success('退款成功'); loadOrders() }
  } catch (e) { /* cancelled */ }
}

async function completeOrder(id) {
  try {
    const res = await orderApi.complete(id)
    if (res.code === 200) { ElMessage.success('订单已完成'); loadOrders() }
  } catch (e) { /* handled */ }
}

async function deleteOrder(id) {
  try {
    await ElMessageBox.confirm('确定删除该订单？', '警告', { type: 'warning' })
    const res = await orderApi.delete(id)
    if (res.code === 200) { ElMessage.success('已删除'); loadOrders() }
  } catch (e) { /* cancelled */ }
}

function addReview(row) {
  editingReviewId.value = null
  reviewForm.orderId = row.id
  reviewForm.productId = row.items?.[0]?.productId
  reviewForm.rating = 5; reviewForm.content = ''
  reviewDialogVisible.value = true
}

function editReview(r) {
  editingReviewId.value = r.id
  reviewForm.orderId = r.orderId; reviewForm.productId = r.productId
  reviewForm.rating = r.rating; reviewForm.content = r.content || ''
  reviewDialogVisible.value = true
}

async function submitReview() {
  if (!reviewForm.content.trim()) { ElMessage.warning('请填写评价内容'); return }
  try {
    let res
    if (editingReviewId.value) {
      res = await reviewApi.update({ id: editingReviewId.value, rating: reviewForm.rating, content: reviewForm.content })
    } else {
      res = await reviewApi.add(reviewForm)
    }
    if (res.code === 200) { ElMessage.success('评价提交成功'); reviewDialogVisible.value = false; loadReviews() }
  } catch (e) { /* handled */ }
}

async function deleteReview(id) {
  try {
    await ElMessageBox.confirm('确定删除该评价？', '警告', { type: 'warning' })
    const res = await reviewApi.delete(id)
    if (res.code === 200) { ElMessage.success('已删除'); loadReviews() }
  } catch (e) { /* cancelled */ }
}

onMounted(() => { loadOrders(); loadReviews() })
</script>
