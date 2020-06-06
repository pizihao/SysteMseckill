package com.liu.miaosha.mapper;

import com.liu.miaosha.pojo.UserPasswordMapper;
import com.liu.miaosha.pojo.UserPasswordMapperExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserPasswordMapperMapper {
    int countByExample(UserPasswordMapperExample example);

    int deleteByExample(UserPasswordMapperExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(UserPasswordMapper record);

    int insertSelective(UserPasswordMapper record);

    List<UserPasswordMapper> selectByExample(UserPasswordMapperExample example);

    UserPasswordMapper selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") UserPasswordMapper record, @Param("example") UserPasswordMapperExample example);

    int updateByExample(@Param("record") UserPasswordMapper record, @Param("example") UserPasswordMapperExample example);

    int updateByPrimaryKeySelective(UserPasswordMapper record);

    int updateByPrimaryKey(UserPasswordMapper record);
}