package com.crytalwjh.smartschedule_teachingmgmt.service.impl;

import com.crytalwjh.smartschedule_teachingmgmt.entities.*;
import com.crytalwjh.smartschedule_teachingmgmt.mapper.*;
import com.crytalwjh.smartschedule_teachingmgmt.service.ScheduleGenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScheduleGenerationServiceImpl implements ScheduleGenerationService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ClassAllMapper classAllMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private ClassRoomMapper classRoomMapper;

    @Autowired
    private CurriculumMapper curriculumMapper;

    @Override
    public List<Curriculum> generateSchedule(Integer classId, Integer schoolYear, Integer term) {
        // 1. 获取班级信息
        ClassAll classInfo = classAllMapper.selectById(classId);
        if (classInfo == null) {
            throw new RuntimeException("班级不存在");
        }

        // 2. 获取该班级的所有课程
        List<Course> courses = courseMapper.getCoursesByStudentClassId(classId);
        if (courses.isEmpty()) {
            throw new RuntimeException("该班级没有课程需要安排");
        }

        // 3. 获取可用的教师
        List<Teacher> availableTeachers = teacherMapper.selectList(null);

        // 4. 获取可用的教室
        List<ClassRoom> availableClassrooms = classRoomMapper.selectList(null);

        // 5. 获取已存在的课程表
        List<Curriculum> existingSchedules = curriculumMapper.selectList(null);

        // 6. 生成新的课程表
        List<Curriculum> newSchedules = new ArrayList<>();
        Random random = new Random();

        for (Course course : courses) {
            // 为每个课程分配教师
            Teacher selectedTeacher = selectTeacher(course, availableTeachers);
            if (selectedTeacher == null) {
                throw new RuntimeException("没有合适的教师可以教授课程: " + course.getCourseName());
            }

            // 为每个课程分配教室
            ClassRoom selectedClassroom = selectClassroom(classInfo.getClassSize(), availableClassrooms);
            if (selectedClassroom == null) {
                throw new RuntimeException("没有合适的教室可以容纳班级: " + classInfo.getClassId());
            }

            // 生成课程时间
            Curriculum curriculum = generateCurriculumTime(course, selectedTeacher, selectedClassroom, 
                classId, schoolYear, term, existingSchedules);
            
            if (curriculum != null) {
                // 检查是否与已生成的课程表冲突
                if (hasConflictWithNewSchedules(curriculum, newSchedules)) {
                    throw new RuntimeException(String.format(
                        "课程 '%s' 与已安排的课程存在时间冲突。请检查教师、教室或班级的时间安排。",
                        course.getCourseName()
                    ));
                }
                newSchedules.add(curriculum);
                existingSchedules.add(curriculum);
            } else {
                throw new RuntimeException("无法为课程 '" + course.getCourseName() + "' 找到合适的时间段");
            }
        }

        return newSchedules;
    }

    @Override
    public boolean hasTimeConflict(Curriculum curriculum, List<Curriculum> existingSchedules) {
        for (Curriculum existing : existingSchedules) {
            // 检查是否在同一时间段
            if (curriculum.getWeekday().equals(existing.getWeekday()) && 
                curriculum.getSection().equals(existing.getSection())) {
                
                // 检查是否在同一周次范围内
                if (!(curriculum.getEndWeek() < existing.getStartWeek() || 
                    curriculum.getStartWeek() > existing.getEndWeek())) {
                    
                    // 检查是否涉及相同的教师、班级或教室
                    if (curriculum.getTeacherId().equals(existing.getTeacherId()) ||
                        curriculum.getClassId().equals(existing.getClassId()) ||
                        curriculum.getClassRoomId().equals(existing.getClassRoomId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isClassroomCapacitySufficient(Curriculum curriculum, Integer classSize) {
        ClassRoom classroom = classRoomMapper.selectById(curriculum.getClassRoomId());
        return classroom != null && classroom.getCapacity() >= classSize;
    }

    private Teacher selectTeacher(Course course, List<Teacher> availableTeachers) {
        // 这里可以根据教师的专业特长、职称等条件选择合适的教师
        // 简化版本：随机选择一个教师
        if (availableTeachers.isEmpty()) {
            return null;
        }
        return availableTeachers.get(new Random().nextInt(availableTeachers.size()));
    }

    private ClassRoom selectClassroom(Integer classSize, List<ClassRoom> availableClassrooms) {
        // 选择容量足够且状态可用的教室
        return availableClassrooms.stream()
            .filter(room -> room.getCapacity() >= classSize && room.getState() == 1)
            .findFirst()
            .orElse(null);
    }

    private Curriculum generateCurriculumTime(Course course, Teacher teacher, ClassRoom classroom,
                                           Integer classId, Integer schoolYear, Integer term,
                                           List<Curriculum> existingSchedules) {
        // 尝试生成不冲突的课程时间
        for (int weekday = 1; weekday <= 5; weekday++) { // 周一到周五
            for (int section = 1; section <= 4; section++) { // 假设每天4节课
                Curriculum curriculum = new Curriculum();
                curriculum.setCourseId(course.getCourseId());
                curriculum.setClassId(classId);
                curriculum.setTeacherId(teacher.getTeacherId().intValue());
                curriculum.setSchoolYear(schoolYear);
                curriculum.setTerm(term);
                curriculum.setStartWeek(1); // 从第1周开始
                curriculum.setEndWeek(16); // 到第16周结束
                curriculum.setWeekday(weekday);
                curriculum.setSection(section);
                curriculum.setLocationId(classroom.getLocationId());
                curriculum.setClassRoomId(classroom.getClassRoomId());

                if (!hasTimeConflict(curriculum, existingSchedules)) {
                    return curriculum;
                }
            }
        }
        return null;
    }

    /**
     * 检查新生成的课程是否与已生成的课程表冲突
     */
    private boolean hasConflictWithNewSchedules(Curriculum curriculum, List<Curriculum> newSchedules) {
        for (Curriculum existing : newSchedules) {
            // 检查是否在同一时间段
            if (curriculum.getWeekday().equals(existing.getWeekday()) && 
                curriculum.getSection().equals(existing.getSection())) {
                
                // 检查是否在同一周次范围内
                if (!(curriculum.getEndWeek() < existing.getStartWeek() || 
                    curriculum.getStartWeek() > existing.getEndWeek())) {
                    
                    // 检查是否涉及相同的教师、班级或教室
                    if (curriculum.getTeacherId().equals(existing.getTeacherId()) ||
                        curriculum.getClassId().equals(existing.getClassId()) ||
                        curriculum.getClassRoomId().equals(existing.getClassRoomId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
} 