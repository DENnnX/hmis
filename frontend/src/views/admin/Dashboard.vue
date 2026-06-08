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

    <!-- 科室排班统计 -->
    <a-card title="科室排班统计" style="margin-bottom: 24px">
      <template #extra>
        <a-space>
          <a-date-picker v-model:value="scheduleDate" format="YYYY-MM-DD" value-format="YYYY-MM-DD" :allow-clear="false" @change="onDateChange" />
          <a-button @click="loadWeek"><template #icon><CalendarOutlined /></template>近7天</a-button>
          <a-button @click="loadToday"><template #icon><ReloadOutlined /></template>今天</a-button>
        </a-space>
      </template>
      <a-table
        :columns="scheduleColumns"
        :data-source="scheduleData"
        :loading="scheduleLoading"
        :pagination="false"
        row-key="department_name"
      />
    </a-card>

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

    <!-- 病人治疗情况 -->
    <a-card title="病人治疗情况">
      <a-table
        :columns="treatmentColumns"
        :data-source="treatmentData"
        :loading="treatmentLoading"
        :pagination="false"
        row-key="patient_id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'visit_count'">
            <a-button v-if="record.visit_count > 0" type="link" size="small" @click="showOutpatient(record)">{{ record.visit_count }}</a-button>
            <span v-else>0</span>
          </template>
          <template v-if="column.key === 'hospitalization_count'">
            <a-button v-if="record.hospitalization_count > 0" type="link" size="small" @click="showHospitalization(record)">{{ record.hospitalization_count }}</a-button>
            <span v-else>0</span>
          </template>
          <template v-if="column.key === 'total_consultation_fee'">
            <MoneyDisplay :value="record.total_consultation_fee" />
          </template>
          <template v-if="column.key === 'total_drug_fee'">
            <MoneyDisplay :value="record.total_drug_fee" />
          </template>
          <template v-if="column.key === 'total_cost'">
            <MoneyDisplay :value="record.total_cost" />
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 门诊记录弹窗 -->
    <a-modal v-model:open="outpatientVisible" :title="`${outpatientPatientName} 的门诊记录`" :footer="null" width="800px">
      <TreatmentDetail :patient-id="outpatientPatientId" />
    </a-modal>

    <!-- 住院记录弹窗 -->
    <a-modal v-model:open="hospitalizationVisible" :title="`${hospitalizationPatientName} 的住院记录`" :footer="null" width="800px">
      <a-spin :spinning="hospitalizationLoading">
        <a-table
          v-if="hospitalizationData.length"
          :columns="hospColumns"
          :data-source="hospitalizationData"
          :pagination="false"
          size="small"
          row-key="id"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="record.status === 'IN_HOSPITAL' ? 'orange' : 'default'">
                {{ record.status === 'IN_HOSPITAL' ? '住院中' : '已出院' }}
              </a-tag>
            </template>
          </template>
        </a-table>
        <a-empty v-else description="无住院记录" />
      </a-spin>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { UserOutlined, TeamOutlined, BankOutlined, CalendarOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { listDoctors, listPatients, listDepartments, getWorkloadStats, getScheduleStats, getPatientTreatmentStats, getPatientHospitalizations } from '@/api/admin'
import { titleMap } from '@/utils/enums'
import MoneyDisplay from '@/components/MoneyDisplay.vue'
import TreatmentDetail from './TreatmentDetail.vue'
import type { WorkloadStat } from '@/types'

const doctorCount = ref(0)
const patientCount = ref(0)
const deptCount = ref(0)

const scheduleDate = ref(dayjs().format('YYYY-MM-DD'))
const scheduleData = ref<any[]>([])
const scheduleLoading = ref(false)

const workloadData = ref<WorkloadStat[]>([])
const workloadLoading = ref(false)

const treatmentData = ref<any[]>([])
const treatmentLoading = ref(false)

// 门诊弹窗
const outpatientVisible = ref(false)
const outpatientPatientId = ref(0)
const outpatientPatientName = ref('')

// 住院弹窗
const hospitalizationVisible = ref(false)
const hospitalizationPatientId = ref(0)
const hospitalizationPatientName = ref('')
const hospitalizationData = ref<any[]>([])
const hospitalizationLoading = ref(false)

const scheduleColumns = [
  { title: '科室', dataIndex: 'department_name', key: 'department_name' },
  { title: '排班总数', dataIndex: 'total', key: 'total' },
  { title: '门诊排班', dataIndex: 'outpatient_count', key: 'outpatient_count' },
  { title: '住院查房', dataIndex: 'inpatient_count', key: 'inpatient_count' },
]

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

const treatmentColumns = [
  { title: '患者', dataIndex: 'patient_name', key: 'patient_name', width: 100 },
  { title: '病案号', dataIndex: 'patient_no', key: 'patient_no', width: 120 },
  { title: '门诊次数', dataIndex: 'visit_count', key: 'visit_count', width: 90 },
  { title: '住院次数', dataIndex: 'hospitalization_count', key: 'hospitalization_count', width: 90 },
  { title: '诊疗费', dataIndex: 'total_consultation_fee', key: 'total_consultation_fee', width: 100 },
  { title: '药费', dataIndex: 'total_drug_fee', key: 'total_drug_fee', width: 100 },
  { title: '总费用', dataIndex: 'total_cost', key: 'total_cost', width: 100 },
]

const hospColumns = [
  { title: '住院号', dataIndex: 'hospitalNo', width: 150 },
  { title: '入院日期', dataIndex: 'admissionDate', width: 120 },
  { title: '出院日期', dataIndex: 'dischargeDate', width: 120, customRender: ({ text }: { text: string | null }) => text || '-' },
  { title: '状态', key: 'status', width: 90 },
]

function showOutpatient(record: any) {
  outpatientPatientId.value = record.patient_id
  outpatientPatientName.value = record.patient_name
  outpatientVisible.value = true
}

async function showHospitalization(record: any) {
  hospitalizationPatientId.value = record.patient_id
  hospitalizationPatientName.value = record.patient_name
  hospitalizationVisible.value = true
  hospitalizationLoading.value = true
  try {
    hospitalizationData.value = await getPatientHospitalizations(record.patient_id)
  } catch {
    // handled
  } finally {
    hospitalizationLoading.value = false
  }
}

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

async function loadScheduleStats() {
  scheduleLoading.value = true
  try {
    scheduleData.value = await getScheduleStats(scheduleDate.value)
  } catch {
    // handled by interceptor
  } finally {
    scheduleLoading.value = false
  }
}

async function loadToday() {
  scheduleDate.value = dayjs().format('YYYY-MM-DD')
  await loadScheduleStats()
}

async function loadWeek() {
  scheduleLoading.value = true
  try {
    const merged = new Map<string, any>()
    for (let i = 0; i < 7; i++) {
      const d = dayjs().add(i, 'day').format('YYYY-MM-DD')
      const data = await getScheduleStats(d)
      for (const row of data) {
        const key = row.department_name
        if (merged.has(key)) {
          const m = merged.get(key)!
          m.total += row.total
          m.outpatient_count += row.outpatient_count
          m.inpatient_count += row.inpatient_count
        } else {
          merged.set(key, { ...row })
        }
      }
    }
    scheduleData.value = Array.from(merged.values())
  } catch {
    // handled
  } finally {
    scheduleLoading.value = false
  }
}

async function onDateChange() {
  await loadScheduleStats()
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

async function loadTreatment() {
  treatmentLoading.value = true
  try {
    treatmentData.value = await getPatientTreatmentStats()
  } catch {
    // handled by interceptor
  } finally {
    treatmentLoading.value = false
  }
}

onMounted(() => {
  loadCounts()
  loadScheduleStats()
  loadWorkload()
  loadTreatment()
})
</script>
