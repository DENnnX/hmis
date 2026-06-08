<template>
  <a-card title="费用配置">
    <a-table :columns="columns" :data-source="list" :loading="loading" :pagination="false" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'title'">
          {{ titleMap[record.title] || record.title }}
        </template>
        <template v-if="column.key === 'consultationFee'">
          <MoneyDisplay :value="record.consultationFee" />
        </template>
        <template v-if="column.key === 'action'">
          <a-button type="link" size="small" @click="openModal(record)">编辑</a-button>
        </template>
      </template>
    </a-table>
  </a-card>

  <a-modal v-model:open="visible" title="编辑费用" @ok="handleOk" @cancel="resetForm">
    <a-form ref="formRef" :model="form" :label-col="{ span: 6 }" :wrapper-col="{ span: 15 }">
      <a-form-item label="职称">
        <a-input :value="titleMap[form.title]" disabled />
      </a-form-item>
      <a-form-item label="诊疗费" name="consultationFee" :rules="[{ required: true, message: '请输入诊疗费' }]">
        <a-input-number v-model:value="form.consultationFee" :min="0" :precision="2" style="width: 100%" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import { listDoctorFees, updateDoctorFee } from '@/api/admin'
import { titleMap } from '@/utils/enums'
import MoneyDisplay from '@/components/MoneyDisplay.vue'
import type { DoctorFee } from '@/types'

const list = ref<DoctorFee[]>([])
const loading = ref(false)
const visible = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<DoctorFee>({ title: '', consultationFee: 0 })

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '职称', dataIndex: 'title', key: 'title' },
  { title: '诊疗费', dataIndex: 'consultationFee', key: 'consultationFee' },
  { title: '操作', key: 'action', width: 100 },
]

async function load() {
  loading.value = true
  try {
    list.value = await listDoctorFees()
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

function openModal(record: DoctorFee) {
  Object.assign(form, { ...record })
  visible.value = true
}

function resetForm() {
  Object.assign(form, { id: undefined, title: '', consultationFee: 0 })
}

async function handleOk() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  try {
    await updateDoctorFee({ ...form })
    message.success('更新成功')
    visible.value = false
    resetForm()
    load()
  } catch {
    // handled
  }
}

onMounted(load)
</script>
