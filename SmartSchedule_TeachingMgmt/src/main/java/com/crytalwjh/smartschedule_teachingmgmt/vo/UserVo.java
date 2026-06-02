package com.crytalwjh.smartschedule_teachingmgmt.vo;

import com.crytalwjh.smartschedule_teachingmgmt.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * user表的拓展
 * @author xz526
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserVo extends User implements Serializable {

    private Integer teacherId; // 教师编号
    private String teacherName; // 教师姓名
    private String speciality; // 教师专业特长
    private String title; // 教师职称


    private Integer studentId; // 学生编号
    private String name; // 学生姓名
    private LocalDate birthday; // 学生出生日期
    private Integer facultyId; // 所属院系编号
    private String facultyName; // 所属院系编号

    private Integer classId; // 所属班级编号
    private String address; // 学生地址
    private String phone; // 学生联系电话
    private String remark; // 备注信息
    private Integer sex; // 学生性别



}
