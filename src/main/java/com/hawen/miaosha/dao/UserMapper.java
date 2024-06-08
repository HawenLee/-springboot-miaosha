package com.hawen.miaosha.dao;

import com.hawen.miaosha.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * <p></p>
 *
 * @author lijiale
 * @date 2024/6/8
 */
@Mapper
public interface UserMapper {
    @Select("select user_id as id, nickname, password, salt, head, register_date as registerDate," +
            "last_login_date as lastLoginDate, login_count as loginCount from user_inf where user_id = #{id}")
    User findById(long id);

    @Update("update user_inf set last_login_date = #{lastLoginDate}, login_count = #{loginCount} where user_id = #{id}")
    void update(User user);

}
