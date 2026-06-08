package cn.edu.scnu.hmis.mapper;

import cn.edu.scnu.hmis.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> findAll();

    User findById(@Param("id") Long id);

    int insert(User user);

    int update(User user);

    int deleteById(@Param("id") Long id);

    User findByUsername(@Param("username") String username);
}
