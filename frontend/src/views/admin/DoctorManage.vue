<template>
  <a-card title="医生管理">
    <template #extra>
      <a-button type="primary" @click="openModal()">
        <template #icon><PlusOutlined /></template>
        新增医生
      </a-button>
    </template>

    <a-table :columns="columns" :data-source="list" :loading="loading" :pagination="false" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'gender'">
          {{ genderMap[record.gender] || record.gender }}
        </template>
        <template v-if="column.key === 'title'">
          {{ titleMap[record.title] || record.title }}
        </template>
        <template v-if="column.key === 'departmentName'">
          {{ deptNameMap[record.departmentId] || '-' }}
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="openModal(record)">编辑</a-button>
            <a-popconfirm title="确定删除该医生？" @confirm="handleDelete(record.id)">
              <a-button type="link" danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </a-card>

  <a-modal v-model:open="visible" :title="isEdit ? '编辑医生' : '新增医生'" @ok="handleOk" @cancel="resetForm">
    <a-form ref="formRef" :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 16 }">
      <a-form-item label="工号" name="doctorNo" :rules="[{ required: true, message: '请输入工号' }]">
        <a-input v-model:value="form.doctorNo" placeholder="请输入工号" />
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
      <a-form-item label="职称" name="title" :rules="[{ required: true, message: '请选择职称' }]">
        <a-select v-model:value="form.title" placeholder="请选择职称" :options="titleOptions" />
      </a-form-item>
      <a-form-item label="电话" name="phone" :rules="[{ required: true, message: '请输入电话' }]">
        <a-input v-model:value="form.phone" placeholder="请输入电话" />
      </a-form-item>
      <a-form-item label="科室" name="departmentId" :rules="[{ required: true, message: '请选择科室' }]">
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
import { listDoctors, listDepartments, addDoctor, updateDoctor, deleteDoctor } from '@/api/admin'
import { genderMap, titleMap, titleOptions } from '@/utils/enums'
import type { Doctor, Department } from '@/types'

const list = ref<Doctor[]>([])
const depts = ref<Department[]>([])
const loading = ref(false)
const visible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<Doctor>({
  doctorNo: '',
  name: '',
  gender: 'M',
  title: undefined as unknown as string,
  phone: '',
  departmentId: undefined as unknown as number,
})

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '工号', dataIndex: 'doctorNo', key: 'doctorNo' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '性别', dataIndex: 'gender', key: 'gender' },
  { title: '职称', dataIndex: 'title', key: 'title' },
  { title: '电话', dataIndex: 'phone', key: 'phone' },
  { title: '科室', key: 'departmentName' },
  { title: '操作', key: 'action', width: 160 },
]

const deptNameMap = computed(() => Object.fromEntries(depts.value.map((d) => [d.id, d.name])))
const deptOptions = computed(() => depts.value.map((d) => ({ value: d.id!, label: d.name })))

async function load() {
  loading.value = true
  try {
    const [d, dept] = await Promise.all([listDoctors(), listDepartments()])
    list.value = d
    depts.value = dept
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

function openModal(record?: Doctor) {
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
    doctorNo: '',
    name: '',
    gender: 'M',
    title: undefined,
    phone: '',
    departmentId: undefined,
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
      await updateDoctor({ ...form })
      message.success('更新成功')
    } else {
      await addDoctor({ ...form })
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
    await deleteDoctor(id)
    message.success('删除成功')
    load()
  } catch {
    // handled
  }
}

onMounted(load)
</script>
