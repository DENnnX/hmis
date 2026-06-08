import request from './request'
import type {
  Schedule, Registration, OutpatientVisit, Prescription, PrescriptionItem,
  Hospitalization, HospitalizationRecord, InpatientPrescriptionItem,
} from '@/types'

export const getSchedule = (doctorId: number, date?: string) =>
  request.get<any, Schedule[]>('/doctor/schedule', { params: { doctorId, ...(date ? { date } : {}) } })

export const getRegistrations = (doctorId: number) =>
  request.get<any, Registration[]>('/doctor/registrations', { params: { doctorId } })

export const getHospitalizations = (doctorId: number) =>
  request.get<any, Hospitalization[]>('/doctor/hospitalizations', { params: { doctorId } })

export const createVisit = (registrationId: number, diagnosis: string, symptoms?: string) =>
  request.post<any, OutpatientVisit>('/doctor/visit', null, { params: { registrationId, diagnosis, ...(symptoms ? { symptoms } : {}) } })

export const prescribe = (visitId: number, items: PrescriptionItem[]) =>
  request.post<any, Prescription>('/doctor/prescribe', items, { params: { visitId } })

export const dailyRound = (hospitalizationId: number, conditionDesc: string, treatmentPlan: string, items?: InpatientPrescriptionItem[]) =>
  request.post<any, HospitalizationRecord>('/doctor/hospitalization/round', items || [], { params: { hospitalizationId, conditionDesc, treatmentPlan } })

export const getRecords = (hospitalizationId: number) =>
  request.get<any, HospitalizationRecord[]>('/patient/hospitalization/records', { params: { hospitalizationId } })
