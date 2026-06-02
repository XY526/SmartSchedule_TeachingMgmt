package com.crytalwjh.smartschedule_teachingmgmt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Course;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.vo.CourseScheduleVo;

import java.util.List;

public interface CourseService {
//    Page<Course> getListByPage(QueryParams params) ;
    Page<Course> selectAllCourse(QueryParams params);
    Course selectById(int id);
//    int updateCourseIDInt(Course course);

    int updateById(Course course);
    int deleteById(int id);
    int insert(Course course);

    List<Course> getCoursesByClassId(Integer classId);

    List<CourseScheduleVo> getCompleteCourseSchedule(Integer classId);
}
