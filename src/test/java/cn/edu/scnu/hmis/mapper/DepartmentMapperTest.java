package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Department;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DepartmentMapperTest {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Test
    void findAll_returnsAllDepartments() {
        List<Department> list = departmentMapper.findAll();
        assertThat(list).isNotEmpty();
    }

    @Test
    void insert_and_findById() {
        Department d = new Department();
        d.setName("测试科室");
        d.setLocation("测试位置");
        departmentMapper.insert(d);

        Department found = departmentMapper.findById(d.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("测试科室");
    }
}
