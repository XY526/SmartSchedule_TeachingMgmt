<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { classRoomGetAllService } from '@/api/classRoom.js';

// 教室列表数据
const classroomList = ref([]);

// 分页数据
const pageNum = ref(1);
const total = ref(0);
const pageSize = ref(10);

// 搜索条件
const locationName = ref('');
const isMultimedia = ref('');
const state = ref('');

// 获取教室列表
const getClassroomList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params: { 
        locationName: locationName.value,
        isMultimedia: isMultimedia.value,
        state: state.value
      }
    };
    const result = await classRoomGetAllService(params);
    total.value = result.data.total;
    classroomList.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取教室列表失败');
    console.error(error);
  }
};

// 重置搜索
const resetSearch = () => {
  locationName.value = '';
  isMultimedia.value = '';
  state.value = '';
  getClassroomList();
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  getClassroomList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  getClassroomList();
};

// 在组件挂载时获取列表数据
onMounted(() => {
  getClassroomList();
});
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>教室信息</span>
      </div>
    </template>
    
    <!-- 搜索表单 -->
    <el-form inline>

      <el-form-item label="教学楼">
        <el-input v-model="locationName" placeholder="请输入教学楼名称"></el-input>
      </el-form-item>
      <el-form-item label="多媒体教室">
        <el-select v-model="isMultimedia" placeholder="请选择" clearable>
          <el-option label="是" value="1"></el-option>
          <el-option label="否" value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="使用状态">
        <el-select v-model="state" placeholder="请选择" clearable>
          <el-option label="未使用" value="0"></el-option>
          <el-option label="已使用" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getClassroomList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 教室列表 -->
    <el-table :data="classroomList" style="width: 100%">
      <el-table-column label="教室编号" prop="classRoomId" align="center"></el-table-column>
      <el-table-column label="教学楼" prop="locationName" align="center"></el-table-column>
      <el-table-column label="容量" prop="capacity" align="center"></el-table-column>
      <el-table-column label="多媒体教室" align="center">
        <template #default="{ row }">
          {{ row.isMultimedia > 0 ? '是' : '否' }}
        </template>
      </el-table-column>
      <el-table-column label="使用状态" align="center">
        <template #default="{ row }">
          {{ row.state === 0 ? '未使用' : '已使用' }}
        </template>
      </el-table-column>
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