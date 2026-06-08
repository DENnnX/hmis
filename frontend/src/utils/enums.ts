export const genderMap: Record<string, string> = { M: '男', F: '女' }
export const titleMap: Record<string, string> = { RESIDENT: '住院医师', ATTENDING: '主治医师', VICE_CHIEF: '副主任医师', CHIEF: '主任医师' }
export const timeSlotMap: Record<string, string> = { MORNING: '上午', AFTERNOON: '下午', EVENING: '晚间' }
export const scheduleTypeMap: Record<string, string> = { OUTPATIENT: '门诊', INPATIENT_ROUND: '住院查房' }
export const registrationStatusMap: Record<string, { label: string; color: string }> = {
  WAITING: { label: '候诊中', color: 'blue' },
  VISITED: { label: '已就诊', color: 'green' },
  CANCELLED: { label: '已取消', color: 'default' },
}
export const paymentTypeMap: Record<string, string> = { CONSULTATION: '诊疗费', DRUG: '药费' }
export const paymentStatusMap: Record<string, { label: string; color: string }> = {
  UNPAID: { label: '未缴费', color: 'red' },
  PAID: { label: '已缴费', color: 'green' },
}
export const hospitalizationStatusMap: Record<string, { label: string; color: string }> = {
  IN_HOSPITAL: { label: '住院中', color: 'orange' },
  DISCHARGED: { label: '已出院', color: 'default' },
}
export const bedStatusMap: Record<string, { label: string; color: string }> = {
  AVAILABLE: { label: '空闲', color: 'green' },
  OCCUPIED: { label: '占用', color: 'red' },
}
export const payMethodMap: Record<string, string> = { CARD: '银行卡', WECHAT: '微信', ALIPAY: '支付宝' }
export const visitTypeMap: Record<string, string> = { FIRST_VISIT: '初诊', REVISIT: '复诊' }
export const BED_AVAILABLE = 'AVAILABLE'

export const titleOptions = [
  { value: 'RESIDENT', label: '住院医师' },
  { value: 'ATTENDING', label: '主治医师' },
  { value: 'VICE_CHIEF', label: '副主任医师' },
  { value: 'CHIEF', label: '主任医师' },
]
export const timeSlotOptions = [
  { value: 'MORNING', label: '上午' },
  { value: 'AFTERNOON', label: '下午' },
]
export const payMethodOptions = [
  { value: 'CARD', label: '银行卡' },
  { value: 'WECHAT', label: '微信' },
  { value: 'ALIPAY', label: '支付宝' },
]

export function formatMoney(v: number | null | undefined): string {
  if (v == null) return '¥0.00'
  return `¥${Number(v).toFixed(2)}`
}
