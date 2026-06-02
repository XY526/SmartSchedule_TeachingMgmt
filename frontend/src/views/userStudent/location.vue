<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { locationGetAllService } from '@/api/location.js';

// 教学楼列表数据
const locationList = ref([]);

// 分页数据
const pageNum = ref(1);
const total = ref(0);
const pageSize = ref(10);

// 搜索条件
const locationName = ref('');

// 获取教学楼列表
const getLocationList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params: { 
        locationName: locationName.value
      }
    };
    const result = await locationGetAllService(params);
    total.value = result.data.total;
    locationList.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取教学楼列表失败');
    console.error(error);
  }
};

// 重置搜索
const resetSearch = () => {
  locationName.value = '';
  getLocationList();
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  getLocationList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  getLocationList();
};

// 在组件挂载时获取列表数据
onMounted(() => {
  getLocationList();
});
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>教学楼信息</span>
      </div>
    </template>
    
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="教学楼名称">
        <el-input v-model="locationName" placeholder="请输入教学楼名称"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getLocationList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 教学楼列表 -->
    <el-table :data="locationList" style="width: 100%">
      <el-table-column label="教学楼编号" prop="locationId" align="center"></el-table-column>
      <el-table-column label="教学楼名称" prop="locationName" align="center"></el-table-column>
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