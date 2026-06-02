package com.crytalwjh.smartschedule_teachingmgmt.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Student;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.vo.StudentVo;
import com.crytalwjh.smartschedule_teachingmgmt.vo.TeacherVo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
    @Select("select * from teacher ")
    @Results({
            @Result(id = true,column = "teacherId" ,property = "teacherId"),
            @Result(column = "facultyId",property = "faculty",
                    one = @One(select = "com.crytalwjh.smartschedule_teachingmgmt.mapper.FacultyMapper.selectById"))
    })
    List<Teacher> selectTeacherAndFacultyAll();

    @Select("select * from teacher WHERE teacherId =#{id}")
    @Results({
            @Result(id = true,column = "teacherId" ,property = "teacherId"),
            @Result(column = "facultyId",property = "faculty",
                    one = @One(select = "com.crytalwjh.smartschedule_teachingmgmt.mapper.FacultyMapper.selectById"))
    })
    Teacher selectTeacherAndFacultyById(int id);

//    @Select("")
//    @Results({
//            @Result(id = true,column = "teacherId" ,property = "teacherId"),
//            @Result(column = "facultyId",property = "faculty",
//                    one = @One(select = "com.crytalwjh.smartschedule_teachingmgmt.mapper.FacultyMapper.selectById"))
//    })
//    int insertTeacher(Teacher teacher);
//    @Select("")
//    @Results({
//            @Result(id = true,column = "teacherId" ,property = "teacherId"),
//            @Result(column = "facultyId",property = "faculty",
//                    one = @One(select = "com.crytalwjh.smartschedule_teachingmgmt.mapper.FacultyMapper.selectById"))
//    })
//    int deleteById(int id);
//    @Select("")
//    @Results({
//            @Result(id = true,column = "teacherId" ,property = "teacherId"),
//            @Result(column = "facultyId",property = "faculty",
//                    one = @One(select = "com.crytalwjh.smartschedule_teachingmgmt.mapper.FacultyMapper.selectById"))
//    })
//    int updateById(int  id);
    @Select("SELECT \n" +
            "    t.teacherId,\n" +
            "    t.teacherName,\n" +
            "    t.speciality,\n" +
            "    t.title,\n" +
            "    f.facultyName\n" +
            "FROM \n" +
            "    smartschedule_teachingmgmt.teacher t\n" +
            "JOIN \n" +
            "    smartschedule_teachingmgmt.faculty f ON t.facultyId = f.facultyId  ${ew.customSqlSegment} ORDER BY  teacherId ASC,  teacherId ASC")
    List<TeacherVo> selectAllClassRoom(Page<TeacherVo> page , @Param(Constants.WRAPPER) QueryWrapper<Teacher> wrapper);

}
