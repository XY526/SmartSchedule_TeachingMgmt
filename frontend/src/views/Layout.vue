<script setup>
import {
    Notebook,
    School,
    HomeFilled,
    OfficeBuilding,
    Location,
    Calendar,
    UserFilled,
    User,
    Crop,
    EditPen,
    SwitchButton,
    CaretBottom,
    Reading,
    Stamp,
    House,
    Collection
} from '@element-plus/icons-vue'
import avatar from '@/assets/default.png'

import {userInfoService} from '@/api/user.js'
import useUserInfoStore from '@/stores/userInfo.js'
import {useTokenStore} from '@/stores/token.js'
import {useRouter} from 'vue-router'
import {ElMessage,ElMessageBox} from 'element-plus'

const tokenStore = useTokenStore();
const userInfoStore = useUserInfoStore();
const router = useRouter();

//调用函数,获取用户详细信息
const getUserInfo = async()=>{
    try {
        //检查是否有token
        const currentToken = tokenStore.token;
        console.log('当前token状态:', currentToken);
        
        if (!currentToken) {
            console.log('没有token，跳转到登录页');
            ElMessage.error('请先登录');
            router.push('/login');
            return;
        }

        // 检查localStorage中的token
        const savedToken = localStorage.getItem('token') || sessionStorage.getItem('token');
        console.log('存储中的token:', savedToken);
        
        if (savedToken && savedToken !== currentToken) {
            console.log('token不一致，使用存储中的token');
            tokenStore.setToken(savedToken, true);
        }

        console.log('开始获取用户信息，当前token:', tokenStore.token);
        //调用接口
        let result = await userInfoService();
        console.log('获取用户信息响应:', result);
        
        if (result.code === 0 || result.code === 200) {
            //数据存储到pinia中
            userInfoStore.setInfo(result.data);
            console.log('用户信息已保存:', result.data);
            
            // 根据用户类型进行路由跳转
            const userType = result.data.type;
            // 输出用户类型信息
            if (userType === 1 || userType === 2) {
                console.log('当前用户类型:', userType, '- 管理员身份');
            } else if (userType === 0) {
                console.log('当前用户类型:', userType, '- 学生身份');
            }
            
            if (userType === 0) {
                // 学生用户，跳转到学生页面
                console.log('学生用户，跳转到学生页面');
                router.push('/user');
            } else if (userType === 1 || userType === 2) {
                // 管理员用户，保持在当前页面
                console.log('管理员用户，保持在当前页面');
            }
        } else {
            console.error('获取用户信息失败:', result);
            ElMessage.error(result.message || '获取用户信息失败');
            if (result.code === 1) {
                //token失效，跳转到登录页
                console.log('token失效，清除token并跳转到登录页');
                tokenStore.removeToken();
                router.push('/login');
            }
        }
    } catch (error) {
        console.error('获取用户信息失败:', error);
        ElMessage.error('获取用户信息失败，请重新登录');
        tokenStore.removeToken();
        router.push('/login');
    }
}

//在组件挂载后获取用户信息
import { onMounted } from 'vue';
onMounted(() => {
    console.log('Layout组件已挂载，准备获取用户信息');
    getUserInfo();
});

//条目被点击后,调用的函数
const handleCommand = (command)=>{
    //判断指令
    if(command === 'logout'){
        //退出登录
        ElMessageBox.confirm(
        '您确认要退出吗?',
        '温馨提示',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    )
        .then(async () => {
            //退出登录
            //1.清空pinia中存储的token以及个人信息
            tokenStore.removeToken()
            userInfoStore.removeInfo()

            //2.跳转到登录页面
            router.push('/login')
            ElMessage({
                type: 'success',
                message: '退出登录成功',
            })
            
        })
        .catch(() => {
            ElMessage({
                type: 'info',
                message: '用户取消了退出登录',
            })
        })
    }else{
        //路由
        router.push('/user/'+command)
    }
}
</script>

<template>
    <!-- element-plus中的容器 -->
    <el-container class="layout-container">
        <!-- 左侧菜单 -->
        <el-aside width="200px">
            <div class="el-aside__logo"></div>
            <!-- element-plus的菜单标签 -->
            <el-menu
                active-text-color="#ffd04b"
                background-color="#08005a"
                text-color="#fff"
                router
                     >
                <!-- 课程管理 -->
                <el-menu-item index="/course/list">
                <el-icon>
                    <Notebook /> <!-- 使用 Notebook 图标 -->
                </el-icon>
                <span>课程管理</span>
                </el-menu-item>

                <!-- 班级管理 -->
                <el-menu-item index="/classAll/list">
                <el-icon>
                    <School /> <!-- 使用 School 图标 -->
                </el-icon>
                <span>班级管理</span>
                </el-menu-item>

                <!-- 教室管理 -->
                <el-menu-item index="/classRoom/list">
                <el-icon>
                    <HomeFilled /> <!-- 使用 HomeFilled 图标 -->
                </el-icon>
                <span>教室管理</span>
                </el-menu-item>

                <!-- 院系管理 -->
                <el-menu-item index="/faculty/list">
                <el-icon>
                    <OfficeBuilding /> <!-- 使用 OfficeBuilding 图标 -->
                </el-icon>
                <span>院系管理</span>
                </el-menu-item>

                <!-- 教学楼管理 -->
                <el-menu-item index="/location/list">
                <el-icon>
                    <Location /> <!-- 使用 Location 图标 -->
                </el-icon>
                <span>教学楼管理</span>
                </el-menu-item>

                <!-- 学生导航 -->
                <el-sub-menu index="/userStudent" v-if="userInfoStore.info.type === 0">
                    <template #title>
                        <el-icon><UserFilled /></el-icon>
                        <span>学生</span>
                    </template>
                    <el-menu-item index="/userStudent/info">
                        <el-icon><User /></el-icon>
                        <span>个人信息</span>
                    </el-menu-item>
                    <el-menu-item index="/userStudent/course">
                        <el-icon><Reading /></el-icon>
                        <span>我的课程</span>
                    </el-menu-item>
                    <el-menu-item index="/userStudent/curriculum">
                        <el-icon><Calendar /></el-icon>
                        <span>我的课程表</span>
                    </el-menu-item>
                    <el-menu-item index="/userStudent/teacher">
                        <el-icon><Stamp /></el-icon>
                        <span>我的老师</span>
                    </el-menu-item>
                    <el-menu-item index="/userStudent/classroom">
                        <el-icon><House /></el-icon>
                        <span>教室信息</span>
                    </el-menu-item>
                    <el-menu-item index="/userStudent/faculty">
                        <el-icon><Collection /></el-icon>
                        <span>院系信息</span>
                    </el-menu-item>
                    <el-menu-item index="/userStudent/score">
                        <el-icon><EditPen /></el-icon>
                        <span>我的成绩</span>
                    </el-menu-item>
                </el-sub-menu>

                <!-- 教师管理 -->
                <el-menu-item index="/teacher/list">
                <el-icon>
                    <User /> <!-- 使用 User 图标 -->
                </el-icon>
                <span>教师管理</span>
                </el-menu-item>

                <!-- 学生管理 -->
                <el-menu-item index="/student/list">
                <el-icon>
                    <UserFilled /> <!-- 使用 UserFilled 图标 -->
                </el-icon>
                <span>学生管理</span>
                </el-menu-item>

                <!-- 成绩管理 -->
                <el-menu-item index="/score/list">
                <el-icon>
                    <EditPen /> <!-- 使用 EditPen 图标 -->
                </el-icon>
                <span>成绩管理</span>
                </el-menu-item>

                <!-- 课程表 -->
                <el-sub-menu>
                <template #title>
                    <el-icon>
                    <Calendar /> <!-- 使用 UserFilled 图标 -->
                    </el-icon>
                    <span>课程表</span>
                    
                </template>
                
                <el-menu-item index="/curriculum/list">
                    <el-icon>
                    <Calendar /> 
                    </el-icon>
                    <span>课程表管理</span>
                </el-menu-item>
                <el-menu-item index="/curriculum/curriculumView">
                    <el-icon>
                    <Calendar /> 
                    </el-icon>
                    <span>课程表详情</span>
                </el-menu-item>
            </el-sub-menu>
            <!-- 个人中心 -->
                <el-sub-menu>
                <template #title>
                    <el-icon>
                    <UserFilled /> <!-- 使用 UserFilled 图标 -->
                    </el-icon>
                    <span>用户管理</span>
                </template>
                <el-menu-item index="/user/list">
                    <el-icon>
                    <User /> <!-- 使用 User 图标 -->
                    </el-icon>
                    <span>所有用户资料</span>
                </el-menu-item>
        </el-sub-menu>
    </el-menu>
    </el-aside>
        <!-- 右侧主区域 -->
        <el-container>
            <!-- 头部区域 -->
            <el-header>
                <div>欢迎您：{{ userInfoStore.info.username }}
                    <template v-if="userInfoStore.info.type === 1">
                         教师：{{ userInfoStore.info.teacherName || '未设置姓名' }}
                    </template>
                     <template v-else-if="userInfoStore.info.type === 0">
                         学生：{{ userInfoStore.info.studentName || '未设置姓名' }}
                    </template>
                    <template v-else-if="userInfoStore.info.type === 2">
                         管理员
                    </template>
                </div>
                <!-- 下拉菜单 -->
                <!-- command: 条目被点击后会触发,在事件函数上可以声明一个参数,接收条目对应的指令 -->
                <el-dropdown placement="bottom-end" @command="handleCommand">
                    <span class="el-dropdown__box">
                        <el-avatar :src="userInfoStore.info.userPic? userInfoStore.info.userPic:avatar" />
                        <el-icon>
                            <CaretBottom />
                        </el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="info" :icon="User">基本资料</el-dropdown-item>
                            <el-dropdown-item command="logout" :icon="SwitchButton">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </el-header>
            <!-- 中间区域 -->
            <el-main>
                <!-- <div style="width: 1290px; height: 570px;border: 1px solid red;">
                    内容展示区
                </div> -->
                <router-view></router-view>
            </el-main>
            <!-- 底部区域 -->
            <el-footer>智慧日程教务系统 ©2025 Created by Crystal</el-footer>
        </el-container>
    </el-container>
</template>

<style lang="scss" scoped>
.layout-container {
    height: 100vh;

    .el-aside {
        background-color: #08005a;

        &__logo {
            height: 120px;
            background: url('@/assets/logo.png') no-repeat center / 120px auto;
        }

        .el-menu {
            border-right: none;
        }
    }

    .el-header {
        background-color: #fff;
        display: flex;
        align-items: center;
        justify-content: space-between;

        .el-dropdown__box {
            display: flex;
            align-items: center;

            .el-icon {
                color: #999;
                margin-left: 10px;
            }

            &:active,
            &:focus {
                outline: none;
            }
        }
    }

    .el-footer {
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
        color: #666;
    }
}
.el-menu {
  border-right: none; /* 去除默认边框 */
}

.el-menu-item,
.el-sub-menu {
  transition: background-color 0.3s ease; /* 添加过渡效果 */
}

.el-menu-item:hover,
.el-sub-menu:hover {
  background-color: #333; /* 鼠标悬停时的背景色 */
}

.el-sub-menu .el-menu-item {
  padding-left: 50px !important; /* 子菜单项缩进 */
}
</style>