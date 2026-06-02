package com.crytalwjh.smartschedule_teachingmgmt.controller;


import com.crytalwjh.smartschedule_teachingmgmt.entities.Student;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.mapper.TeacherMapper;
import com.crytalwjh.smartschedule_teachingmgmt.service.TeacherService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    @PostMapping("/list")
    public Result selectAll(@RequestBody QueryParams queryParams) {
        // 创建 Pageable 对象，用于分页查询
        System.out.println("66666666666"+queryParams);
        return Result.success(teacherService.selectAllTeacher(queryParams));
    }
    @PostMapping("/insertTeacher")
    Result insertClass(@RequestBody Teacher teacher){
        return Result.success(teacherService.insertTeacher(teacher));
    }


    @DeleteMapping("/deleteById")
    Result deleteById(@RequestParam int id){
        return Result.success(teacherService.deleteById(id));
    }

    @PutMapping("/updateById")
    Result deleteById(@RequestBody Teacher teacher){

        return Result.success(teacherService.updateById(teacher));
    }
//    @GetMapping("/selectById")
//    Teacher selectById(@RequestParam int id ){
//        return teacherService.selectTeacherAndFacultyById(id);
//    }
//    @GetMapping("/list")
//    Result selectAll(){
//        return Result.success(teacherService.selectTeacherAndFacultyAll());
//    }
//
//    @PostMapping("/insertTeacher")
//    Result insertTeacher(@RequestBody Teacher teacher){
//        try{
//            int rows = teacherService.insertTeacher(teacher);
//            if (rows>0){
//                return Result.success("插入成功");
//            } else{
//                return Result.fail("插入失败");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.fail("插入过程中出现异常"+e.getMessage());
//        }
//
//    }

}
