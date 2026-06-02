package com.crytalwjh.smartschedule_teachingmgmt.controller;


import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import com.crytalwjh.smartschedule_teachingmgmt.entities.User;
import com.crytalwjh.smartschedule_teachingmgmt.service.UserService;
import com.crytalwjh.smartschedule_teachingmgmt.service.EmailService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.*;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 用户注册，查找方法
 */
@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    //发送注册验证码
    @PostMapping("/sendRegisterCode")
    public Result sendRegisterCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            return Result.fail("邮箱不能为空");
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return Result.fail("邮箱格式不正确");
        }
        
        // 检查邮箱是否已被注册
        User existingUser = userService.selectByEmail(email);
        if (existingUser != null) {
            return Result.fail("该邮箱已被注册");
        }
        
        try {
            // 生成6位随机验证码
            String code = String.format("%06d", (int)(Math.random() * 1000000));
            
            // 发送验证码邮件
            String subject = "注册验证码";
            String content = "您的注册验证码是：" + code + "，有效期为5分钟。";
            emailService.sendEmail(email, subject, content);
            
            // TODO: 将验证码保存到Redis或数据库中，设置5分钟过期时间
            
            return Result.success("验证码已发送到您的邮箱");
        } catch (Exception e) {
            return Result.fail("验证码发送失败：" + e.getMessage());
        }
    }

    //注册
    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String email = params.get("email");

        if (username != null && username.length() >= 5 && username.length() <= 16 && password != null && password.length() >= 5
                && password.length() <= 15) {//判断用户名和密码是否符合要求
            User user1 = userService.selectByUserName(username);
            if (user1 == null) {//用户未被占用，可以注册
                User user2 = new User();
                user2.setUsername(username);
                user2.setPassword(password);
                user2.setEmail(email);
                userService.insertUser(user2);
                
                // 发送注册成功邮件
                try {
                    String subject = "注册成功通知";
                    String content = "亲爱的 " + username + "：<br><br>" +
                            "恭喜您成功注册我们的系统！<br>" +
                            "您的账号信息如下：<br>" +
                            "用户名：" + username + "<br>" +
                            "邮箱：" + email + "<br><br>" +
                            "请妥善保管您的账号信息。<br>" +
                            "如有任何问题，请联系管理员。<br><br>" +
                            "祝您使用愉快！";
                    emailService.sendEmail(email, subject, content);
                } catch (Exception e) {
                    // 邮件发送失败不影响注册流程
                    System.err.println("注册成功邮件发送失败：" + e.getMessage());
                }
                
                return Result.success("注册成功");
            } else {//用户已存在，不可注册
                return Result.fail("用户已存在，不可注册");
            }
        } else {
            return Result.fail("用户名或密码不符合注册要求");
        }
    }

    //登录
    @PostMapping("/login")
    public Result login(@RequestParam String username, String password, @RequestParam(required = false, defaultValue = "false") boolean rememberMe) {
        if (username != null && username.length() >= 5 && username.length() <= 16 && password != null && password.length() >= 5
                && password.length() <= 15) {//判断用户名和密码是否符合要求
            User loginUser = userService.selectByUserName(username);
            //判断该用户是否存在
            if (loginUser == null) {
                return Result.fail("用户不存在");
            }
            // 检查用户是否被禁用
            if (loginUser.getDisabled() != null && loginUser.getDisabled() == 1) {
                return Result.fail("该用户已被禁用，请联系管理员");
            }
            // 检查用户类型是否为null
            if (loginUser.getType() == null) {
                return Result.fail("请先联系管理员设置用户类型");
            }
            if (Md5Util.getMD5String(password).equals(loginUser.getPassword())) {
                //登录成功
                Map<String, Object> claims = new HashMap<>();
                claims.put("id", loginUser.getId());
                claims.put("username", loginUser.getUsername());
                String token = JwtUtil.genToken(claims, rememberMe);
                return Result.success(token);
            }
            return Result.fail("密码错误");

        } else {
            return Result.fail("账户或密码不符合输入要求");
        }
    }

    //管理员查看用户信息
    @PostMapping("/list")
    Result selectUser(@RequestBody QueryParams queryParams) {
        return Result.success(userService.selectAllAndTeacherAndStudent(queryParams));
    }
    //根据用户名获取用户信息
    @GetMapping("/userInfo")
    Result userInfo() {
        try {
            Map<String, Object> map = ThreadLocalUtil.get();
            if (map == null) {
                return Result.fail("未登录或登录已过期");
            }
            
            String username = (String) map.get("username");
            if (username == null) {
                return Result.fail("用户信息不完整");
            }
            
            User user = userService.selectByUserName(username);
            if (user == null) {
                return Result.fail("用户不存在");
            }
            
            // 移除敏感信息
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.fail("获取用户信息失败：" + e.getMessage());
        }
    }

    @PostMapping("/insertUser")
    Result insertClass(@RequestBody User user){
        return Result.success(userService.insertUser(user));
    }


    @DeleteMapping("/deleteById")
    Result deleteById(@RequestParam int id){
        return Result.success(userService.deleteById(id));
    }

    @PutMapping("/updateById")
    Result updateById(@RequestBody User user){
        // 获取原用户信息
        User originalUser = userService.selectByUserName(user.getUsername());
        if (originalUser == null) {
            return Result.fail("用户不存在");
        }

        // 如果用户类型从学生(2)变为教师(1)，清除学生ID
        if (originalUser.getType() == 2 && user.getType() == 1) {
            user.setStudentId(null);
        }
        // 如果用户类型从教师(1)变为学生(2)，清除教师ID
        else if (originalUser.getType() == 1 && user.getType() == 2) {
            user.setTeacherId(null);
        }

        return Result.success(userService.updateById(user));
    }

//    //更新用户信息
//    @PutMapping("/updateUser")
//    public Result updateByUsername(@RequestBody @Validated User user) {
//        if (user.getUsername() != null && user.getUsername().length() >= 5 && user.getUsername().length() <= 16 && user.getPassword() != null && user.getPassword().length() >= 5
//                && user.getPassword().length() <= 15) {//判断用户名和密码是否符合要求
//            User user1 = userService.selectByUserName(user.getUsername());
//            if (user1 != null) {//用户未被占用，可更改
//
//                userService.updateByUsername(user);
//                return Result.success("更新成功");
//
//            }
//        } else {
//            return Result.fail("用户名或密码不符合要求");
//
//        }
//        return null;
    }
