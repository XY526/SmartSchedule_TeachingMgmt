package com.crytalwjh.smartschedule_teachingmgmt.controller;


import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassAll;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import org.springframework.data.domain.Page;

import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassRoom;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Course;
import com.crytalwjh.smartschedule_teachingmgmt.service.ClassRoomService;
import com.crytalwjh.smartschedule_teachingmgmt.service.CourseService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/classRoom")
public class ClassRoomController {
    @Autowired
        private ClassRoomService classRoomService;

//        查询所有教室
        @PostMapping("/list")
        public Result selectAll(@RequestBody QueryParams queryParams) {
            // 创建 Pageable 对象，用于分页查询
            System.out.println("66666666666"+queryParams);
            return Result.success(classRoomService.getListByPage(queryParams));
        }
        @PostMapping("/insertClassRoom")
        Result insertClass(@RequestBody ClassRoom classRoom){
            return Result.success(classRoomService.insertClassRoom(classRoom));
        }


        @DeleteMapping("/deleteById")
        Result deleteById(@RequestParam int id){
            return Result.success(classRoomService.deleteById(id));
        }

        @PutMapping("/updateById")
        Result deleteById(@RequestBody ClassRoom classRoom){

            return Result.success(classRoomService.updateById(classRoom));
        }

}
