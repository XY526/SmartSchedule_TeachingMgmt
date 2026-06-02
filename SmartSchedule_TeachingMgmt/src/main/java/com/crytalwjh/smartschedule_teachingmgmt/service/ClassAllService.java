package com.crytalwjh.smartschedule_teachingmgmt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassAll;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.vo.ClassAllVo;

import java.util.List;

public interface ClassAllService {


    Page<ClassAllVo> selectTeacherAndFacultyAndFaculty(QueryParams queryParams) ;

    int insertClass(ClassAll classAll);


    int updateById(ClassAll classAll);

    int deleteById(int id);
}
