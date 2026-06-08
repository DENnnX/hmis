<template>
  <div>
    <!-- Card 1: 待诊患者列表 -->
    <a-card>
      <template #title>
        <a-space>
          <TeamOutlined />
          <span>待诊患者</span>
          <a-badge :count="waitingList.length" :number-style="{ backgroundColor: '#52c41a' }" />
        </a-space>
      </template>
      <template #extra>
        <a-button @click="loadData"><template #icon><ReloadOutlined /></template>刷新</a-button>
      </template>

      <a-spin :spinning="loading">
        <a-empty v-if="!loading && waitingList.length === 0" description="暂无待诊患者" />

        <a-table v-if="waitingList.length > 0" :columns="columns" :data-source="waitingList" :pagination="false" row-key="id" size="middle">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'patientName'">
              <a-space>
                <UserOutlined />
                <span style="font-weight: 500">{{ patientNameMap[record.patientId] || `患者 #${record.patientId}` }}</span>
              </a-space>
            </template>
            <template v-if="column.key === 'timeSlot'">
              <a-tag :color="record.timeSlot === 'MORNING' ? 'orange' : 'purple'">
                {{ timeSlotMap[record.timeSlot] || record.timeSlot }}
              </a-tag>
            </template>
            <template v-if="column.key === 'visitType'">
              <a-tag :color="record.visitType === 'FIRST_VISIT' ? 'blue' : 'cyan'">
                {{ visitTypeMap[record.visitType] || record.visitType }}
              </a-tag>
            </template>
            <template v-if="column.key === 'action'">
              <a-button type="primary" size="small" @click="handleReceive(record)">
                <template #icon><Stethoscope /></template>接诊
              </a-button>
            </template>
          </template>
        </a-table>
      </a-spin>
    </a-card>

    <!-- Card 2: 填写诊断 -->
    <a-card v-if="selectedReg" title="填写诊断" style="margin-top: 16px">
      <a-alert :message="`正在接诊：${patientNameMap[selectedReg.patientId] || '患者'} ｜ 挂号 #${selectedReg.id}`" type="info" show-icon style="margin-bottom: 16px" />
      <a-form ref="diagnosisFormRef" :model="diagnosisForm" :label-col="{ span: 4 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="诊断" name="diagnosis" :rules="[{ required: true, message: '请输入诊断结果' }]">
          <a-input v-model:value="diagnosisForm.diagnosis" placeholder="请输入诊断结果" />
        </a-form-item>
        <a-form-item label="症状" name="symptoms">
          <a-textarea v-model:value="diagnosisForm.symptoms" placeholder="请输入症状描述" :rows="3" />
        </a-form-item>
        <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
          <a-space>
            <a-button type="primary" :loading="submittingVisit" @click="handleSubmitVisit">提交就诊</a-button>
            <a-button @click="cancelReceive">取消</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- Card 3: 开具处方（可选） -->
    <a-card v-if="createdVisit" title="开具处方（可选）" style="margin-top: 16px">
      <a-alert message="就诊记录已创建。如需开处方请在下方添加药品，否则点击「完成就诊」。" type="success" show-icon style="margin-bottom: 16px" />
      <DrugPicker v-model="drugItems" />
      <div style="margin-top: 16px">
        <a-space>
          <a-button type="primary" :loading="submittingRx" :disabled="!drugItems.length" @click="handleSubmitPrescribe">
            提交处方
          </a-button>
          <a-button @click="finishVisit">完成就诊（不开处方）</a-button>
        </a-space>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { TeamOutlined, ReloadOutlined, UserOutlined } from '@ant-design/icons-vue'
import DrugPicker from '@/components/DrugPicker.vue'
import { createVisit, prescribe, getRegistrations } from '@/api/doctor'
import { listPatients } from '@/api/admin'
import { getCurrentUser } from '@/utils/user'
import { timeSlotMap, visitTypeMap } from '@/utils/enums'
import type { Registration, OutpatientVisit, Patient, DrugItem } from '@/types'

// Stethoscope icon not in @ant-design/icons-vue, use MedicineBoxOutlined as substitute
import { MedicineBoxOutlined as Stethoscope } from '@ant-design/icons-vue'

const user = getCurrentUser()
const doctorId = user.referenceId!

const patients = ref<Patient[]>([])
const registrations = ref<Registration[]>([])
const loading = ref(false)

const selectedReg = ref<Registration | null>(null)
const createdVisit = ref<OutpatientVisit | null>(null)

const diagnosisFormRef = ref()
const diagnosisForm = reactive({ diagnosis: '', symptoms: '' })
const drugItems = ref<DrugItem[]>([])

const submittingVisit = ref(false)
const submittingRx = ref(false)

const patientNameMap = computed(() => {
  const map: Record<number, string> = {}
  patients.value.forEach((p) => { map[p.id!] = p.name })
  return map
})

const waitingList = computed(() =>
  registrations.value.filter((r) => r.status === 'WAITING')
)

const columns = [
  { title: '患者', key: 'patientName', width: 140 },
  { title: '挂号日期', dataIndex: 'registrationDate', width: 110 },
  { title: '时段', key: 'timeSlot', width: 80 },
  { title: '就诊类型', key: 'visitType', width: 90 },
  { title: '操作', key: 'action', width: 100 },
]

async function loadData() {
  loading.value = true
  try {
    const [regs, pats] = await Promise.all([getRegistrations(doctorId), listPatients()])
    registrations.value = regs
    patients.value = pats
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

function handleReceive(record: Registration) {
  selectedReg.value = record
  createdVisit.value = null
  diagnosisForm.diagnosis = ''
  diagnosisForm.symptoms = ''
  drugItems.value = []
}

function cancelReceive() {
  selectedReg.value = null
  createdVisit.value = null
}

function finishVisit() {
  selectedReg.value = null
  createdVisit.value = null
  drugItems.value = []
  loadData()
  message.success('就诊完成')
}

async function handleSubmitVisit() {
  if (!diagnosisFormRef.value) return
  await diagnosisFormRef.value.validate()
  submittingVisit.value = true
  try {
    const visit = await createVisit(selectedReg.value!.id!, diagnosisForm.diagnosis, diagnosisForm.symptoms || undefined)
    createdVisit.value = visit
    message.success('就诊记录已创建')
  } catch {
    // handled
  } finally {
    submittingVisit.value = false
  }
}

async function handleSubmitPrescribe() {
  submittingRx.value = true
  try {
    const items = drugItems.value.map(({ drugId, quantity, dosage }) => ({ drugId, quantity, dosage }))
    await prescribe(createdVisit.value!.id!, items as any)
    message.success('处方已提交')
    drugItems.value = []
    await loadData()
    selectedReg.value = null
    createdVisit.value = null
  } catch {
    // handled
  } finally {
    submittingRx.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
</style>
