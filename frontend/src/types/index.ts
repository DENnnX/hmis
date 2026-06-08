export interface ApiResponse<T> {
  success: boolean
  data: T
  error: string | null
}

export interface LoginUser {
  id: number
  username: string
  role: string
  referenceId: number | null
}

export interface Department { id?: number; name: string; location: string }
export interface Doctor { id?: number; doctorNo: string; name: string; gender: string; title: string; phone: string; departmentId: number }
export interface Patient { id?: number; patientNo: string; name: string; gender: string; birthDate: string; address: string; phone: string }
export interface Drug { id?: number; drugNo: string; name: string; specification: string; unit: string; unitPrice: number }
export interface DoctorFee { id?: number; title: string; consultationFee: number }
export interface Ward { id?: number; wardNo: string; location: string; dailyFee: number; departmentId: number }
export interface Bed { id?: number; wardId: number; bedNo: string; status: string }
export interface Schedule { id?: number; doctorId: number; scheduleDate: string; timeSlot: string; scheduleType: string }
export interface Registration { id?: number; patientId: number; doctorId: number; registrationDate: string; timeSlot: string; visitType: string; status: string }
export interface OutpatientVisit { id?: number; registrationId: number; doctorId: number; patientId: number; visitTime: string; diagnosis: string; symptoms: string; consultationFee: number }
export interface Prescription { id?: number; visitId: number; totalDrugPrice: number }
export interface PrescriptionItem { id?: number; prescriptionId?: number; drugId: number; quantity: number; dosage: string; unitPrice?: number; subtotal?: number }
export interface Payment { id?: number; patientId: number; type: string; referenceId: number; amount: number; status: string; payTime: string | null; payMethod: string | null }
export interface Hospitalization { id?: number; hospitalNo: string; patientId: number; attendingDoctorId: number; wardId: number; bedId: number; admissionDate: string; dischargeDate: string | null; status: string }
export interface HospitalizationRecord { id?: number; hospitalizationId: number; recordDate: string; conditionDesc: string; treatmentPlan: string }
export interface InpatientPrescriptionItem { id?: number; prescriptionId?: number; drugId: number; quantity: number; dosage: string; unitPrice?: number; subtotal?: number }
export interface DailyCharge { id?: number; hospitalizationId: number; chargeDate: string; bedFee: number; drugFee: number; treatmentFee: number; totalFee: number }
export interface Deposit { id?: number; hospitalizationId: number; amount: number; depositTime: string }

export interface DrugItem { drugId: number; quantity: number; dosage: string; drugName?: string }

export interface ScheduleStat { department_name: string; schedule_date: string; time_slot: string; schedule_type: string; doctor_name: string; doctor_title: string }
export interface WorkloadStat { doctor_id: number; doctor_name: string; title: string; department_name: string; total_registrations: number; total_visits: number; total_hospitalizations: number }
export interface HospitalizationCostStat { hospitalization_id: number; hospital_no: string; patient_name: string; department_name: string; admission_date: string; discharge_date: string | null; status: string; total_deposit: number; total_charge: number; balance: number }
export interface PatientHistory { patient_no: string; patient_name: string; registration_date: string; time_slot: string; doctor_name: string; doctor_title: string; visit_time: string; diagnosis: string; symptoms: string; consultation_fee: number; total_drug_price: number; total_cost: number }

export interface AvailableSchedule {
  schedule_id: number
  doctor_id: number
  schedule_date: string
  time_slot: string
  schedule_type: string
  doctor_name: string
  doctor_title: string
  doctor_phone: string
  department_name: string
  department_id: number
}
