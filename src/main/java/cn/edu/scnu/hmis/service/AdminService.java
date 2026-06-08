package cn.edu.scnu.hmis.service;

import cn.edu.scnu.hmis.entity.*;
import cn.edu.scnu.hmis.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminService {

    private final DepartmentMapper departmentMapper;
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;
    private final DrugMapper drugMapper;
    private final DoctorFeeMapper doctorFeeMapper;
    private final WardMapper wardMapper;
    private final BedMapper bedMapper;

    public AdminService(DepartmentMapper departmentMapper,
                        DoctorMapper doctorMapper,
                        PatientMapper patientMapper,
                        DrugMapper drugMapper,
                        DoctorFeeMapper doctorFeeMapper,
                        WardMapper wardMapper,
                        BedMapper bedMapper) {
        this.departmentMapper = departmentMapper;
        this.doctorMapper = doctorMapper;
        this.patientMapper = patientMapper;
        this.drugMapper = drugMapper;
        this.doctorFeeMapper = doctorFeeMapper;
        this.wardMapper = wardMapper;
        this.bedMapper = bedMapper;
    }

    // ========== Department ==========

    public List<Department> listDepartments() {
        return departmentMapper.findAll();
    }

    public Department getDepartment(Long id) {
        return departmentMapper.findById(id);
    }

    public int addDepartment(Department d) {
        return departmentMapper.insert(d);
    }

    public int updateDepartment(Department d) {
        return departmentMapper.update(d);
    }

    public int deleteDepartment(Long id) {
        return departmentMapper.deleteById(id);
    }

    // ========== Doctor ==========

    public List<Doctor> listDoctors() {
        return doctorMapper.findAll();
    }

    public Doctor getDoctor(Long id) {
        return doctorMapper.findById(id);
    }

    public int addDoctor(Doctor d) {
        return doctorMapper.insert(d);
    }

    public int updateDoctor(Doctor d) {
        return doctorMapper.update(d);
    }

    public int deleteDoctor(Long id) {
        return doctorMapper.deleteById(id);
    }

    // ========== Drug ==========

    public List<Drug> listDrugs() {
        return drugMapper.findAll();
    }

    public Drug getDrug(Long id) {
        return drugMapper.findById(id);
    }

    public int addDrug(Drug d) {
        return drugMapper.insert(d);
    }

    public int updateDrug(Drug d) {
        return drugMapper.update(d);
    }

    public int deleteDrug(Long id) {
        return drugMapper.deleteById(id);
    }

    // ========== DoctorFee ==========

    public List<DoctorFee> listDoctorFees() {
        return doctorFeeMapper.findAll();
    }

    public int updateDoctorFee(DoctorFee f) {
        return doctorFeeMapper.update(f);
    }

    // ========== Ward ==========

    public List<Ward> listWards() {
        return wardMapper.findAll();
    }

    public int addWard(Ward w) {
        return wardMapper.insert(w);
    }

    // ========== Bed ==========

    public List<Bed> listBeds() {
        return bedMapper.findAll();
    }

    public int addBed(Bed b) {
        return bedMapper.insert(b);
    }

    // ========== Patient ==========

    public List<Patient> listPatients() {
        return patientMapper.findAll();
    }

    public Patient getPatient(Long id) {
        return patientMapper.findById(id);
    }

    public int addPatient(Patient p) {
        return patientMapper.insert(p);
    }

    public int updatePatient(Patient p) {
        return patientMapper.update(p);
    }

    public int deletePatient(Long id) {
        return patientMapper.deleteById(id);
    }
}
