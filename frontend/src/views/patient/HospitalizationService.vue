<template>
  <div>
    <a-spin :spinning="loading">
      <!-- 患者信息 -->
      <a-alert :message="`当前患者：${patientName}`" type="info" show-icon style="margin-bottom: 16px" />

      <!-- 当前在院状态 -->
      <a-card v-if="activeHosp" style="margin-bottom: 16px">
        <template #title>
          <a-space>
            <HomeOutlined />
            <span>当前住院</span>
            <a-tag color="orange">住院中</a-tag>
          </a-space>
        </template>

        <a-descriptions bordered :column="2" size="small">
          <a-descriptions-item label="住院号">{{ activeHosp.hospitalNo }}</a-descriptions-item>
          <a-descriptions-item label="入院日期">{{ activeHosp.admissionDate }}</a-descriptions-item>
          <a-descriptions-item label="主治医生">
            {{ doctorMap[activeHosp.attendingDoctorId] || `#${activeHosp.attendingDoctorId}` }}
          </a-descriptions-item>
          <a-descriptions-item label="病房/床位">
            {{ wardMap[activeHosp.wardId] || `#${activeHosp.wardId}` }} / {{ bedMap[activeHosp.bedId] || `#${activeHosp.bedId}` }}
          </a-descriptions-item>
        </a-descriptions>

        <!-- 余额 -->
        <a-row :gutter="16" style="margin-top: 16px">
          <a-col :span="8">
            <a-statistic title="住院余额" :value="balance" :precision="2" prefix="¥"
              :value-style="{ color: balance >= 0 ? '#3f8600' : '#cf1322', fontSize: '28px' }" />
          </a-col>
          <a-col :span="16" style="display: flex; align-items: flex-end; justify-content: flex-end; gap: 12px">
            <a-button type="primary" @click="showDeposit = !showDeposit">
              <template #icon><DollarOutlined /></template>缴纳预缴金
            </a-button>
            <a-popconfirm title="确定办理出院？出院后床位将释放。" @confirm="handleDischarge(activeHosp.id!)">
              <a-button danger :loading="dischargingId === activeHosp.id">
                <template #icon><LogoutOutlined /></template>办理出院
              </a-button>
            </a-popconfirm>
          </a-col>
        </a-row>

        <!-- 预缴金表单（展开/收起）-->
        <a-collapse v-model:activeKey="depositPanel" style="margin-top: 16px">
          <a-collapse-panel key="deposit" header="缴纳预缴金" v-show="showDeposit">
            <a-form layout="inline">
              <a-form-item label="金额">
                <a-input-number v-model:value="depositAmount" :min="100" :precision="2" :step="500" style="width: 200px" />
              </a-form-item>
              <a-form-item>
                <a-button type="primary" :loading="depositing" @click="handleDeposit">确认缴费</a-button>
              </a-form-item>
            </a-form>
          </a-collapse-panel>
        </a-collapse>

        <!-- 查房记录 -->
        <a-divider>查房记录</a-divider>
        <a-table :data-source="records" :columns="recordColumns" :pagination="false" size="small" row-key="id" :loading="recordsLoading">
          <template #bodyCell="{ column, record: rec }">
            <template v-if="column.key === 'action'">
              <a-button type="link" size="small" @click="showRecordDetail(rec)">查看清单</a-button>
            </template>
          </template>
        </a-table>
        <a-empty v-if="!recordsLoading && records.length === 0" description="暂无查房记录" />
      </a-card>

      <!-- 无在院 → 显示入院表单 -->
      <a-card v-if="!activeHosp && !loading" title="办理入院" style="margin-bottom: 16px">
        <a-form ref="admitFormRef" :model="admitForm" :label-col="{ span: 4 }" :wrapper-col="{ span: 16 }">
          <a-form-item label="主治医生" name="doctorId" :rules="[{ required: true, message: '请选择主治医生' }]">
            <a-select v-model:value="admitForm.doctorId" placeholder="请选择主治医生" show-search :filter-option="filterDoctor">
              <a-select-option v-for="d in doctors" :key="d.id" :value="d.id">
                {{ d.name }}（{{ titleMap[d.title] || d.title }}）
              </a-select-option>
            </a-select>
          </a-form-item>
          <BedSelector @change="onBedChange" />
          <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
            <a-button type="primary" :loading="admitting" @click="handleAdmit">办理入院</a-button>
          </a-form-item>
        </a-form>
      </a-card>

      <!-- 历史住院记录 -->
      <a-card v-if="pastHosps.length > 0" title="历史住院记录">
        <a-table :data-source="pastHosps" :columns="hospColumns" :pagination="false" size="small" row-key="id">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag color="default">已出院</a-tag>
            </template>
            <template v-if="column.key === 'doctor'">
              {{ doctorMap[record.attendingDoctorId] || `#${record.attendingDoctorId}` }}
            </template>
            <template v-if="column.key === 'action'">
              <a-button type="link" size="small" @click="loadPastRecords(record.id!)">
                <template #icon><EyeOutlined /></template>查看记录
              </a-button>
            </template>
          </template>
        </a-table>

        <!-- 展开的查房记录 -->
        <a-card v-if="viewHospId" size="small" style="margin-top: 12px" title="查房记录">
          <a-table :data-source="pastRecords" :columns="recordColumns" :pagination="false" size="small" row-key="id"
            :loading="pastRecordsLoading" :locale="{ emptyText: '暂无查房记录' }">
            <template #bodyCell="{ column, record: rec }">
              <template v-if="column.key === 'action'">
                <a-button type="link" size="small" @click="showRecordDetail(rec)">查看清单</a-button>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-card>
    </a-spin>

    <!-- 用药清单弹窗 -->
    <a-modal v-model:open="detailModalVisible" title="用药清单" :footer="null" width="700px">
      <a-spin :spinning="detailLoading">
        <a-table
          v-if="detailData.length"
          :columns="prescriptionColumns"
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
import { ref, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { HomeOutlined, DollarOutlined, LogoutOutlined, EyeOutlined } from '@ant-design/icons-vue'
import BedSelector from '@/components/BedSelector.vue'
import {
  getHospitalizations, getRecords, getBalance as fetchBalance,
  admit, deposit, discharge, getRecordPrescriptionItems,
} from '@/api/patient'
import { listDoctors, listWards, listBeds, listPatients } from '@/api/admin'
import { getCurrentUser } from '@/utils/user'
import { titleMap } from '@/utils/enums'
import type { Doctor, Ward, Bed, Patient, Hospitalization, HospitalizationRecord, DrugDetail } from '@/types'

const user = getCurrentUser()
const patientId = user.referenceId!
const patientName = ref('')

const loading = ref(true)
const hospitalizations = ref<Hospitalization[]>([])
const doctors = ref<Doctor[]>([])
const wards = ref<Ward[]>([])
const beds = ref<Bed[]>([])

// 查房记录
const records = ref<HospitalizationRecord[]>([])
const recordsLoading = ref(false)

// 余额
const balance = ref(0)

// 预缴金
const showDeposit = ref(false)
const depositPanel = ref<string[]>([])
const depositAmount = ref(1000)
const depositing = ref(false)

// 出院
const dischargingId = ref<number | null>(null)

// 入院表单
const admitFormRef = ref()
const admitForm = ref({ doctorId: undefined as number | undefined, wardId: 0, bedId: 0 })
const admitting = ref(false)

// 计算属性
const activeHosp = computed(() => hospitalizations.value.find((h) => h.status === 'IN_HOSPITAL'))
const pastHosps = computed(() => hospitalizations.value.filter((h) => h.status === 'DISCHARGED'))

const doctorMap = computed(() => Object.fromEntries(doctors.value.map((d) => [d.id, d.name])))
const wardMap = computed(() => Object.fromEntries(wards.value.map((w) => [w.id, w.wardNo])))
const bedMap = computed(() => Object.fromEntries(beds.value.map((b) => [b.id, b.bedNo])))

const hospColumns = [
  { title: '住院号', dataIndex: 'hospitalNo' },
  { title: '主治医生', key: 'doctor' },
  { title: '入院日期', dataIndex: 'admissionDate' },
  { title: '出院日期', dataIndex: 'dischargeDate' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'action', width: 100 },
]

// 历史住院查房记录
const viewHospId = ref<number | null>(null)
const pastRecords = ref<HospitalizationRecord[]>([])
const pastRecordsLoading = ref(false)

const recordColumns = [
  { title: '日期', dataIndex: 'recordDate', width: 120 },
  { title: '病情描述', dataIndex: 'conditionDesc', ellipsis: true },
  { title: '治疗方案', dataIndex: 'treatmentPlan', ellipsis: true },
  { title: '操作', key: 'action', width: 100 },
]

// 用药清单弹窗
const detailModalVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<DrugDetail[]>([])

const prescriptionColumns = [
  { title: '药品名称', dataIndex: 'drug_name', key: 'drug_name', width: 120 },
  { title: '规格', dataIndex: 'specification', key: 'specification', width: 120 },
  { title: '数量', dataIndex: 'quantity', key: 'quantity', width: 60 },
  { title: '用法', dataIndex: 'dosage', key: 'dosage', ellipsis: true },
  { title: '单价', dataIndex: 'unit_price', key: 'unit_price', width: 80 },
  { title: '小计', dataIndex: 'subtotal', key: 'subtotal', width: 80 },
]

async function showRecordDetail(record: HospitalizationRecord) {
  detailModalVisible.value = true
  detailLoading.value = true
  detailData.value = []
  try {
    detailData.value = await getRecordPrescriptionItems(record.id!)
  } catch {
    // handled
  } finally {
    detailLoading.value = false
  }
}

// 加载数据
async function loadAll() {
  loading.value = true
  try {
    const [hosps, docList, wardList, bedList, patientList] = await Promise.all([
      getHospitalizations(patientId),
      listDoctors(),
      listWards(),
      listBeds(),
      listPatients(),
    ])
    hospitalizations.value = hosps
    doctors.value = docList
    wards.value = wardList
    beds.value = bedList
    const pat = patientList.find((p) => p.id === patientId)
    patientName.value = pat ? `${pat.name}（${pat.patientNo}）` : `患者 #${patientId}`

    // 如果有在院记录，加载余额和查房记录
    if (activeHosp.value) {
      await Promise.all([loadBalance(), loadRecords()])
    }
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

async function loadBalance() {
  if (!activeHosp.value?.id) return
  try { balance.value = await fetchBalance(activeHosp.value.id) } catch { /* handled */ }
}

async function loadRecords() {
  if (!activeHosp.value?.id) return
  recordsLoading.value = true
  try { records.value = await getRecords(activeHosp.value.id) } catch { /* handled */ }
  finally { recordsLoading.value = false }
}

async function loadPastRecords(hospId: number) {
  if (viewHospId.value === hospId) {
    viewHospId.value = null
    pastRecords.value = []
    return
  }
  viewHospId.value = hospId
  pastRecordsLoading.value = true
  try { pastRecords.value = await getRecords(hospId) } catch { /* handled */ }
  finally { pastRecordsLoading.value = false }
}

// 入院
function onBedChange(val: { wardId: number; bedId: number }) {
  admitForm.value.wardId = val.wardId
  admitForm.value.bedId = val.bedId
}

function filterDoctor(input: string, option: { label: string }) {
  return option.label.toLowerCase().includes(input.toLowerCase())
}

async function handleAdmit() {
  if (!admitFormRef.value) return
  await admitFormRef.value.validate()
  if (!admitForm.value.wardId || !admitForm.value.bedId) {
    message.warning('请选择病房和床位')
    return
  }
  admitting.value = true
  try {
    const res = await admit(patientId, admitForm.value.doctorId!, admitForm.value.wardId, admitForm.value.bedId)
    message.success(`入院成功，住院号：${res.hospitalNo}`)
    await loadAll()
  } catch {
    // handled
  } finally {
    admitting.value = false
  }
}

// 预缴
async function handleDeposit() {
  if (!activeHosp.value?.id) return
  depositing.value = true
  try {
    await deposit(activeHosp.value.id, depositAmount.value)
    message.success('预缴成功')
    showDeposit.value = false
    await loadBalance()
  } catch {
    // handled
  } finally {
    depositing.value = false
  }
}

// 出院
async function handleDischarge(id: number) {
  dischargingId.value = id
  try {
    const result = await discharge(id)
    const refund = Number(result.refund) || 0
    if (refund > 0) {
      message.success(`出院办理成功，核算后退还余额：¥${refund.toFixed(2)}`)
    } else if (refund < 0) {
      message.warning(`出院办理成功，账户欠费：¥${Math.abs(refund).toFixed(2)}`)
    } else {
      message.success('出院办理成功')
    }
    await loadAll()
  } catch {
    // handled
  } finally {
    dischargingId.value = null
  }
}

onMounted(loadAll)
</script>

<style scoped>
</style>
