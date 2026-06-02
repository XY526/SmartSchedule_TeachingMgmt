package com.crytalwjh.smartschedule_teachingmgmt.entities;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教师实体类
 * @author crytalwjh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("teacher")
public class Teacher {
    @TableId
    private Long teacherId; // 教师编号
    private String teacherName; // 教师姓名
    private Long facultyId; // 所属院系编号
    private String speciality; // 教师专业特长
    private String title; // 教师职称
    @TableField(exist = false)
    private Faculty faculty;
}