package com.crytalwjh.smartschedule_teachingmgmt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Student;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.entities.User;
import com.crytalwjh.smartschedule_teachingmgmt.mapper.UserMapper;
import com.crytalwjh.smartschedule_teachingmgmt.service.UserService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Md5Util;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.ThreadLocalUtil;
import com.crytalwjh.smartschedule_teachingmgmt.vo.TeacherVo;
import com.crytalwjh.smartschedule_teachingmgmt.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service

public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public Page<UserVo> selectAllAndTeacherAndStudent(QueryParams queryParams) {
        Integer pageNum = queryParams.getPageNum();
        Integer pageSize = queryParams.getPageSize();

        if (pageNum==null||pageNum==0){
            pageNum=1;
        }
        if (pageSize==null||pageSize==0){
            pageSize=5;
        }
        
        // 创建分页对象
        Page<UserVo> page = new Page<>(pageNum, pageSize);
        
        // 构建查询条件
        QueryWrapper<UserVo> wrapper = new QueryWrapper<>();
        
        // 添加用户名模糊查询条件
        String username = (String) queryParams.getParams().get("username");
        if (StrUtil.isNotBlank(username)) {
            wrapper.like("u.username", username);
        }

        // 添加教师姓名模糊查询条件
        String teacherName = (String) queryParams.getParams().get("teacherName");
        if (StrUtil.isNotBlank(teacherName)) {
            wrapper.and(w -> w.eq("u.type", 1).like("t.teacherName", teacherName));
        }

        // 添加学生姓名模糊查询条件
        String studentName = (String) queryParams.getParams().get("studentName");
        if (StrUtil.isNotBlank(studentName)) {
            wrapper.and(w -> w.eq("u.type", 2).like("s.name", studentName));
        }

        // 执行分页查询
        List<UserVo> records = userMapper.selectAllAndTeacherAndStudent(page, wrapper);
        page.setRecords(records);
        
        // 获取总数
        Integer total = userMapper.getCount(wrapper);
        page.setTotal(total);
        
        return page;
    }

    @Override
    public User selectByUserName(String username) {
        User user = userMapper.selectByUserName(username);
//        user.setPassword(Md5Util.getMD5String(user.getPassword()));
        return user;
    }

    @Override
    public int insertUser(User user) {
        //加密
        user.setPassword(Md5Util.getMD5String(user.getPassword()));
        //添加
        return userMapper.insert(user);
    }

    @Override
    public int updateById(User user) {
        user.setPassword(Md5Util.getMD5String(user.getPassword()));
        return userMapper.updateUserById(user);
    }

    @Override
    public int deleteById(int id) {
        return userMapper.deleteById(id);
    }

    @Override
    public void updateAvatar(String avatarUrl) {
        Map<String,Object> map = ThreadLocalUtil.get();
        Integer id = (Integer) map.get("id");
        userMapper.updateAvatar(avatarUrl,id);
    }

    @Override
    public User selectByEmail(String email) {
        return userMapper.selectByEmail(email);
    }
}
