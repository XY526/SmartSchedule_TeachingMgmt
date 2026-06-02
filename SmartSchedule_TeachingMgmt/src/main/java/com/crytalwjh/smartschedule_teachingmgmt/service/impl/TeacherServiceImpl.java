package com.crytalwjh.smartschedule_teachingmgmt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Student;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.mapper.TeacherMapper;
import com.crytalwjh.smartschedule_teachingmgmt.service.TeacherService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import com.crytalwjh.smartschedule_teachingmgmt.vo.ClassAllVo;
import com.crytalwjh.smartschedule_teachingmgmt.vo.StudentVo;
import com.crytalwjh.smartschedule_teachingmgmt.vo.TeacherVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private TeacherMapper teacherMapper;

    @Override
    public Page<TeacherVo> selectAllTeacher(QueryParams queryParams) {
        Integer pageNum = queryParams.getPageNum();
        Integer pageSize = queryParams.getPageSize();

        if (pageNum==null||pageNum==0){
            pageNum=1;
        }
        if (pageSize==null||pageSize==0){
            pageSize=5;
        }
        Page<TeacherVo> page= new Page<>(pageNum,pageSize);

        QueryWrapper<Teacher> wrapper = new QueryWrapper<>();
        String teacherName = (String) queryParams.getParams().get("teacherName");
        wrapper.like(StrUtil.isNotBlank(teacherName),"teacherName",teacherName);

        System.out.println("66666666666666666666660"+teacherName);
        String facultyName = (String) queryParams.getParams().get("facultyName");
        wrapper.like(StrUtil.isNotBlank(facultyName),"facultyName",facultyName);
        page.setRecords(teacherMapper.selectAllClassRoom(page,wrapper));
        return page;
    }

    @Override
    public Teacher selectTeacherAndFacultyById(int id) {
        return teacherMapper.selectTeacherAndFacultyById(id);
    }

    @Override
    public List<Teacher> selectTeacherAndFacultyAll() {
        return teacherMapper.selectTeacherAndFacultyAll();
    }

    @Override
    public int insertTeacher(Teacher teacher) {
        return teacherMapper.insert(teacher);
    }

    @Override
    public int deleteById(int id) {
        return teacherMapper.deleteById(id);
    }

    @Override
    public int updateById(Teacher teacher) {
        return teacherMapper.updateById(teacher);
    }
}
