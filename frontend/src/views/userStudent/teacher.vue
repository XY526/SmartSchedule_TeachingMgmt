<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { teacherGetAllService } from '@/api/teacher.js';

// 教师列表数据
const teacherList = ref([]);

// 分页数据
const pageNum = ref(1);
const total = ref(0);
const pageSize = ref(10);

// 搜索条件
const teacherName = ref('');
const facultyName = ref('');

// 获取教师列表
const getTeacherList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params: { 
        teacherName: teacherName.value,
        facultyName: facultyName.value 
      }
    };
    const result = await teacherGetAllService(params);
    total.value = result.data.total;
    teacherList.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取教师列表失败');
    console.error(error);
  }
};

// 重置搜索
const resetSearch = () => {
  teacherName.value = '';
  facultyName.value = '';
  getTeacherList();
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  getTeacherList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  getTeacherList();
};

// 在组件挂载时获取列表数据
onMounted(() => {
  getTeacherList();
});
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>教师信息</span>
      </div>
    </template>
    
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="教师姓名">
        <el-input v-model="teacherName" placeholder="请输入教师姓名"></el-input>
      </el-form-item>
      <el-form-item label="院系名称">
        <el-input v-model="facultyName" placeholder="请输入院系名称"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getTeacherList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 教师列表 -->
    <el-table :data="teacherList" style="width: 100%">
      <el-table-column label="教师编号" prop="teacherId" align="center"></el-table-column>
      <el-table-column label="教师姓名" prop="teacherName" align="center"></el-table-column>
      <el-table-column label="院系" prop="facultyName" align="center"></el-table-column>
      <el-table-column label="教师专业" prop="speciality" align="center"></el-table-column>
      <el-table-column label="职称" prop="title" align="center"></el-table-column>
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