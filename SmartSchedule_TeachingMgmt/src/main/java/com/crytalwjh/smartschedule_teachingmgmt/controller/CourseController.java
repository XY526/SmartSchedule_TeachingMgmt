package com.crytalwjh.smartschedule_teachingmgmt.controller;


import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassAll;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Course;
import com.crytalwjh.smartschedule_teachingmgmt.service.CourseService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;


    //查询所有课程表
    @PostMapping("/list")
    Result selectAll(@RequestBody QueryParams queryParams){


        System.out.println("66666666666"+queryParams);
        return Result.success(courseService.selectAllCourse(queryParams));
    }
    @PostMapping("/insertCourse")
    Result insertClass(@RequestBody Course course){
        return Result.success(courseService.insert(course));
    }


    @DeleteMapping("/deleteById")
    Result deleteById(@RequestParam int id){
        return Result.success(courseService.deleteById(id));
    }

    @PutMapping("/updateById")
    Result deleteById(@RequestBody Course course){
        return Result.success(courseService.updateById(course));
    }

    @GetMapping("/getByClassId")
    Result getCoursesByClassId(@RequestParam Integer classId) {
        return Result.success(courseService.getCoursesByClassId(classId));
    }

    @GetMapping("/getCompleteSchedule")
    Result getCompleteSchedule(@RequestParam Integer classId) {
        return Result.success(courseService.getCompleteCourseSchedule(classId));
    }


//    //根据ID查课程表，使用http://127.0.0.1:8093/course/findById/id发起请求
//    @GetMapping("/findById/{id}")
//    @ResponseBody
//    public Course findById1( @PathVariable int id) {
//        return courseService.selectById(id);
//    }
//
//    //根据ID查课程表，使用http://127.0.0.1:8093/course/findById/？id=3发起请求
//    @GetMapping("/findById")
//    @ResponseBody
//    public Course findById( @RequestParam int id) {
//        return courseService.selectById(id);
//    }

//    @PutMapping("/updateById1")
//    @ResponseBody
//    public Result updateById1(@RequestBody Course course) {
//        try {
//            int rows = courseService.updateById(course);
//            if (rows > 0) {
//                return Result.success("修改成功");
//            } else {
//                return Result.fail("修改失败");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.fail("修改过程中出现异常：" + e.getMessage());
//        }
//    }

//    //插入课程---JOSN插入
//    @PostMapping("/insertCourse")
//    @ResponseBody
//    public Result insertCourse(@RequestBody Course course){
//        try{
//            int rows = courseService.insert(course);
//            if (rows>0){
//                return Result.success("插入成功");
//            }else {
//                return Result.fail("插入失败");
//            }
//        } catch (Exception e) {
//        e.printStackTrace();
//        return Result.fail("插入过程中出现异常：" + e.getMessage());
//    }
//    }

//    //根据ID修改课程
//    @PutMapping("/updateById")
//    @ResponseBody
//    @CrossOrigin
//    public Result updateById(@RequestBody Course course) {
//        try {
//            int rows = courseService.updateById(course);
//            if (rows > 0) {
//                return Result.success("修改成功");
//            } else {
//                return Result.fail("修改失败");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.fail("修改过程中出现异常：" + e.getMessage());
//        }
//    }
//根据ID修改课程
//    @PutMapping("/updateById")
//    @ResponseBody
//    @CrossOrigin
//    public Result updateById(@RequestBody Course course) {
//        try {
//            int rows = courseService.updateById(course);
//            if (rows > 0) {
//                return Result.success("修改成功");
//            } else {
//                return Result.fail("修改失败");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.fail("修改过程中出现异常：" + e.getMessage());
//        }
//    }
    //根据ID删课程
//    @DeleteMapping("/deleteById")
//    @ResponseBody
//    public Result deleteById(@RequestParam int id) {
//        try {
//            int rows = courseService.deleteById(id);
//            if (rows > 0) {
//                return Result.success("删除成功");
//            } else {
//                return Result.fail("删除失败");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Result.fail("删除过程中出现异常：" + e.getMessage());
//        }
//    }


}
