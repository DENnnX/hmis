<template>
  <div>
    <a-card>
      <template #title>
        <a-space>
          <HistoryOutlined />
          <span>接诊历史</span>
          <a-tag color="blue">{{ doctorName }}</a-tag>
        </a-space>
      </template>
      <template #extra>
        <a-button @click="loadData"><template #icon><ReloadOutlined /></template>刷新</a-button>
      </template>

      <a-table
        :columns="columns"
        :data-source="history"
        :loading="loading"
        row-key="id"
        :pagination="{ pageSize: 10, showTotal: (t: number) => `共 ${t} 条` }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'consultation_fee'">
            <MoneyDisplay :value="record.consultation_fee" />
          </template>
          <template v-if="column.key === 'total_drug_price'">
            <MoneyDisplay :value="record.total_drug_price" />
          </template>
          <template v-if="column.key === 'total_cost'">
            <MoneyDisplay :value="record.total_cost" />
          </template>
          <template v-if="column.key === 'action'">
            <a-button v-if="record.prescription_id" type="link" size="small" @click="showDetail(record)">查看清单</a-button>
          </template>
        </template>
      </a-table>
      <a-empty v-if="!loading && history.length === 0" description="暂无接诊记录" />
    </a-card>

    <!-- 用药清单弹窗 -->
    <a-modal v-model:open="detailModalVisible" title="用药清单" :footer="null" width="700px">
      <a-spin :spinning="detailLoading">
        <a-table
          v-if="detailData.length"
          :columns="detailColumns"
          :data-source="detailData"
          :pagination="false"
          size="small"
          row-key="drug_name"
        />
        <a-empty v-else description="无用药清单" />
      </a-spin>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { HistoryOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import MoneyDisplay from '@/components/MoneyDisplay.vue'
import { getVisitHistory, getPrescriptionDetail } from '@/api/doctor'
import { listDoctors } from '@/api/admin'
import { getCurrentUser } from '@/utils/user'
import type { DrugDetail } from '@/types'

const user = getCurrentUser()
const doctorId = user.referenceId!
const doctorName = ref('')
const history = ref<any[]>([])
const loading = ref(false)

const detailModalVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<DrugDetail[]>([])

const columns = [
  { title: '患者', dataIndex: 'patient_name', key: 'patient_name', width: 100 },
  { title: '病案号', dataIndex: 'patient_no', key: 'patient_no', width: 120 },
  { title: '就诊时间', dataIndex: 'visit_time', key: 'visit_time', width: 160 },
  { title: '诊断', dataIndex: 'diagnosis', key: 'diagnosis', ellipsis: true },
  { title: '症状', dataIndex: 'symptoms', key: 'symptoms', ellipsis: true },
  { title: '诊疗费', dataIndex: 'consultation_fee', key: 'consultation_fee', width: 100 },
  { title: '药费', dataIndex: 'total_drug_price', key: 'total_drug_price', width: 100 },
  { title: '总费用', dataIndex: 'total_cost', key: 'total_cost', width: 100 },
  { title: '操作', key: 'action', width: 100 },
]

const detailColumns = [
  { title: '药品名称', dataIndex: 'drug_name', key: 'drug_name', width: 120 },
  { title: '规格', dataIndex: 'specification', key: 'specification', width: 120 },
  { title: '数量', dataIndex: 'quantity', key: 'quantity', width: 60 },
  { title: '用法', dataIndex: 'dosage', key: 'dosage', ellipsis: true },
  { title: '单价', dataIndex: 'unit_price', key: 'unit_price', width: 80 },
  { title: '小计', dataIndex: 'subtotal', key: 'subtotal', width: 80 },
]

async function loadData() {
  loading.value = true
  try {
    const [data, doctors] = await Promise.all([getVisitHistory(doctorId), listDoctors()])
    history.value = data
    const doc = doctors.find((d) => d.id === doctorId)
    doctorName.value = doc ? `${doc.name}（${doc.title}）` : `医生 #${doctorId}`
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

async function showDetail(record: any) {
  if (!record.prescription_id) return
  detailModalVisible.value = true
  detailLoading.value = true
  detailData.value = []
  try {
    detailData.value = await getPrescriptionDetail(record.prescription_id)
  } catch {
    // handled
  } finally {
    detailLoading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
</style>
