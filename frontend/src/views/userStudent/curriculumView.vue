<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { curriculumGetAllService } from '@/api/curriculum.js';
import { courseGetAllService } from '@/api/course.js';
import { classGetAllService } from '@/api/classAll.js';
import { teacherGetAllService } from '@/api/teacher.js';
import { locationGetAllService } from '@/api/location.js';
import { classRoomGetAllService } from '@/api/classRoom.js';
import useUserInfoStore from '@/stores/userInfo.js';
import dayjs from 'dayjs';
import { userInfoService } from '@/api/user.js';

// 获取用户信息store
const userInfoStore = useUserInfoStore();

// 课程表数据
const scheduleData = ref([]);
// 关联数据
const courseList = ref([]);
const classList = ref([]);
const teacherList = ref([]);
const locationList = ref([]);
const classroomList = ref([]);
// 当前学年和学期
const currentYear = ref('2024'); // 默认设置为2024学年
const currentTerm = ref('1');
const currentWeek = ref(""); // 默认不发送周次信息

// 周次选项
const weekOptions = ref([
  { value: '', label: '全部' },
  ...Array.from({ length: 20 }, (_, i) => ({
    value: String(i + 1),
    label: `第${i + 1}周`
  }))
]);

// 学年选项
const yearOptions = ref([]);
// 节次配置
const sections = [
  { label: '第一节', time: '8:00-9:40', value: '1' },
  { label: '第二节', time: '10:00-11:40', value: '2' },
  { label: '第三节', time: '14:00-15:40', value: '3' },
  { label: '第四节', time: '16:00-17:40', value: '4' },
  { label: '第五节', time: '19:00-20:40', value: '5' }
];
// 星期数组
const weekdays = ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日'];

// 处理后的课程表数据
const processedSchedule = ref(Array(5).fill(null).map(() => Array(7).fill(null)));

// 获取所有关联数据
const getAllRelatedData = async () => {
  try {
    const [courses, classes, teachers, locations, classrooms] = await Promise.all([
      courseGetAllService({ pageSize: 999, pageNum: 1 }),
      classGetAllService({ pageSize: 999, pageNum: 1 }),
      teacherGetAllService({ pageSize: 999, pageNum: 1 }),
      locationGetAllService({ pageSize: 999, pageNum: 1 }),
      classRoomGetAllService({ pageSize: 999, pageNum: 1 })
    ]);

    courseList.value = courses.data.records;
    classList.value = classes.data.records;
    teacherList.value = teachers.data.records;
    locationList.value = locations.data.records;
    classroomList.value = classrooms.data.records;
  } catch (error) {
    console.error('获取关联数据失败:', error);
  }
};

// 获取名称的辅助函数
const getCourseName = (courseId) => {
  const course = courseList.value.find(c => c.courseId === courseId);
  return course ? course.courseName : '未知课程';
};

const getClassName = (classId) => {
  const class_ = classList.value.find(c => c.classId === classId);
  return class_ ? class_.className : '未知班级';
};

const getTeacherName = (teacherId) => {
  const teacher = teacherList.value.find(t => t.teacherId === teacherId);
  return teacher ? teacher.teacherName : '未知教师';
};

const getLocationName = (locationId) => {
  const location = locationList.value.find(l => l.locationId === locationId);
  return location ? location.locationName : '未知地点';
};

const getClassroomName = (classRoomId) => {
  const classroom = classroomList.value.find(c => c.classRoomId === classRoomId);
  return classroom ? classroom.roomName : '未知教室';
};

// 获取课程表数据
const getScheduleData = async () => {
  try {
    // 确保用户信息存在
    if (!userInfoStore.info || !userInfoStore.info.id) {
      // 尝试重新获取用户信息
      const userInfoResult = await userInfoService();
      if (userInfoResult.code === 0 && userInfoResult.data) {
        userInfoStore.setInfo(userInfoResult.data);
      } else {
        ElMessage.error('未获取到用户信息，请重新登录');
        return;
      }
    }

    // 检查班级信息
    if (!userInfoStore.info.classId) {
      ElMessage.error('未获取到班级信息，请联系管理员');
      return;
    }

    const params = {
      pageNum: 1,
      pageSize: 999,
      params: {
        schoolYear: currentYear.value,
        term: currentTerm.value ? Number(currentTerm.value) : null,
        classId: Number(userInfoStore.info.classId),
        week: currentWeek.value === '' ? null : Number(currentWeek.value)
      }
    };

    console.log('组件发送的参数:', params);

    const result = await curriculumGetAllService(params);
    if (result.data) {
      scheduleData.value = result.data.records;
      console.log('获取到的数据:', result.data.records);

      // 提取不重复的学年并更新 yearOptions
      const years = [...new Set(scheduleData.value.map(item => item.schoolYear))].filter(Boolean).sort();
      console.log('提取的学年:', years);
      
      yearOptions.value = years.map(year => ({ label: year, value: year }));
      console.log('学年选项:', yearOptions.value);

      // 如果当前选中的学年不在选项中，选择第一个学年
      if (yearOptions.value.length > 0 && !yearOptions.value.some(option => option.value === currentYear.value)) {
        currentYear.value = yearOptions.value[0].value;
        // 使用新选中的学年重新获取数据
        getScheduleData();
        return;
      }

      processScheduleData();
    } else {
      scheduleData.value = [];
      yearOptions.value = [];
      currentYear.value = '2024'; // 重置为2024
      processScheduleData();
    }
  } catch (error) {
    ElMessage.error('获取课程表数据失败');
    console.error('获取课程表数据失败:', error);
    scheduleData.value = [];
    yearOptions.value = [];
    currentYear.value = '2024'; // 重置为2024
    processScheduleData();
  }
};

// 处理课程表数据
const processScheduleData = () => {
  processedSchedule.value = Array(5).fill(null).map(() => Array(7).fill(null));
  
  scheduleData.value.forEach(course => {
    try {
      const sectionIndex = parseInt(course.section) - 1;
      const weekdayIndex = parseInt(course.weekday) - 1;
      
      if (sectionIndex >= 0 && sectionIndex < 5 && weekdayIndex >= 0 && weekdayIndex < 7) {
        processedSchedule.value[sectionIndex][weekdayIndex] = {
          courseName: getCourseName(course.courseId),
          className: getClassName(course.classId),
          teacherName: getTeacherName(course.teacherId),
          locationName: getLocationName(course.locationId),
          classRoomName: getClassroomName(course.classRoomId),
          weekRange: `${course.startWeek}-${course.endWeek}周`
        };
      }
    } catch (error) {
      console.error('处理课程数据失败:', error, course);
    }
  });
};

onMounted(async () => {
  currentTerm.value = '1';
  currentWeek.value = ''; // 默认不发送周次信息
  currentYear.value = '2024'; // 默认设置为2024学年

  // 先获取所有关联数据
  await getAllRelatedData();
  // 再获取课程表数据
  await getScheduleData();
});
</script>

<template>
  <el-card class="schedule-container">
    <template #header>
      <div class="header">
        <div class="title">
          <el-icon><Calendar /></el-icon>
          <span>我的课程表</span>
        </div>
        <div class="filter">
          <el-select v-model="currentYear" placeholder="选择学年" @change="getScheduleData" size="large">
            <el-option
              v-for="item in yearOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
          <el-select v-model="currentTerm" placeholder="选择学期" @change="getScheduleData" size="large">
            <el-option label="第一学期" value="1" />
            <el-option label="第二学期" value="2" />
          </el-select>
           <el-select v-model="currentWeek" placeholder="选择周次" @change="getScheduleData" size="large">
            <el-option
              v-for="item in weekOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
      </div>
    </template>

    <div class="schedule-table">
      <table>
        <thead>
          <tr>
            <th class="time-column">时间</th>
            <th v-for="(day, index) in weekdays" :key="index">{{ day }}</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(section, sIndex) in sections" :key="section.value">
            <td class="time-column">
              <div class="section-info">
                <div>{{ section.label }}</div>
                <div class="time">{{ section.time }}</div>
              </div>
            </td>
            <td 
              v-for="(day, dIndex) in weekdays" 
              :key="dIndex"
              :class="{ 'has-course': processedSchedule[sIndex][dIndex] }"
            >
              <template v-if="processedSchedule[sIndex][dIndex]">
                <div class="course-info">
                  <div class="course-name">
                    {{ processedSchedule[sIndex][dIndex].courseName }}
                  </div>
                  <div class="course-detail">
                    {{ processedSchedule[sIndex][dIndex].teacherName }}
                  </div>
                  <div class="course-detail">
                    {{ processedSchedule[sIndex][dIndex].className }}
                  </div>
                  <div class="course-detail">
                    {{ processedSchedule[sIndex][dIndex].locationName }}
                    {{ processedSchedule[sIndex][dIndex].classRoomName }}
                  </div>
                  <div class="course-detail">
                    {{ processedSchedule[sIndex][dIndex].weekRange }}
                  </div>
                </div>
              </template>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </el-card>
</template>

<style lang="scss" scoped>
.schedule-container {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .filter {
      display: flex;
      gap: 10px;
    }
  }

  .schedule-table {
    margin-top: 20px;
    overflow-x: auto;

    table {
      width: 100%;
      border-collapse: collapse;
      table-layout: fixed;
      
      th, td {
        border: 1px solid #dcdfe6;
        text-align: center;
        width: 160px;
        height: 120px;
        vertical-align: top;
        padding: 8px;
        box-sizing: border-box;
      }

      th {
        background-color: #f5f7fa;
        height: 40px;
      }

      .time-column {
        width: 120px;
        background-color: #f5f7fa;
        
        .section-info {
          display: flex;
          flex-direction: column;
          justify-content: center;
          height: 100%;
          
          .time {
            font-size: 12px;
            color: #666;
            margin-top: 4px;
          }
        }
      }

      .has-course {
        background-color: #f0f9eb;
      }

      .course-info {
        height: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        padding: 4px;
        overflow: hidden;

        .course-name {
          font-weight: bold;
          color: #409EFF;
          margin-bottom: 4px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }

        .course-detail {
          font-size: 12px;
          color: #666;
          margin-top: 2px;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
        }
      }
    }
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    padding: 10px 0;
  }
}
</style> 