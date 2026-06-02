package com.crytalwjh.smartschedule_teachingmgmt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Course;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Curriculum;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;

public interface CurriculumService {

    Page<Curriculum> selectAlCurriculum(QueryParams params);
//    Course selectById(int id);


    int updateById( Curriculum curriculum);
    int deleteById(int id);
    int insertCurriculum(Curriculum curriculum);

}


