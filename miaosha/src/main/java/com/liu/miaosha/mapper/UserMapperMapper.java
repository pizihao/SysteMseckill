package com.liu.miaosha.mapper;

import com.liu.miaosha.pojo.UserMapper;
import com.liu.miaosha.pojo.UserMapperExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserMapperMapper {
    int countByExample(UserMapperExample example);

    int deleteByExample(UserMapperExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(UserMapper record);

    int insertSelective(UserMapper record);

    List<UserMapper> selectByExample(UserMapperExample example);

    UserMapper selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") UserMapper record, @Param("example") UserMapperExample example);

    int updateByExample(@Param("record") UserMapper record, @Param("example") UserMapperExample example);

    int updateByPrimaryKeySelective(UserMapper record);

    int updateByPrimaryKey(UserMapper record);
}