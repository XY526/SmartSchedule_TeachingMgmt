<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { facultyGetAllService } from '@/api/faculty.js';

// 院系列表数据
const facultyList = ref([]);

// 分页数据
const pageNum = ref(1);
const total = ref(0);
const pageSize = ref(10);

// 搜索条件
const facultyName = ref('');

// 获取院系列表
const getFacultyList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params: { 
        facultyName: facultyName.value
      }
    };
    const result = await facultyGetAllService(params);
    total.value = result.data.total;
    facultyList.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取院系列表失败');
    console.error(error);
  }
};

// 重置搜索
const resetSearch = () => {
  facultyName.value = '';
  getFacultyList();
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  getFacultyList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  getFacultyList();
};

// 在组件挂载时获取列表数据
onMounted(() => {
  getFacultyList();
});
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>院系信息</span>
      </div>
    </template>
    
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="院系名称">
        <el-input v-model="facultyName" placeholder="请输入院系名称"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getFacultyList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 院系列表 -->
    <el-table :data="facultyList" style="width: 100%">
      <el-table-column label="院系编号" prop="facultyId" align="center"></el-table-column>
      <el-table-column label="院系名称" prop="facultyName" align="center"></el-table-column>
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