package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Drug;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DrugMapper {

    List<Drug> findAll();

    Drug findById(@Param("id") Long id);

    int insert(Drug drug);

    int update(Drug drug);

    int deleteById(@Param("id") Long id);
}
