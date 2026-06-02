<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { curriculumGetAllService } from '@/api/curriculum.js';
import { courseGetAllService } from '@/api/course.js';
import { classGetAllService } from '@/api/classAll.js';
import { teacherGetAllService } from '@/api/teacher.js';
import { locationGetAllService } from '@/api/location.js';
import { classRoomGetAllService } from '@/api/classRoom.js';

// 课程表数据
const scheduleData = ref([]);
// 关联数据
const courseList = ref([]);
const classList = ref([]);
const teacherList = ref([]);
const locationList = ref([]);
const classroomList = ref([]);
// 当前学年和学期
const currentYear = ref('2024');
const currentTerm = ref('1');
const currentWeek = ref(''); // 默认不发送周次信息
// 学年选项
const yearOptions = ref([]); // 添加学年选项 ref
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

// 分页数据 (只保留 pageSize)
// const pageNum = ref(1); // Removed
// const total = ref(0); // Removed
const pageSize = ref(999); // Fetch up to 999 items

// 周次选项
const weekOptions = [
  { value: '', label: '全部' }, // 添加全部选项
  ...Array.from({ length: 20 }, (_, i) => ({
    value: String(i + 1),
    label: `第${i + 1}周`
  }))
];

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
  return classroom ? classroom.classRoomName : '未知教室';
};

// 处理课程表数据 (Moved before getScheduleData)
const processScheduleData = () => {
  processedSchedule.value = Array(5).fill(null).map(() => Array(7).fill(null));

  scheduleData.value.forEach(course => {
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
    } else {
        console.warn('Invalid section or weekday for course:', course);
    }
  });
};

// 获取课程表数据
const getScheduleData = async () => {
  try {
    const params = {
      pageNum: 1, // Fetch from the first page
      pageSize: pageSize.value, // Use the set pageSize (999)
      params: {
        schoolYear: currentYear.value,
        term: currentTerm.value,
        week: currentWeek.value === '' ? null : Number(currentWeek.value) // Send null for '全部', otherwise convert to Number
      }
    };

    const result = await curriculumGetAllService(params);
    if (result.data) {
      scheduleData.value = result.data.records;
      // total.value = result.data.total; // Pagination removed, no need for total

      // 提取不重复的学年并更新 yearOptions
      const years = [...new Set(scheduleData.value.map(item => item.schoolYear))].sort();
      yearOptions.value = years.map(year => ({ label: year, value: year }));

      // 如果当前选中的学年不在新的选项中，则默认选中第一个学年
      if (yearOptions.value.length > 0 && !yearOptions.value.some(option => option.value === currentYear.value)) {
          currentYear.value = yearOptions.value[0].value;
      } else if (yearOptions.value.length === 0) {
           currentYear.value = ''; // 如果没有学年数据，清空选中项
      }


      processScheduleData();
    } else {
        scheduleData.value = [];
        // total.value = 0; // Pagination removed
        yearOptions.value = [];
        currentYear.value = '';
        processScheduleData();
    }
  } catch (error) {
    ElMessage.error('获取课程表数据失败');
    console.error(error);
    scheduleData.value = [];
    // total.value = 0; // Pagination removed
    yearOptions.value = [];
    currentYear.value = '';
    processScheduleData();
  }
};

// Pagination removed, no need for onSizeChange and onCurrentChange
// const onSizeChange = (size) => {
//   pageSize.value = size;
//   getScheduleData();
// };

// const onCurrentChange = (num) => {
//   pageNum.value = num;
//   getScheduleData();
// };

onMounted(async () => {
  currentTerm.value = '1';
  currentWeek.value = ''; // 默认显示全部周次

  // 先获取所有关联数据
  await getAllRelatedData();
  // 再获取课程表数据，此时 getScheduleData 会根据返回数据填充 yearOptions 并设置 default year
  await getScheduleData();
});
</script>

<template>
  <el-card class="schedule-container">
    <template #header>
      <div class="header">
        <div class="title">
          <el-icon><Calendar /></el-icon>
          <span>课程表</span>
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

    <!-- Pagination component removed -->
    <!--
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="onSizeChange"
        @current-change="onCurrentChange"
        background
      />
    </div>
    -->
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

      th, td {
        border: 1px solid #dcdfe6;
        text-align: center;
        min-width: 150px;
        height: 120px;
        vertical-align: top;
      }

      th {
        background-color: #f5f7fa;
        height: 40px;
        padding: 8px;
      }

      .time-column {
        width: 100px;
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

        .course-name {
          font-weight: bold;
          color: #409EFF;
          margin-bottom: 4px;
        }

        .course-detail {
          font-size: 12px;
          color: #666;
          margin-top: 2px;
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