package com.crytalwjh.smartschedule_teachingmgmt.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassAll;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Faculty;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.vo.ClassAllVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassAllMapper extends BaseMapper<ClassAll> {


    @Select("SELECT c.classId, f.facultyName,t.teacherName AS teacherName , c.classSize,c.year\n" +
            "FROM class c\n" +
            "LEFT JOIN faculty f ON c.facultyId = f.facultyId\n" +
            "LEFT JOIN  teacher t ON c.teacherId = t.teacherId  ${ew.customSqlSegment} ORDER BY  c.classId ASC,  c.classId ASC limit #{startIndex},#{size} ")
    List<ClassAllVo> selectTeacherAndFacultyAndFaculty(@Param(Constants.WRAPPER) QueryWrapper<Teacher> wrapper,
                                                       @Param("startIndex") Integer startIndex,@Param("size") Integer size);

    @Select("SELECT COUNT(*)\n" +
            "FROM class c\n" +
            "LEFT JOIN faculty f ON c.facultyId = f.facultyId\n" +
            "LEFT JOIN  teacher t ON c.teacherId = t.teacherId ${ew.customSqlSegment} ")
    Integer getCount(@Param(Constants.WRAPPER) QueryWrapper<Teacher> wrapper);

    int update(ClassAll classAll);
}

