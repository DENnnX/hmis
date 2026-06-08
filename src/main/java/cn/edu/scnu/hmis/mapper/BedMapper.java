package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.Bed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BedMapper {

    List<Bed> findAll();

    Bed findById(@Param("id") Long id);

    int insert(Bed bed);

    int update(Bed bed);

    int deleteById(@Param("id") Long id);

    List<Bed> findAvailableByWardId(@Param("wardId") Long wardId);
}
