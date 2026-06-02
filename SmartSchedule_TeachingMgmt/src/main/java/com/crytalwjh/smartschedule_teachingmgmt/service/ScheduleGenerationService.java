package com.crytalwjh.smartschedule_teachingmgmt.service;

import com.crytalwjh.smartschedule_teachingmgmt.entities.Curriculum;
import java.util.List;

public interface ScheduleGenerationService {
    /**
     * 生成课程表
     * @param classId 班级ID
     * @param schoolYear 学年
     * @param term 学期
     * @return 生成的课程表列表
     */
    List<Curriculum> generateSchedule(Integer classId, Integer schoolYear, Integer term);
    
    /**
     * 检查时间冲突
     * @param curriculum 待检查的课程
     * @param existingSchedules 已存在的课程表
     * @return 是否存在冲突
     */
    boolean hasTimeConflict(Curriculum curriculum, List<Curriculum> existingSchedules);
    
    /**
     * 检查教室容量是否满足要求
     * @param curriculum 待检查的课程
     * @param classSize 班级人数
     * @return 是否满足要求
     */
    boolean isClassroomCapacitySufficient(Curriculum curriculum, Integer classSize);
} 