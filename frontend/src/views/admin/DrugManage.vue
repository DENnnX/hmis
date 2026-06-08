<template>
  <a-card title="药品管理">
    <template #extra>
      <a-button type="primary" @click="openModal()">
        <template #icon><PlusOutlined /></template>
        新增药品
      </a-button>
    </template>

    <a-table :columns="columns" :data-source="list" :loading="loading" :pagination="false" row-key="id">
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'unitPrice'">
          <MoneyDisplay :value="record.unitPrice" />
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button type="link" size="small" @click="openModal(record)">编辑</a-button>
            <a-popconfirm title="确定删除该药品？" @confirm="handleDelete(record.id)">
              <a-button type="link" danger size="small">删除</a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>
  </a-card>

  <a-modal v-model:open="visible" :title="isEdit ? '编辑药品' : '新增药品'" @ok="handleOk" @cancel="resetForm">
    <a-form ref="formRef" :model="form" :label-col="{ span: 5 }" :wrapper-col="{ span: 16 }">
      <a-form-item label="药品编号" name="drugNo" :rules="[{ required: true, message: '请输入药品编号' }]">
        <a-input v-model:value="form.drugNo" placeholder="请输入药品编号" />
      </a-form-item>
      <a-form-item label="名称" name="name" :rules="[{ required: true, message: '请输入名称' }]">
        <a-input v-model:value="form.name" placeholder="请输入名称" />
      </a-form-item>
      <a-form-item label="规格" name="specification" :rules="[{ required: true, message: '请输入规格' }]">
        <a-input v-model:value="form.specification" placeholder="请输入规格" />
      </a-form-item>
      <a-form-item label="单位" name="unit" :rules="[{ required: true, message: '请输入单位' }]">
        <a-input v-model:value="form.unit" placeholder="如：盒、瓶、支" />
      </a-form-item>
      <a-form-item label="单价" name="unitPrice" :rules="[{ required: true, message: '请输入单价' }]">
        <a-input-number v-model:value="form.unitPrice" :min="0" :precision="2" style="width: 100%" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import { listDrugs, addDrug, updateDrug, deleteDrug } from '@/api/admin'
import MoneyDisplay from '@/components/MoneyDisplay.vue'
import type { Drug } from '@/types'

const list = ref<Drug[]>([])
const loading = ref(false)
const visible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const form = reactive<Drug>({
  drugNo: '',
  name: '',
  specification: '',
  unit: '',
  unitPrice: 0,
})

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 60 },
  { title: '药品编号', dataIndex: 'drugNo', key: 'drugNo' },
  { title: '名称', dataIndex: 'name', key: 'name' },
  { title: '规格', dataIndex: 'specification', key: 'specification' },
  { title: '单位', dataIndex: 'unit', key: 'unit' },
  { title: '单价', dataIndex: 'unitPrice', key: 'unitPrice' },
  { title: '操作', key: 'action', width: 160 },
]

async function load() {
  loading.value = true
  try {
    list.value = await listDrugs()
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

function openModal(record?: Drug) {
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
  Object.assign(form, { id: undefined, drugNo: '', name: '', specification: '', unit: '', unitPrice: 0 })
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
      await updateDrug({ ...form })
      message.success('更新成功')
    } else {
      await addDrug({ ...form })
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
    await deleteDrug(id)
    message.success('删除成功')
    load()
  } catch {
    // handled
  }
}

onMounted(load)
</script>
