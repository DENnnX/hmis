<template>
  <div>
    <a-card>
      <template #title>
        <a-space>
          <FileTextOutlined />
          <span>住院查房</span>
          <a-tag color="blue">{{ doctorName }}</a-tag>
        </a-space>
      </template>
      <template #extra>
        <a-button @click="loadData"><template #icon><ReloadOutlined /></template>刷新</a-button>
      </template>

      <!-- 筛选栏 -->
      <a-row :gutter="16" style="margin-bottom: 16px">
        <a-col :span="8">
          <a-input v-model:value="searchText" placeholder="搜索患者姓名或住院号" allow-clear>
            <template #prefix><SearchOutlined /></template>
          </a-input>
        </a-col>
        <a-col :span="6">
          <a-select v-model:value="filterWard" placeholder="全部病房" allow-clear style="width: 100%">
            <a-select-option v-for="w in wards" :key="w.id" :value="w.id">
              {{ w.wardNo }} - {{ w.location }}
            </a-select-option>
          </a-select>
        </a-col>
        <a-col :span="10">
          <a-segmented v-model:value="filterStatus" :options="statusOptions" />
        </a-col>
      </a-row>

      <!-- 病人列表 -->
      <a-table
        :data-source="filteredHosps"
        :columns="hospColumns"
        :pagination="{ pageSize: 10, showTotal: (t: number) => `共 ${t} 条` }"
        :loading="loading"
        row-key="id"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'patientName'">
            <a-space>
              <UserOutlined />
              <span style="font-weight: 500">{{ patientMap[record.patientId] || `患者 #${record.patientId}` }}</span>
            </a-space>
          </template>
          <template v-if="column.key === 'wardBed'">
            {{ wardMap[record.wardId] || `#${record.wardId}` }} / {{ bedMap[record.bedId] || `#${record.bedId}` }}
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'IN_HOSPITAL' ? 'orange' : 'default'">
              {{ hospitalizationStatusMap[record.status]?.label || record.status }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewRecords(record)">
                <template #icon><EyeOutlined /></template>查看记录
              </a-button>
              <a-button v-if="record.status === 'IN_HOSPITAL'" type="link" size="small" @click="selectForRound(record)">
                <template #icon><EditOutlined /></template>查房
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>

      <a-empty v-if="!loading && filteredHosps.length === 0" description="无匹配的住院记录" />
    </a-card>

    <!-- 查看/查房面板（合并，避免重复） -->
    <a-card v-if="viewingHosp && !roundHosp" style="margin-top: 16px">
      <template #title>
        <a-space>
          <EyeOutlined />
          <span>查房记录</span>
          <a-tag color="blue">{{ patientMap[viewingHosp.patientId] || `患者 #${viewingHosp.patientId}` }}</a-tag>
          <a-tag>住院号：{{ viewingHosp.hospitalNo }}</a-tag>
          <a-tag :color="viewingHosp.status === 'IN_HOSPITAL' ? 'orange' : 'default'">
            {{ hospitalizationStatusMap[viewingHosp.status]?.label || viewingHosp.status }}
          </a-tag>
          <a-tag>{{ viewingHosp.admissionDate }}{{ viewingHosp.dischargeDate ? ' ~ ' + viewingHosp.dischargeDate : ' 至今' }}</a-tag>
        </a-space>
      </template>
      <template #extra>
        <a-button @click="viewingHosp = null">收起</a-button>
      </template>

      <a-table :data-source="records" :columns="recordColumns" :pagination="false" size="small" row-key="id" :loading="recordsLoading"
        :locale="{ emptyText: '暂无查房记录' }">
        <template #bodyCell="{ column, record: rec }">
          <template v-if="column.key === 'action'">
            <a-button type="link" size="small" @click="showRecordDetail(rec)">查看清单</a-button>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 写查房记录面板（仅在院，含历史记录） -->
    <a-card v-if="roundHosp" style="margin-top: 16px">
      <template #title>
        <a-space>
          <EditOutlined />
          <span>查房</span>
          <a-tag color="blue">{{ patientMap[roundHosp.patientId] || `患者 #${roundHosp.patientId}` }}</a-tag>
          <a-tag>住院号：{{ roundHosp.hospitalNo }}</a-tag>
        </a-space>
      </template>

      <a-form ref="formRef" :model="form" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
        <a-form-item label="病情描述" name="conditionDesc" :rules="[{ required: true, message: '请输入病情描述' }]">
          <a-textarea v-model:value="form.conditionDesc" placeholder="请输入当日病情描述" :rows="3" />
        </a-form-item>
        <a-form-item label="治疗方案" name="treatmentPlan" :rules="[{ required: true, message: '请输入治疗方案' }]">
          <a-textarea v-model:value="form.treatmentPlan" placeholder="请输入治疗方案" :rows="3" />
        </a-form-item>
        <a-form-item label="处方药品">
          <DrugPicker v-model="drugItems" />
        </a-form-item>
        <a-form-item :wrapper-col="{ offset: 3, span: 20 }">
          <a-space>
            <a-button type="primary" :loading="submitting" @click="handleSubmit">提交查房记录</a-button>
            <a-button @click="roundHosp = null">取消</a-button>
          </a-space>
        </a-form-item>
      </a-form>

      <a-divider>历史查房记录</a-divider>
      <a-table :data-source="records" :columns="recordColumns" :pagination="false" size="small" row-key="id" :loading="recordsLoading"
        :locale="{ emptyText: '暂无查房记录' }">
        <template #bodyCell="{ column, record: rec }">
          <template v-if="column.key === 'action'">
            <a-button type="link" size="small" @click="showRecordDetail(rec)">查看清单</a-button>
          </template>
        </template>
      </a-table>
    </a-card>

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
import { message, Modal } from 'ant-design-vue'
import {
  FileTextOutlined, ReloadOutlined, UserOutlined,
  EditOutlined, EyeOutlined, SearchOutlined,
} from '@ant-design/icons-vue'
import DrugPicker from '@/components/DrugPicker.vue'
import { getHospitalizations, dailyRound, getRecords, getRecordPrescriptionItems } from '@/api/doctor'
import { listPatients, listDoctors, listWards, listBeds } from '@/api/admin'
import { getCurrentUser } from '@/utils/user'
import { hospitalizationStatusMap } from '@/utils/enums'
import type { Hospitalization, HospitalizationRecord, Patient, Ward, Bed, DrugItem, DrugDetail } from '@/types'

const user = getCurrentUser()
const doctorId = user.referenceId!

const doctorName = ref('')
const loading = ref(false)
const allHosps = ref<Hospitalization[]>([])
const patients = ref<Patient[]>([])
const wards = ref<Ward[]>([])
const beds = ref<Bed[]>([])

// 筛选
const searchText = ref('')
const filterWard = ref<number | undefined>()
const filterStatus = ref('全部')
const statusOptions = [
  { label: '全部', value: '全部' },
  { label: '住院中', value: 'IN_HOSPITAL' },
  { label: '已出院', value: 'DISCHARGED' },
]

// 查看记录
const viewingHosp = ref<Hospitalization | null>(null)
const records = ref<HospitalizationRecord[]>([])
const recordsLoading = ref(false)

// 用药清单弹窗
const detailModalVisible = ref(false)
const detailLoading = ref(false)
const detailData = ref<DrugDetail[]>([])

// 写查房
const roundHosp = ref<Hospitalization | null>(null)
const formRef = ref()
const form = ref({ conditionDesc: '', treatmentPlan: '' })
const drugItems = ref<DrugItem[]>([])
const submitting = ref(false)

const patientMap = computed(() => Object.fromEntries(patients.value.map((p) => [p.id, p.name])))
const wardMap = computed(() => Object.fromEntries(wards.value.map((w) => [w.id, w.wardNo])))
const bedMap = computed(() => Object.fromEntries(beds.value.map((b) => [b.id, b.bedNo])))

// 筛选后的列表
const filteredHosps = computed(() => {
  return allHosps.value.filter((h) => {
    // 状态筛选
    if (filterStatus.value !== '全部' && h.status !== filterStatus.value) return false
    // 病房筛选
    if (filterWard.value && h.wardId !== filterWard.value) return false
    // 搜索
    if (searchText.value) {
      const s = searchText.value.toLowerCase()
      const name = (patientMap.value[h.patientId] || '').toLowerCase()
      const hospNo = (h.hospitalNo || '').toLowerCase()
      if (!name.includes(s) && !hospNo.includes(s)) return false
    }
    return true
  })
})

const hospColumns = [
  { title: '患者', key: 'patientName', width: 120 },
  { title: '住院号', dataIndex: 'hospitalNo', width: 150 },
  { title: '病房/床位', key: 'wardBed', width: 110 },
  { title: '入院日期', dataIndex: 'admissionDate', width: 110 },
  { title: '出院日期', dataIndex: 'dischargeDate', width: 110, customRender: ({ text }: { text: string | null }) => text || '-' },
  { title: '状态', key: 'status', width: 90 },
  { title: '操作', key: 'action', width: 200 },
]

const recordColumns = [
  { title: '日期', dataIndex: 'recordDate', width: 120 },
  { title: '病情描述', dataIndex: 'conditionDesc', ellipsis: true },
  { title: '治疗方案', dataIndex: 'treatmentPlan', ellipsis: true },
  { title: '操作', key: 'action', width: 100 },
]

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

async function loadData() {
  loading.value = true
  try {
    const [hosps, patList, wardList, bedList] = await Promise.all([
      getHospitalizations(doctorId),
      listPatients(),
      listWards(),
      listBeds(),
    ])
    allHosps.value = hosps
    patients.value = patList
    wards.value = wardList
    beds.value = bedList
    const docList = await listDoctors()
    const doc = docList.find((d) => d.id === doctorId)
    doctorName.value = doc ? `${doc.name}（${doc.title}）` : `医生 #${doctorId}`
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

// 查看记录（任何状态都行）
async function viewRecords(hosp: Hospitalization) {
  viewingHosp.value = hosp
  roundHosp.value = null
  await loadRecords(hosp.id!)
}

// 查房（仅在院）
function selectForRound(hosp: Hospitalization) {
  roundHosp.value = hosp
  viewingHosp.value = hosp
  form.value = { conditionDesc: '', treatmentPlan: '' }
  drugItems.value = []
  loadRecords(hosp.id!)
}

async function loadRecords(hospId: number) {
  recordsLoading.value = true
  try {
    records.value = await getRecords(hospId)
  } catch {
    // handled
  } finally {
    recordsLoading.value = false
  }
}

const todayStr = new Date().toISOString().substring(0, 10)

async function doSubmit() {
  submitting.value = true
  try {
    await dailyRound(
      roundHosp.value!.id!,
      form.value.conditionDesc,
      form.value.treatmentPlan,
      drugItems.value.length > 0 ? drugItems.value : undefined,
    )
    message.success('查房记录提交成功')
    form.value = { conditionDesc: '', treatmentPlan: '' }
    drugItems.value = []
    await loadRecords(roundHosp.value!.id!)
  } catch {
    // handled
  } finally {
    submitting.value = false
  }
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate()

  // Check if there's already a record for today
  const hasToday = records.value.some((r) => r.recordDate === todayStr)
  if (hasToday) {
    Modal.confirm({
      title: '当天已有查房记录',
      content: '当天已有查房记录，是否确认继续提交？',
      okText: '确认提交',
      cancelText: '取消',
      onOk: () => doSubmit(),
    })
  } else {
    await doSubmit()
  }
}

onMounted(loadData)
</script>

<style scoped>
</style>
