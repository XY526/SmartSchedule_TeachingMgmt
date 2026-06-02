<script setup>
import { Edit, Delete } from '@element-plus/icons-vue';
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { curriculumGetAllService, curriculumAddService, curriculumUpdateService, curriculumDeleteService, curriculumBatchInsertService } from '@/api/curriculum.js';
import { courseGetAllService } from '@/api/course.js';
import { classGetAllService } from '@/api/classAll.js';
import { teacherGetAllService } from '@/api/teacher.js';
import { locationGetAllService } from '@/api/location.js';
import { classRoomGetAllService } from '@/api/classRoom.js';
import { scheduleGenerateService } from '@/api/schedule.js';

// 课程表数据
const curriculums = ref([]);

// 下拉选择数据
const courseList = ref([]);
const classList = ref([]);
const teacherList = ref([]);
const locationList = ref([]);
const classroomList = ref([]);

// 分页数据
const pageNum = ref(1);
const total = ref(0);
const pageSize = ref(5);

// 搜索条件
const searchForm = ref({
  courseId: '',
  classId: '',
  teacherId: '',
  schoolYear: '2024',
  term: '',
  week: '',
});

// 学期选项
const termOptions = [
  { value: '1', label: '第一学期' },
  { value: '2', label: '第二学期' }
];

// 学年选项
const yearOptions = ref([
  { value: '2026', label: '2026年' },
  { value: '2025', label: '2025年' },
  { value: '2024', label: '2024年' },
  { value: '2023', label: '2023年' },
  { value: '2022', label: '2022年' },
  { value: '2021', label: '2021年' },
  { value: '2020', label: '2020年' }
]);

// 周次选项
const weekOptions = Array.from({ length: 20 }, (_, i) => ({
  value: String(i + 1),
  label: `第${i + 1}周`
}));

// 抽屉控制
const visibleDrawer = ref(false);
const visibleDrawer1 = ref(false);

// 表单数据模型
const curriculumModel = ref({
  courseId: '',
  classId: '',
  teacherId: '',
  schoolYear: '2024',
  term: '',
  startWeek: '',
  endWeek: '',
  weekday: '',
  section: '',
  locationId: '',
  classRoomId: '',
});

// 表单校验规则
const rules = {
  courseId: [
    { required: true, message: '请输入课程编号', trigger: 'blur' },
  ],
  classId: [
    { required: true, message: '请输入班级编号', trigger: 'blur' },
  ],
  teacherId: [
    { required: true, message: '请输入教师编号', trigger: 'blur' },
  ],
  schoolYear: [
    { required: true, message: '请输入学年', trigger: 'blur' },
  ],
  term: [
    { required: true, message: '请输入学期', trigger: 'blur' },
  ],
  startWeek: [
    { required: true, message: '请输入开课周次', trigger: 'blur' },
  ],
  endWeek: [
    { required: true, message: '请输入结课周次', trigger: 'blur' },
  ],
  weekday: [
    { required: true, message: '请输入星期几', trigger: 'blur' },
  ],
  section: [
    { required: true, message: '请输入节次', trigger: 'blur' },
  ],
  locationId: [
    { required: true, message: '请输入地点编号', trigger: 'blur' },
  ],
  classRoomId: [
    { required: true, message: '请输入教室编号', trigger: 'blur' },
  ],
};

// 获取所有下拉列表数据
const getAllSelectData = async () => {
  try {
    const [courses, classes, teachers, locations, classrooms] = await Promise.all([
      courseGetAllService({ pageSize: 999, pageNum: 1 }),
      classGetAllService({ pageSize: 999, pageNum: 1 }),
      teacherGetAllService({ pageSize: 999, pageNum: 1 }),
      locationGetAllService({ pageSize: 999, pageNum: 1 }),
      classRoomGetAllService({ pageSize: 999, pageNum: 1 }),
    ]);
    
    courseList.value = courses.data.records;
    classList.value = classes.data.records;
    teacherList.value = teachers.data.records;
    locationList.value = locations.data.records;
    classroomList.value = classrooms.data.records;
  } catch (error) {
    ElMessage.error('获取下拉列表数据失败');
    console.error(error);
  }
};

onMounted(() => {
  getAllSelectData();
});

const curriculumList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params: searchForm.value,
    };
    console.log('请求参数:', params);
    const result = await curriculumGetAllService(params);
    console.log('API 返回结果:', result);
    total.value = result.data.total;
    curriculums.value = result.data.records;
    // ElMessage.success('获取课程表成功');
  } catch (error) {
    ElMessage.error('获取课程表失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  curriculumList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  curriculumList();
};

// 编辑课程表
const handleEdit = (row) => {
  curriculumModel.value = { ...row };
  visibleDrawer1.value = true;
};

// 保存编辑后的课程表
const saveEditedCurriculum = async () => {
  try {
    await formRef1.value.validate();
    const result = await curriculumUpdateService(curriculumModel.value);
    ElMessage.success(result.msg ? result.msg : '编辑成功');
    visibleDrawer1.value = false;
    clearCurriculumModel();
    curriculumList();
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('编辑课程表失败，请检查网络或联系管理员');
      console.error(error);
    }
  }
};

// 删除课程表
const handleDelete = async (row) => {
  try {
    const confirm = await ElMessageBox.confirm(
      '确定要删除该课程安排吗？',
      '温馨提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    if (confirm) {
      const result = await curriculumDeleteService(row.id);
      ElMessage.success(result.msg ? result.msg : '删除成功');
      curriculumList();
    }
  } catch (error) {
    ElMessage.error('删除课程表失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 添加课程表
const addCurriculum = async () => {
  try {
    await formRef.value.validate();
    const result = await curriculumAddService(curriculumModel.value);
    ElMessage.success(result.msg ? result.msg : '添加成功');
    visibleDrawer.value = false;
    clearCurriculumModel();
    curriculumList();
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('添加课程表失败，请检查网络或联系管理员');
      console.error(error);
    }
  }
};

// 清空表单数据
const clearCurriculumModel = () => {
  curriculumModel.value = {
    courseId: '',
    classId: '',
    teacherId: '',
    schoolYear: '2024',
    term: '',
    startWeek: '',
    endWeek: '',
    weekday: '',
    section: '',
    locationId: '',
    classRoomId: '',
  };
};

// 重置搜索条件
const resetSearch = () => {
  searchForm.value = {
    courseId: '',
    classId: '',
    teacherId: '',
    schoolYear: '2024',
    term: '',
    week: '',
  };
  curriculumList();
};

// 表单引用
const formRef = ref(null);
const formRef1 = ref(null);

// 生成课程表
const generateSchedule = async () => {
  try {
    const params = {
      classId: searchForm.value.classId,
      schoolYear: searchForm.value.schoolYear,
      term: searchForm.value.term
    };
    
    if (!params.classId) {
      ElMessage.warning('请选择班级');
      return;
    }
    if (!params.schoolYear) {
      ElMessage.warning('请输入学年');
      return;
    }
    if (!params.term) {
      ElMessage.warning('请选择学期');
      return;
    }

    const result = await scheduleGenerateService(params);
    console.log('生成课程表结果：', result);

    if (result.code === 0 && result.data) {
      // 生成成功后，保存课程表
      try {
        // 确保数据是数组
        const scheduleData = Array.isArray(result.data) ? result.data : [result.data];
        console.log('准备保存的课程表数据：', scheduleData);

        if (scheduleData.length > 0) {
          // 批量保存课程表
          const saveResult = await curriculumBatchInsertService(scheduleData);
          console.log('保存课程表结果：', saveResult);

          if (saveResult.code === 0) {
            ElMessage.success('课程表生成并保存成功');
            curriculumList(); // 刷新课程表列表
          } else {
            ElMessage.error(saveResult.message || '课程表保存失败');
          }
        } else {
          ElMessage.warning('没有生成课程表数据');
        }
      } catch (saveError) {
        console.error('保存课程表失败：', saveError);
        ElMessage.error('保存课程表失败，请检查网络或联系管理员');
      }
    } else {
      ElMessage.error(result.message || '课程表生成失败');
    }
  } catch (error) {
    console.error('生成课程表失败：', error);
    ElMessage.error('生成课程表失败，请检查网络或联系管理员');
  }
};

// 初始化加载课程表
curriculumList();
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>课程表管理</span>
        <div class="extra">
          <el-button type="success" @click="generateSchedule" :disabled="!searchForm.classId || !searchForm.schoolYear || !searchForm.term">生成课程表</el-button>
          <el-button type="primary" @click="visibleDrawer = true; clearCurriculumModel()">添加课程安排</el-button>
        </div>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="课程：">
        <el-select v-model="searchForm.courseId" placeholder="请选择课程" clearable>
          <el-option
            v-for="course in courseList"
            :key="course.courseId"
            :label="course.courseName"
            :value="course.courseId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="班级：">
        <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable>
          <el-option
            v-for="class_ in classList"
            :key="class_.classId"
            :label="class_.className"
            :value="class_.classId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="教师：">
        <el-select v-model="searchForm.teacherId" placeholder="请选择教师" clearable>
          <el-option
            v-for="teacher in teacherList"
            :key="teacher.teacherId"
            :label="teacher.teacherName"
            :value="teacher.teacherId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="学年：">
        <el-select v-model="searchForm.schoolYear" placeholder="请选择学年" clearable>
          <el-option
            v-for="item in yearOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="学期：">
        <el-select v-model="searchForm.term" placeholder="请选择学期" clearable>
          <el-option
            v-for="item in termOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="周次：">
        <el-select v-model="searchForm.week" placeholder="请选择周次" clearable>
          <el-option
            v-for="item in weekOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="curriculumList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 课程表列表 -->
    <el-table :data="curriculums" style="width: 100%">
      <el-table-column label="ID" width="80" prop="id" align="center"></el-table-column>
      <el-table-column label="课程名称" align="center">
        <template #default="{ row }">
          {{ courseList.find(course => course.courseId === row.courseId)?.courseName }}
        </template>
      </el-table-column>
      <el-table-column label="班级" prop="classId" align="center"></el-table-column>
      <el-table-column label="教师" align="center">
        <template #default="{ row }">
          {{ teacherList.find(teacher => teacher.teacherId === row.teacherId)?.teacherName }}
        </template>
      </el-table-column>
      <el-table-column label="学年" prop="schoolYear" align="center"></el-table-column>
      <el-table-column label="学期" prop="term" align="center">
        <template #default="{ row }">
          {{ row.term === '1' ? '第一学期' : '第二学期' }}
        </template>
      </el-table-column>
      <el-table-column label="周次" align="center">
        <template #default="{ row }">
          {{ row.startWeek }}-{{ row.endWeek }}周
        </template>
      </el-table-column>
      <el-table-column label="星期" align="center">
        <template #default="{ row }">
          {{ `星期${['一', '二', '三', '四', '五', '六', '日'][Number(row.weekday) - 1]}` }}
        </template>
      </el-table-column>
      <el-table-column label="节次" prop="section" align="center"></el-table-column>
      <el-table-column label="地点" align="center">
        <template #default="{ row }">
          {{ locationList.find(location => location.locationId === row.locationId)?.locationName }}
        </template>
      </el-table-column>
      <el-table-column label="教室" prop="classRoomId" align="center"></el-table-column>
      <el-table-column label="操作" width="150" align="center">
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

    <!-- 添加课程表抽屉 -->
    <el-drawer v-model="visibleDrawer" title="添加课程安排" direction="rtl" size="50%">
      <el-form ref="formRef" :model="curriculumModel" label-width="100px" :rules="rules">
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="curriculumModel.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option
              v-for="course in courseList"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级" prop="classId">
          <el-select v-model="curriculumModel.classId" placeholder="请选择班级" style="width: 100%">
            <el-option
              v-for="class_ in classList"
              :key="class_.classId"
              :label="class_.className"
              :value="class_.classId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教师" prop="teacherId">
          <el-select v-model="curriculumModel.teacherId" placeholder="请选择教师" style="width: 100%">
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.teacherId"
              :label="teacher.teacherName"
              :value="teacher.teacherId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学年" prop="schoolYear">
          <el-select v-model="curriculumModel.schoolYear" placeholder="请选择学年" style="width: 100%">
            <el-option
              v-for="item in yearOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学期" prop="term">
          <el-select v-model="curriculumModel.term" placeholder="请选择学期" style="width: 100%">
            <el-option
              v-for="item in termOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开课周次" prop="startWeek">
          <el-select v-model="curriculumModel.startWeek" placeholder="请选择开课周次" style="width: 100%">
            <el-option
              v-for="item in weekOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="结课周次" prop="endWeek">
          <el-select v-model="curriculumModel.endWeek" placeholder="请选择结课周次" style="width: 100%">
            <el-option
              v-for="item in weekOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="星期" prop="weekday">
          <el-select v-model="curriculumModel.weekday" placeholder="请选择星期" style="width: 100%">
            <el-option label="星期一" value="1" />
            <el-option label="星期二" value="2" />
            <el-option label="星期三" value="3" />
            <el-option label="星期四" value="4" />
            <el-option label="星期五" value="5" />
            <el-option label="星期六" value="6" />
            <el-option label="星期日" value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="节次" prop="section">
          <el-input v-model="curriculumModel.section" placeholder="请输入节次"></el-input>
        </el-form-item>
        <el-form-item label="地点" prop="locationId">
          <el-select v-model="curriculumModel.locationId" placeholder="请选择地点" style="width: 100%">
            <el-option
              v-for="location in locationList"
              :key="location.locationId"
              :label="location.locationName"
              :value="location.locationId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教室" prop="classRoomId">
          <el-select v-model="curriculumModel.classRoomId" placeholder="请选择教室" style="width: 100%">
            <el-option
              v-for="classroom in classroomList"
              :key="classroom.classRoomId"
              :label="classroom.classRoomName"
              :value="classroom.classRoomId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addCurriculum">确定</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 编辑课程表抽屉 -->
    <el-drawer v-model="visibleDrawer1" title="编辑课程安排" direction="rtl" size="50%">
      <el-form ref="formRef1" :model="curriculumModel" label-width="100px" :rules="rules">
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="curriculumModel.courseId" placeholder="请选择课程" style="width: 100%">
            <el-option
              v-for="course in courseList"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="班级" prop="classId">
          <el-select v-model="curriculumModel.classId" placeholder="请选择班级" style="width: 100%">
            <el-option
              v-for="class_ in classList"
              :key="class_.classId"
              :label="class_.className"
              :value="class_.classId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教师" prop="teacherId">
          <el-select v-model="curriculumModel.teacherId" placeholder="请选择教师" style="width: 100%">
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.teacherId"
              :label="teacher.teacherName"
              :value="teacher.teacherId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学年" prop="schoolYear">
          <el-select v-model="curriculumModel.schoolYear" placeholder="请选择学年" style="width: 100%">
            <el-option
              v-for="item in yearOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学期" prop="term">
          <el-select v-model="curriculumModel.term" placeholder="请选择学期" style="width: 100%">
            <el-option
              v-for="item in termOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="开课周次" prop="startWeek">
          <el-select v-model="curriculumModel.startWeek" placeholder="请选择开课周次" style="width: 100%">
            <el-option
              v-for="item in weekOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="结课周次" prop="endWeek">
          <el-select v-model="curriculumModel.endWeek" placeholder="请选择结课周次" style="width: 100%">
            <el-option
              v-for="item in weekOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="星期" prop="weekday">
          <el-select v-model="curriculumModel.weekday" placeholder="请选择星期" style="width: 100%">
            <el-option label="星期一" value="1" />
            <el-option label="星期二" value="2" />
            <el-option label="星期三" value="3" />
            <el-option label="星期四" value="4" />
            <el-option label="星期五" value="5" />
            <el-option label="星期六" value="6" />
            <el-option label="星期日" value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="节次" prop="section">
          <el-input v-model="curriculumModel.section" placeholder="请输入节次"></el-input>
        </el-form-item>
        <el-form-item label="地点" prop="locationId">
          <el-select v-model="curriculumModel.locationId" placeholder="请选择地点" style="width: 100%">
            <el-option
              v-for="location in locationList"
              :key="location.locationId"
              :label="location.locationName"
              :value="location.locationId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="教室" prop="classRoomId">
          <el-select v-model="curriculumModel.classRoomId" placeholder="请选择教室" style="width: 100%">
            <el-option
              v-for="classroom in classroomList"
              :key="classroom.classRoomId"
              :label="classroom.classRoomName"
              :value="classroom.classRoomId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveEditedCurriculum">确定</el-button>
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