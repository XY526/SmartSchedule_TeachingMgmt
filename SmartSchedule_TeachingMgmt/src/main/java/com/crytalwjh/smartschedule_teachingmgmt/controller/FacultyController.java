package com.crytalwjh.smartschedule_teachingmgmt.controller;

import com.crytalwjh.smartschedule_teachingmgmt.entities.Faculty;
import com.crytalwjh.smartschedule_teachingmgmt.service.FacultyService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    //查询所有院系
    @PostMapping("/list")
    Result selectAllFaculty(@RequestBody QueryParams queryParams){
        return Result.success(facultyService.selectAllFaculty(queryParams));
    }
    //根据ID查找院系
    @GetMapping("/findById")
    Faculty selectById(@RequestParam int id){
        return facultyService.selectById(id);
    }

    @DeleteMapping("/deleteById")
    Result deleteById( @RequestParam int id){
        try{
            int rows = facultyService.deleteById(id);
            if(rows>0){
                return Result.success("删除成功");
            }else{
                return Result.fail("删除失败");
            }
        }catch (Exception e) {
            e.printStackTrace();
            return Result.fail("插入过程中出现异常：" + e.getMessage());
        }
    }


    @PostMapping("/insertFaculty")
    Result insertFaculty(@RequestBody Faculty faculty){
        try{
            int rows = facultyService.insertFaculty(faculty);
            if (rows >0){
                return Result.success("更新成功");
            }else{
                return Result.fail("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("更新过程中出现异常：" + e.getMessage());
        }

    }


    @PutMapping("/updateById")
    Result updateById(@RequestBody Faculty faculty){
        try{
            int rows = facultyService.updateById(faculty);
            if (rows > 0) {
                return Result.success("插入成功");
            }else {
                return Result.fail("插入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("插入过程中出现异常"+e.getMessage());
        }
    }
}
