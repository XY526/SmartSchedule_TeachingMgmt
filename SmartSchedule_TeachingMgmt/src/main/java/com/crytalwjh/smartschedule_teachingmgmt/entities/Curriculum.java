package com.crytalwjh.smartschedule_teachingmgmt.entities;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课表实体类
 * @author crytalwjh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("curriculum")
public class Curriculum {
    @TableId
    private Integer Id; // 课表记录编号
    private Integer courseId; // 课程编号
    private Integer classId; // 班级编号
    private Integer teacherId; // 授课教师编号
    private Integer schoolYear; // 学年
    private Integer term; // 学期
    private Integer startWeek; // 开课起始周次
    private Integer endWeek; // 开课结束周次
    private Integer weekday; // 星期几
    private Integer section; // 节次
    private Integer locationId; // 上课地点编号
    private Integer classRoomId; // 教室编号

    @TableField(exist = false)
    private Course course;
    @TableField(exist = false)
    private ClassAll classAll;
    @TableField(exist = false)
    private Teacher teacher;
    @TableField(exist = false)
    private Location location;
    @TableField(exist = false)
    private ClassRoom classRoom;
}