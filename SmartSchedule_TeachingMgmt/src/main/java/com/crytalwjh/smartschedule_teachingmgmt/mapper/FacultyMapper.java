package com.crytalwjh.smartschedule_teachingmgmt.mapper;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Course;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Faculty;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.util.List;

@Repository
public interface FacultyMapper  extends BaseMapper<Faculty> {
//    default List<Faculty> selectAllFaculty(){
//        return this.selectList(null);
//    }
//
//
//    default Faculty selectById(int id){
//        return  this.selectById(id);
//    }
    @Select("select * from faculty   ${ew.customSqlSegment} ORDER BY  faculty.facultyId ASC")
     Page<Faculty> selectFaculty(Page<Faculty> page , @Param(Constants.WRAPPER) QueryWrapper<Faculty> wrapper);
}
