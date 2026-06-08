<template>
  <a-card title="科室管理">
    <template #extra>
      <a-button type="primary" @click="openModal()">
        <template #icon><PlusOutlined /></template>
        新增科室
      </a-button>
    </template>

    <a-table :columns="columns" :data-source="list" :loading="loading" :pagination="false" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="openModal(record)">编辑</a-button>
            <a-popconfirm title="确定删除该科室？" @confirm="handleDelete(record.id)">
              <a-button type="link" danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </a-card>

  <a-modal v-model:open="visible" :title="isEdit ? '编辑科室' : '新增科室'" @ok="handleOk" @cancel="resetForm">
    <a-form ref="formRef" :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 16 }">
      <a-form-item label="科室名称" name="name" :rules="[{ required: true, message: '请输入科室名称' }]">
        <a-input v-model:value="form.name" placeholder="请输入科室名称" />
      </a-form-item>
      <a-form-item label="位置" name="location" :rules="[{ required: true, message: '请输入位置' }]">
        <a-input v-model:value="form.location" placeholder="请输入位置" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import { listDepartments, addDepartment, updateDepartment, deleteDepartment } from '@/api/admin'
import type { Department } from '@/types'

const list = ref<Department[]>([])
const loading = ref(false)
const visible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<Department>({ name: '', location: '' })

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '科室名称', dataIndex: 'name', key: 'name' },
  { title: '位置', dataIndex: 'location', key: 'location' },
  { title: '操作', key: 'action', width: 160 },
]

async function load() {
  loading.value = true
  try {
    list.value = await listDepartments()
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

function openModal(record?: Department) {
  if (record) {
    isEdit.value = true
    Object.assign(form, { id: record.id, name: record.name, location: record.location })
  } else {
    isEdit.value = false
    resetForm()
  }
  visible.value = true
}

function resetForm() {
  Object.assign(form, { id: undefined, name: '', location: '' })
}

async function handleOk() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  try {
    if (isEdit.value) {
      await updateDepartment({ ...form })
      message.success('更新成功')
    } else {
      await addDepartment({ ...form })
      message.success('新增成功')
    }
    visible.value = false
    resetForm()
    load()
  } catch {
    // handled
  }
}

async function handleDelete(id: number) {
  try {
    await deleteDepartment(id)
    message.success('删除成功')
    load()
  } catch {
    // handled
  }
}

onMounted(load)
</script>
