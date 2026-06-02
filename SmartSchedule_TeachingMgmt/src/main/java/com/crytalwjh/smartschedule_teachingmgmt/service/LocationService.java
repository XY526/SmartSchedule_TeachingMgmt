package com.crytalwjh.smartschedule_teachingmgmt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Location;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;

import java.util.List;

public interface LocationService {

    Page<Location> selectAll(QueryParams queryParams);
    Location selectById(int id);
    int deleteById(int id);
    int insertLocation(Location location);
    int updateById(Location location);
    Location selectLocationAndClassRoomById(int id );

}
