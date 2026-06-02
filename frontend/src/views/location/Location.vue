<script setup>
import { Edit, Delete } from '@element-plus/icons-vue';
import { ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { locationGetAllService, locationAddService, locationUpdateService, locationDeleteService } from '@/api/location.js';

// 教学楼列表数据
const locations = ref([]);

// 分页数据
const pageNum = ref(1); // 当前页
const total = ref(0); // 总条数
const pageSize = ref(5); // 每页条数

// 搜索条件
const locationName = ref('');

// 抽屉控制
const visibleDrawer = ref(false); // 添加抽屉
const visibleDrawer1 = ref(false); // 编辑抽屉

// 表单数据模型
const locationModel = ref({
  locationName: '',

});

// 表单校验规则
const rules = {
  locationName: [
    { required: true, message: '请输入教学楼名称', trigger: 'blur' },
    { min: 2, max: 50, message: '教学楼名称长度在 2 到 50 个字符之间', trigger: 'blur' },
  ],
};

const locationList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params:{locationName: locationName.value },
    };
    console.log(locationName.value);
    // console.log('请求参数:', params); // 打印请求参数
    const result = await locationGetAllService(params);
    // console.log('API 返回结果:', result); // 打印 API 返回的完整结果
    total.value = result.data.total;
    locations.value = result.data.records;
    // ElMessage.success('获取教学楼列表成功');
  } catch (error) {
    ElMessage.error('获取教学楼列表失败，请检查网络或联系管理员');
    console.error(error);
  }
};
// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  locationList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  locationList();
};

// 编辑教学楼
const handleEdit = (row) => {
  locationModel.value = { ...row }; // 填充表单数据
  visibleDrawer1.value = true; // 打开编辑抽屉
};

// 保存编辑后的教学楼
const saveEditedCourse = async () => {
  try {
    // 触发表单校验
    await formRef1.value.validate();
    const result = await locationUpdateService(locationModel.value);
    ElMessage.success(result.msg ? result.msg : '编辑成功');
    visibleDrawer1.value = false;
    clearCourseModel();
    locationList();
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('编辑教学楼失败，请检查网络或联系管理员');
      console.error(error);
    }
    // 校验失败时，不执行后续逻辑
  }
};

// 删除教学楼
const handleDelete = async (row) => {
  try {
    const confirm = await ElMessageBox.confirm(
      '确定要删除该教学楼吗？',
      '温馨提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    if (confirm) {
      const result = await locationDeleteService(row.locationId);
      ElMessage.success(result.msg ? result.msg : '删除成功');
      locationList();
    }
  } catch (error) {
    ElMessage.error('删除教学楼失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 添加教学楼
const addCourse = async () => {
  try {
    // 触发表单校验
    await formRef.value.validate();
    const result = await locationAddService(locationModel.value);
    ElMessage.success(result.msg ? result.msg : '添加成功');
    visibleDrawer.value = false;
    clearCourseModel();
    locationList();
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('添加教学楼失败，请检查网络或联系管理员');
      console.error(error);
    }
    // 校验失败时，不执行后续逻辑
  }
};

// 清空表单数据
const clearCourseModel = () => {
  locationModel.value = {
    locationName: '',
    hour: '',
    credit: '',
  };
};

// 表单引用
const formRef = ref(null); // 添加表单引用
const formRef1 = ref(null); // 编辑表单引用

// 初始化加载教学楼列表
locationList();
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>教学楼管理</span>
        <div class="extra">
          <el-button type="primary" @click="visibleDrawer = true; clearCourseModel()">添加教学楼</el-button>
        </div>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="教学楼名称：">
        <el-input v-model="locationName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="locationList">搜索</el-button>
        <el-button @click="locationName = ''; locationList()">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 教学楼列表 -->
    <el-table :data="locations" style="width: 100%">
      <el-table-column label="序号" width="100" prop="locationId" align="center"></el-table-column>
      <el-table-column label="教学楼名称" prop="locationName" align="center"></el-table-column>

      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-button :icon="Edit" circle plain type="primary" @click="handleEdit(row)"></el-button>
          <el-button :icon="Delete" circle plain type="danger" @click="handleDelete(row)"></el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty description="没有数据" />
      </template>
    </el-table>
    <!-- 分页条 -->
    <el-pagination
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :page-sizes="[3, 5, 10, 15]"
      layout="jumper, total, sizes, prev, pager, next"
      background
      :total="total"
      @size-change="onSizeChange"
      @current-change="onCurrentChange"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <!-- 添加教学楼抽屉 -->
    <el-drawer v-model="visibleDrawer" title="添加教学楼" direction="rtl" size="50%">
      <el-form ref="formRef" :model="locationModel" label-width="100px" :rules="rules">
        <el-form-item label="教学楼名称" prop="locationName">
          <el-input v-model="locationModel.locationName" placeholder="请输入教学楼名称"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="addCourse">确定</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 编辑教学楼抽屉 -->
    <el-drawer v-model="visibleDrawer1" title="编辑教学楼" direction="rtl" size="50%">
      <el-form ref="formRef1" :model="locationModel" label-width="100px" :rules="rules">
        <el-form-item label="教学楼名称" prop="locationName">
          <el-input v-model="locationModel.locationName" placeholder="请输入教学楼名称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveEditedCourse">确定</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
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