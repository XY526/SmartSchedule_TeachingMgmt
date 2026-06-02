package com.crytalwjh.smartschedule_teachingmgmt.vo;

import lombok.Data;

@Data
public class CourseScheduleVo {
    private Integer courseId;
    private String courseName;
    private Integer teacherId;
    private String teacherName;
    private Integer locationId;
    private String locationName;
    private Integer classRoomId;
    private Integer weekday;
    private Integer section;
    private Integer startWeek;
    private Integer endWeek;
    private Integer schoolYear;
    private Integer term;
} 