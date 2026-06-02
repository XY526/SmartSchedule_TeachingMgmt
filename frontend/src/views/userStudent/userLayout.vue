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
    ChatDotRound,
    Reading
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
                // 管理员用户，跳转到管理员页面
                console.log('管理员用户，跳转到管理员页面');
                router.push('/');
            } else if (userType === 0) {
                console.log('当前用户类型:', userType, '- 学生身份');
                // 学生用户，如果不在学生页面，则跳转到学生页面
                const currentPath = router.currentRoute.value.path;
                if (!currentPath.startsWith('/user')) {
                    router.push('/user/student-info');
                }
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
    // 如果已经有用户信息，且是学生用户，直接跳转到学生页面
    if (userInfoStore.info && userInfoStore.info.type === 0) {
        const currentPath = router.currentRoute.value.path;
        if (!currentPath.startsWith('/user')) {
            router.push('/user/student-info');
        }
    } else {
        getUserInfo();
    }
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
        // 其他命令直接跳转到对应路由
        router.push('/user/' + command)
    }
}
</script>

<template>
    <!-- element-plus中的容器 -->
    <el-container class="layout-container">
        <!-- 左侧菜单 -->
        <el-aside width="200px">
            <div class="el-aside__logo">
                <div class="system-title">智慧日程教务系统</div>
            </div>
            <!-- element-plus的菜单标签 -->
            <el-menu
                active-text-color="#ffd04b"
                background-color="#2c2c2c"
                text-color="#fff"
                router
                :default-active="$route.path"
                     >
                <!-- AI助手 -->
                <el-menu-item index="/user/ai">
                <el-icon>
                    <ChatDotRound />
                </el-icon>
                <span>AI助手</span>
                </el-menu-item>

                    <!-- 日程管理 -->
                    <el-menu-item index="/user/schedule">
                <el-icon>
                    <Calendar />
                </el-icon>
                <span>我的日程</span>
                </el-menu-item>
                <!-- 课程信息 -->
                <el-menu-item index="/user/course">
                <el-icon>
                    <Notebook />
                </el-icon>
                <span>课程信息</span>
                </el-menu-item>
                <el-menu-item index="/user/score">
                <el-icon>
                    <Reading />
                </el-icon>
                <span>成绩信息</span>
                </el-menu-item>
                <!-- 教室信息 -->
                <el-menu-item index="/user/classroom">
                <el-icon>
                    <HomeFilled />
                </el-icon>
                <span>教室信息</span>
                </el-menu-item>

                <!-- 院系信息 -->
                <el-menu-item index="/user/faculty">
                <el-icon>
                    <OfficeBuilding />
                </el-icon>
                <span>院系信息</span>
                </el-menu-item>

                <!-- 教学楼信息 -->
                <el-menu-item index="/user/location">
                <el-icon>
                    <Location />
                </el-icon>
                <span>教学楼信息</span>
                </el-menu-item>

                <!-- 教师信息 -->
                <el-menu-item index="/user/teacher">
                <el-icon>
                    <User />
                </el-icon>
                <span>教师信息</span>
                </el-menu-item>

                <!-- 课程表查看 -->
                <el-menu-item index="/user/curriculum">
                <el-icon>
                    <Calendar />
                </el-icon>
                <span>我的课程表</span>
                </el-menu-item>



                <!-- 个人信息 -->
                <el-menu-item index="/user/student-info">
                <el-icon>
                    <User />
                </el-icon>
                <span>个人信息</span>
                </el-menu-item>
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
                         学生：{{ userInfoStore.info.name || '未设置姓名' }}
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
        background-color: #2c2c2c;
        padding-top: 20px;

        &__logo {
            height: 60px;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 10px 0;
            margin-bottom: 20px;

            .system-title {
                color: #fff;
                font-size: 18px;
                font-weight: bold;
                text-align: center;
                padding: 0 10px;
                line-height: 1.4;
            }
        }

        .el-menu {
            border-right: none;
            background-color: #2c2c2c;
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
  background-color: #3c3c3c; /* 鼠标悬停时的背景色 */
}

.el-sub-menu .el-menu-item {
  padding-left: 50px !important; /* 子菜单项缩进 */
}
</style>