package com.crytalwjh.smartschedule_teachingmgmt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Faculty;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Student;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.mapper.StudentMapper;
import com.crytalwjh.smartschedule_teachingmgmt.service.StudentService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import com.crytalwjh.smartschedule_teachingmgmt.vo.ClassAllVo;
import com.crytalwjh.smartschedule_teachingmgmt.vo.StudentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;


    @Override
    public Page<StudentVo> selectAllStudent(QueryParams queryParams) {
        Integer pageNum = queryParams.getPageNum();
        Integer pageSize = queryParams.getPageSize();

        if (pageNum==null||pageNum==0){
            pageNum=1;
        }
        if (pageSize==null||pageSize==0){
            pageSize=5;
        }
        Page<StudentVo> page= new Page<>(pageNum,pageSize);

        QueryWrapper<Student> wrapper = new QueryWrapper<>();
        String facultyName = (String) queryParams.getParams().get("facultyName");
        wrapper.like(StrUtil.isNotBlank(facultyName),"facultyName",facultyName);
        String name = (String) queryParams.getParams().get("name");
        wrapper.like(StrUtil.isNotBlank(name),"name",name);

        page.setRecords(studentMapper.selectAllStudent(page,wrapper));
        return page;
    }

    @Override
    public int insertStudent(Student student) {
        return studentMapper.insert(student);
    }

    @Override
    public int deleteById(int id) {
        return studentMapper.deleteById(id);
    }

    @Override
    public int updateById(Student student) {
        return studentMapper.updateById(student);
    }
}
