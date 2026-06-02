package com.crytalwjh.smartschedule_teachingmgmt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Faculty;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Location;
import com.crytalwjh.smartschedule_teachingmgmt.mapper.LocationMapper;
import com.crytalwjh.smartschedule_teachingmgmt.service.LocationService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {
    @Autowired
    private LocationMapper locationMapper;

    @Override
    public Page<Location> selectAll(QueryParams queryParams) {
        Integer pageSize = queryParams.getPageSize();
        Integer pageNum = queryParams.getPageNum();
        if (pageNum==null||pageNum==0){
            pageNum=1;
        }
        if (pageSize==null||pageSize==0){
            pageSize=5;
        }
        // 创建分页对象
        Page<Location> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        QueryWrapper<Location> wrapper = new QueryWrapper<>();
        String locationName = (String) queryParams.getParams().get("locationName");
        String isMultimedia = (String) queryParams.getParams().get("isMultimedia");
        String state = (String) queryParams.getParams().get("state");

        // 使用 like 进行模糊查询，并添加 % 通配符
        if (StrUtil.isNotBlank(locationName)) {
            wrapper.like("locationName", "%" + locationName + "%");
        }
        
        // 添加多媒体和状态条件
        if (StrUtil.isNotBlank(isMultimedia)) {
            wrapper.eq("isMultimedia", isMultimedia);
        }
        if (StrUtil.isNotBlank(state)) {
            wrapper.eq("state", state);
        }

        // 执行分页查询
        Page<Location> result = locationMapper.selectLocation(page, wrapper);

        // 设置分页数据
        page.setRecords(result.getRecords());
        page.setTotal(result.getTotal());

        return page;
    }


    @Override
    public Location selectById(int id) {
        return locationMapper.selectById(id);
    }

    @Override
    public int deleteById(int id) {
        return locationMapper.deleteById(id);
    }

    @Override
    public int insertLocation(Location location) {
        return locationMapper.insert(location);
    }

    @Override
    public int updateById(Location location) {
        return locationMapper.updateById(location);
    }

    @Override
    public Location selectLocationAndClassRoomById(int iId) {
        return locationMapper.selectById(iId);
    }

}
