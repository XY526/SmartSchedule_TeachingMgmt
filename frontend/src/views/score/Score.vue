<script setup>
import { Edit, Delete } from '@element-plus/icons-vue';
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  scoreListService, 
  scoreAddService, 
  scoreUpdateService, 
  scoreDeleteService 
} from '@/api/score.js';
import { studentGetAllService } from '@/api/student.js';
import { courseGetAllService } from '@/api/course.js';
import { teacherGetAllService } from '@/api/teacher.js';

// 成绩列表数据
const scores = ref([]);

// 分页数据
const pageNum = ref(1);
const total = ref(0);
const pageSize = ref(10);

// 搜索条件
const searchParams = ref({
  studentId: '',
  courseId: '',
  examType: null,
  remark: ''
});

// 抽屉控制
const visibleDrawer = ref(false); // 添加抽屉
const visibleDrawer1 = ref(false); // 编辑抽屉

// 表单数据模型
const scoreModel = ref({
  studentId: '',
  courseId: '',
  teacherId: '',
  examType: 0,
  score: '',
  examDate: '',
  remark: ''
});

// 学生、课程、教师列表
const students = ref([]);
const courses = ref([]);
const teachers = ref([]);

// 考试类型选项
const examTypeOptions = [
  { value: 0, label: '期中考试' },
  { value: 1, label: '期末考试' },
  { value: 2, label: '平时测验' }
];

// 自定义成绩校验器
const validateScore = (rule, value, callback) => {
  if (value === '' || value === null || value === undefined) {
    callback(new Error('请输入成绩'));
  } else {
    const numValue = parseFloat(value);
    if (isNaN(numValue)) {
      callback(new Error('成绩必须为数字'));
    } else if (numValue < 0 || numValue > 100) {
      callback(new Error('成绩必须在0-100之间'));
    } else {
      callback();
    }
  }
};

// 表单校验规则
const rules = {
  studentId: [
    { required: true, message: '请选择学生', trigger: 'change' }
  ],
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  teacherId: [
    { required: true, message: '请选择教师', trigger: 'change' }
  ],
  examType: [
    { required: true, message: '请选择考试类型', trigger: 'change' }
  ],
  score: [
    { required: true, validator: validateScore, trigger: 'change' }
  ],
  examDate: [
    { required: true, message: '请选择考试日期', trigger: 'change' }
  ]
};

// 获取学生列表
const getStudentList = async () => {
  try {
    const result = await studentGetAllService();
    students.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取学生列表失败');
    console.error(error);
  }
};

// 获取课程列表
const getCourseList = async () => {
  try {
    const result = await courseGetAllService();
    courses.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取课程列表失败');
    console.error(error);
  }
};

// 获取教师列表
const getTeacherList = async () => {
  try {
    const result = await teacherGetAllService();
    teachers.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取教师列表失败');
    console.error(error);
  }
};

// 获取成绩列表
const getScoreList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params: {
        studentId: searchParams.value.studentId || null,
        courseId: searchParams.value.courseId || null,
        remark: searchParams.value.remark || null
      }
    };
    
    // 单独处理 examType，确保它是数字类型
    if (searchParams.value.examType !== null) {
      params.params.examType = Number(searchParams.value.examType);
      }

    console.log('Search params:', params);
    const result = await scoreListService(params);
    if (result.code === 0) {
    total.value = result.data.total;
    scores.value = result.data.records;
    } else {
      ElMessage.error(result.msg || '获取成绩列表失败');
    }
  } catch (error) {
    ElMessage.error('获取成绩列表失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  getScoreList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  getScoreList();
};

// 编辑成绩
const handleEdit = (row) => {
  scoreModel.value = { ...row };
  visibleDrawer1.value = true;
};

// 保存编辑后的成绩
const saveEditedScore = async () => {
  try {
    await formRef1.value.validate();
    const result = await scoreUpdateService(scoreModel.value);
    ElMessage.success(result.msg ? result.msg : '编辑成功');
    visibleDrawer1.value = false;
    clearScoreModel();
    getScoreList();
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('编辑成绩失败，请检查网络或联系管理员');
      console.error(error);
    }
  }
};

// 删除成绩
const handleDelete = async (row) => {
  try {
    const confirm = await ElMessageBox.confirm(
      '确定要删除该成绩记录吗？',
      '温馨提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    if (confirm) {
      const result = await scoreDeleteService(row.scoreId);
      ElMessage.success(result.msg ? result.msg : '删除成功');
      getScoreList();
    }
  } catch (error) {
    ElMessage.error('删除成绩失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 添加成绩
const addScore = async () => {
  try {
    await formRef.value.validate();
    const result = await scoreAddService(scoreModel.value);
    ElMessage.success(result.msg ? result.msg : '添加成功');
    visibleDrawer.value = false;
    clearScoreModel();
    getScoreList();
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('添加成绩失败，请检查网络或联系管理员');
      console.error(error);
    }
  }
};

// 清空表单数据
const clearScoreModel = () => {
  scoreModel.value = {
    studentId: '',
    courseId: '',
    teacherId: '',
    examType: 0,
    score: '',
    examDate: '',
    remark: ''
  };
};

// 重置搜索条件
const resetSearch = () => {
  searchParams.value = {
    studentId: '',
    courseId: '',
    examType: null,
    remark: ''
  };
  getScoreList();
};

// 表单引用
const formRef = ref(null);
const formRef1 = ref(null);

// 初始化加载数据
onMounted(() => {
  getScoreList();
  getStudentList();
  getCourseList();
  getTeacherList();
});
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>成绩管理</span>
        <div class="extra">
          <el-button type="primary" @click="visibleDrawer = true; clearScoreModel()">添加成绩</el-button>
        </div>
      </div>
    </template>

    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="学生：">
        <el-select v-model="searchParams.studentId" placeholder="请选择学生" clearable>
          <el-option
            v-for="student in students"
            :key="student.studentId"
            :label="student.name"
            :value="student.studentId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="课程：">
        <el-select v-model="searchParams.courseId" placeholder="请选择课程" clearable>
          <el-option
            v-for="course in courses"
            :key="course.courseId"
            :label="course.courseName"
            :value="course.courseId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="考试类型：">
        <el-select v-model="searchParams.examType" placeholder="请选择考试类型" clearable>
          <el-option
            v-for="item in examTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注：">
        <el-input v-model="searchParams.remark" placeholder="请输入备注关键词"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="getScoreList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 成绩列表 -->
    <el-table :data="scores" style="width: 100%">
      <el-table-column label="成绩ID" prop="scoreId" width="80" align="center"></el-table-column>
      <el-table-column label="学生" width="120" align="center">
        <template #default="{ row }">
          {{ students.find(s => s.studentId === row.studentId)?.name || '未知' }}
        </template>
      </el-table-column>
      <el-table-column label="课程" width="120" align="center">
        <template #default="{ row }">
          {{ courses.find(c => c.courseId === row.courseId)?.courseName || '未知' }}
        </template>
      </el-table-column>
      <el-table-column label="教师" width="120" align="center">
        <template #default="{ row }">
          {{ teachers.find(t => t.teacherId === row.teacherId)?.teacherName || '未知' }}
        </template>
      </el-table-column>
      <el-table-column label="考试类型" width="100" align="center">
        <template #default="{ row }">
          {{ examTypeOptions.find(opt => opt.value === row.examType)?.label || '未知' }}
        </template>
      </el-table-column>
      <el-table-column label="成绩" prop="score" width="80" align="center"></el-table-column>
      <el-table-column label="考试日期" prop="examDate" width="120" align="center"></el-table-column>
      <el-table-column label="备注" prop="remark" align="center"></el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center"></el-table-column>
      <el-table-column label="操作" width="120" align="center">
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
      :page-sizes="[10, 20, 30, 50]"
      layout="jumper, total, sizes, prev, pager, next"
      background
      :total="total"
      @size-change="onSizeChange"
      @current-change="onCurrentChange"
      style="margin-top: 20px; justify-content: flex-end"
    />

    <!-- 添加成绩抽屉 -->
    <el-drawer v-model="visibleDrawer" title="添加成绩" direction="rtl" size="50%">
      <el-form ref="formRef" :model="scoreModel" label-width="100px" :rules="rules">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="scoreModel.studentId" placeholder="请选择学生">
            <el-option
              v-for="student in students"
              :key="student.studentId"
              :label="student.name"
              :value="student.studentId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="scoreModel.courseId" placeholder="请选择课程">
            <el-option
              v-for="course in courses"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教师" prop="teacherId">
          <el-select v-model="scoreModel.teacherId" placeholder="请选择教师">
            <el-option
              v-for="teacher in teachers"
              :key="teacher.teacherId"
              :label="teacher.teacherName"
              :value="teacher.teacherId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="考试类型" prop="examType">
          <el-select v-model="scoreModel.examType" placeholder="请选择考试类型">
            <el-option
              v-for="item in examTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="成绩" prop="score">
          <el-input v-model.number="scoreModel.score" placeholder="请输入成绩"></el-input>
        </el-form-item>
        <el-form-item label="考试日期" prop="examDate">
          <el-date-picker
            v-model="scoreModel.examDate"
            type="date"
            placeholder="选择考试日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="scoreModel.remark" type="textarea" placeholder="请输入备注"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addScore">确定</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 编辑成绩抽屉 -->
    <el-drawer v-model="visibleDrawer1" title="编辑成绩" direction="rtl" size="50%">
      <el-form ref="formRef1" :model="scoreModel" label-width="100px" :rules="rules">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="scoreModel.studentId" placeholder="请选择学生">
            <el-option
              v-for="student in students"
              :key="student.studentId"
              :label="student.name"
              :value="student.studentId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="scoreModel.courseId" placeholder="请选择课程">
            <el-option
              v-for="course in courses"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教师" prop="teacherId">
          <el-select v-model="scoreModel.teacherId" placeholder="请选择教师">
            <el-option
              v-for="teacher in teachers"
              :key="teacher.teacherId"
              :label="teacher.teacherName"
              :value="teacher.teacherId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="考试类型" prop="examType">
          <el-select v-model="scoreModel.examType" placeholder="请选择考试类型">
            <el-option
              v-for="item in examTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="成绩" prop="score">
          <el-input v-model.number="scoreModel.score" placeholder="请输入成绩"></el-input>
        </el-form-item>
        <el-form-item label="考试日期" prop="examDate">
          <el-date-picker
            v-model="scoreModel.examDate"
            type="date"
            placeholder="选择考试日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="scoreModel.remark" type="textarea" placeholder="请输入备注"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveEditedScore">确定</el-button>
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