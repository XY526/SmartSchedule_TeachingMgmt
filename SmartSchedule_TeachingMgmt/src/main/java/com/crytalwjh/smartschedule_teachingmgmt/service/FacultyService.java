package com.crytalwjh.smartschedule_teachingmgmt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Faculty;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;

import java.util.List;

public interface FacultyService {

    Page<Faculty> selectAllFaculty(QueryParams queryParams);

    Faculty selectById(int id);

    int deleteById(int id);

    int insertFaculty(Faculty faculty);
    int updateById(Faculty faculty);
}
