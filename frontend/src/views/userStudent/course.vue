<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { courseGetByClassIdService } from '@/api/course.js';
import useUserInfoStore from '@/stores/userInfo.js';

// 课程列表数据
const courseList = ref([]);
const userStore = useUserInfoStore();

// 分页数据
const pageNum = ref(1);
const total = ref(0);
const pageSize = ref(10);

// 搜索条件
const courseName = ref('');

// 获取课程列表
const getCourseList = async () => {
  try {
    // 检查用户信息是否存在
    if (!userStore.info) {
      ElMessage.error('未获取到用户信息，请重新登录');
      return;
    }

    // 获取学生信息中的班级ID
    const classId = userStore.info.classId;
    if (!classId) {
      ElMessage.error('未获取到班级信息，请重新登录');
      return;
    }

    const result = await courseGetByClassIdService(classId);
    if (result.code === 0) {
      // 在本地进行搜索过滤
      let filteredData = result.data;
      if (courseName.value) {
        filteredData = filteredData.filter(course => 
          course.courseName.toLowerCase().includes(courseName.value.toLowerCase())
        );
      }
      
      
      // 本地分页处理
      total.value = filteredData.length;
      const start = (pageNum.value - 1) * pageSize.value;
      const end = start + pageSize.value;
      courseList.value = filteredData.slice(start, end);
    } else {
      ElMessage.error(result.message || '获取课程列表失败');
    }
  } catch (error) {
    ElMessage.error('获取课程列表失败');
    console.error('错误详情:', error);
  }
};

// 重置搜索
const resetSearch = () => {
  courseName.value = '';
  getCourseList();
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  getCourseList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  getCourseList();
};

// 在组件挂载时获取列表数据
onMounted(() => {
  getCourseList();
});
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>课程信息</span>
      </div>
    </template>
    
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="课程名称">
        <el-input v-model="courseName" placeholder="请输入课程名称"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="getCourseList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 课程列表 -->
    <el-table :data="courseList" style="width: 100%">
      <el-table-column label="课程编号" prop="courseId" align="center"></el-table-column>
      <el-table-column label="课程名称" prop="courseName" align="center"></el-table-column>
      <el-table-column label="学分" prop="credit" align="center"></el-table-column>
      <el-table-column label="课时" prop="hour" align="center"></el-table-column>
    </el-table>

    <!-- 分页条 -->
    <el-pagination
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :page-sizes="[10, 20, 30, 50]"
      layout="total, sizes, prev, pager, next"
      :total="total"
      @size-change="onSizeChange"
      @current-change="onCurrentChange"
      style="margin-top: 20px; justify-content: flex-end"
    />
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
  }
}
</style> 