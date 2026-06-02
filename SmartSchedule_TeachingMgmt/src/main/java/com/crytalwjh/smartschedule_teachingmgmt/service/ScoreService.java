package com.crytalwjh.smartschedule_teachingmgmt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Score;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.vo.ScoreVo;

import java.util.List;

public interface ScoreService {

    /**
     * 分页查询成绩
     * @param queryParams 查询参数
     * @return 分页结果
     */
    Page<Score> selectPage(QueryParams queryParams);

    /**
     * 添加成绩
     * @param score 成绩信息
     * @return 影响行数
     */
    int insert(Score score);

    /**
     * 根据ID删除成绩
     * @param id 成绩ID
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 根据ID更新成绩
     * @param score 成绩信息
     * @return 影响行数
     */
    int updateById(Score score);

    /**
     * 根据学生ID查询成绩
     * @param studentId 学生ID
     * @return 成绩列表
     */
    List<Score> selectByStudentId(Integer studentId);

    /**
     * 根据课程ID查询成绩
     * @param courseId 课程ID
     * @return 成绩列表
     */
    List<Score> selectByCourseId(Integer courseId);

    /**
     * 根据ID查询成绩
     * @param id 成绩ID
     * @return 成绩信息
     */
    Score selectById(Integer id);

    List<ScoreVo> getScoresByStudentId(Integer studentId);
} 