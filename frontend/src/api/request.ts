import axios from 'axios'
import { message } from 'ant-design-vue'
import type { ApiResponse } from '@/types'

const request = axios.create({ baseURL: '/api', timeout: 10000 })

request.interceptors.response.use(
  (response) => {
    const res = response.data as ApiResponse<any>
    if (res.success) return res.data
    message.error(res.error || '请求失败')
    return Promise.reject(new Error(res.error || '请求失败'))
  },
  (error) => {
    message.error(error.message || '网络错误')
    return Promise.reject(error)
  },
)

export default request
