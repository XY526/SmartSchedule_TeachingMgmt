package com.crytalwjh.smartschedule_teachingmgmt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassAll;
import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassRoom;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Course;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Location;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.vo.ClassRoomVo;

import java.util.List;

public interface ClassRoomService {
    Page<ClassRoomVo> getListByPage(QueryParams params) ;
//    ClassRoom selectLocationAndClassRoomById(int id);

    int insertClassRoom(ClassRoom classRoom);


    int updateById(ClassRoom classRoom);

    int deleteById(int id);

}
