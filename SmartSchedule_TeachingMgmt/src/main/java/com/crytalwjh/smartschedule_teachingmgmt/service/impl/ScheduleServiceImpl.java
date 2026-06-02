package com.crytalwjh.smartschedule_teachingmgmt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Schedule;
import com.crytalwjh.smartschedule_teachingmgmt.mapper.ScheduleMapper;
import com.crytalwjh.smartschedule_teachingmgmt.service.ScheduleService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleMapper scheduleMapper;

    @Override
    public Page<Schedule> selectPage(QueryParams queryParams) {
        Integer pageNum = queryParams.getPageNum();
        Integer pageSize = queryParams.getPageSize();

        if (pageNum == null || pageNum == 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize == 0) {
            pageSize = 10; // Default page size
        }
        Page<Schedule> page = new Page<>(pageNum, pageSize);

        QueryWrapper<Schedule> wrapper = new QueryWrapper<>();
        // Add filtering based on queryParams if needed
        // Example: filter by title or description
        String title = (String) queryParams.getParams().get("title");
        wrapper.like(StrUtil.isNotBlank(title), "title", title);

        String description = (String) queryParams.getParams().get("description");
        wrapper.like(StrUtil.isNotBlank(description), "description", description);
        
        // You might also want to filter by userId based on the current logged-in user
        // Integer userId = (Integer) queryParams.getParams().get("userId");
        // wrapper.eq(userId != null, "userId", userId);

        return scheduleMapper.selectPage(page, wrapper);
    }

    @Override
    public int insert(Schedule schedule) {
        // Set createTime before inserting
        schedule.setCreateTime(new Date());
        return scheduleMapper.insert(schedule);
    }

    @Override
    public int deleteById(Integer id) {
        return scheduleMapper.deleteById(id);
    }

    @Override
    public int updateById(Schedule schedule) {
        return scheduleMapper.updateById(schedule);
    }

    @Override
    public List<Schedule> selectByUserId(Integer userId) {
        QueryWrapper<Schedule> wrapper = new QueryWrapper<>();
        wrapper.eq("userId", userId);
        return scheduleMapper.selectList(wrapper);
    }

    @Override
    public Schedule selectById(Integer id) {
        return scheduleMapper.selectById(id);
    }
} 