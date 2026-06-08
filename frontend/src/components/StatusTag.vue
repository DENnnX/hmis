<template>
  <a-tag v-if="config" :color="config.color">{{ config.label }}</a-tag>
  <span v-else>{{ value }}</span>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  registrationStatusMap,
  paymentStatusMap,
  paymentTypeMap,
  hospitalizationStatusMap,
  bedStatusMap,
} from '@/utils/enums'

const props = defineProps<{
  value: string
  category: 'registration' | 'payment' | 'paymentType' | 'hospitalization' | 'bed'
}>()

const mapByCategory: Record<string, Record<string, { label: string; color: string }>> = {
  registration: registrationStatusMap,
  payment: paymentStatusMap,
  hospitalization: hospitalizationStatusMap,
  bed: bedStatusMap,
  paymentType: Object.fromEntries(
    Object.entries(paymentTypeMap).map(([k, v]) => [k, { label: v, color: 'blue' }]),
  ),
}

const config = computed(() => mapByCategory[props.category]?.[props.value])
</script>
