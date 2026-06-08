<template>
  <div>
    <!-- 统计卡片 -->
    <a-row :gutter="16" style="margin-bottom: 24px">
      <a-col :span="8">
        <a-card>
          <a-statistic title="医生总数" :value="doctorCount">
            <template #prefix><UserOutlined /></template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card>
          <a-statistic title="患者总数" :value="patientCount">
            <template #prefix><TeamOutlined /></template>
          </a-statistic>
        </a-card>
      </a-col>
      <a-col :span="8">
        <a-card>
          <a-statistic title="科室总数" :value="deptCount">
            <template #prefix><BankOutlined /></template>
          </a-statistic>
        </a-card>
      </a-col>
    </a-row>

    <!-- 工作量统计 -->
    <a-card title="医生工作量统计" style="margin-bottom: 24px">
      <a-table
        :columns="workloadColumns"
        :data-source="workloadData"
        :loading="workloadLoading"
        :pagination="false"
        row-key="doctor_id"
      />
    </a-card>

    <!-- 住院费用统计 -->
    <a-card title="住院费用统计">
      <a-table
        :columns="costColumns"
        :data-source="costData"
        :loading="costLoading"
        :pagination="false"
        row-key="hospitalization_id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <StatusTag :value="record.status" category="hospitalization" />
          </template>
          <template v-if="column.key === 'total_charge'">
            <MoneyDisplay :value="record.total_charge" />
          </template>
          <template v-if="column.key === 'total_deposit'">
            <MoneyDisplay :value="record.total_deposit" />
          </template>
          <template v-if="column.key === 'balance'">
            <MoneyDisplay :value="record.balance" />
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { UserOutlined, TeamOutlined, BankOutlined } from '@ant-design/icons-vue'
import { listDoctors, listPatients, listDepartments, getWorkloadStats, getHospitalizationCostStats } from '@/api/admin'
import { titleMap } from '@/utils/enums'
import StatusTag from '@/components/StatusTag.vue'
import MoneyDisplay from '@/components/MoneyDisplay.vue'
import type { WorkloadStat, HospitalizationCostStat } from '@/types'

const doctorCount = ref(0)
const patientCount = ref(0)
const deptCount = ref(0)

const workloadData = ref<WorkloadStat[]>([])
const workloadLoading = ref(false)
const costData = ref<HospitalizationCostStat[]>([])
const costLoading = ref(false)

const workloadColumns = [
  { title: '医生姓名', dataIndex: 'doctor_name', key: 'doctor_name' },
  { title: '科室', dataIndex: 'department_name', key: 'department_name' },
  {
    title: '职称',
    dataIndex: 'title',
    key: 'title',
    customRender: ({ text }: { text: string }) => titleMap[text] || text,
  },
  { title: '挂号数', dataIndex: 'total_registrations', key: 'total_registrations' },
  { title: '就诊数', dataIndex: 'total_visits', key: 'total_visits' },
  { title: '住院数', dataIndex: 'total_hospitalizations', key: 'total_hospitalizations' },
]

const costColumns = [
  { title: '住院号', dataIndex: 'hospital_no', key: 'hospital_no' },
  { title: '患者姓名', dataIndex: 'patient_name', key: 'patient_name' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '总费用', dataIndex: 'total_charge', key: 'total_charge' },
  { title: '总押金', dataIndex: 'total_deposit', key: 'total_deposit' },
  { title: '余额', dataIndex: 'balance', key: 'balance' },
]

async function loadCounts() {
  try {
    const [doctors, patients, depts] = await Promise.all([
      listDoctors(),
      listPatients(),
      listDepartments(),
    ])
    doctorCount.value = doctors.length
    patientCount.value = patients.length
    deptCount.value = depts.length
  } catch {
    // handled by interceptor
  }
}

async function loadWorkload() {
  workloadLoading.value = true
  try {
    workloadData.value = await getWorkloadStats()
  } catch {
    // handled by interceptor
  } finally {
    workloadLoading.value = false
  }
}

async function loadCost() {
  costLoading.value = true
  try {
    costData.value = await getHospitalizationCostStats()
  } catch {
    // handled by interceptor
  } finally {
    costLoading.value = false
  }
}

onMounted(() => {
  loadCounts()
  loadWorkload()
  loadCost()
})
</script>
