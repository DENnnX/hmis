<template>
  <div>
    <a-table
      v-if="history.length"
      :columns="columns"
      :data-source="history"
      row-key="visit_time"
      :pagination="false"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'doctor_title'">
          {{ titleMap[record.doctor_title] || record.doctor_title }}
        </template>
        <template v-if="column.key === 'consultation_fee'">
          <MoneyDisplay :value="record.consultation_fee" />
        </template>
        <template v-if="column.key === 'total_drug_price'">
          <MoneyDisplay :value="record.total_drug_price" />
        </template>
        <template v-if="column.key === 'total_cost'">
          <MoneyDisplay :value="record.total_cost" />
        </template>
      </template>
    </a-table>
    <a-empty v-else-if="!loading" description="暂无就诊历史" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import MoneyDisplay from '@/components/MoneyDisplay.vue'
import { getHistory } from '@/api/patient'
import { getCurrentUser } from '@/utils/user'
import { titleMap } from '@/utils/enums'
import type { PatientHistory } from '@/types'

const history = ref<PatientHistory[]>([])
const loading = ref(false)

const columns = [
  { title: '就诊时间', dataIndex: 'visit_time', key: 'visit_time' },
  { title: '医生', dataIndex: 'doctor_name', key: 'doctor_name' },
  { title: '职称', dataIndex: 'doctor_title', key: 'doctor_title' },
  { title: '诊断', dataIndex: 'diagnosis', key: 'diagnosis' },
  { title: '症状', dataIndex: 'symptoms', key: 'symptoms' },
  { title: '诊疗费', dataIndex: 'consultation_fee', key: 'consultation_fee' },
  { title: '药费', dataIndex: 'total_drug_price', key: 'total_drug_price' },
  { title: '总费用', dataIndex: 'total_cost', key: 'total_cost' },
]

onMounted(async () => {
  const user = getCurrentUser()
  if (!user.referenceId) {
    message.error('未绑定患者信息，请联系管理员')
    return
  }
  loading.value = true
  try {
    history.value = await getHistory(user.referenceId)
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
</style>
