<template>
  <a-card title="病房管理">
    <template #extra>
      <a-button type="primary" @click="openModal()">
        <template #icon><PlusOutlined /></template>
        新增病房
      </a-button>
    </template>

    <a-table :columns="columns" :data-source="list" :loading="loading" :pagination="false" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'dailyFee'">
          <MoneyDisplay :value="record.dailyFee" />
        </template>
        <template v-if="column.key === 'departmentName'">
          {{ deptNameMap[record.departmentId] || '-' }}
        </template>
      </template>
    </a-table>
  </a-card>

  <a-modal v-model:open="visible" title="新增病房" @ok="handleOk" @cancel="resetForm">
    <a-form ref="formRef" :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 16 }">
      <a-form-item label="病房号" name="wardNo" :rules="[{ required: true, message: '请输入病房号' }]">
        <a-input v-model:value="form.wardNo" placeholder="请输入病房号" />
      </a-form-item>
      <a-form-item label="位置" name="location" :rules="[{ required: true, message: '请输入位置' }]">
        <a-input v-model:value="form.location" placeholder="请输入位置" />
      </a-form-item>
      <a-form-item label="日费用" name="dailyFee" :rules="[{ required: true, message: '请输入日费用' }]">
        <a-input-number v-model:value="form.dailyFee" :min="0" :precision="2" style="width: 100%" />
      </a-form-item>
      <a-form-item label="所属科室" name="departmentId" :rules="[{ required: true, message: '请选择科室' }]">
        <a-select v-model:value="form.departmentId" placeholder="请选择科室" :options="deptOptions" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import { listWards, listDepartments, addWard } from '@/api/admin'
import MoneyDisplay from '@/components/MoneyDisplay.vue'
import type { Ward, Department } from '@/types'

const list = ref<Ward[]>([])
const depts = ref<Department[]>([])
const loading = ref(false)
const visible = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<Ward>({
  wardNo: '',
  location: '',
  dailyFee: 0,
  departmentId: undefined as unknown as number,
})

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '病房号', dataIndex: 'wardNo', key: 'wardNo' },
  { title: '位置', dataIndex: 'location', key: 'location' },
  { title: '日费用', dataIndex: 'dailyFee', key: 'dailyFee' },
  { title: '所属科室', key: 'departmentName' },
]

const deptNameMap = computed(() => Object.fromEntries(depts.value.map((d) => [d.id, d.name])))
const deptOptions = computed(() => depts.value.map((d) => ({ value: d.id!, label: d.name })))

async function load() {
  loading.value = true
  try {
    const [w, d] = await Promise.all([listWards(), listDepartments()])
    list.value = w
    depts.value = d
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
  Object.assign(form, { id: undefined, wardNo: '', location: '', dailyFee: 0, departmentId: undefined })
}

async function handleOk() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  try {
    await addWard({ ...form })
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
