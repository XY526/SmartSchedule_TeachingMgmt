<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>教师管理</span>
        <div class="extra">
          <el-button type="primary" @click="openAddDrawer">添加教师</el-button>
        </div>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="教师名：">
        <el-input v-model="teacherName"></el-input>
      </el-form-item>
      <el-form-item label="院系名称：">
        <el-input v-model="facultyName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="teacherAllList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 教师列表 -->
    <el-table :data="teacherAll" style="width: 100%">
      <el-table-column label="序号" width="100" prop="teacherId" align="center"></el-table-column>
      <el-table-column label="教师名" prop="teacherName" align="center"></el-table-column>
      <el-table-column label="院系名称" prop="facultyName" align="center"></el-table-column>
      <el-table-column label="教师专业" prop="speciality" align="center"></el-table-column>
      <el-table-column label="教师职称" prop="title" align="center"></el-table-column>
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

    <!-- 添加教师抽屉 -->
    <el-drawer v-model="visibleDrawer" title="添加教师" direction="rtl" size="50%">
      <el-form ref="formRef" :model="teacherModel" label-width="100px" :rules="rules">
        <el-form-item label="教师名" prop="teacherName">
          <el-input v-model="teacherModel.teacherName" placeholder="请输入教师名"></el-input>
        </el-form-item>
        <el-form-item label="院系" prop="facultyId">
          <el-select 
            v-model="teacherModel.facultyId" 
            placeholder="请选择院系"
            style="width: 100%"
          >
            <el-option
              v-for="faculty in facultyList"
              :key="faculty.facultyId"
              :label="faculty.facultyName"
              :value="faculty.facultyId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教师专业" prop="speciality">
          <el-input v-model="teacherModel.speciality" placeholder="请输入教师专业"></el-input>
        </el-form-item>
        <el-form-item label="教师职称" prop="title">
          <el-input v-model="teacherModel.title" placeholder="请输入教师职称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addTeacher">确定</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 编辑教师抽屉 -->
    <el-drawer v-model="visibleDrawer1" title="编辑教师" direction="rtl" size="50%">
      <el-form ref="formRef" :model="teacherModel" label-width="100px" :rules="rules">
        <el-form-item label="教师名" prop="teacherName">
          <el-input v-model="teacherModel.teacherName" placeholder="请输入教师名"></el-input>
        </el-form-item>
        <el-form-item label="院系" prop="facultyId">
          <el-select 
            v-model="teacherModel.facultyId" 
            placeholder="请选择院系"
            style="width: 100%"
          >
            <el-option
              v-for="faculty in facultyList"
              :key="faculty.facultyId"
              :label="faculty.facultyName"
              :value="faculty.facultyId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教师专业" prop="speciality">
          <el-input v-model="teacherModel.speciality" placeholder="请输入教师专业"></el-input>
        </el-form-item>
        <el-form-item label="教师职称" prop="title">
          <el-input v-model="teacherModel.title" placeholder="请输入教师职称"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveEditedTeacher">确定</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
  </el-card>
</template>

<script setup>
import { Edit, Delete } from '@element-plus/icons-vue';
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { teacherGetAllService, teacherAddService, teacherEditService, teacherDeleteService } from '@/api/teacher.js';
import { facultyGetAllService } from '@/api/faculty.js';

// 教师列表数据
const teacherAll = ref([]);

// 分页数据
const pageNum = ref(1); // 当前页
const total = ref(0); // 总条数
const pageSize = ref(5); // 每页条数

// 搜索条件
const teacherName = ref('');
const facultyName = ref('');

// 抽屉控制
const visibleDrawer = ref(false); // 添加抽屉
const visibleDrawer1 = ref(false); // 编辑抽屉

// 表单数据模型
const teacherModel = ref({
  teacherId: '',
  teacherName: '',
  facultyId: '',
  speciality: '',
  title: ''
});

// 添加院系列表数据
const facultyList = ref([]);

// 表单校验规则
const rules = {
  teacherName: [
    { required: true, message: '请输入教师名', trigger: 'blur' }
  ],
  facultyId: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ],
  speciality: [
    { required: true, message: '请输入教师专业', trigger: 'blur' }
  ],
  title: [
    { required: true, message: '请输入教师职称', trigger: 'blur' }
  ]
};

// 表单引用
const formRef = ref();

// 打开添加抽屉并清空表单
const openAddDrawer = () => {
  clearTeacherModel();
  visibleDrawer.value = true;
};

// 清空表单数据
const clearTeacherModel = () => {
  teacherModel.value = {
    teacherId: '',
    teacherName: '',
    facultyId: '',
    speciality: '',
    title: ''
  };
};

// 获取教师列表
const teacherAllList = async () => {
  try {
    const params = {
      pageNum: pageNum.value || 1,
      pageSize: pageSize.value || 10,
      params: {
        teacherName: teacherName.value || '',
        facultyName: facultyName.value || '',
      }
    };
    console.log('Teacher.vue - 发送请求参数:', params);
    const result = await teacherGetAllService(params);
    console.log('Teacher.vue - 接收到的响应:', result);
    if (result && result.data) {
      total.value = result.data.total;
      teacherAll.value = result.data.records;
    } else {
      console.error('Teacher.vue - 响应数据格式不正确:', result);
      ElMessage.error('获取教师列表失败，响应数据格式不正确');
    }
  } catch (error) {
    console.error('Teacher.vue - 获取教师列表失败:', error);
    ElMessage.error('获取教师列表失败，请检查网络或联系管理员');
  }
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  teacherAllList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  teacherAllList();
};

// 编辑教师
const handleEdit = (row) => {
  teacherModel.value = { ...row };
  visibleDrawer1.value = true;
};

// 保存编辑后的教师
const saveEditedTeacher = async () => {
  try {
    const result = await teacherEditService(teacherModel.value);
    ElMessage.success(result.msg? result.msg : '编辑成功');
    visibleDrawer1.value = false;
    clearTeacherModel();
    teacherAllList();
  } catch (error) {
    ElMessage.error('编辑教师失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 删除教师
const handleDelete = async (row) => {
  try {
    const confirm = window.confirm('确定要删除该教师吗？');
    if (confirm) {
      const result = await teacherDeleteService(row.teacherId);
      ElMessage.success(result.msg? result.msg : '删除成功');
      teacherAllList();
    }
  } catch (error) {
    ElMessage.error('删除教师失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 添加教师
const addTeacher = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const result = await teacherAddService(teacherModel.value);
        ElMessage.success(result.msg? result.msg : '添加成功');
        visibleDrawer.value = false;
        clearTeacherModel();
        teacherAllList();
      } catch (error) {
        ElMessage.error('添加教师失败，请检查网络或联系管理员');
        console.error(error);
      }
    } else {
      ElMessage.error('表单校验不通过，请检查输入内容');
    }
  });
};

// 重置搜索条件
const resetSearch = () => {
  teacherName.value = '';
  facultyName.value = '';
  teacherAllList();
};

// 获取所有院系列表
const getAllFaculties = async () => {
  try {
    const result = await facultyGetAllService({
      pageSize: 999,
      pageNum: 1,
    });
    facultyList.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取院系列表失败');
    console.error(error);
  }
};

// 在组件挂载时获取院系列表
onMounted(() => {
  getAllFaculties();
  teacherAllList();
});
</script>

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