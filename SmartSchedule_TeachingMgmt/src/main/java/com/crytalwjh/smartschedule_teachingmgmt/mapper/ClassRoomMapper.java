package com.crytalwjh.smartschedule_teachingmgmt.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassRoom;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Location;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.vo.ClassRoomVo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRoomMapper extends BaseMapper<ClassRoom> {



//    @Select("select * from classroom where classRoomId=#{cId}")
//    @Results({
//            @Result(id = true,property = "classRoomId",column = "classRoomId"),
//            @Result(property = "locationList",column = "locationId",
//                    many = @Many(select = "com.crytalwjh.smartschedule_teachingmgmt.mapper.ClassRoomMapper.selectClassRoomAndLocationById")
//            )})
    @Select("select * from ClassRoom where locationId=#{cId}")
    ClassRoom selectClassRoomAndLocationById(Integer cId);

    @Select("SELECT c.classRoomId, c.capacity, c.isMultimedia, c.state, l.locationName " +
            "FROM classroom c " +
            "LEFT JOIN location l ON c.locationId = l.locationId " +
            "${ew.customSqlSegment} " +
            "ORDER BY c.classRoomId ASC")
    List<ClassRoomVo> selectAllClassRoom(Page<ClassRoomVo> page, @Param(Constants.WRAPPER) QueryWrapper<ClassRoom> wrapper);
//    @Select("SELECT count(*)\n" +
//            "FROM classroom c\n" +
//            "LEFT JOIN location l ON c.locationId = l.locationId  ${ew.customSqlSegment}")
//    Integer getCount(@Param(Constants.WRAPPER) QueryWrapper<Location> wrapper);
}
