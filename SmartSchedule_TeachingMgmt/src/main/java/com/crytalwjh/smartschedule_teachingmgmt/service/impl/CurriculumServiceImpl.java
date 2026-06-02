package com.crytalwjh.smartschedule_teachingmgmt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Curriculum;
import com.crytalwjh.smartschedule_teachingmgmt.mapper.CurriculumMapper;
import com.crytalwjh.smartschedule_teachingmgmt.service.CurriculumService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurriculumServiceImpl implements CurriculumService {

    @Autowired
    private CurriculumMapper curriculumMapper;

    @Override
    public Page<Curriculum> selectAlCurriculum(QueryParams params) {
        Integer pageNum = params.getPageNum();
        Integer pageSize = params.getPageSize();
        
        if (pageNum == null || pageNum == 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize == 0) {
            pageSize = 10;
        }

        Page<Curriculum> page = new Page<>(pageNum, pageSize);
        QueryWrapper<Curriculum> wrapper = new QueryWrapper<>();

        // 添加学年筛选
        Object schoolYearObj = params.getParams().get("schoolYear");
        if (schoolYearObj != null) {
            String schoolYear = schoolYearObj.toString();
            if (StrUtil.isNotBlank(schoolYear)) {
                wrapper.eq("c.schoolYear", schoolYear);
            }
        }

        // 添加学期筛选
        Object termObj = params.getParams().get("term");
        if (termObj != null) {
            String term = termObj.toString();
            if (StrUtil.isNotBlank(term)) {
                wrapper.eq("c.term", term);
            }
        }

        // 添加周次筛选
        Object weekObj = params.getParams().get("week");
        if (weekObj != null) {
            String week = weekObj.toString();
            if (StrUtil.isNotBlank(week)) {
                try {
                    int weekNum = Integer.parseInt(week);
                    // 只有当周次不为0时才添加周次筛选条件
                    if (weekNum != 0) {
                        wrapper.le("c.startWeek", weekNum)
                              .ge("c.endWeek", weekNum);
                    }
                } catch (NumberFormatException e) {
                    // 如果周次不是有效的数字，则忽略此筛选条件
                }
            }
        }

        // 添加班级筛选
        Object classIdObj = params.getParams().get("classId");
        if (classIdObj != null) {
            String classId = classIdObj.toString();
            if (StrUtil.isNotBlank(classId)) {
                wrapper.eq("c.classId", classId);
            }
        }

        // 添加教师筛选
        Object teacherIdObj = params.getParams().get("teacherId");
        if (teacherIdObj != null) {
            String teacherId = teacherIdObj.toString();
            if (StrUtil.isNotBlank(teacherId)) {
                wrapper.eq("c.teacherId", teacherId);
            }
        }

        // 添加课程筛选
        Object courseIdObj = params.getParams().get("courseId");
        if (courseIdObj != null) {
            String courseId = courseIdObj.toString();
            if (StrUtil.isNotBlank(courseId)) {
                wrapper.eq("c.courseId", courseId);
            }
        }

        return curriculumMapper.selectCurriculumWithDetails(page, wrapper);
    }

    @Override
    public int updateById(Curriculum curriculum) {
        return curriculumMapper.updateById(curriculum);
    }

    @Override
    public int deleteById(int id) {
        return curriculumMapper.deleteById(id);
    }

    @Override
    public int insertCurriculum(Curriculum curriculum) {
        return curriculumMapper.insert(curriculum);
    }
}    