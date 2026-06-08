<template>
  <div>
    <a-tabs v-model:activeKey="activeTab">
      <a-tab-pane key="unpaid">
        <template #tab>
          待缴费
          <a-badge v-if="unpaidPayments.length" :count="unpaidPayments.length" :offset="[6, 0]" />
        </template>
        <a-table
          :columns="unpaidColumns"
          :data-source="unpaidPayments"
          row-key="id"
          :pagination="false"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'type'">
              <a-tag color="blue">{{ paymentTypeMap[record.type] || record.type }}</a-tag>
            </template>
            <template v-if="column.key === 'amount'">
              <MoneyDisplay :value="record.amount" />
            </template>
            <template v-if="column.key === 'action'">
              <a-space>
                <a-radio-group v-model:value="record._payMethod" size="small">
                  <a-radio-button value="CARD">银行卡</a-radio-button>
                  <a-radio-button value="WECHAT">微信</a-radio-button>
                  <a-radio-button value="ALIPAY">支付宝</a-radio-button>
                </a-radio-group>
                <a-button type="primary" size="small" :loading="record._paying" @click="handlePay(record)">支付</a-button>
              </a-space>
            </template>
          </template>
        </a-table>
        <a-empty v-if="!unpaidPayments.length" description="暂无待缴费项" />
      </a-tab-pane>

      <a-tab-pane key="history" tab="缴费记录">
        <a-table
          :columns="historyColumns"
          :data-source="allPayments"
          row-key="id"
          :pagination="false"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'type'">
              <a-tag color="blue">{{ paymentTypeMap[record.type] || record.type }}</a-tag>
            </template>
            <template v-if="column.key === 'amount'">
              <MoneyDisplay :value="record.amount" />
            </template>
            <template v-if="column.key === 'status'">
              <a-tag :color="paymentStatusMap[record.status]?.color">
                {{ paymentStatusMap[record.status]?.label || record.status }}
              </a-tag>
            </template>
            <template v-if="column.key === 'payMethod'">
              {{ payMethodMap[record.payMethod] || '-' }}
            </template>
          </template>
        </a-table>
        <a-empty v-if="!allPayments.length" description="暂无缴费记录" />
      </a-tab-pane>
    </a-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import MoneyDisplay from '@/components/MoneyDisplay.vue'
import { getPayments, getUnpaidPayments, pay } from '@/api/patient'
import { getCurrentUser } from '@/utils/user'
import { paymentTypeMap, paymentStatusMap, payMethodMap } from '@/utils/enums'
import type { Payment } from '@/types'

interface PayablePayment extends Payment {
  _payMethod?: string
  _paying?: boolean
}

const patientId = ref<number | null>(null)
const activeTab = ref('unpaid')

const allPayments = ref<Payment[]>([])
const unpaidPayments = ref<PayablePayment[]>([])

const unpaidColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '类型', dataIndex: 'type', key: 'type' },
  { title: '关联编号', dataIndex: 'referenceId', key: 'referenceId' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '操作', key: 'action', width: 340 },
]

const historyColumns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '类型', dataIndex: 'type', key: 'type' },
  { title: '关联编号', dataIndex: 'referenceId', key: 'referenceId' },
  { title: '金额', dataIndex: 'amount', key: 'amount' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '支付方式', dataIndex: 'payMethod', key: 'payMethod' },
  { title: '支付时间', dataIndex: 'payTime', key: 'payTime' },
]

async function loadData() {
  if (!patientId.value) return
  try {
    const [all, unpaid] = await Promise.all([
      getPayments(patientId.value),
      getUnpaidPayments(patientId.value),
    ])
    allPayments.value = all
    unpaidPayments.value = unpaid.map((p) => ({ ...p, _payMethod: 'WECHAT', _paying: false }))
  } catch {
    // handled by interceptor
  }
}

async function handlePay(record: PayablePayment) {
  if (!record._payMethod) {
    message.warning('请选择支付方式')
    return
  }
  record._paying = true
  try {
    await pay(record.id!, record._payMethod)
    message.success('支付成功')
    await loadData()
  } catch {
    // handled by interceptor
  } finally {
    record._paying = false
  }
}

onMounted(() => {
  const user = getCurrentUser()
  if (!user.referenceId) {
    message.error('未绑定患者信息，请联系管理员')
    return
  }
  patientId.value = user.referenceId
  loadData()
})
</script>

<style scoped>
</style>
