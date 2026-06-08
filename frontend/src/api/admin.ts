import request from './request'
import type {
  Department, Doctor, Patient, Drug, DoctorFee, Ward, Bed, Schedule,
  ScheduleStat, WorkloadStat, HospitalizationCostStat,
} from '@/types'

export const listDepartments = () => request.get<any, Department[]>('/admin/departments')
export const addDepartment = (d: Department) => request.post<any, number>('/admin/departments', d)
export const updateDepartment = (d: Department) => request.put<any, number>('/admin/departments', d)
export const deleteDepartment = (id: number) => request.delete<any, number>(`/admin/departments/${id}`)

export const listDoctors = () => request.get<any, Doctor[]>('/admin/doctors')
export const addDoctor = (d: Doctor) => request.post<any, number>('/admin/doctors', d)
export const updateDoctor = (d: Doctor) => request.put<any, number>('/admin/doctors', d)
export const deleteDoctor = (id: number) => request.delete<any, number>(`/admin/doctors/${id}`)

export const listPatients = () => request.get<any, Patient[]>('/admin/patients')
export const addPatient = (p: Patient) => request.post<any, number>('/admin/patients', p)
export const updatePatient = (p: Patient) => request.put<any, number>('/admin/patients', p)
export const deletePatient = (id: number) => request.delete<any, number>(`/admin/patients/${id}`)

export const listDrugs = () => request.get<any, Drug[]>('/admin/drugs')
export const addDrug = (d: Drug) => request.post<any, number>('/admin/drugs', d)
export const updateDrug = (d: Drug) => request.put<any, number>('/admin/drugs', d)
export const deleteDrug = (id: number) => request.delete<any, number>(`/admin/drugs/${id}`)

export const listDoctorFees = () => request.get<any, DoctorFee[]>('/admin/doctor-fees')
export const updateDoctorFee = (f: DoctorFee) => request.put<any, number>('/admin/doctor-fees', f)

export const listWards = () => request.get<any, Ward[]>('/admin/wards')
export const addWard = (w: Ward) => request.post<any, number>('/admin/wards', w)

export const listBeds = () => request.get<any, Bed[]>('/admin/beds')
export const addBed = (b: Bed) => request.post<any, number>('/admin/beds', b)

export const listSchedules = () => request.get<any, Schedule[]>('/admin/schedules')
export const addSchedule = (s: Schedule) => request.post<any, number>('/admin/schedules', s)
export const deleteSchedule = (id: number) => request.delete<any, number>(`/admin/schedules/${id}`)

export const getWorkloadStats = () => request.get<any, WorkloadStat[]>('/admin/statistics/workload')
export const getHospitalizationCostStats = () => request.get<any, HospitalizationCostStat[]>('/admin/statistics/hospitalization-cost')
export const getScheduleStats = (date: string) => request.get<any, any[]>('/admin/statistics/schedule', { params: { date } })
export const getPatientTreatmentStats = () => request.get<any, any[]>('/admin/statistics/patient-treatment')
export const getPatientTreatmentDetail = (patientId: number) => request.get<any, any[]>('/admin/statistics/patient-treatment/detail', { params: { patientId } })
export const getPatientHospitalizations = (patientId: number) => request.get<any, any[]>('/admin/patient-hospitalizations', { params: { patientId } })
