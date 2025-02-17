package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid =#{id} ")
    User getUserById(String id);

    void insertUser(User user);

    @Select("select * from user where id=#{id}")
    User getById(Long userId);
}
