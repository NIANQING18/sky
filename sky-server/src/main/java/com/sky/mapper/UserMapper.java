package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 用户登录
     * @param user
     * @return
     */
    void insert(User user);

    /**
     * 通过openid查找用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 通过id查找用户
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{userId}")
    User getByUserId(Long userId);
}
