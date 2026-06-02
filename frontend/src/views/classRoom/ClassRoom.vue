<script setup>
import { Edit, Delete } from '@element-plus/icons-vue';
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { classRoomGetAllService, classRoomAddService, classRoomEditService, classRoomDeleteService } from '@/api/classRoom.js';
import { locationGetAllService } from '@/api/location.js';

// 教室列表数据
const classRoomAll = ref([]);

// 分页数据
const pageNum = ref(1); // 当前页
const total = ref(0); // 总条数
const pageSize = ref(5); // 每页条数

// 搜索条件
const isMultimedia = ref();
const state = ref();
const locationName = ref(); // 修改为locationName

// 抽屉控制
const visibleDrawer = ref(false); // 添加抽屉
const visibleDrawer1 = ref(false); // 编辑抽屉

// 表单数据模型
const classRoomModel = ref({
  capacity: '',
  isMultimedia: 0,
  state: 0,
  locationId: '',
});

// 表单校验规则
const rules = {
  capacity: [
    { required: true, message: '请输入教室容量', trigger: 'blur' },
    { min: 1, max: 2, message: '教室容量必须在1-99之间', trigger: 'blur' },
  ],
  locationId: [
    { required: true, message: '请输入教室地点ID', trigger: 'blur' },
    { min: 1, max: 2, message: '教室地点ID必须在1-99之间', trigger: 'blur' },
  ],
};

// 添加位置列表数据
const locationList = ref([]);

// 获取所有位置列表
const getAllLocations = async () => {
  try {
    const result = await locationGetAllService({
      pageSize: 999,
      pageNum: 1,
    });
    locationList.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取位置列表失败');
    console.error(error);
  }
};

// 获取教室列表
const classRoomAllList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params:{
        isMultimedia: isMultimedia.value !== null ? isMultimedia.value : null,
        state: state.value !== null ? state.value : null,
        locationName: locationName.value || null // 使用locationName
      }
    };
    const result = await classRoomGetAllService(params);
    total.value = result.data.total;
    classRoomAll.value = result.data.records;
    // ElMessage.success('获取教室列表成功');
  } catch (error) {
    ElMessage.error('获取教室列表失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  classRoomAllList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  classRoomAllList();
};

// 编辑教室
const handleEdit = (row) => {
  classRoomModel.value = { ...row }; // 填充表单数据
  visibleDrawer1.value = true; // 打开编辑抽屉
};

// 保存编辑后的教室
const saveEditedClassRoom = async () => {
  try {
    const result = await classRoomEditService(classRoomModel.value);
    ElMessage.success(result.msg ? result.msg : '编辑成功');
    visibleDrawer1.value = false;
    clearClassRoomModel();
    classRoomAllList();
  } catch (error) {
    ElMessage.error('编辑教室失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 删除教室
const handleDelete = async (row) => {
  try {
    const confirm = window.confirm('确定要删除该教室吗？');
    if (confirm) {
      const result = await classRoomDeleteService(row.classRoomId);
      ElMessage.success(result.msg ? result.msg : '删除成功');
      classRoomAllList();
    }
  } catch (error) {
    ElMessage.error('删除教室失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 添加教室
const addClassRoom = async () => {
  try {
    const result = await classRoomAddService(classRoomModel.value);
    ElMessage.success(result.msg ? result.msg : '添加成功');
    visibleDrawer.value = false;
    clearClassRoomModel();
    classRoomAllList();
  } catch (error) {
    ElMessage.error('添加教室失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 清空表单数据
const clearClassRoomModel = () => {
  classRoomModel.value = {
    capacity: '',
    isMultimedia: 0,
    state: 1,
    locationId: '',
  };
};

// 重置搜索条件
const resetSearch = () => {
  isMultimedia.value = '';
  state.value = '';
  locationName.value = ''; // 重置locationName
  classRoomAllList();
};

// 在组件挂载时获取位置列表
onMounted(() => {
  getAllLocations();
  classRoomAllList();
});
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>教室管理</span>
        <div class="extra">
          <el-button type="primary" @click="visibleDrawer = true">添加教室</el-button>
        </div>
      </div>
    </template>
      <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="教学楼">
        <el-select v-model="locationName" placeholder="请选择教学楼" clearable>
          <el-option
            v-for="location in locationList"
            :key="location.locationId"
            :label="location.locationName"
            :value="location.locationName"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="多媒体教室">
        <el-select placeholder="请选择是否为多媒体教室" v-model="isMultimedia">
          <el-option label="是" value="1"></el-option>
          <el-option label="否" value="0"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="使用状态：">
        <el-select placeholder="请选择" v-model="state">
          <el-option label="未使用" value="0"></el-option>
          <el-option label="已使用" value="1"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="classRoomAllList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 教室列表 -->
    <el-table :data="classRoomAll" style="width: 100%">
      <el-table-column label="序号" width="100" prop="classRoomId" align="center"></el-table-column>
      <el-table-column label="教室容量" prop="capacity" align="center"></el-table-column>
      <el-table-column label="是否为多媒体教室" align="center">
        <template #default="{ row }">
          {{ row.isMultimedia > 0 ? '是' : '否' }}
        </template>
      </el-table-column>
      <el-table-column label="教室使用状态" align="center">
        <template #default="{ row }">
          {{ row.state === 0 ? '未使用' : '已使用' }}
        </template>
      </el-table-column>
      <el-table-column label="教室位置" prop="locationName" align="center"></el-table-column>
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

    <!-- 添加教室抽屉 -->
    <el-drawer v-model="visibleDrawer" title="添加教室" direction="rtl" size="50%">
      <el-form :model="classRoomModel" label-width="100px" :rules="rules">
        <el-form-item label="教室容量" prop="capacity">
          <el-input v-model="classRoomModel.capacity" placeholder="请输入教室容量"></el-input>
        </el-form-item>
        <el-form-item label="是否为多媒体教室" prop="isMultimedia">
          <el-select v-model="classRoomModel.isMultimedia" placeholder="请选择">
            <el-option label="是" :value="1"></el-option>
            <el-option label="否" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="教室使用状态" prop="state">
          <el-select v-model="classRoomModel.state" placeholder="请选择">
            <el-option label="未使用" :value="0"></el-option>
            <el-option label="已使用" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="教室位置" prop="locationId">
          <el-select v-model="classRoomModel.locationId" placeholder="请选择位置" style="width: 100%">
            <el-option
              v-for="location in locationList"
              :key="location.locationId"
              :label="location.locationName"
              :value="location.locationId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addClassRoom">确定</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 编辑教室抽屉 -->
    <el-drawer v-model="visibleDrawer1" title="编辑教室" direction="rtl" size="50%">
      <el-form :model="classRoomModel" label-width="100px" :rules="rules">
        <el-form-item label="教室容量" prop="capacity" >
          <el-input v-model="classRoomModel.capacity" placeholder="请输入教室容量"></el-input>
        </el-form-item>
        <el-form-item label="是否为多媒体教室" prop="isMultimedia">
          <el-select v-model="classRoomModel.isMultimedia" placeholder="请选择" prop="locationId">
            <el-option label="是" :value="1"></el-option>
            <el-option label="否" :value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="教室使用状态">
          <el-select v-model="classRoomModel.state" placeholder="请选择" prop="state">
            <el-option label="未使用" :value="0"></el-option>
            <el-option label="已使用" :value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="教室位置" prop="locationId">
          <el-select v-model="classRoomModel.locationId" placeholder="请选择位置" style="width: 100%">
            <el-option
              v-for="location in locationList"
              :key="location.locationId"
              :label="location.locationName"
              :value="location.locationId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveEditedClassRoom">确定</el-button>
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