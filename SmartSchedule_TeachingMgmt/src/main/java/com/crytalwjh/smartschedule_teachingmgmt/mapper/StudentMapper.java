package com.crytalwjh.smartschedule_teachingmgmt.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassRoom;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Student;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.service.StudentService;
import com.crytalwjh.smartschedule_teachingmgmt.vo.ClassRoomVo;
import com.crytalwjh.smartschedule_teachingmgmt.vo.StudentVo;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentMapper extends BaseMapper<Student> {

    @Select("SELECT s.studentId , s.name,s.birthday,f.facultyName,s.classId,s.address,s.phone,s.remark,s.sex\n" +
            "FROM smartschedule_teachingmgmt.student s\n" +
            "JOIN smartschedule_teachingmgmt.faculty f ON s.facultyId = f.facultyId ${ew.customSqlSegment} ORDER BY s.studentId ASC, s.studentId ASC ")
    List<StudentVo> selectAllStudent(Page<StudentVo> page , @Param(Constants.WRAPPER) QueryWrapper<Student> wrapper);
}
