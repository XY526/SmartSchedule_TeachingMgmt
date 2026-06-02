package com.crytalwjh.smartschedule_teachingmgmt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Score;
import com.crytalwjh.smartschedule_teachingmgmt.mapper.ScoreMapper;
import com.crytalwjh.smartschedule_teachingmgmt.service.ScoreService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.vo.ScoreVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {

    @Autowired
    private ScoreMapper scoreMapper;

    @Override
    public Page<Score> selectPage(QueryParams queryParams) {
        Integer pageNum = queryParams.getPageNum();
        Integer pageSize = queryParams.getPageSize();

        if (pageNum == null || pageNum == 0) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize == 0) {
            pageSize = 10; // Default page size
        }
        Page<Score> page = new Page<>(pageNum, pageSize);

        QueryWrapper<Score> wrapper = new QueryWrapper<>();
        // Add filtering based on queryParams if needed
        // Example: filter by studentId, courseId, examType, remark
        Integer studentId = (Integer) queryParams.getParams().get("studentId");
        wrapper.eq(studentId != null, "studentId", studentId);

        Integer courseId = (Integer) queryParams.getParams().get("courseId");
        wrapper.eq(courseId != null, "courseId", courseId);

        Object examTypeObj = queryParams.getParams().get("examType");
        if (examTypeObj != null) {
            if (examTypeObj instanceof Integer) {
                wrapper.eq("examType", ((Integer) examTypeObj).byteValue());
            } else if (examTypeObj instanceof Byte) {
                wrapper.eq("examType", examTypeObj);
            }
        }

        String remark = (String) queryParams.getParams().get("remark");
        wrapper.like(StrUtil.isNotBlank(remark), "remark", remark);

        // Order by examDate or createTime if needed
        // wrapper.orderByDesc("examDate");

        return scoreMapper.selectPage(page, wrapper);
    }

    @Override
    public int insert(Score score) {
        // Set createTime before inserting
        score.setCreateTime(LocalDateTime.now());
        return scoreMapper.insert(score);
    }

    @Override
    public int deleteById(Integer id) {
        return scoreMapper.deleteById(id);
    }

    @Override
    public int updateById(Score score) {
        return scoreMapper.updateById(score);
    }

    @Override
    public List<Score> selectByStudentId(Integer studentId) {
        QueryWrapper<Score> wrapper = new QueryWrapper<>();
        wrapper.eq("studentId", studentId);
        // You might want to order these, e.g., by examDate or courseId
        // wrapper.orderByAsc("examDate", "courseId");
        return scoreMapper.selectList(wrapper);
    }

    @Override
    public List<Score> selectByCourseId(Integer courseId) {
        QueryWrapper<Score> wrapper = new QueryWrapper<>();
        wrapper.eq("courseId", courseId);
        // You might want to order these, e.g., by examDate or studentId
        // wrapper.orderByAsc("examDate", "studentId");
        return scoreMapper.selectList(wrapper);
    }

    @Override
    public Score selectById(Integer id) {
        return scoreMapper.selectById(id);
    }

    @Override
    public List<ScoreVo> getScoresByStudentId(Integer studentId) {
        return scoreMapper.getScoresByStudentId(studentId);
    }
} 