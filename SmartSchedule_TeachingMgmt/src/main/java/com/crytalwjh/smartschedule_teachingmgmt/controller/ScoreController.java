package com.crytalwjh.smartschedule_teachingmgmt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Score;
import com.crytalwjh.smartschedule_teachingmgmt.service.ScoreService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/score")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    /**
     * 分页查询成绩
     * @param queryParams 查询参数 (包含pageNum, pageSize, studentId, courseId, examType, remark等)
     * @return 分页结果
     */
    @PostMapping("/list")
    public Result selectPage(@RequestBody QueryParams queryParams) {
        Page<Score> page = scoreService.selectPage(queryParams);
        return Result.success(page);
    }

    /**
     * 添加成绩
     * @param score 成绩信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result addScore(@RequestBody Score score) {
        int result = scoreService.insert(score);
        return Result.success(result);
    }

    /**
     * 根据ID删除成绩
     * @param id 成绩ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteScoreById(@PathVariable Integer id) {
        int result = scoreService.deleteById(id);
        return Result.success(result);
    }

    /**
     * 根据ID更新成绩
     * @param score 成绩信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public Result updateScoreById(@RequestBody Score score) {
        int result = scoreService.updateById(score);
        return Result.success(result);
    }

    /**
     * 根据学生ID查询成绩
     * @param studentId 学生ID
     * @return 成绩列表
     */
    @GetMapping("/student/{studentId}")
    public Result getScoresByStudentId(@PathVariable Integer studentId) {
        List<Score> scores = scoreService.selectByStudentId(studentId);
        return Result.success(scores);
    }

    /**
     * 根据课程ID查询成绩
     * @param courseId 课程ID
     * @return 成绩列表
     */
    @GetMapping("/course/{courseId}")
    public Result getScoresByCourseId(@PathVariable Integer courseId) {
        List<Score> scores = scoreService.selectByCourseId(courseId);
        return Result.success(scores);
    }

    /**
     * 根据ID查询成绩
     * @param id 成绩ID
     * @return 成绩信息
     */
    @GetMapping("/{id}")
    public Result getScoreById(@PathVariable Integer id) {
        Score score = scoreService.selectById(id);
        return Result.success(score);
    }
}