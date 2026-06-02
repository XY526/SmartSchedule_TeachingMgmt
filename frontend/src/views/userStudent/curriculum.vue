<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { curriculumGetAllService } from '@/api/curriculum.js';

// 课程列表数据
const curriculumList = ref([]);

// 分页数据
const pageNum = ref(1);
const total = ref(0);
const pageSize = ref(10);

// 搜索条件
const curriculumName = ref('');
const facultyName = ref('');

// 获取课程列表
const getCurriculumList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params: { 
        curriculumName: curriculumName.value,
        facultyName: facultyName.value
      }
    };
    const result = await curriculumGetAllService(params);
    total.value = result.data.total;
    curriculumList.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取课程列表失败');
    console.error(error);
  }
};

// 重置搜索
const resetSearch = () => {
  curriculumName.value = '';
  facultyName.value = '';
  getCurriculumList();
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  getCurriculumList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  getCurriculumList();
};

// 在组件挂载时获取列表数据
onMounted(() => {
  getCurriculumList();
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
        <el-input v-model="curriculumName" placeholder="请输入课程名称"></el-input>
      </el-form-item>
      <el-form-item label="院系名称">
        <el-input v-model="facultyName" placeholder="请输入院系名称"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getCurriculumList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 课程列表 -->
    <el-table :data="curriculumList" style="width: 100%">
      <el-table-column label="课程编号" prop="curriculumId" align="center"></el-table-column>
      <el-table-column label="课程名称" prop="curriculumName" align="center"></el-table-column>
      <el-table-column label="院系" prop="facultyName" align="center"></el-table-column>
      <el-table-column label="学分" prop="credit" align="center"></el-table-column>
      <el-table-column label="学时" prop="period" align="center"></el-table-column>
      <el-table-column label="备注" prop="remark" align="center"></el-table-column>
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