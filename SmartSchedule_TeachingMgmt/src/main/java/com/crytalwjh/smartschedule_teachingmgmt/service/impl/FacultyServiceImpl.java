package com.crytalwjh.smartschedule_teachingmgmt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Course;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Faculty;
import com.crytalwjh.smartschedule_teachingmgmt.mapper.FacultyMapper;
import com.crytalwjh.smartschedule_teachingmgmt.service.FacultyService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class FacultyServiceImpl implements FacultyService {
    @Autowired
    private FacultyMapper facultyMapper;

    @Override
    public Page<Faculty> selectAllFaculty(QueryParams queryParams) {
        Integer pageSize = queryParams.getPageSize();

        Integer pageNum = queryParams.getPageNum();
        if (pageNum==null||pageNum==0){
            pageNum=1;
        }
        if (pageSize==null||pageSize==0){
            pageSize=5;
        }
        // 处理分页参数
        // 创建分页对象
        Page<Faculty> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        QueryWrapper<Faculty> wrapper = new QueryWrapper<>();
        String facultyName = (String) queryParams.getParams().get("facultyName");
        wrapper.like(StrUtil.isNotBlank(facultyName), "facultyName", facultyName);

        // 执行分页查询
        Page<Faculty> result = facultyMapper.selectFaculty(page, wrapper);

        // 设置分页数据
        page.setRecords(result.getRecords());
        page.setTotal(result.getTotal());

        return page;
    }

    @Override
    public Faculty selectById(int id) {
        return facultyMapper.selectById(id);
    }

    @Override
    public int deleteById(int id) {
        return facultyMapper.deleteById(id);
    }

    @Override
    public int insertFaculty(Faculty faculty) {
        return  facultyMapper.insert(faculty);
    }

    @Override
    public int updateById(Faculty faculty) {
        return facultyMapper.updateById(faculty);
    }
}
