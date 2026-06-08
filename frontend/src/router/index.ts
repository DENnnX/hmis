import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'Login', component: () => import('@/views/Login.vue') },
    {
      path: '/',
      component: () => import('@/layouts/MainLayout.vue'),
      beforeEnter: (_to, _from, next) => {
        if (!sessionStorage.getItem('user')) next('/login')
        else next()
      },
      children: [
        { path: '', redirect: '/admin' },
        // 管理端
        { path: 'admin', name: 'Dashboard', component: () => import('@/views/admin/Dashboard.vue') },
        { path: 'admin/departments', name: 'Departments', component: () => import('@/views/admin/DepartmentManage.vue') },
        { path: 'admin/doctors', name: 'Doctors', component: () => import('@/views/admin/DoctorManage.vue') },
        { path: 'admin/patients', name: 'Patients', component: () => import('@/views/admin/PatientManage.vue') },
        { path: 'admin/drugs', name: 'Drugs', component: () => import('@/views/admin/DrugManage.vue') },
        { path: 'admin/doctor-fees', name: 'DoctorFees', component: () => import('@/views/admin/DoctorFeeManage.vue') },
        { path: 'admin/wards', name: 'Wards', component: () => import('@/views/admin/WardManage.vue') },
        { path: 'admin/beds', name: 'Beds', component: () => import('@/views/admin/BedManage.vue') },
        { path: 'admin/schedules', name: 'Schedules', component: () => import('@/views/admin/ScheduleManage.vue') },
        // 医生端
        { path: 'doctor/schedule', name: 'DoctorSchedule', component: () => import('@/views/doctor/DoctorSchedule.vue') },
        { path: 'doctor/visit', name: 'OutpatientVisit', component: () => import('@/views/doctor/OutpatientVisit.vue') },
        { path: 'doctor/hospitalization', name: 'HospitalizationManage', component: () => import('@/views/doctor/HospitalizationManage.vue') },
        // 患者端
        { path: 'patient/register', name: 'PatientRegister', component: () => import('@/views/patient/PatientRegister.vue') },
        { path: 'patient/payments', name: 'PaymentCenter', component: () => import('@/views/patient/PaymentCenter.vue') },
        { path: 'patient/history', name: 'OutpatientHistory', component: () => import('@/views/patient/OutpatientHistory.vue') },
        { path: 'patient/hospitalization', name: 'HospitalizationService', component: () => import('@/views/patient/HospitalizationService.vue') },
      ],
    },
  ],
})

export default router
