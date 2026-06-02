package com.crytalwjh.smartschedule_teachingmgmt.controller;

import com.crytalwjh.smartschedule_teachingmgmt.entities.Curriculum;
import com.crytalwjh.smartschedule_teachingmgmt.service.ScheduleGenerationService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule/generation")
public class ScheduleGenerationController {

    @Autowired
    private ScheduleGenerationService scheduleGenerationService;

    /**
     * 生成课程表
     * @param classId 班级ID
     * @param schoolYear 学年
     * @param term 学期
     * @return 生成的课程表
     */
    @PostMapping("/generate")
    public Result generateSchedule(
            @RequestParam Integer classId,
            @RequestParam Integer schoolYear,
            @RequestParam Integer term) {
        try {
            List<Curriculum> schedules = scheduleGenerationService.generateSchedule(classId, schoolYear, term);
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
} 