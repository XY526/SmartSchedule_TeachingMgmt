package com.crytalwjh.smartschedule_teachingmgmt.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Student;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.entities.User;
import com.crytalwjh.smartschedule_teachingmgmt.vo.TeacherVo;
import com.crytalwjh.smartschedule_teachingmgmt.vo.UserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<User> {


    @Select("SELECT u.id, u.username, u.password, u.type, u.disabled, u.studentId, u.teacherId, u.avatarUrl, u.email, " +
            "s.studentId AS s_studentId, s.name, s.birthday, s.facultyId AS s_facultyId, s.classId, s.address, s.phone, s.remark, s.sex, " +
            "t.teacherId AS t_teacherId, t.teacherName, t.speciality, t.title, " +
            "f.facultyName \n" +
            "FROM user u\n" +
            "LEFT JOIN student s ON u.studentId = s.studentId\n" +
            "LEFT JOIN teacher t ON u.teacherId = t.teacherId\n" +
            "LEFT JOIN faculty f ON s.facultyId = f.facultyId\n" +
            "WHERE u.username = #{username}")
    User selectByUserName(String username);



    @Select("SELECT u.id, u.username, u.password, u.type, u.disabled, u.studentId, u.teacherId, u.avatarUrl, u.email, " +
            "s.name AS studentName, s.birthday, s.facultyId, s.classId, s.address, s.phone, s.remark, s.sex, " +
            "t.teacherName, t.speciality, t.title, " +
            "f.facultyName \n" +
            "FROM user u\n" +
            "LEFT JOIN student s ON u.studentId = s.studentId\n" +
            "LEFT JOIN teacher t ON u.teacherId = t.teacherId\n" +
            "LEFT JOIN faculty f ON s.facultyId = f.facultyId\n" +
            "${ew.customSqlSegment}\n" +
            "ORDER BY u.id ASC")
    List<UserVo> selectAllAndTeacherAndStudent(Page<UserVo> page,@Param(Constants.WRAPPER) QueryWrapper<UserVo> wrapper);

    @Select("SELECT COUNT(*)\n" +
            "FROM user u \n" +
            "LEFT JOIN student s ON u.studentId = s.studentId\n" +
            "LEFT JOIN teacher t ON u.teacherId = t.teacherId\n" +
            "LEFT JOIN faculty f ON s.facultyId = f.facultyId\n" +
            "${ew.customSqlSegment}")
    Integer getCount(@Param(Constants.WRAPPER) QueryWrapper<UserVo> wrapper);


    @Select("SELECT u.id, u.username, u.password, u.type, u.disabled, u.studentId, u.teacherId, u.avatarUrl, u.email, " +
            "s.name, t.teacherName \n" +
            "FROM user  u\n" +
            "LEFT JOIN student s ON u.studentId = s.studentId\n" +
            "LEFT JOIN teacher t ON u.teacherId = t.teacherId" )
    List<UserVo> selectAllAndTeacherAndStudent();

    @Update("update user set avatarUrl=#{avatarUrl} where id=#{id}")
    void updateAvatar(String avatarUrl,Integer id);

    @Update("UPDATE user SET username=#{username}, password=#{password}, type=#{type}, disabled=#{disabled}, " +
            "studentId=#{studentId}, teacherId=#{teacherId}, avatarUrl=#{avatar}, email=#{email} WHERE id=#{id}")
    int updateUserById(User user);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User selectByEmail(String email);

}