package com.crytalwjh.smartschedule_teachingmgmt.controller;

import com.crytalwjh.smartschedule_teachingmgmt.entities.Curriculum;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Faculty;
import com.crytalwjh.smartschedule_teachingmgmt.service.CurriculumService;
import com.crytalwjh.smartschedule_teachingmgmt.service.FacultyService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/curriculum")
public class CurriculumController {

    @Autowired
    private CurriculumService curriculumService;

    //查询所有院系
    @PostMapping("/list")
    Result selectAllCurriculum(@RequestBody QueryParams queryParams){
        return Result.success(curriculumService.selectAlCurriculum(queryParams));
    }
//    //根据ID查找院系
//    @GetMapping("/findById")
//    Faculty selectById(@RequestParam int id){
//        return curriculumService.s(id);
//    }

    @DeleteMapping("/deleteById")
    Result deleteById( @RequestParam int id){
        try{
            int rows = curriculumService.deleteById(id);
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

    /**
     * 保存课程表
     * @param curriculum 课程表信息
     * @return 保存结果
     */
    @PostMapping("/insertCurriculum")
    public Result insertCurriculum(@RequestBody Curriculum curriculum) {
        try {
            int result = curriculumService.insertCurriculum(curriculum);
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 批量保存课程表
     * @param curriculums 课程表列表
     * @return 保存结果
     */
    @PostMapping("/batchInsert")
    public Result batchInsertCurriculum(@RequestBody List<Curriculum> curriculums) {
        try {
            int successCount = 0;
            for (Curriculum curriculum : curriculums) {
                successCount += curriculumService.insertCurriculum(curriculum);
            }
            return Result.success(successCount);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/updateById")
    Result updateById(@RequestBody Curriculum curriculum){
        try{
            int rows = curriculumService.updateById(curriculum);
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
