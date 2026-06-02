package com.crytalwjh.smartschedule_teachingmgmt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassAll;
import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassRoom;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Faculty;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.mapper.ClassAllMapper;
import com.crytalwjh.smartschedule_teachingmgmt.service.ClassAllService;

import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import com.crytalwjh.smartschedule_teachingmgmt.vo.ClassAllVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ClassAllServiceImpl implements ClassAllService {
    @Autowired
    private ClassAllMapper classAllMapper;

        //查询班级列表
    @Override
    public Page<ClassAllVo> selectTeacherAndFacultyAndFaculty(QueryParams queryParams) {
        Integer pageNum = queryParams.getPageNum();
        Integer pageSize = queryParams.getPageSize();

        if (pageNum==null||pageNum==0){
            pageNum=1;
        }
        if (pageSize==null||pageSize==0){
            pageSize=5;
        }
        Page<ClassAllVo> page= new Page<>(pageNum,pageSize);

        QueryWrapper<Teacher> wrapper = new QueryWrapper<>();
        String teacherName = (String) queryParams.getParams().get("teacherName");
        wrapper.like(StrUtil.isNotBlank(teacherName),"teacherName",teacherName);

        String facultyName = (String) queryParams.getParams().get("facultyName");
        wrapper.like(StrUtil.isNotBlank(facultyName),"facultyName",facultyName);
        System.out.println(Result.success("哈哈哈哈哈哈哈"+facultyName+teacherName));
        page.setRecords(classAllMapper.selectTeacherAndFacultyAndFaculty(wrapper,(pageNum-1)*pageSize,pageSize));
        page.setTotal(classAllMapper.getCount(wrapper));
//        wrapper.setParamAlias("ew1");
        return page;
    }

    @Override
    public int insertClass(ClassAll classAll) {
        return classAllMapper.insert(classAll);
    }

    @Override
    public int updateById(ClassAll classAll) {
        return classAllMapper.updateById(classAll);
    }

    @Override
    public int deleteById(int id) {
        return classAllMapper.deleteById(id);
    }


}