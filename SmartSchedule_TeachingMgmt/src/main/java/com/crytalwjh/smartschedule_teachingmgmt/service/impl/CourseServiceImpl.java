package com.crytalwjh.smartschedule_teachingmgmt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassRoom;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Course;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.mapper.CourseMapper;
import com.crytalwjh.smartschedule_teachingmgmt.service.CourseService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import com.crytalwjh.smartschedule_teachingmgmt.vo.CourseScheduleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

//    @Override
//    public Page<Course> getListByPage(QueryParams queryParams) {
//        Integer pageNum = queryParams.getPageNum();
//        Integer pageSize = queryParams.getPageSize();
//
//        if (pageNum==null||pageNum==0){
//            pageNum=1;
//        }
//        if (pageSize==null||pageSize==0){
//            pageSize=5;
//        }
//        Page<Course > page= new Page<>(pageNum,pageSize);
//        page.setRecords(courseMapper.selectList(page,null));
//        // 分页查询客户信息
//        return  page;
//    }


    @Override
    public Page<Course> selectAllCourse(QueryParams queryParams) {

        Integer pageNum = queryParams.getPageNum();
        Integer pageSize = queryParams.getPageSize();

        if (pageNum==null||pageNum==0){
            pageNum=1;
        }
        if (pageSize==null||pageSize==0){
            pageSize=5;
        }
        // 处理分页参数
        // 创建分页对象
        Page<Course> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        String courseName = (String) queryParams.getParams().get("courseName");
        wrapper.like(StrUtil.isNotBlank(courseName), "courseName", courseName);

        // 执行分页查询
        Page<Course> result = courseMapper.selectAllCourse1(page, wrapper);

        // 设置分页数据
        page.setRecords(result.getRecords());
        page.setTotal(result.getTotal());

        return page;
//        Integer pageNum = queryParams.getPageNum();
//        Integer pageSize = queryParams.getPageSize();
//
//        if (pageNum==null||pageNum==0){
//            pageNum=1;
//        }
//        if (pageSize==null||pageSize==0){
//            pageSize=5;
//        }
////         分页查询客户信息
//        Page<Course> page= new Page<>(pageNum,pageSize);
//
//        QueryWrapper<Course> wrapper = new QueryWrapper<>();
//        String courseName = (String) queryParams.getParams().get("courseName");
//        wrapper.like(StrUtil.isNotBlank(courseName),"courseName",courseName);
//
//        page.setRecords(courseMapper.selectAllCourse(page, wrapper));
////        page.setTotal(classRoomMapper.getCount(wrapper));
//        return  page;
    }

    @Override
    public Course selectById(int courseID) {
        return courseMapper.selectById(courseID);
    }

//    @Override
//    public int updateCourseIDInt(Course course) {
//        return courseMapper.updateCourseIDInt(course);
//    }

//        @Override
//    public int updateById(Course course) {
//        UpdateWrapper<Course> wrapper = new UpdateWrapper<>();
//        wrapper.eq("course_id", course.getCourseID());
//        return courseMapper.updateById(course, wrapper);
//    }
    @Override
    public int updateById(Course course) {

        return courseMapper.updateById(course);
    }

    @Override
    public int deleteById(int id) {
        return courseMapper.deleteById(id);
    }

    @Override
    public int insert(Course course) {
        return courseMapper.insert(course);
    }

    @Override
    public List<Course> getCoursesByClassId(Integer classId) {
        return courseMapper.getCoursesByStudentClassId(classId);
    }

    @Override
    public List<CourseScheduleVo> getCompleteCourseSchedule(Integer classId) {
        return courseMapper.getCompleteCourseSchedule(classId);
    }
}
