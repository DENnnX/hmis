<template>
  <div>
    <a-card>
      <template #title>
        <a-space>
          <CalendarOutlined />
          <span>我的排班</span>
          <a-tag color="blue">{{ doctorName }}</a-tag>
        </a-space>
      </template>
      <template #extra>
        <a-space>
          <a-date-picker v-model:value="filterDate" value-format="YYYY-MM-DD" placeholder="选择日期" allow-clear @change="onDateChange" />
          <a-button @click="loadWeek"><template #icon><CalendarOutlined /></template>近7天</a-button>
          <a-button @click="loadToday"><template #icon><ReloadOutlined /></template>今天</a-button>
        </a-space>
      </template>

      <a-spin :spinning="loading">
        <a-row :gutter="16" style="margin-bottom: 24px">
          <a-col :span="8"><a-statistic title="总排班" :value="schedules.length" suffix="条" /></a-col>
          <a-col :span="8"><a-statistic title="门诊" :value="outpatientCount" suffix="条" :value-style="{ color: '#1890ff' }" /></a-col>
          <a-col :span="8"><a-statistic title="住院查房" :value="inpatientCount" suffix="条" :value-style="{ color: '#52c41a' }" /></a-col>
        </a-row>

        <a-empty v-if="!loading && grouped.length === 0" description="暂无排班记录" />

        <div v-for="group in grouped" :key="group.date" class="day-card">
          <div class="day-header">
            <span class="day-date">{{ group.date }}</span>
            <span class="day-weekday">{{ group.weekday }}</span>
            <a-tag v-if="group.isToday" color="red">今天</a-tag>
          </div>
          <div class="day-items">
            <div v-for="item in group.items" :key="item.id" class="slot-item" :class="item.scheduleType?.toLowerCase()">
              <a-tag :color="item.timeSlot === 'MORNING' ? 'orange' : 'purple'" style="margin: 0">
                {{ timeSlotMap[item.timeSlot] || item.timeSlot }}
              </a-tag>
              <a-tag :color="item.scheduleType === 'OUTPATIENT' ? 'blue' : 'green'" style="margin: 0">
                {{ scheduleTypeMap[item.scheduleType] || item.scheduleType }}
              </a-tag>
            </div>
          </div>
        </div>
      </a-spin>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { CalendarOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import { getSchedule } from '@/api/doctor'
import { listDoctors } from '@/api/admin'
import { getCurrentUser } from '@/utils/user'
import { timeSlotMap, scheduleTypeMap } from '@/utils/enums'
import type { Schedule } from '@/types'
import dayjs from 'dayjs'

const user = getCurrentUser()
const doctorId = user.referenceId!
const doctorName = ref('')
const schedules = ref<Schedule[]>([])
const loading = ref(false)
const filterDate = ref<string | undefined>()

const weekdayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
const todayStr = dayjs().format('YYYY-MM-DD')

const outpatientCount = computed(() => schedules.value.filter((s) => s.scheduleType === 'OUTPATIENT').length)
const inpatientCount = computed(() => schedules.value.filter((s) => s.scheduleType === 'INPATIENT_ROUND').length)

const grouped = computed(() => {
  const map = new Map<string, Schedule[]>()
  for (const s of schedules.value) {
    const key = s.scheduleDate?.substring(0, 10) || '未知'
    if (!map.has(key)) map.set(key, [])
    map.get(key)!.push(s)
  }
  return Array.from(map.entries())
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([date, items]) => {
      const d = new Date(date + 'T00:00:00')
      return {
        date,
        weekday: weekdayNames[d.getDay()],
        isToday: date === todayStr,
        items: items.sort((a, b) => {
          const order: Record<string, number> = { MORNING: 0, AFTERNOON: 1, EVENING: 2 }
          return (order[a.timeSlot] ?? 9) - (order[b.timeSlot] ?? 9)
        }),
      }
    })
})

async function loadDoctor() {
  const doctors = await listDoctors()
  const doc = doctors.find((d) => d.id === doctorId)
  doctorName.value = doc ? `${doc.name}（${doc.title}）` : `医生 #${doctorId}`
}

async function loadToday() {
  filterDate.value = undefined
  loading.value = true
  try {
    schedules.value = await getSchedule(doctorId, todayStr)
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

async function loadWeek() {
  filterDate.value = undefined
  loading.value = true
  try {
    const results: Schedule[] = []
    for (let i = 0; i < 7; i++) {
      const d = dayjs().add(i, 'day').format('YYYY-MM-DD')
      const s = await getSchedule(doctorId, d)
      results.push(...s)
    }
    schedules.value = results
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

async function onDateChange() {
  if (!filterDate.value) {
    loadToday()
    return
  }
  loading.value = true
  try {
    schedules.value = await getSchedule(doctorId, filterDate.value)
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadDoctor()
  loadToday()
})
</script>

<style scoped>
.day-card {
  margin-bottom: 20px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
}
.day-header {
  background: #fafafa;
  padding: 10px 16px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-bottom: 1px solid #f0f0f0;
}
.day-date { font-weight: 600; font-size: 15px; }
.day-weekday { color: #999; }
.day-items { padding: 12px 16px; display: flex; gap: 12px; flex-wrap: wrap; }
.slot-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  border: 1px solid #e8e8e8;
}
.slot-item.outpatient { border-left: 3px solid #1890ff; }
.slot-item.inpatient_round { border-left: 3px solid #52c41a; }
</style>
