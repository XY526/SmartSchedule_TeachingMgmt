package com.crytalwjh.smartschedule_teachingmgmt.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Faculty;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Location;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationMapper extends BaseMapper<Location> {




    @Select("select * from location where locationId=#{lId}")
    @Results({
            @Result(id = true,property = "locationId",column = "locationId"),
            @Result(property = "ClassRoom",column = "locationId",
                    many = @Many(select = "com.crytalwjh.smartschedule_teachingmgmt.mapper.ClassRoomMapper.selectClassRoomAndLocationById")
            )})
    Location selectLocationAndClassRoom(Integer id);

    @Select("SELECT * FROM location ${ew.customSqlSegment} ORDER BY locationId ASC")
    Page<Location> selectLocation(Page<Location> page, @Param(Constants.WRAPPER) QueryWrapper<Location> wrapper);
}
