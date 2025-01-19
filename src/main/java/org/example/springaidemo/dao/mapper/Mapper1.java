package org.example.springaidemo.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.springaidemo.moudle.UserModel;
import org.example.springaidemo.moudle.UserRestModel;
import java.time.LocalDateTime;

@Mapper
public interface Mapper1 {
    /**
     * 添加新用户
     * @param userModel 用户信息
     */
    void addUser(UserModel userModel);

    /**
     * 根据用户ID获取用户信息
     * @param userid 用户ID
     * @return 用户信息
     */
    UserModel getUser(String userid);

    /**
     * 添加用户休息记录
     * @param restModel 休息记录信息
     */
    void addUserRest(UserRestModel restModel);

    /**
     * 查询最近休假申请
     */
    @Select("SELECT * FROM rest WHERE userid = #{userid} " +
            "AND start_rest_day = #{startTime} " +
            "AND end_rest_day = #{endTime} " +
            "AND status != 'REJECTED' " +  // 忽略已拒绝的申请
            "ORDER BY id DESC LIMIT 1")
    UserRestModel getLastRestRequest(@Param("userid") String userid, 
                                   @Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);
}
