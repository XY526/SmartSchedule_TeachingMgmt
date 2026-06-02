package com.crytalwjh.smartschedule_teachingmgmt.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ScoreVo {
    private Integer scoreId;
    private Integer studentId;
    private Integer courseId;
    private String courseName;
    private Integer teacherId;
    private String teacherName;
    private Integer examType;
    private Double score;
    private String examDate;
    private String remark;
    private LocalDateTime createTime;
} 