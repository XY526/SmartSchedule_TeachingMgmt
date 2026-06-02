<script setup>
import { Edit, Delete } from '@element-plus/icons-vue';
import { ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { scheduleGetByUserIdService, scheduleAddService, scheduleUpdateService, scheduleDeleteService } from '@/api/schedule.js';
import useUserInfoStore from '@/stores/userInfo.js';
import { userInfoService } from '@/api/user.js';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import timezone from 'dayjs/plugin/timezone';

// 配置 dayjs 插件
dayjs.extend(utc);
dayjs.extend(timezone);

// 获取用户信息store
const userInfoStore = useUserInfoStore();

// 日程列表数据
const schedules = ref([]);

// 分页数据
const pageNum = ref(1);
const total = ref(0);
const pageSize = ref(10);

// 搜索条件
const searchParams = ref({
  title: '',
  description: ''
});

// 抽屉控制
const visibleDrawer = ref(false); // 添加抽屉
const visibleDrawer1 = ref(false); // 编辑抽屉

// 表单数据模型
const scheduleModel = ref({
  title: '',
  description: '',
  startTime: '',
  endTime: '',
  status: 0
});

// 状态选项
const statusOptions = [
  { value: 0, label: '未开始' },
  { value: 1, label: '进行中' },
  { value: 2, label: '已完成' },
  { value: 3, label: '已取消' }
];

// 表单校验规则
const rules = {
  title: [
    { required: true, message: '请输入日程标题', trigger: 'blur' },
    { min: 2, max: 50, message: '标题长度在 2 到 50 个字符之间', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入日程描述', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
};

// 获取日程列表
const getScheduleList = async () => {
  try {
    // 确保用户信息存在
    if (!userInfoStore.info || !userInfoStore.info.id) {
      // 尝试重新获取用户信息
      const userInfoResult = await userInfoService();
      if (userInfoResult.code === 0 && userInfoResult.data) {
        userInfoStore.setInfo(userInfoResult.data);
      } else {
        ElMessage.error('未获取到用户信息，请重新登录');
        return;
      }
    }

    const result = await scheduleGetByUserIdService(userInfoStore.info.id);
    if (result.code === 0) {
      // 在本地进行搜索过滤
      let filteredData = result.data;
      if (searchParams.value.title) {
        filteredData = filteredData.filter(schedule => 
          schedule.title.toLowerCase().includes(searchParams.value.title.toLowerCase())
        );
      }
      if (searchParams.value.description) {
        filteredData = filteredData.filter(schedule => 
          schedule.description.toLowerCase().includes(searchParams.value.description.toLowerCase())
        );
      }
      
      // 本地分页处理
      total.value = filteredData.length;
      const start = (pageNum.value - 1) * pageSize.value;
      const end = start + pageSize.value;
      schedules.value = filteredData.slice(start, end);
    } else {
      ElMessage.error(result.message || '获取日程列表失败');
    }
  } catch (error) {
    ElMessage.error('获取日程列表失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  getScheduleList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  getScheduleList();
};

// 编辑日程
const handleEdit = (row) => {
  scheduleModel.value = { ...row };
  visibleDrawer1.value = true;
};

// 保存编辑后的日程
const saveEditedSchedule = async () => {
  try {
    await formRef1.value.validate();
    const result = await scheduleUpdateService(scheduleModel.value);
    ElMessage.success(result.message ? result.message : '编辑成功');
    visibleDrawer1.value = false;
    clearScheduleModel();
    getScheduleList();
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('编辑日程失败，请检查网络或联系管理员');
      console.error(error);
    }
  }
};

// 删除日程
const handleDelete = async (row) => {
  try {
    const confirm = await ElMessageBox.confirm(
      '确定要删除该日程吗？',
      '温馨提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    if (confirm) {
      const result = await scheduleDeleteService(row.scheduleId);
      ElMessage.success(result.message ? result.message : '删除成功');
      getScheduleList();
    }
  } catch (error) {
    ElMessage.error('删除日程失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 添加日程
const addSchedule = async () => {
  try {
    await formRef.value.validate();
    // 创建新对象，排除 scheduleId
    const { scheduleId, ...scheduleData } = {
      ...scheduleModel.value,
      userId: userInfoStore.info.id
    };
    const result = await scheduleAddService(scheduleData);
    ElMessage.success(result.message ? result.message : '添加成功');
    visibleDrawer.value = false;
    clearScheduleModel();
    getScheduleList();
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('添加日程失败，请检查网络或联系管理员');
      console.error(error);
    }
  }
};

// 清空表单数据
const clearScheduleModel = () => {
  scheduleModel.value = {
    title: '',
    description: '',
    startTime: '',
    endTime: '',
    status: 0
  };
};

// 重置搜索条件
const resetSearch = () => {
  searchParams.value = {
    title: '',
    description: ''
  };
  getScheduleList();
};

// 表单引用
const formRef = ref(null);
const formRef1 = ref(null);

// 初始化加载日程列表
getScheduleList();
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>我的日程</span>
        <div class="extra">
          <el-button type="primary" @click="visibleDrawer = true; clearScheduleModel()">添加日程</el-button>
        </div>
      </div>
    </template>

    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="标题：">
        <el-input v-model="searchParams.title" placeholder="请输入日程标题"></el-input>
      </el-form-item>
      <el-form-item label="描述：">
        <el-input v-model="searchParams.description" placeholder="请输入日程描述"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getScheduleList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 日程列表 -->
    <el-table :data="schedules" style="width: 100%">
      <el-table-column label="序号" width="80" type="index" :index="(index) => (pageNum - 1) * pageSize + index + 1" align="center"></el-table-column>
      <el-table-column label="标题" prop="title" align="center"></el-table-column>
      <el-table-column label="描述" prop="description" align="center"></el-table-column>
      <el-table-column label="开始时间" prop="startTime" align="center">
        <template #default="{ row }">
          {{ dayjs(row.startTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
      </el-table-column>
      <el-table-column label="结束时间" prop="endTime" align="center">
        <template #default="{ row }">
          {{ dayjs(row.endTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'info' : row.status === 1 ? 'warning' : row.status === 2 ? 'success' : 'danger'">
            {{ statusOptions.find(opt => opt.value === row.status)?.label }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center">
        <template #default="{ row }">
          {{ dayjs(row.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center">
        <template #default="{ row }">
          <el-button type="primary" :icon="Edit" circle @click="handleEdit(row)" />
          <el-button type="danger" :icon="Delete" circle @click="handleDelete(row)" />
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
      :page-sizes="[10, 20, 30, 50]"
      layout="jumper, total, sizes, prev, pager, next"
      background
      :total="total"
      @size-change="onSizeChange"
      @current-change="onCurrentChange"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <!-- 添加日程抽屉 -->
    <el-drawer v-model="visibleDrawer" title="添加日程" direction="rtl" size="50%">
      <el-form ref="formRef" :model="scheduleModel" label-width="100px" :rules="rules">
        <el-form-item label="标题" prop="title">
          <el-input v-model="scheduleModel.title" placeholder="请输入日程标题"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="scheduleModel.description" type="textarea" placeholder="请输入日程描述"></el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="scheduleModel.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="scheduleModel.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="scheduleModel.status" placeholder="请选择状态">
            <el-option
              v-for="option in statusOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addSchedule">确定</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 编辑日程抽屉 -->
    <el-drawer v-model="visibleDrawer1" title="编辑日程" direction="rtl" size="50%">
      <el-form ref="formRef1" :model="scheduleModel" label-width="100px" :rules="rules">
        <el-form-item label="标题" prop="title">
          <el-input v-model="scheduleModel.title" placeholder="请输入日程标题"></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="scheduleModel.description" type="textarea" placeholder="请输入日程描述"></el-input>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="scheduleModel.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="scheduleModel.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="scheduleModel.status" placeholder="请选择状态">
            <el-option
              v-for="option in statusOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveEditedSchedule">确定</el-button>
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