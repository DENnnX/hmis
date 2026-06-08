<template>
  <div>
    <a-card title="排班管理">
      <template #extra>
        <a-space>
          <a-date-picker v-model:value="filterDate" value-format="YYYY-MM-DD" placeholder="筛选日期" allow-clear @change="load" />
          <a-select v-model:value="filterDoctorId" placeholder="筛选医生" allow-clear show-search :filter-option="filterDoctor" style="width: 180px" @change="load" :options="doctorOptions" />
          <a-button type="primary" @click="openModal()">
            <template #icon><PlusOutlined /></template>新增排班
          </a-button>
        </a-space>
      </template>

      <!-- 按日期分组展示 -->
      <div v-if="groupedData.length === 0 && !loading" style="text-align: center; padding: 40px 0">
        <a-empty description="暂无排班数据" />
      </div>

      <div v-for="group in groupedData" :key="group.date" class="schedule-group">
        <div class="group-header">
          <a-typography-title :level="5" style="margin: 0">
            <CalendarOutlined /> {{ group.date }}（{{ group.items.length }} 条）
          </a-typography-title>
        </div>

        <a-table :columns="columns" :data-source="group.items" :pagination="false" row-key="id" size="small" bordered>
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'doctorName'">
              <a-tag color="blue">{{ doctorNameMap[record.doctorId] || '-' }}</a-tag>
            </template>
            <template v-if="column.key === 'timeSlot'">
              <a-tag :color="record.timeSlot === 'MORNING' ? 'orange' : 'purple'">
                {{ timeSlotMap[record.timeSlot] || record.timeSlot }}
              </a-tag>
            </template>
            <template v-if="column.key === 'scheduleType'">
              <a-tag :color="record.scheduleType === 'OUTPATIENT' ? 'blue' : 'green'">
                {{ scheduleTypeMap[record.scheduleType] || record.scheduleType }}
              </a-tag>
            </template>
            <template v-if="column.key === 'action'">
              <a-popconfirm title="确定删除该排班？" @confirm="handleDelete(record.id)">
                <a-button type="link" danger size="small">删除</a-button>
              </a-popconfirm>
            </template>
          </template>
        </a-table>
      </div>
    </a-card>

    <!-- 新增弹窗 -->
    <a-modal v-model:open="visible" title="新增排班" @ok="handleOk" @cancel="resetForm">
      <a-form ref="formRef" :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="医生" name="doctorId" :rules="[{ required: true, message: '请选择医生' }]">
          <a-select v-model:value="form.doctorId" placeholder="请选择医生" show-search :filter-option="filterDoctor" :options="doctorOptions" />
        </a-form-item>
        <a-form-item label="日期" name="scheduleDate" :rules="[{ required: true, message: '请选择日期' }]">
          <a-date-picker v-model:value="form.scheduleDate" value-format="YYYY-MM-DD" style="width: 100%" />
        </a-form-item>
        <a-form-item label="时段" name="timeSlot" :rules="[{ required: true, message: '请选择时段' }]">
          <a-radio-group v-model:value="form.timeSlot">
            <a-radio-button value="MORNING">上午</a-radio-button>
            <a-radio-button value="AFTERNOON">下午</a-radio-button>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="类型" name="scheduleType" :rules="[{ required: true, message: '请选择类型' }]">
          <a-radio-group v-model:value="form.scheduleType">
            <a-radio-button value="OUTPATIENT">门诊</a-radio-button>
            <a-radio-button value="INPATIENT_ROUND">住院查房</a-radio-button>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { PlusOutlined, CalendarOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import { listSchedules, listDoctors, addSchedule, deleteSchedule } from '@/api/admin'
import { timeSlotMap, scheduleTypeMap } from '@/utils/enums'
import type { Schedule, Doctor } from '@/types'

const list = ref<Schedule[]>([])
const doctors = ref<Doctor[]>([])
const loading = ref(false)
const visible = ref(false)
const formRef = ref<FormInstance>()

const filterDate = ref<string | undefined>()
const filterDoctorId = ref<number | undefined>()

const form = reactive({
  doctorId: undefined as number | undefined,
  scheduleDate: undefined as string | undefined,
  timeSlot: undefined as string | undefined,
  scheduleType: undefined as string | undefined,
})

const columns = [
  { title: '医生', key: 'doctorName', width: 140 },
  { title: '时段', key: 'timeSlot', width: 100 },
  { title: '类型', key: 'scheduleType', width: 120 },
  { title: '操作', key: 'action', width: 80 },
]

const doctorNameMap = computed(() => Object.fromEntries(doctors.value.map((d) => [d.id, d.name])))
const doctorOptions = computed(() =>
  doctors.value.map((d) => ({ value: d.id!, label: `${d.name}（${d.doctorNo}）` })),
)

// 按日期分组，排序
const groupedData = computed(() => {
  const map = new Map<string, Schedule[]>()
  for (const s of list.value) {
    const key = s.scheduleDate?.substring(0, 10) || '未知'
    if (!map.has(key)) map.set(key, [])
    map.get(key)!.push(s)
  }
  return Array.from(map.entries())
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([date, items]) => ({
      date,
      items: items.sort((a, b) => {
        const slotOrder: Record<string, number> = { MORNING: 0, AFTERNOON: 1, EVENING: 2 }
        return (slotOrder[a.timeSlot] ?? 9) - (slotOrder[b.timeSlot] ?? 9)
      }),
    }))
})

function filterDoctor(input: string, option: { label: string }) {
  return option.label.toLowerCase().includes(input.toLowerCase())
}

async function load() {
  loading.value = true
  try {
    const [s, d] = await Promise.all([
      filterDoctorId.value
        ? import('@/api/doctor').then((m) => m.getSchedule(filterDoctorId.value!, filterDate.value))
        : listSchedules(),
      listDoctors(),
    ])
    list.value = s
    doctors.value = d
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

function openModal() {
  resetForm()
  visible.value = true
}

function resetForm() {
  Object.assign(form, { doctorId: undefined, scheduleDate: undefined, timeSlot: undefined, scheduleType: undefined })
}

async function handleOk() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  try {
    await addSchedule(form as any)
    message.success('新增成功')
    visible.value = false
    resetForm()
    load()
  } catch {
    // handled
  }
}

async function handleDelete(id: number) {
  try {
    await deleteSchedule(id)
    message.success('删除成功')
    load()
  } catch {
    // handled
  }
}

onMounted(load)
</script>

<style scoped>
.schedule-group {
  margin-bottom: 24px;
}
.group-header {
  padding: 8px 0;
  border-bottom: 2px solid #1890ff;
  margin-bottom: 8px;
}
</style>
