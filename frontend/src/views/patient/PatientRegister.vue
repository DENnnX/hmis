<template>
  <div>
    <a-card>
      <template #title>
        <a-space>
          <ScheduleOutlined />
          <span>预约挂号</span>
        </a-space>
      </template>
      <template #extra>
        <a-button @click="loadData"><template #icon><ReloadOutlined /></template>刷新</a-button>
      </template>

      <a-alert v-if="patientName" type="info" show-icon style="margin-bottom: 16px">
        <template #message>当前患者：{{ patientName }}</template>
      </a-alert>

      <a-space style="margin-bottom: 16px">
        <span>科室筛选：</span>
        <a-select
          v-model:value="filterDeptId"
          placeholder="全部科室"
          allow-clear
          style="width: 200px"
        >
          <a-select-option v-for="d in deptOptions" :key="d.id" :value="d.id">
            {{ d.name }}
          </a-select-option>
        </a-select>
      </a-space>

      <a-spin :spinning="loading">
        <a-empty v-if="!loading && groupedByDate.length === 0" description="暂无可预约的门诊排班" />

        <div v-for="group in groupedByDate" :key="group.date" style="margin-bottom: 20px">
          <a-divider orientation="left">
            <a-space>
              <CalendarOutlined />
              <span>{{ group.date }} {{ group.weekday }}</span>
              <a-tag v-if="group.date === today" color="blue">今天</a-tag>
            </a-space>
          </a-divider>

          <a-row :gutter="[12, 12]">
            <a-col v-for="slot in group.slots" :key="slot.schedule_id" :xs="24" :sm="12" :md="8" :lg="6">
              <a-card
                hoverable
                size="small"
                :class="{ 'selected-slot': isSelected(slot) }"
                @click="selectSlot(slot)"
              >
                <a-space direction="vertical" :size="4" style="width: 100%">
                  <a-space>
                    <UserOutlined />
                    <span style="font-weight: 600; font-size: 15px">{{ slot.doctor_name }}</span>
                    <a-tag :color="titleColorMap[slot.doctor_title]" style="margin-left: 0">
                      {{ titleMap[slot.doctor_title] || slot.doctor_title }}
                    </a-tag>
                  </a-space>
                  <a-space>
                    <a-tag color="purple">{{ timeSlotMap[slot.time_slot] || slot.time_slot }}</a-tag>
                    <span style="color: #888; font-size: 12px">{{ slot.department_name }}</span>
                  </a-space>
                </a-space>
              </a-card>
            </a-col>
          </a-row>
        </div>
      </a-spin>
    </a-card>

    <a-card v-if="selectedSlot" title="确认挂号信息" style="margin-top: 16px">
      <a-descriptions :column="{ xs: 1, sm: 2 }" bordered size="small">
        <a-descriptions-item label="医生">
          {{ selectedSlot.doctor_name }}
          <a-tag :color="titleColorMap[selectedSlot.doctor_title]" style="margin-left: 8px">
            {{ titleMap[selectedSlot.doctor_title] || selectedSlot.doctor_title }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="科室">{{ selectedSlot.department_name }}</a-descriptions-item>
        <a-descriptions-item label="日期">{{ selectedSlot.schedule_date }}</a-descriptions-item>
        <a-descriptions-item label="时段">{{ timeSlotMap[selectedSlot.time_slot] || selectedSlot.time_slot }}</a-descriptions-item>
      </a-descriptions>

      <a-form style="margin-top: 16px" :label-col="{ span: 4 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="就诊类型" required>
          <a-radio-group v-model:value="visitType">
            <a-radio-button value="FIRST_VISIT">初诊</a-radio-button>
            <a-radio-button value="REVISIT">复诊</a-radio-button>
          </a-radio-group>
        </a-form-item>
        <a-form-item :wrapper-col="{ offset: 4, span: 16 }">
          <a-space>
            <a-button type="primary" :loading="submitting" :disabled="!patientId" @click="handleRegister">
              确认挂号
            </a-button>
            <a-button @click="selectedSlot = null">取消选择</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-result
      v-if="result"
      status="success"
      title="挂号成功"
      sub-title="请按时前往就诊"
      style="margin-top: 24px"
    >
      <template #extra>
        <a-descriptions :column="1" bordered size="small">
          <a-descriptions-item label="挂号编号">{{ result.id }}</a-descriptions-item>
          <a-descriptions-item label="挂号日期">{{ result.registrationDate }}</a-descriptions-item>
          <a-descriptions-item label="时段">{{ timeSlotMap[result.timeSlot] || result.timeSlot }}</a-descriptions-item>
          <a-descriptions-item label="就诊类型">{{ visitTypeMap[result.visitType] || result.visitType }}</a-descriptions-item>
        </a-descriptions>
      </template>
    </a-result>

    <a-card title="我的挂号记录" style="margin-top: 16px">
      <a-table
        :columns="regColumns"
        :data-source="registrations"
        :pagination="false"
        row-key="id"
        size="middle"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'registrationDate'">
            {{ record.registrationDate }}
          </template>
          <template v-if="column.key === 'timeSlot'">
            {{ timeSlotMap[record.timeSlot] || record.timeSlot }}
          </template>
          <template v-if="column.key === 'visitType'">
            {{ visitTypeMap[record.visitType] || record.visitType }}
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="registrationStatusMap[record.status]?.color">
              {{ registrationStatusMap[record.status]?.label || record.status }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-popconfirm
              v-if="record.status === 'WAITING'"
              title="确定取消此挂号？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleCancel(record)"
            >
              <a-button type="link" danger size="small">取消挂号</a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
      <a-empty v-if="!registrations.length" description="暂无挂号记录" />
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { ScheduleOutlined, ReloadOutlined, CalendarOutlined, UserOutlined } from '@ant-design/icons-vue'
import { getAvailableSchedules, register, getRegistrations, cancelRegistration } from '@/api/patient'
import { listPatients } from '@/api/admin'
import { getCurrentUser } from '@/utils/user'
import { timeSlotMap, titleMap, visitTypeMap, registrationStatusMap } from '@/utils/enums'
import type { AvailableSchedule, Registration } from '@/types'

const titleColorMap: Record<string, string> = {
  RESIDENT: 'default',
  ATTENDING: 'blue',
  VICE_CHIEF: 'purple',
  CHIEF: 'red',
}

const weekdayMap = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

const loading = ref(false)
const submitting = ref(false)
const schedules = ref<AvailableSchedule[]>([])
const registrations = ref<Registration[]>([])
const patientId = ref<number | null>(null)
const patientName = ref('')
const filterDeptId = ref<number | undefined>(undefined)
const selectedSlot = ref<AvailableSchedule | null>(null)
const visitType = ref('FIRST_VISIT')
const result = ref<Registration | null>(null)

const today = new Date().toISOString().slice(0, 10)

const deptOptions = computed(() => {
  const seen = new Map<number, string>()
  schedules.value.forEach((s) => {
    if (!seen.has(s.department_id)) seen.set(s.department_id, s.department_name)
  })
  return Array.from(seen, ([id, name]) => ({ id, name }))
})

const filteredSchedules = computed(() => {
  if (!filterDeptId.value) return schedules.value
  return schedules.value.filter((s) => s.department_id === filterDeptId.value)
})

const groupedByDate = computed(() => {
  const map = new Map<string, AvailableSchedule[]>()
  filteredSchedules.value.forEach((s) => {
    if (!map.has(s.schedule_date)) map.set(s.schedule_date, [])
    map.get(s.schedule_date)!.push(s)
  })
  return Array.from(map, ([date, slots]) => ({
    date,
    weekday: weekdayMap[new Date(date).getDay()],
    slots,
  }))
})

const regColumns = [
  { title: '编号', dataIndex: 'id', key: 'id', width: 70 },
  { title: '挂号日期', dataIndex: 'registrationDate', key: 'registrationDate', width: 120 },
  { title: '时段', dataIndex: 'timeSlot', key: 'timeSlot', width: 80 },
  { title: '就诊类型', dataIndex: 'visitType', key: 'visitType', width: 90 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '操作', key: 'action', width: 100 },
]

function isSelected(slot: AvailableSchedule) {
  return selectedSlot.value?.schedule_id === slot.schedule_id
}

function selectSlot(slot: AvailableSchedule) {
  if (isSelected(slot)) {
    selectedSlot.value = null
  } else {
    selectedSlot.value = slot
    result.value = null
  }
}

async function loadData() {
  loading.value = true
  try {
    const [scheds, patients, regs] = await Promise.all([getAvailableSchedules(), listPatients(), getRegistrations(patientId.value!)])
    schedules.value = scheds
    registrations.value = regs
    const matched = patients.find((p) => p.id === patientId.value)
    if (matched) patientName.value = `${matched.name}（${matched.patientNo}）`
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

async function handleCancel(reg: Registration) {
  try {
    await cancelRegistration(reg.id!)
    message.success('已取消挂号')
    await loadData()
  } catch {
    // handled
  }
}

async function handleRegister() {
  if (!patientId.value || !selectedSlot.value) return
  submitting.value = true
  result.value = null
  try {
    const s = selectedSlot.value
    const res = await register(patientId.value, s.doctor_id, s.schedule_date, s.time_slot, visitType.value)
    result.value = res
    selectedSlot.value = null
    message.success('挂号成功')
    loadData()
  } catch {
    // handled
  } finally {
    submitting.value = false
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
.selected-slot {
  border-color: #1890ff !important;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}
</style>
