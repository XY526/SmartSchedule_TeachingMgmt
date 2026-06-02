package com.crytalwjh.smartschedule_teachingmgmt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassRoom;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Course;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Location;

import com.crytalwjh.smartschedule_teachingmgmt.mapper.ClassRoomMapper;
import com.crytalwjh.smartschedule_teachingmgmt.service.ClassRoomService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import com.crytalwjh.smartschedule_teachingmgmt.vo.ClassAllVo;
import com.crytalwjh.smartschedule_teachingmgmt.vo.ClassRoomVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassRoomServiceImpl implements ClassRoomService {

    @Autowired
    private ClassRoomMapper classRoomMapper;

    @Override
    public Page<ClassRoomVo> getListByPage(QueryParams queryParams) {
        Integer pageNum = queryParams.getPageNum();
        Integer pageSize = queryParams.getPageSize();

        if (pageNum==null||pageNum==0){
            pageNum=1;
        }
        if (pageSize==null||pageSize==0){
            pageSize=5;
        }
        // 分页查询客户信息
        Page<ClassRoomVo> page = new Page<>(pageNum, pageSize);

        QueryWrapper<ClassRoom> wrapper = new QueryWrapper<>();
        
        // 添加地点名称查询条件
        String locationName = (String) queryParams.getParams().get("locationName");
        if (StrUtil.isNotBlank(locationName)) {
            wrapper.exists("SELECT 1 FROM location l WHERE l.locationId = c.locationId AND l.locationName LIKE '%" + locationName + "%'");
        }

        // 添加查询条件：是否为多媒体教室
        String isMultimediaStr = (String) queryParams.getParams().get("isMultimedia");
        if (StrUtil.isNotBlank(isMultimediaStr)) {
            try {
                Integer isMultimedia = Integer.parseInt(isMultimediaStr);
                wrapper.eq("isMultimedia", isMultimedia);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("isMultimedia 参数必须是整数");
            }
        }

        // 添加查询条件：教室使用状态
        String stateStr = (String) queryParams.getParams().get("state");
        if (StrUtil.isNotBlank(stateStr)) {
            try {
                Integer state = Integer.parseInt(stateStr);
                wrapper.eq("state", state);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("state 参数必须是整数");
            }
        }

        page.setRecords(classRoomMapper.selectAllClassRoom(page, wrapper));
        return page;
    }

    @Override
    public int insertClassRoom(ClassRoom classRoom) {
        return classRoomMapper.insert(classRoom);
    }

    @Override
    public int updateById(ClassRoom classRoom) {
        return classRoomMapper.updateById(classRoom);
    }

    @Override
    public int deleteById(int id) {
        return classRoomMapper.deleteById(id);
    }



}
