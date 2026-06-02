package com.crytalwjh.smartschedule_teachingmgmt.controller;

import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassRoom;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Student;
import com.crytalwjh.smartschedule_teachingmgmt.mapper.StudentMapper;
import com.crytalwjh.smartschedule_teachingmgmt.service.StudentService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;
    //        查询所有教室
    @PostMapping("/list")
    public Result selectAll(@RequestBody QueryParams queryParams) {
        // 创建 Pageable 对象，用于分页查询
        System.out.println("66666666666"+queryParams);
        return Result.success(studentService.selectAllStudent(queryParams));
    }
    @PostMapping("/insertStudent")
    Result insertClass(@RequestBody Student student){
        return Result.success(studentService.insertStudent(student));
    }


    @DeleteMapping("/deleteById")
    Result deleteById(@RequestParam int id){
        return Result.success(studentService.deleteById(id));
    }

    @PutMapping("/updateById")
    Result deleteById(@RequestBody Student student){

        return Result.success(studentService.updateById(student));
    }

}
