package com.crytalwjh.smartschedule_teachingmgmt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Schedule;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;

import java.util.List;

public interface ScheduleService {

    /**
     * 分页查询日程
     * @param queryParams 查询参数
     * @return 分页结果
     */
    Page<Schedule> selectPage(QueryParams queryParams);

    /**
     * 添加日程
     * @param schedule 日程信息
     * @return 影响行数
     */
    int insert(Schedule schedule);

    /**
     * 根据ID删除日程
     * @param id 日程ID
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 根据ID更新日程
     * @param schedule 日程信息
     * @return 影响行数
     */
    int updateById(Schedule schedule);

    /**
     * 根据用户ID查询日程
     * @param userId 用户ID
     * @return 日程列表
     */
    List<Schedule> selectByUserId(Integer userId);

    /**
     * 根据ID查询日程
     * @param id 日程ID
     * @return 日程信息
     */
    Schedule selectById(Integer id);
} 