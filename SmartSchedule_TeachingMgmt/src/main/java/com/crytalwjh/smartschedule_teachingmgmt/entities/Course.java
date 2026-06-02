package com.crytalwjh.smartschedule_teachingmgmt.entities;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程列表
 * @author crytalwjh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("course")
public class Course {
    @TableId
    private Integer courseId;//课程id
    private String courseName;//课程名字
    private Integer hour;//课程学时
    private Integer credit;//课程学分
}
