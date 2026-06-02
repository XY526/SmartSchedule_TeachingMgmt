package com.crytalwjh.smartschedule_teachingmgmt.entities;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@TableName("schedule")
public class Schedule {
    @TableId
    private Integer scheduleId;
    private Integer userId;
    private String title;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    private Byte status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
} 