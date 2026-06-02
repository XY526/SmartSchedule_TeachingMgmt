import { createRouter, createWebHistory } from 'vue-router'
import useUserInfoStore from '@/stores/userInfo.js'
// import { useUserStore } from '@/stores/user.js' // Removed incorrect import
import { ElMessage } from 'element-plus';

//导入组件
import LoginVue from '@/views/Login.vue'
import LayoutVue from '@/views/Layout.vue'

//课程
import CourseVue from '@/views/course/course.vue'

//班级
import ClassAllVue from '@/views/classAll/classAll.vue'
//教室
import classRoomVue from '@/views/classRoom/classRoom.vue'
//院系
import facultyVue from '@/views/faculty/faculty.vue'
//教学楼
import locationVue from '@/views/location/location.vue'
//学生
import studentVue from '@/views/student/student.vue'
//教师
import teacherVue from '@/views/teacher/teacher.vue'
//用户
import userVue from '@/views/user/user.vue'
//成绩
import ScoreVue from '@/views/score/Score.vue'

import curriculumViewVue from '@/views/curriculum/curriculumView.vue'

import UserInfoVue from '@/views/user/UserInfo.vue'

//课程表
import curriculumVue from '@/views/curriculum/curriculum.vue'

//日程管理
import ScheduleVue from '@/views/schedule/index.vue'

//学生页面
//学生导航栏
import studentLayoutVue from '@/views/userStudent/userLayout.vue'
//学生AI
import aiVue from '@/views/userStudent/ai.vue'
//学生查看组件
import studentCourseVue from '@/views/userStudent/course.vue'
import studentClassroomVue from '@/views/userStudent/classroom.vue'
import studentFacultyVue from '@/views/userStudent/faculty.vue'
import studentLocationVue from '@/views/userStudent/location.vue'
import studentTeacherVue from '@/views/userStudent/teacher.vue'
import studentInfoVue from '@/views/userStudent/info.vue'
import studentCurriculumViewVue from '@/views/userStudent/curriculumView.vue'
import studentScoreVue from '@/views/userStudent/StudentScore.vue'

//定义路由关系
const routes = [
    { path: '/login', component: LoginVue },
    { path: '/user/login', component: LoginVue },
    {
        path: '/', 
        component: LayoutVue,
        redirect: '/course/list', 
        children: [
            //课程
            { path: '/course/list', component: CourseVue },
            //班级
            { path: '/classAll/list', component: ClassAllVue },
            //教室
            { path: '/classRoom/list', component: classRoomVue },
            //院系
            { path: '/faculty/list', component: facultyVue },
            //教学楼
            { path: '/location/list', component: locationVue },
            //学生
            { path: '/student/list', component: studentVue },
            //教师
            { path: '/teacher/list', component: teacherVue },
            //成绩
            { path: '/score/list', component: ScoreVue },
            //课程表
            { path: '/curriculum/list', component: curriculumVue },
            //课程表详情
            { path: '/curriculum/curriculumView', component: curriculumViewVue },
            //用户
            { path: '/user/list', component: userVue },
            //用户修改信息
            { path: '/user/info', component: UserInfoVue }
        ]
    },
    {
        path: '/user', 
        component: studentLayoutVue, 
        redirect: '/user/ai',
        children: [
            { path: 'ai', component: aiVue },
            { path: 'student-info', component: studentInfoVue },
            { path: 'course', component: studentCourseVue },
            { path: 'classroom', component: studentClassroomVue },
            { path: 'faculty', component: studentFacultyVue },
            { path: 'location', component: studentLocationVue },
            { path: 'teacher', component: studentTeacherVue },
            { path: 'curriculum', component: studentCurriculumViewVue },
            { path: 'schedule', component: ScheduleVue },
            {
                path: 'score',
                name: 'studentScore',
                component: studentScoreVue,
            }
        ]
    }
]

//创建路由器
const router = createRouter({
    history: createWebHistory(),
    routes: routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
    const userInfoStore = useUserInfoStore();
    const userType = userInfoStore.info?.type;

    // 如果是登录页面，直接放行
    if (to.path === '/login' || to.path === '/user/login') {
        next();
        return;
    }

    // 如果没有用户信息，跳转到登录页
    if (!userInfoStore.info) {
        next('/login');
        return;
    }

    // 根据用户类型限制访问
    if (userType === 0) { // 学生
        if (!to.path.startsWith('/user')) {
            next('/user/student-info');
            return;
        }
    } else if (userType === 1 || userType === 2) { // 管理员
        if (to.path.startsWith('/user') && to.path !== '/user/info' && to.path !== '/user/list') {
            next('/');
            return;
        }
        // 只有管理员可以访问学生管理页面
        if (to.path === '/student/list' && userType !== 2) {
            next('/');
            return;
        }
    }

    next();
});

//导出路由
export default router
