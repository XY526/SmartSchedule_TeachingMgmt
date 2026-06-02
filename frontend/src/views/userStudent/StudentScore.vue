<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { Calendar } from '@element-plus/icons-vue';
import useUserInfoStore from '@/stores/userInfo.js';
import { scoreGetByStudentService } from '@/api/score.js';
import { courseGetAllService } from '@/api/course.js';
import { teacherGetAllService } from '@/api/teacher.js';

const userStore = useUserInfoStore();
const userInfo = ref(userStore.info);

// 学生成绩数据
const studentScores = ref([]);
// 课程列表
const courses = ref([]);
// 教师列表
const teachers = ref([]);

// 分页相关
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 搜索条件
const searchCourseName = ref('');
const searchExamType = ref('');

// 考试类型选项
const examTypeOptions = [
  { value: 0, label: '期中考试' },
  { value: 1, label: '期末考试' },
  { value: 2, label: '平时测验' }
];

// 获取学生成绩列表
const getStudentScores = async () => {
  console.log('Current user info:', userStore.info);
  if (!userStore.info || !userStore.info.studentId) {
    console.error('User info or studentId is missing:', userStore.info);
    ElMessage.error('未能获取学生ID，请重新登录');
    return;
  }
  try {
    const result = await scoreGetByStudentService(userStore.info.studentId);
    console.log('Score result:', result);
    if (result.data) {
      studentScores.value = result.data;
      total.value = result.data.length || 0;
    }
  } catch (error) {
    ElMessage.error('获取学生成绩失败');
    console.error('Error fetching student scores:', error);
  }
};

// 获取课程列表
const getCourseList = async () => {
  try {
    const result = await courseGetAllService();
    if (result.data && result.data.records) {
      courses.value = result.data.records;
    } else {
      ElMessage.error('获取课程列表数据格式错误');
      console.error('Invalid course list data format:', result);
    }
  } catch (error) {
    ElMessage.error('获取课程列表失败');
    console.error('Error fetching course list:', error);
  }
};

// 获取教师列表
const getTeacherList = async () => {
  try {
    const result = await teacherGetAllService();
    if (result.data && result.data.records) {
      teachers.value = result.data.records;
    } else {
      ElMessage.error('获取教师列表数据格式错误');
      console.error('Invalid teacher list data format:', result);
    }
  } catch (error) {
    ElMessage.error('获取教师列表失败');
    console.error('Error fetching teacher list:', error);
  }
};

// 初始化数据
const initializeData = async () => {
  // 确保用户信息已经加载
  if (!userStore.info || !userStore.info.studentId) {
    // 尝试从localStorage恢复
    const storedInfo = localStorage.getItem('userInfo');
    if (storedInfo) {
      try {
        const parsedInfo = JSON.parse(storedInfo);
        userStore.setInfo(parsedInfo);
        console.log('Restored user info from localStorage:', parsedInfo);
      } catch (error) {
        console.error('Error parsing stored user info:', error);
      }
    }
  }

  console.log('Initializing data with user info:', userStore.info);
  if (userStore.info && userStore.info.studentId) {
    await Promise.all([
      getStudentScores(),
      getCourseList(),
      getTeacherList()
    ]);
  } else {
    console.error('User info not available during initialization');
    ElMessage.error('用户信息未加载，请重新登录');
  }
};

// 在组件挂载时初始化数据
onMounted(() => {
  initializeData();
});

// 根据ID获取课程名称
const getCourseNameById = (courseId) => {
  const course = courses.value.find(c => c.courseId === courseId);
  return course ? course.courseName : '未知课程';
};

// 根据ID获取教师名称
const getTeacherNameById = (teacherId) => {
  const teacher = teachers.value.find(t => t.teacherId === teacherId);
  return teacher ? teacher.teacherName : '未知教师';
};

// 根据考试类型value获取label
const getExamTypeLabel = (value) => {
  const option = examTypeOptions.find(opt => opt.value === value);
  return option ? option.label : '未知类型';
};

// 过滤后的成绩列表 (根据搜索条件在前端进行过滤)
const filteredScores = computed(() => {
  let filtered = studentScores.value;

  if (searchCourseName.value) {
    filtered = filtered.filter(score =>
      getCourseNameById(score.courseId).toLowerCase().includes(searchCourseName.value.toLowerCase())
    );
  }
  if (searchExamType.value !== '') {
    filtered = filtered.filter(score =>
      score.examType === searchExamType.value
    );
  }

  // 计算总数
  total.value = filtered.length;

  // 进行分页
  const start = (pageNum.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filtered.slice(start, end);
});

// 重置搜索
const resetSearch = () => {
  searchCourseName.value = '';
  searchExamType.value = '';
  pageNum.value = 1; // 重置到第一页
};

// 处理页码改变
const handleCurrentChange = (val) => {
  pageNum.value = val;
};

// 处理每页条数改变
const handleSizeChange = (val) => {
  pageSize.value = val;
  pageNum.value = 1; // 重置到第一页
};
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <div class="title">
          <el-icon><Calendar /></el-icon>
          <span>我的成绩</span>
        </div>
      </div>
    </template>

    <div class="score-section">

      <!-- 搜索表单 -->
      <el-form inline class="score-search-form">
        <el-form-item label="课程名称：">
          <el-input v-model="searchCourseName" placeholder="请输入课程名称关键词" clearable></el-input>
        </el-form-item>
        <el-form-item label="考试类型：">
          <el-select v-model="searchExamType" placeholder="请选择考试类型" clearable>
            <el-option
              v-for="item in examTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getStudentScores">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 成绩列表 -->
      <el-table :data="filteredScores" style="width: 100%">
        <el-table-column label="序号" width="80" align="center">
          <template #default="{ $index }">
            {{ (pageNum - 1) * pageSize + $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column label="课程名称" width="150" align="center">
          <template #default="{ row }">
            {{ getCourseNameById(row.courseId) }}
          </template>
        </el-table-column>
        <el-table-column label="教师名称" width="150" align="center">
          <template #default="{ row }">
            {{ getTeacherNameById(row.teacherId) }}
          </template>
        </el-table-column>
        <el-table-column label="考试类型" width="120" align="center">
          <template #default="{ row }">
            {{ getExamTypeLabel(row.examType) }}
          </template>
        </el-table-column>
        <el-table-column label="成绩" prop="score" width="100" align="center"></el-table-column>
        <el-table-column label="考试日期" prop="examDate" width="120" align="center"></el-table-column>
        <el-table-column label="备注" prop="remark" align="center"></el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" align="center"></el-table-column>
        <template #empty>
          <el-empty description="没有成绩数据" />
        </template>
      </el-table>

      <!-- 分页控件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </el-card>
</template>

<style lang="scss" scoped>
.page-container {
  min-height: 100%;
  box-sizing: border-box;

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 18px;
      font-weight: bold;
    }
  }

  .score-section {
    margin-top: 20px;
  }

  .score-search-form {
    margin-bottom: 15px;
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style> 