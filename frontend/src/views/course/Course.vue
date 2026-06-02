<script setup>
import { Edit, Delete } from '@element-plus/icons-vue';
import { ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { courseGetAllService, courseAddService, courseUpdateService, courseDeleteService } from '@/api/course.js';

// 课程列表数据
const courses = ref([]);

// 分页数据
const pageNum = ref(1); // 当前页
const total = ref(0); // 总条数
const pageSize = ref(5); // 每页条数

// 搜索条件
const courseName = ref('');

// 抽屉控制
const visibleDrawer = ref(false); // 添加抽屉
const visibleDrawer1 = ref(false); // 编辑抽屉

// 表单数据模型
const courseModel = ref({
  courseName: '',
  hour: '',
  credit: '',
});

// 表单校验规则
const rules = {
  courseName: [
    { required: true, message: '请输入课程名称', trigger: 'blur' },
    { min: 2, max: 50, message: '课程名称长度在 2 到 50 个字符之间', trigger: 'blur' },
  ],
  hour: [
    { required: true, message: '请输入课程学时', trigger: 'blur' },
    { type: 'number', message: '课程学时必须为数字', trigger: 'blur', transform: (value) => Number(value) },
  ],
  credit: [
    { required: true, message: '请输入课程学分', trigger: 'blur' },
    { type: 'number', message: '课程学分必须为数字', trigger: 'blur', transform: (value) => Number(value) },
  ],
};

const courseList = async () => {
  try {
    const params = {
      pageNum: pageNum.value || 1,
      pageSize: pageSize.value || 10,
      params: {
        courseName: courseName.value || ''
      }
    };
    console.log('Course.vue - 发送请求参数:', params);
    const result = await courseGetAllService(params);
    console.log('Course.vue - 接收到的响应:', result);
    if (result && result.data) {
      total.value = result.data.total;
      courses.value = result.data.records;
    } else {
      console.error('Course.vue - 响应数据格式不正确:', result);
      ElMessage.error('获取课程列表失败，响应数据格式不正确');
    }
  } catch (error) {
    console.error('Course.vue - 获取课程列表失败:', error);
    ElMessage.error('获取课程列表失败，请检查网络或联系管理员');
  }
};
// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  courseList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  courseList();
};

// 编辑课程
const handleEdit = (row) => {
  courseModel.value = { ...row }; // 填充表单数据
  visibleDrawer1.value = true; // 打开编辑抽屉
};

// 保存编辑后的课程
const saveEditedCourse = async () => {
  try {
    // 触发表单校验
    await formRef1.value.validate();
    const result = await courseUpdateService(courseModel.value);
    ElMessage.success(result.msg ? result.msg : '编辑成功');
    visibleDrawer1.value = false;
    clearCourseModel();
    courseList();
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('编辑课程失败，请检查网络或联系管理员');
      console.error(error);
    }
    // 校验失败时，不执行后续逻辑
  }
};

// 删除课程
const handleDelete = async (row) => {
  try {
    const confirm = await ElMessageBox.confirm(
      '确定要删除该课程吗？',
      '温馨提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    if (confirm) {
      const result = await courseDeleteService(row.courseId);
      ElMessage.success(result.msg ? result.msg : '删除成功');
      courseList();
    }
  } catch (error) {
    ElMessage.error('删除课程失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 添加课程
const addCourse = async () => {
  try {
    // 触发表单校验
    await formRef.value.validate();
    const result = await courseAddService(courseModel.value);
    ElMessage.success(result.msg ? result.msg : '添加成功');
    visibleDrawer.value = false;
    clearCourseModel();
    courseList();
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('添加课程失败，请检查网络或联系管理员');
      console.error(error);
    }
    // 校验失败时，不执行后续逻辑
  }
};

// 清空表单数据
const clearCourseModel = () => {
  courseModel.value = {
    courseName: '',
    hour: '',
    credit: '',
  };
};

// 表单引用
const formRef = ref(null); // 添加表单引用
const formRef1 = ref(null); // 编辑表单引用

// 初始化加载课程列表
courseList();
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>课程管理</span>
        <div class="extra">
          <el-button type="primary" @click="visibleDrawer = true; clearCourseModel()">添加课程</el-button>
        </div>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="课程名称：">
        <el-input v-model="courseName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="courseList">搜索</el-button>
        <el-button @click="courseName = ''; courseList()">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 课程列表 -->
    <el-table :data="courses" style="width: 100%">
      <el-table-column label="序号" width="100" prop="courseId" align="center"></el-table-column>
      <el-table-column label="课程名称" prop="courseName" align="center"></el-table-column>
      <el-table-column label="课程学时" prop="hour" align="center"></el-table-column>
      <el-table-column label="课程学分" prop="credit" align="center"></el-table-column>
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

    <!-- 添加课程抽屉 -->
    <el-drawer v-model="visibleDrawer" title="添加课程" direction="rtl" size="50%">
      <el-form ref="formRef" :model="courseModel" label-width="100px" :rules="rules">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="courseModel.courseName" placeholder="请输入课程名称"></el-input>
        </el-form-item>
        <el-form-item label="课程学时" prop="hour">
          <el-input v-model="courseModel.hour" placeholder="请输入课程学时"></el-input>
        </el-form-item>
        <el-form-item label="课程学分" prop="credit">
          <el-input v-model="courseModel.credit" placeholder="请输入课程学分"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addCourse">确定</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 编辑课程抽屉 -->
    <el-drawer v-model="visibleDrawer1" title="编辑课程" direction="rtl" size="50%">
      <el-form ref="formRef1" :model="courseModel" label-width="100px" :rules="rules">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="courseModel.courseName" placeholder="请输入课程名称"></el-input>
        </el-form-item>
        <el-form-item label="课程学时" prop="hour">
          <el-input v-model="courseModel.hour" placeholder="请输入课程学时"></el-input>
        </el-form-item>
        <el-form-item label="课程学分" prop="credit">
          <el-input v-model="courseModel.credit" placeholder="请输入课程学分"></el-input>
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