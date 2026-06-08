package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Ward;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WardMapper {

    List<Ward> findAll();

    Ward findById(@Param("id") Long id);

    int insert(Ward ward);

    int update(Ward ward);

    int deleteById(@Param("id") Long id);
}
