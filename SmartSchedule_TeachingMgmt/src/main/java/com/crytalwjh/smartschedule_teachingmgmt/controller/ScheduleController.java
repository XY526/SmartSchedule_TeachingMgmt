package com.crytalwjh.smartschedule_teachingmgmt.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Schedule;
import com.crytalwjh.smartschedule_teachingmgmt.service.ScheduleService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 分页查询日程
     * @param queryParams 查询参数 (包含pageNum, pageSize, title, description, userId等)
     * @return 分页结果
     */
    @PostMapping("/list")
    public Result selectPage(@RequestBody QueryParams queryParams) {
        Page<Schedule> page = scheduleService.selectPage(queryParams);
        return Result.success(page);
    }

    /**
     * 添加日程
     * @param schedule 日程信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result addSchedule(@RequestBody Schedule schedule) {
        int result = scheduleService.insert(schedule);
        return Result.success(result);
    }

    /**
     * 根据ID删除日程
     * @param id 日程ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteScheduleById(@PathVariable Integer id) {
        int result = scheduleService.deleteById(id);
        return Result.success(result);
    }

    /**
     * 根据ID更新日程
     * @param schedule 日程信息
     * @return 更新结果
     */
    @PutMapping("/updateById")
    public Result updateScheduleById(@RequestBody Schedule schedule) {
        int result = scheduleService.updateById(schedule);
        return Result.success(result);
    }

    /**
     * 根据用户ID查询日程
     * @param userId 用户ID
     * @return 日程列表
     */
    @GetMapping("/user/{userId}")
    public Result getSchedulesByUserId(@PathVariable Integer userId) {
        List<Schedule> schedules = scheduleService.selectByUserId(userId);
        return Result.success(schedules);
    }

    /**
     * 根据ID查询日程
     * @param id 日程ID
     * @return 日程信息
     */
    @GetMapping("/{id}")
    public Result getScheduleById(@PathVariable Integer id) {
        Schedule schedule = scheduleService.selectById(id);
        return Result.success(schedule);
    }
} 