package com.crytalwjh.smartschedule_teachingmgmt.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Curriculum;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Faculty;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurriculumMapper extends BaseMapper<Curriculum> {
    @Select("SELECT c.*, co.courseName, t.teacherName, l.locationName, cr.capacity, cr.isMultimedia " +
            "FROM curriculum c " +
            "LEFT JOIN course co ON c.courseId = co.courseId " +
            "LEFT JOIN teacher t ON c.teacherId = t.teacherId " +
            "LEFT JOIN location l ON c.locationId = l.locationId " +
            "LEFT JOIN classroom cr ON c.classRoomId = cr.classRoomId " +
            "${ew.customSqlSegment} " +
            "ORDER BY c.Id ASC, " +
            "c.schoolYear DESC, " +
            "c.term ASC, " +
            "c.startWeek ASC, " +
            "c.weekday ASC, " +
            "c.section ASC")
    Page<Curriculum> selectCurriculumWithDetails(Page<Curriculum> page, @Param(Constants.WRAPPER) QueryWrapper<Curriculum> wrapper);

    @Select("SELECT COUNT(*) FROM curriculum c ${ew.customSqlSegment}")
    Integer getCount(@Param(Constants.WRAPPER) QueryWrapper<Curriculum> wrapper);
}
