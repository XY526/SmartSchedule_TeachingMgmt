package com.crytalwjh.smartschedule_teachingmgmt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Score;
import com.crytalwjh.smartschedule_teachingmgmt.vo.ScoreVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreMapper extends BaseMapper<Score> {
    @Select("SELECT " +
            "s.scoreId, s.studentId, " +
            "s.courseId, c.courseName, " +
            "s.teacherId, t.teacherName, " +
            "s.examType, s.score, " +
            "s.examDate, s.remark, s.createTime " +
            "FROM score s " +
            "LEFT JOIN course c ON s.courseId = c.courseId " +
            "LEFT JOIN teacher t ON s.teacherId = t.teacherId " +
            "WHERE s.studentId = #{studentId}")
    List<ScoreVo> getScoresByStudentId(@Param("studentId") Integer studentId);
} 