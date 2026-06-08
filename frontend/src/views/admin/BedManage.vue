<template>
  <a-card title="床位管理">
    <template #extra>
      <a-button type="primary" @click="openModal()">
        <template #icon><PlusOutlined /></template>
        新增床位
      </a-button>
    </template>

    <a-table :columns="columns" :data-source="list" :loading="loading" :pagination="false" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'wardNo'">
          {{ wardNoMap[record.wardId] || '-' }}
        </template>
        <template v-if="column.key === 'status'">
          <StatusTag :value="record.status" category="bed" />
        </template>
      </template>
    </a-table>
  </a-card>

  <a-modal v-model:open="visible" title="新增床位" @ok="handleOk" @cancel="resetForm">
    <a-form ref="formRef" :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 16 }">
      <a-form-item label="病房" name="wardId" :rules="[{ required: true, message: '请选择病房' }]">
        <a-select v-model:value="form.wardId" placeholder="请选择病房" :options="wardOptions" />
      </a-form-item>
      <a-form-item label="床位号" name="bedNo" :rules="[{ required: true, message: '请输入床位号' }]">
        <a-input v-model:value="form.bedNo" placeholder="请输入床位号" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import { listBeds, listWards, addBed } from '@/api/admin'
import { BED_AVAILABLE } from '@/utils/enums'
import StatusTag from '@/components/StatusTag.vue'
import type { Bed, Ward } from '@/types'

const list = ref<Bed[]>([])
const wards = ref<Ward[]>([])
const loading = ref(false)
const visible = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<Bed>({
  wardId: undefined as unknown as number,
  bedNo: '',
  status: BED_AVAILABLE,
})

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '床位号', dataIndex: 'bedNo', key: 'bedNo' },
  { title: '病房号', key: 'wardNo' },
  { title: '状态', dataIndex: 'status', key: 'status' },
]

const wardNoMap = computed(() => Object.fromEntries(wards.value.map((w) => [w.id, w.wardNo])))
const wardOptions = computed(() => wards.value.map((w) => ({ value: w.id!, label: `${w.wardNo} - ${w.location}` })))

async function load() {
  loading.value = true
  try {
    const [b, w] = await Promise.all([listBeds(), listWards()])
    list.value = b
    wards.value = w
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
  Object.assign(form, { id: undefined, wardId: undefined, bedNo: '', status: BED_AVAILABLE })
}

async function handleOk() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  try {
    await addBed({ ...form })
    message.success('新增成功')
    visible.value = false
    resetForm()
    load()
  } catch {
    // handled
  }
}

onMounted(load)
</script>
