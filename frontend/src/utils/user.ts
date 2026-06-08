import type { LoginUser } from '@/types'

export function getCurrentUser(): LoginUser {
  const raw = sessionStorage.getItem('user')
  if (!raw) throw new Error('未登录')
  return JSON.parse(raw)
}
