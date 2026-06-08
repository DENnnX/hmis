<template>
  <div>
    <a-spin v-if="loading" style="padding: 16px; display: block; text-align: center" />
    <a-table v-else-if="details.length" :columns="columns" :data-source="details" :pagination="false" size="small" row-key="visit_time">
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
    <a-empty v-else description="无就诊记录" :image-style="{ height: '20px' }" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPatientTreatmentDetail } from '@/api/admin'
import { titleMap } from '@/utils/enums'
import MoneyDisplay from '@/components/MoneyDisplay.vue'

const props = defineProps<{ patientId: number }>()

const loading = ref(true)
const details = ref<any[]>([])

const columns = [
  { title: '就诊时间', dataIndex: 'visit_time', key: 'visit_time', width: 160 },
  { title: '医生', dataIndex: 'doctor_name', key: 'doctor_name', width: 100 },
  { title: '职称', dataIndex: 'doctor_title', key: 'doctor_title', width: 100 },
  { title: '诊断', dataIndex: 'diagnosis', key: 'diagnosis', ellipsis: true },
  { title: '诊疗费', dataIndex: 'consultation_fee', key: 'consultation_fee', width: 100 },
  { title: '药费', dataIndex: 'total_drug_price', key: 'total_drug_price', width: 100 },
  { title: '总费用', dataIndex: 'total_cost', key: 'total_cost', width: 100 },
]

onMounted(async () => {
  try {
    details.value = await getPatientTreatmentDetail(props.patientId)
  } catch {
    // handled
  } finally {
    loading.value = false
  }
})
</script>
