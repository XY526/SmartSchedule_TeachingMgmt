<script setup>
import { Edit, Delete } from '@element-plus/icons-vue';
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { classGetAllService, classAddService, classEditService, classDeleteService } from '@/api/classAll.js';
import { teacherGetAllService } from '@/api/teacher.js';
import { facultyGetAllService } from '@/api/faculty.js';

// 班级列表数据
const classAll = ref([]);

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
const classModel = ref({
  facultyId: '',
  classSize: '',
  year: '',
  teacherId: '',
});

// 表单校验规则
const rules = {
  facultyId: [
    { required: true, message: '请选择院系', trigger: 'change' },
  ],
  classSize: [
    { required: true, message: '请输入班级人数', trigger: 'blur' },
    { min: 1, max: 3, message: '班级人数必须在1-999之间', trigger: 'blur' },
  ],
  year: [{ required: true, message: '请选择日期', trigger: 'blur' }],
  teacherId: [
    { required: true, message: '请选择授课教师', trigger: 'change' },
  ],
};

// 表单引用
const formRef = ref();

// 添加教师列表数据
const teacherList = ref([]);

// 添加院系列表数据
const facultyList = ref([]);

// 获取所有教师列表
const getAllTeachers = async () => {
  try {
    const result = await teacherGetAllService({
      pageSize: 999, // 获取所有教师
      pageNum: 1,
    });
    teacherList.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取教师列表失败');
    console.error(error);
  }
};

// 获取所有院系列表
const getAllFaculties = async () => {
  try {
    const result = await facultyGetAllService({
      pageSize: 999, // 获取所有院系
      pageNum: 1,
    });
    facultyList.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取院系列表失败');
    console.error(error);
  }
};

// 打开添加抽屉并清空表单
const openAddDrawer = () => {
  clearClassModel(); // 清空表单数据
  visibleDrawer.value = true; // 打开添加抽屉
};

// 清空表单数据
const clearClassModel = () => {
  classModel.value = {
    facultyId: '',
    classSize: '',
    year: '',
    teacherId: '',
  };
};

// 获取班级列表
const classAllList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params: { teacherName: teacherName.value, facultyName: facultyName.value },
    };
    const result = await classGetAllService(params);
    total.value = result.data.total;
    classAll.value = result.data.records;
    // ElMessage.success('获取班级列表成功');
  } catch (error) {
    ElMessage.error('获取班级列表失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  classAllList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  classAllList();
};

// 编辑班级
const handleEdit = (row) => {
  classModel.value = { ...row }; // 填充表单数据
  visibleDrawer1.value = true; // 打开编辑抽屉
};

// 保存编辑后的班级
const saveEditedClass = async () => {
  try {
    const result = await classEditService(classModel.value);
    ElMessage.success(result.msg ? result.msg : '编辑成功');
    visibleDrawer1.value = false;
    clearClassModel();
    classAllList();
  } catch (error) {
    ElMessage.error('编辑班级失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 删除班级
const handleDelete = async (row) => {
  try {
    const confirm = window.confirm('确定要删除该班级吗？');
    if (confirm) {
      const result = await classDeleteService(row.classId);
      ElMessage.success(result.msg ? result.msg : '删除成功');
      classAllList();
    }
  } catch (error) {
    ElMessage.error('删除班级失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 添加班级
const addClass = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const result = await classAddService(classModel.value);
        ElMessage.success(result.msg ? result.msg : '添加成功');
        visibleDrawer.value = false;
        clearClassModel();
        classAllList();
      } catch (error) {
        ElMessage.error('添加班级失败，请检查网络或联系管理员');
        console.error(error);
      }
    } else {
      ElMessage.error('表单校验不通过，请检查输入内容');
    }
  });
};

// 在组件挂载时获取教师列表
onMounted(() => {
  getAllTeachers();
  getAllFaculties();
  classAllList();
});
</script>
<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>班级管理</span>
        <div class="extra">
          <el-button type="primary" @click="openAddDrawer">添加班级</el-button>
        </div>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="院系名称：">
        <el-input v-model="facultyName"></el-input>
      </el-form-item>
      <el-form-item label="教师名：">
        <el-input v-model="teacherName"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="classAllList">搜索</el-button>
        <el-button @click="facultyName = ''; teacherName = ''; classAllList()">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 班级列表 -->
    <el-table :data="classAll" style="width: 100%">
      <el-table-column label="序号" width="100" prop="classId" align="center"></el-table-column>
      <el-table-column label="院系名称" prop="facultyName" align="center"></el-table-column>
      <el-table-column label="授课老师" prop="teacherName" align="center"></el-table-column>
      <el-table-column label="班级人数" prop="classSize" align="center"></el-table-column>
      <el-table-column label="开班时间" prop="year" align="center"></el-table-column>
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

    <!-- 添加班级抽屉 -->
    <el-drawer v-model="visibleDrawer" title="添加班级" direction="rtl" size="50%">
      <el-form ref="formRef" :model="classModel" label-width="100px" :rules="rules">
        <el-form-item label="院系" prop="facultyId">
          <el-select 
            v-model="classModel.facultyId" 
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
        <el-form-item label="班级人数" prop="classSize">
          <el-input v-model="classModel.classSize" placeholder="请输入班级人数"></el-input>
        </el-form-item>
        <el-form-item label="班级建立时间" prop="year">
          <el-date-picker v-model="classModel.year" type="date" placeholder="请输入班级建立时间" clearable />
        </el-form-item>
        <el-form-item label="授课教师" prop="teacherId">
          <el-select 
            v-model="classModel.teacherId" 
            placeholder="请选择授课教师"
            style="width: 100%"
          >
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.teacherId"
              :label="teacher.teacherName"
              :value="teacher.teacherId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addClass">确定</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 编辑班级抽屉 -->
    <el-drawer v-model="visibleDrawer1" title="编辑班级" direction="rtl" size="50%">
      <el-form ref="formRef" :model="classModel" label-width="100px" :rules="rules">
        <el-form-item label="院系" prop="facultyId">
          <el-select 
            v-model="classModel.facultyId" 
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
        <el-form-item label="班级人数" prop="classSize">
          <el-input v-model="classModel.classSize" placeholder="请输入班级人数"></el-input>
        </el-form-item>
        <el-form-item label="班级建立时间" prop="year">
          <el-date-picker v-model="classModel.year" type="date" placeholder="请输入班级建立时间" clearable />
        </el-form-item>
        <el-form-item label="授课教师" prop="teacherId">
          <el-select 
            v-model="classModel.teacherId" 
            placeholder="请选择授课教师"
            style="width: 100%"
          >
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.teacherId"
              :label="teacher.teacherName"
              :value="teacher.teacherId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveEditedClass">确定</el-button>
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