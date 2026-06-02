package com.crytalwjh.smartschedule_teachingmgmt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

    // 可以根据需要添加自定义查询方法
    @Select("SELECT * FROM schedule WHERE userId = #{userId}")
    List<Schedule> selectByUserId(Integer userId);
} 