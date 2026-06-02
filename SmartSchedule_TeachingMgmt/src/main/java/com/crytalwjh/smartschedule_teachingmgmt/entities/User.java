package com.crytalwjh.smartschedule_teachingmgmt.entities;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 用户实体类
 * @author crytalwjh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {
    @TableId
    private Integer id; // 用户编号
    private String username; // 用户名
    private String password; // 用户密码
    private Integer type; // 用户类型
    private Integer disabled; // 用户是否禁用，0 表示启用，1 表示禁用
    private Integer StudentId; // 关联的学生编号
    private Integer TeacherId; // 关联的教师编号
    private String avatar;
    private String email;
    @TableField(exist = false)
    private Student student;
    @TableField(exist = false)
    private Teacher teacher;

    @TableField(exist = false)
    private String teacherName; // 教师姓名
    @TableField(exist = false)
    private String name; // 学生姓名
    @TableField(exist = false)
    private LocalDate birthday; // 学生出生日期
    @TableField(exist = false)
    private Integer facultyId; // 所属院系编号
    @TableField(exist = false)
    private String facultyName; // 所属院系编号
    @TableField(exist = false)
    private Integer classId; // 所属班级编号
    @TableField(exist = false)
    private String address; // 学生地址
    @TableField(exist = false)
    private String phone; // 学生联系电话
    @TableField(exist = false)
    private String remark; // 备注信息
    @TableField(exist = false)
    private Integer sex; // 学生性别
}