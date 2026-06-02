package com.crytalwjh.smartschedule_teachingmgmt.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crytalwjh.smartschedule_teachingmgmt.entities.User;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.vo.UserVo;

import java.util.List;

public interface UserService {

    Page<UserVo> selectAllAndTeacherAndStudent(QueryParams queryParams);


    User selectByUserName(String username);
    int insertUser(User user);
    int updateById(User user);
    int deleteById(int id);
    //更新头像
    void updateAvatar(String avatarUrl);

    //根据邮箱查询用户
    User selectByEmail(String email);
}
