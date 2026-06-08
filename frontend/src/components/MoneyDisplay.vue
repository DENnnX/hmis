<template>
  <span>{{ formatMoney(value) }}</span>
</template>

<script setup lang="ts">
import { formatMoney } from '@/utils/enums'

defineProps<{ value: number | null | undefined }>()
</script>
