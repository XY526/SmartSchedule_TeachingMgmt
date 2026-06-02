package com.crytalwjh.smartschedule_teachingmgmt.controller;


import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassAll;
import com.crytalwjh.smartschedule_teachingmgmt.service.ClassAllService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("classAll")


public class ClassAllController {
    @Autowired
    private ClassAllService classAllService;

    @PostMapping("/list")
    Result selectAll(@RequestBody QueryParams queryParams){
        System.out.println("66666666666"+queryParams);
        return Result.success(classAllService.selectTeacherAndFacultyAndFaculty(queryParams));
    }
    @PostMapping("/insertClass")
    Result insertClass(@RequestBody ClassAll classAll){
        return Result.success(classAllService.insertClass(classAll));
    }


    @DeleteMapping("/deleteById")
    Result deleteById(@RequestParam int id){
        return Result.success(classAllService.deleteById(id));
    }

    @PutMapping("/updateById")
    Result deleteById(@RequestBody ClassAll classAll){

        return Result.success(classAllService.updateById(classAll));
    }
}
