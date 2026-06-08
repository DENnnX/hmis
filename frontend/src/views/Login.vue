<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <h1>医院管理系统</h1>
        <p>Hospital Management Information System</p>
      </div>
      <a-form :model="form" @finish="handleLogin" layout="vertical" size="large">
        <a-form-item name="username" :rules="[{ required: true, message: '请输入用户名' }]">
          <a-input v-model:value="form.username" placeholder="用户名" allow-clear>
            <template #prefix><UserOutlined /></template>
          </a-input>
        </a-form-item>
        <a-form-item name="password" :rules="[{ required: true, message: '请输入密码' }]">
          <a-input-password v-model:value="form.password" placeholder="密码">
            <template #prefix><LockOutlined /></template>
          </a-input-password>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" block :loading="loading">登 录</a-button>
        </a-form-item>
      </a-form>
      <div class="login-hint">
        <a-divider>测试账号</a-divider>
        <div class="accounts">
          <a-tag color="blue" @click="fill('admin', 'admin123')">管理员 admin</a-tag>
          <a-tag color="green" @click="fill('zhangwei', '123456')">医生 zhangwei</a-tag>
          <a-tag color="orange" @click="fill('zhangsan', '123456')">患者 zhangsan</a-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { login } from '@/api/auth'
import type { LoginUser } from '@/types'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

function fill(u: string, p: string) {
  form.username = u
  form.password = p
}

async function handleLogin() {
  loading.value = true
  try {
    const user: LoginUser = await login(form.username, form.password)
    sessionStorage.setItem('user', JSON.stringify(user))
    message.success(`欢迎，${user.username}`)
    const routes: Record<string, string> = { ADMIN: '/admin', DOCTOR: '/doctor/schedule', PATIENT: '/patient/register' }
    router.push(routes[user.role] || '/admin')
  } catch {
    // error already shown by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.15);
}
.login-header {
  text-align: center;
  margin-bottom: 32px;
}
.login-header h1 {
  font-size: 24px;
  color: #1d1e1f;
  margin-bottom: 4px;
}
.login-header p {
  color: #999;
  font-size: 13px;
}
.accounts {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}
.accounts .ant-tag {
  cursor: pointer;
}
</style>
