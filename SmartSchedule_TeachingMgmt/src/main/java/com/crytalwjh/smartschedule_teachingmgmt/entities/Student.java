package com.crytalwjh.smartschedule_teachingmgmt.entities;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;

/**
 * 学生实体类
 * @author crytalwjh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("student")
public class Student {
    @TableId
    private Integer studentId; // 学生编号
    private String name; // 学生姓名
    private LocalDate birthday; // 学生出生日期
    private Integer facultyId; // 所属院系编号
    private Integer classId; // 所属班级编号
    private String address; // 学生地址
    private String phone; // 学生联系电话
    private String remark; // 备注信息
    private Integer sex; // 学生性别


    @TableField(exist = false)
    private Faculty faculty;
    @TableField(exist = false)
    private ClassAll classAll;
}