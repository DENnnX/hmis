<template>
  <div>
    <a-form-item label="病房" name="wardId" :rules="[{ required: true, message: '请选择病房' }]">
      <a-select
        v-model:value="selectedWardId"
        placeholder="请选择病房"
        style="width: 100%"
        allow-clear
        show-search
        :filter-option="filterWard"
        @change="onWardChange"
      >
        <a-select-option v-for="w in wards" :key="w.id" :value="w.id">
          {{ w.wardNo }} - {{ w.location }}（¥{{ w.dailyFee }}/天）
        </a-select-option>
      </a-select>
    </a-form-item>

    <a-form-item label="床位" name="bedId" :rules="[{ required: true, message: '请选择床位' }]">
      <a-select
        v-model:value="selectedBedId"
        placeholder="请选择空闲床位"
        style="width: 100%"
        allow-clear
        :disabled="!selectedWardId"
        @change="onBedChange"
      >
        <a-select-option v-for="b in availableBeds" :key="b.id" :value="b.id">
          {{ b.bedNo }}
        </a-select-option>
      </a-select>
      <div v-if="selectedWardId && availableBeds.length === 0" style="color: #ff4d4f; font-size: 12px; margin-top: 4px">
        该病房暂无空闲床位
      </div>
      <div v-if="selectedWardId && availableBeds.length > 0" style="color: #999; font-size: 12px; margin-top: 4px">
        共 {{ availableBeds.length }} 张空闲床位
      </div>
    </a-form-item>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { listWards, listBeds } from '@/api/admin'
import { BED_AVAILABLE } from '@/utils/enums'
import type { Ward, Bed } from '@/types'

const emit = defineEmits<{ change: [payload: { wardId: number; bedId: number }] }>()

const wards = ref<Ward[]>([])
const beds = ref<Bed[]>([])
const selectedWardId = ref<number | undefined>()
const selectedBedId = ref<number | undefined>()

const availableBeds = computed(() =>
  beds.value.filter((b) => b.wardId === selectedWardId.value && b.status === BED_AVAILABLE),
)

function filterWard(input: string, option: { label: string }) {
  return option.label.toLowerCase().includes(input.toLowerCase())
}

function onWardChange() {
  selectedBedId.value = undefined
}

function onBedChange() {
  if (selectedWardId.value && selectedBedId.value) {
    emit('change', { wardId: selectedWardId.value, bedId: selectedBedId.value })
  }
}

async function load() {
  try {
    const [w, b] = await Promise.all([listWards(), listBeds()])
    wards.value = w
    beds.value = b
  } catch {
    // handled by interceptor
  }
}

onMounted(load)
</script>
