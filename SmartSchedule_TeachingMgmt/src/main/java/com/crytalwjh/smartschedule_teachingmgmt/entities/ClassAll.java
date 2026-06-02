package com.crytalwjh.smartschedule_teachingmgmt.entities;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

/**
 * 班级实体类
 * @author crytalwjh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("class")
public class ClassAll {
    @TableId
    private Integer classId; // 班级编号
    private Integer facultyId; // 所属院系编号
    private Integer classSize; // 班级人数
    private LocalDate year; // 班级成立年份
    private Integer teacherId; // 辅导员编号

    @TableField(exist = false)
    private Faculty faculty;
    @TableField(exist = false)
    private Teacher teacher;
}