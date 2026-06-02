package com.crytalwjh.smartschedule_teachingmgmt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.vo.StudentVo;
import com.crytalwjh.smartschedule_teachingmgmt.vo.TeacherVo;

import java.util.List;

public interface TeacherService {
    Page<TeacherVo> selectAllTeacher(QueryParams queryParams);

    Teacher selectTeacherAndFacultyById(int id);
    List<Teacher> selectTeacherAndFacultyAll();

    int  insertTeacher(Teacher teacher);
    int deleteById(int id);
    int updateById(Teacher teacher);

}
