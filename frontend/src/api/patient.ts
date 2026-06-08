import request from './request'
import type { Registration, Payment, PatientHistory, Hospitalization, HospitalizationRecord, Deposit, AvailableSchedule, DrugDetail } from '@/types'

export const getRegistrations = (patientId: number) =>
  request.get<any, Registration[]>('/patient/registrations', { params: { patientId } })

export const cancelRegistration = (registrationId: number) =>
  request.post<any, number>('/patient/registrations/cancel', null, { params: { registrationId } })

export const getAvailableSchedules = () =>
  request.get<any, AvailableSchedule[]>('/patient/available-schedules')

export const register = (patientId: number, doctorId: number, date: string, timeSlot: string, visitType: string) =>
  request.post<any, Registration>('/patient/register', null, { params: { patientId, doctorId, date, timeSlot, visitType } })

export const pay = (paymentId: number, payMethod: string) =>
  request.post<any, number>('/patient/pay', null, { params: { paymentId, payMethod } })

export const getPayments = (patientId: number) =>
  request.get<any, Payment[]>('/patient/payments', { params: { patientId } })

export const getUnpaidPayments = (patientId: number) =>
  request.get<any, Payment[]>('/patient/unpaid', { params: { patientId } })

export const getHistory = (patientId: number) =>
  request.get<any, PatientHistory[]>('/patient/history', { params: { patientId } })

export const getBalance = (hospitalizationId: number) =>
  request.get<any, number>('/patient/hospitalization/balance', { params: { hospitalizationId } })

export const getHospitalizations = (patientId: number) =>
  request.get<any, Hospitalization[]>('/patient/hospitalizations', { params: { patientId } })

export const getRecords = (hospitalizationId: number) =>
  request.get<any, HospitalizationRecord[]>('/patient/hospitalization/records', { params: { hospitalizationId } })

export const admit = (patientId: number, doctorId: number, wardId: number, bedId: number) =>
  request.post<any, Hospitalization>('/patient/hospitalization/admit', null, { params: { patientId, doctorId, wardId, bedId } })

export const deposit = (hospitalizationId: number, amount: number) =>
  request.post<any, Deposit>('/patient/hospitalization/deposit', null, { params: { hospitalizationId, amount } })

export const discharge = (hospitalizationId: number) =>
  request.post<any, { rows: number; refund: number }>('/patient/hospitalization/discharge', null, { params: { hospitalizationId } })

export const getPrescriptionDetail = (prescriptionId: number) =>
  request.get<any, DrugDetail[]>('/patient/prescription-detail', { params: { prescriptionId } })

export const getRecordPrescriptionItems = (recordId: number) =>
  request.get<any, DrugDetail[]>('/patient/hospitalization/prescription-items', { params: { recordId } })
