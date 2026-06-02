package com.crytalwjh.smartschedule_teachingmgmt.entities;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.TableId;

@Data
public class Score {
    @TableId
    private Integer scoreId;
    private Integer studentId;
    private Integer courseId;
    private Integer teacherId;
    private Byte examType;
    private BigDecimal score;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate examDate;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
} 