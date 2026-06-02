package com.crytalwjh.smartschedule_teachingmgmt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Student;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.vo.StudentVo;

public interface StudentService {

    Page<StudentVo> selectAllStudent(QueryParams queryParams);
    int  insertStudent(Student student);
    int deleteById(int id);
    int updateById(Student student);
}
