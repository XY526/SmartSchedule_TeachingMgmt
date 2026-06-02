package com.crytalwjh.smartschedule_teachingmgmt.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Course;
import com.crytalwjh.smartschedule_teachingmgmt.vo.CourseScheduleVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseMapper extends BaseMapper<Course> {
//    //查找所有课程数据
//    default List<Course> selectAllCourse() {
//        return this.selectList(null);
//    }
//
//
//    //根据ID查找课程数据
//    default Course selectById(){
//        return  this.selectById();
//    }
    @Select("select * from  course  ${ew.customSqlSegment} ORDER BY  courseId ASC ")
    Page<Course> selectAllCourse1(Page<Course> page , @Param(Constants.WRAPPER) QueryWrapper<Course> wrapper);

    @Select("SELECT DISTINCT c.courseId, c.courseName, c.hour, c.credit " +
            "FROM course c " +
            "INNER JOIN curriculum cu ON c.courseId = cu.courseId " +
            "INNER JOIN student s ON cu.classId = s.classId " +
            "WHERE s.classId = #{classId}")
    List<Course> getCoursesByStudentClassId(@Param("classId") Integer classId);
//    //根据ID更改课程数据
//    @Update("UPDATE course " +
//            "SET courseName = #{courseName}, " +
//            "    hour = #{hour}, " +
//            "    credit = #{credit} " +
//            "WHERE courseID = #{courseID}")
//    int updateById(Course course);
//    default int updateCourseIDInt(Course course){
//        return this.updateById(new Course());
//    }
//    @Delete("DELETE FROM course WHERE courseID = #{id}")

    //根据ID删除课程数据
//    int deleteById(int id);

    //插入课程数据
//    @Insert("INSERT INTO course (courseName, hour, credit) VALUES (#{courseName}, #{hour}, #{credit})")
//    int insert(Course course);

    @Select("SELECT " +
            "cu.courseId, " +
            "COALESCE(co.courseName, '未知课程') as courseName, " +
            "cu.teacherId, " +
            "COALESCE(t.teacherName, '未知教师') as teacherName, " +
            "cu.locationId, " +
            "COALESCE(l.locationName, '未知地点') as locationName, " +
            "cu.classRoomId, " +
            "cu.weekday, " +
            "cu.section, " +
            "cu.startWeek, " +
            "cu.endWeek, " +
            "cu.schoolYear, " +
            "cu.term " +
            "FROM curriculum cu " +
            "LEFT JOIN course co ON cu.courseId = co.courseId " +
            "LEFT JOIN teacher t ON cu.teacherId = t.teacherId " +
            "LEFT JOIN location l ON cu.locationId = l.locationId " +
            "LEFT JOIN classroom cr ON cu.classRoomId = cr.classRoomId " +
            "WHERE cu.classId = #{classId}")
    List<CourseScheduleVo> getCompleteCourseSchedule(@Param("classId") Integer classId);
}
