<template>
  <a-layout class="layout">
    <!-- 侧边栏 -->
    <a-layout-sider v-model:collapsed="collapsed" :trigger="null" collapsible theme="dark" width="220">
      <div class="logo">
        <MedicineBoxOutlined />
        <span v-show="!collapsed">医院管理系统</span>
      </div>
      <a-menu theme="dark" mode="inline" :selected-keys="selectedKeys" @click="onMenuClick">
        <!-- 管理端 -->
        <template v-if="user.role === 'ADMIN'">
          <a-menu-item key="/admin"><template #icon><DashboardOutlined /></template>仪表盘</a-menu-item>
          <a-menu-item key="/admin/schedules"><template #icon><CalendarOutlined /></template>排班管理</a-menu-item>
          <a-menu-item key="/admin/departments"><template #icon><BankOutlined /></template>科室管理</a-menu-item>
          <a-menu-item key="/admin/doctors"><template #icon><UserOutlined /></template>医生管理</a-menu-item>
          <a-menu-item key="/admin/doctor-fees"><template #icon><MoneyCollectOutlined /></template>费用配置</a-menu-item>
          <a-menu-item key="/admin/patients"><template #icon><TeamOutlined /></template>病人管理</a-menu-item>
          <a-menu-item key="/admin/drugs"><template #icon><MedicineBoxOutlined /></template>药品管理</a-menu-item>
          <a-menu-item key="/admin/wards"><template #icon><HomeOutlined /></template>病房管理</a-menu-item>
          <a-menu-item key="/admin/beds"><template #icon><AppstoreOutlined /></template>床位管理</a-menu-item>
        </template>
        <!-- 医生端 -->
        <template v-if="user.role === 'DOCTOR'">
          <a-menu-item key="/doctor/schedule"><template #icon><CalendarOutlined /></template>我的排班</a-menu-item>
          <a-menu-item key="/doctor/visit"><template #icon><StethoscopeOutlined /></template>门诊就诊</a-menu-item>
          <a-menu-item key="/doctor/hospitalization"><template #icon><FileTextOutlined /></template>住院查房</a-menu-item>
          <a-menu-item key="/doctor/visit-history"><template #icon><HistoryOutlined /></template>接诊历史</a-menu-item>
        </template>
        <!-- 患者端 -->
        <template v-if="user.role === 'PATIENT'">
          <a-menu-item key="/patient/register"><template #icon><FormOutlined /></template>挂号</a-menu-item>
          <a-menu-item key="/patient/payments"><template #icon><WalletOutlined /></template>缴费中心</a-menu-item>
          <a-menu-item key="/patient/history"><template #icon><ProfileOutlined /></template>就诊历史</a-menu-item>
          <a-menu-item key="/patient/hospitalization"><template #icon><CreditCardOutlined /></template>住院服务</a-menu-item>
        </template>
      </a-menu>
    </a-layout-sider>

    <!-- 右侧 -->
    <a-layout>
      <!-- 顶栏 -->
      <a-layout-header class="header">
        <div class="header-left">
          <span class="trigger" @click="collapsed = !collapsed">
            <MenuUnfoldOutlined v-if="collapsed" />
            <MenuFoldOutlined v-else />
          </span>
          <a-breadcrumb>
            <a-breadcrumb-item>{{ roleLabel }}</a-breadcrumb-item>
            <a-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        <div class="header-right">
          <a-space>
            <a-tag :color="roleColor">{{ roleLabel }}</a-tag>
            <span>{{ user.username }}</span>
            <a-button type="link" size="small" @click="logout">退出</a-button>
          </a-space>
        </div>
      </a-layout-header>

      <!-- 内容区 -->
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  DashboardOutlined, BankOutlined, UserOutlined, TeamOutlined,
  MedicineBoxOutlined, MoneyCollectOutlined, HomeOutlined, AppstoreOutlined,
  CalendarOutlined, MenuFoldOutlined, MenuUnfoldOutlined,
  FormOutlined, WalletOutlined, ProfileOutlined, CreditCardOutlined,
  FileTextOutlined, HistoryOutlined,
} from '@ant-design/icons-vue'
import type { LoginUser } from '@/types'

// StethoscopeOutlined 不在 @ant-design/icons-vue 中，用其他图标替代
const StethoscopeOutlined = MedicineBoxOutlined

const route = useRoute()
const router = useRouter()
const collapsed = ref(false)

const user: LoginUser = JSON.parse(sessionStorage.getItem('user') || '{}')

const roleLabel = computed(() => ({ ADMIN: '管理端', DOCTOR: '医生端', PATIENT: '患者端' }[user.role] || ''))
const roleColor = computed(() => ({ ADMIN: 'blue', DOCTOR: 'green', PATIENT: 'orange' }[user.role] || 'default'))

const selectedKeys = computed(() => [route.path])

const titleMap: Record<string, string> = {
  '/admin': '仪表盘', '/admin/departments': '科室管理', '/admin/doctors': '医生管理',
  '/admin/patients': '病人管理', '/admin/drugs': '药品管理', '/admin/doctor-fees': '费用配置',
  '/admin/wards': '病房管理', '/admin/beds': '床位管理', '/admin/schedules': '排班管理',
  '/doctor/schedule': '我的排班', '/doctor/visit': '门诊就诊',
  '/doctor/hospitalization': '住院查房',
  '/doctor/visit-history': '接诊历史',
  '/patient/register': '挂号', '/patient/payments': '缴费中心',
  '/patient/history': '就诊历史', '/patient/hospitalization': '住院服务',
}
const currentTitle = computed(() => titleMap[route.path] || '')

function onMenuClick({ key }: { key: string }) {
  router.push(key)
}

function logout() {
  sessionStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.layout { min-height: 100vh; }
.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid #2d2d2d;
}
.logo .anticon { font-size: 20px; }
.header {
  background: #fff;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.header-left { display: flex; align-items: center; gap: 16px; }
.trigger {
  font-size: 18px;
  cursor: pointer;
  color: #333;
}
.content {
  margin: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  min-height: 360px;
}
</style>
