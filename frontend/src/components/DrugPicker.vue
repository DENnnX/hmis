<template>
  <div>
    <a-button type="dashed" block @click="openModal">
      <template #icon><PlusOutlined /></template>
      添加药品
    </a-button>

    <a-table
      v-if="modelValue.length"
      :columns="columns"
      :data-source="modelValue"
      :pagination="false"
      size="small"
      row-key="drugId"
      style="margin-top: 8px"
    >
      <template #bodyCell="{ column, record, index }">
        <template v-if="column.key === 'action'">
          <a-popconfirm title="确定移除该药品？" @confirm="removeItem(index)">
            <a-button type="link" danger size="small">删除</a-button>
          </a-popconfirm>
        </template>
      </template>
    </a-table>

    <a-empty v-else description="暂无药品" style="margin-top: 8px" />

    <a-modal v-model:open="visible" title="添加药品" @ok="handleOk" @cancel="resetForm">
      <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 16 }">
        <a-form-item label="药品" required>
          <a-select
            v-model:value="form.drugId"
            placeholder="请选择药品"
            show-search
            :filter-option="filterDrug"
            :options="drugOptions"
          />
        </a-form-item>
        <a-form-item label="数量" required>
          <a-input-number v-model:value="form.quantity" :min="1" style="width: 100%" />
        </a-form-item>
        <a-form-item label="用法">
          <a-input v-model:value="form.dosage" placeholder="如：每日三次，每次一片" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { listDrugs } from '@/api/admin'
import type { Drug, DrugItem } from '@/types'

const props = defineProps<{ modelValue: DrugItem[] }>()
const emit = defineEmits<{ 'update:modelValue': [value: DrugItem[]] }>()

const visible = ref(false)
const drugs = ref<Drug[]>([])
const form = reactive({ drugId: undefined as number | undefined, quantity: 1, dosage: '' })

const drugOptions = ref<{ value: number; label: string }[]>([])

const columns = [
  { title: '药品名称', dataIndex: 'drugName', key: 'drugName' },
  { title: '数量', dataIndex: 'quantity', key: 'quantity' },
  { title: '用法', dataIndex: 'dosage', key: 'dosage' },
  { title: '操作', key: 'action', width: 80 },
]

function filterDrug(input: string, option: { label: string }) {
  return option.label.toLowerCase().includes(input.toLowerCase())
}

async function loadDrugs() {
  try {
    drugs.value = await listDrugs()
    drugOptions.value = drugs.value.map((d) => ({ value: d.id!, label: `${d.name}（${d.specification}）` }))
  } catch {
    // error handled by interceptor
  }
}

function openModal() {
  visible.value = true
}

function resetForm() {
  form.drugId = undefined
  form.quantity = 1
  form.dosage = ''
}

function handleOk() {
  if (!form.drugId) {
    message.warning('请选择药品')
    return
  }
  const drug = drugs.value.find((d) => d.id === form.drugId)
  if (!drug) return

  const item: DrugItem = {
    drugId: form.drugId,
    quantity: form.quantity,
    dosage: form.dosage,
    drugName: drug.name,
  }
  emit('update:modelValue', [...props.modelValue, item])
  visible.value = false
  resetForm()
}

function removeItem(index: number) {
  const newList = [...props.modelValue]
  newList.splice(index, 1)
  emit('update:modelValue', newList)
}

onMounted(loadDrugs)
</script>
