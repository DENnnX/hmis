<template>
  <a-card title="患者管理">
    <template #extra>
      <a-button type="primary" @click="openModal()">
        <template #icon><PlusOutlined /></template>
        新增患者
      </a-button>
    </template>

    <a-table :columns="columns" :data-source="list" :loading="loading" :pagination="false" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'gender'">
          {{ genderMap[record.gender] || record.gender }}
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="openModal(record)">编辑</a-button>
            <a-popconfirm title="确定删除该患者？" @confirm="handleDelete(record.id)">
              <a-button type="link" danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </a-card>

  <a-modal v-model:open="visible" :title="isEdit ? '编辑患者' : '新增患者'" @ok="handleOk" @cancel="resetForm">
    <a-form ref="formRef" :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 16 }">
      <a-form-item label="患者编号" name="patientNo" :rules="[{ required: true, message: '请输入患者编号' }]">
        <a-input v-model:value="form.patientNo" placeholder="请输入患者编号" />
      </a-form-item>
      <a-form-item label="姓名" name="name" :rules="[{ required: true, message: '请输入姓名' }]">
        <a-input v-model:value="form.name" placeholder="请输入姓名" />
      </a-form-item>
      <a-form-item label="性别" name="gender" :rules="[{ required: true, message: '请选择性别' }]">
        <a-radio-group v-model:value="form.gender">
          <a-radio value="M">男</a-radio>
          <a-radio value="F">女</a-radio>
        </a-radio-group>
      </a-form-item>
      <a-form-item label="出生日期" name="birthDate" :rules="[{ required: true, message: '请选择出生日期' }]">
        <a-date-picker v-model:value="form.birthDate" value-format="YYYY-MM-DD" style="width: 100%" />
      </a-form-item>
      <a-form-item label="电话" name="phone" :rules="[{ required: true, message: '请输入电话' }]">
        <a-input v-model:value="form.phone" placeholder="请输入电话" />
      </a-form-item>
      <a-form-item label="地址" name="address">
        <a-input v-model:value="form.address" placeholder="请输入地址" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import { listPatients, addPatient, updatePatient, deletePatient } from '@/api/admin'
import { genderMap } from '@/utils/enums'
import type { Patient } from '@/types'

const list = ref<Patient[]>([])
const loading = ref(false)
const visible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<Patient>({
  patientNo: '',
  name: '',
  gender: 'M',
  birthDate: '',
  phone: '',
  address: '',
})

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '患者编号', dataIndex: 'patientNo', key: 'patientNo' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '性别', dataIndex: 'gender', key: 'gender' },
  { title: '出生日期', dataIndex: 'birthDate', key: 'birthDate' },
  { title: '电话', dataIndex: 'phone', key: 'phone' },
  { title: '地址', dataIndex: 'address', key: 'address' },
  { title: '操作', key: 'action', width: 160 },
]

async function load() {
  loading.value = true
  try {
    list.value = await listPatients()
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

function openModal(record?: Patient) {
  if (record) {
    isEdit.value = true
    Object.assign(form, { ...record })
  } else {
    isEdit.value = false
    resetForm()
  }
  visible.value = true
}

function resetForm() {
  Object.assign(form, {
    id: undefined,
    patientNo: '',
    name: '',
    gender: 'M',
    birthDate: '',
    phone: '',
    address: '',
  })
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
      await updatePatient({ ...form })
      message.success('更新成功')
    } else {
      await addPatient({ ...form })
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
    await deletePatient(id)
    message.success('删除成功')
    load()
  } catch {
    // handled
  }
}

onMounted(load)
</script>
