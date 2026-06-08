import request from './request'
import type { LoginUser } from '@/types'

export const login = (username: string, password: string) =>
  request.post<any, LoginUser>('/auth/login', null, { params: { username, password } })
