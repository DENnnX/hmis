package cn.edu.scnu.hmis.service;

import cn.edu.scnu.hmis.entity.Department;
import cn.edu.scnu.hmis.mapper.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private DepartmentMapper departmentMapper;
    @Mock
    private DoctorMapper doctorMapper;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private DrugMapper drugMapper;
    @Mock
    private DoctorFeeMapper doctorFeeMapper;
    @Mock
    private WardMapper wardMapper;
    @Mock
    private BedMapper bedMapper;

    @InjectMocks
    private AdminService adminService;

    @Test
    void listDepartments_returnsAll() {
        Department d = new Department();
        d.setId(1L);
        d.setName("内科");
        when(departmentMapper.findAll()).thenReturn(List.of(d));

        List<Department> result = adminService.listDepartments();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("内科");
    }

    @Test
    void addDepartment_delegatesToMapper() {
        Department d = new Department();
        d.setName("新科室");
        when(departmentMapper.insert(d)).thenReturn(1);

        int result = adminService.addDepartment(d);
        assertThat(result).isEqualTo(1);
    }
}
