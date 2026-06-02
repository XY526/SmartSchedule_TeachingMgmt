<script setup>
import { Edit, Delete } from '@element-plus/icons-vue';
import { ref, onMounted, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { userGetAllService, userAddService, userEditService, userDeleteService } from '@/api/user.js';
import { teacherGetAllService } from '@/api/teacher.js';
import { studentGetAllService } from '@/api/student.js';

// 用户列表数据
const userAll = ref([]);

// 分页数据
const pageNum = ref(1);
const total = ref(0);
const pageSize = ref(5);

// 搜索条件
const teacherName = ref('');
const username = ref('');
const studentName = ref('');

// 抽屉控制
const visibleDrawer = ref(false); // 添加抽屉
const visibleDrawer1 = ref(false); // 编辑抽屉

// 表单数据模型
const userModel = ref({
  username: '',
  password: '',
  type: '',
  disabled: '0',
  studentId: '',
  teacherId: '',
  email: ''
});

// 添加教师和学生列表数据
const teacherList = ref([]);
const studentList = ref([]);

// 表单校验规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度需在 2 - 20 位之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度需在 6 - 20 位之间', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择用户类型', trigger: 'change' }
  ],
  disabled: [
    { required: true, message: '请选择是否禁用', trigger: 'change' }
  ],
  email: [
    { type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] }
  ],
  studentId: [
    { validator: (rule, value, callback) => {
        if (userModel.value.type === '0' && (!value || value === '')) {
          callback(new Error('请选择学生'));
        } else {
          callback();
        }
      }, trigger: 'change' }
  ],
  teacherId: [
    { validator: (rule, value, callback) => {
        if (userModel.value.type === '1' && (!value || value === '')) {
          callback(new Error('请选择教师'));
        } else {
          callback();
        }
      }, trigger: 'change' }
  ]
};

// 表单引用
const formRef = ref();

// 打开添加抽屉并清空表单
const openAddDrawer = () => {
  clearUserModel();
  visibleDrawer.value = true;
  console.log('打开添加用户抽屉');
  // Log studentList when drawer opens
  console.log('studentList when opening add drawer:', studentList.value);
};

// 清空表单数据
const clearUserModel = () => {
  userModel.value = {
    username: '',
    password: '',
    type: '',
    disabled: '0',
    studentId: '',
    teacherId: '',
    email: ''
  };
};

// 获取用户列表
const userAllList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params: {
        username: username.value,
        teacherName: teacherName.value,
        studentName: studentName.value
      }
    };
    const result = await userGetAllService(params);
    total.value = result.data.total;
    userAll.value = result.data.records;
    console.log('返回的数据：', result.data.records);
  } catch (error) {
    console.error('获取数据错误：', error);
    ElMessage.error('获取用户列表失败，请检查网络或联系管理员');
  }
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  userAllList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  userAllList();
};

// 编辑用户
const handleEdit = (row) => {
  // Store the studentId temporarily
  const tempStudentId = row.studentId;
  const tempTeacherId = row.teacherId;

  // Clear the userModel first, or set other properties
  userModel.value = { ...row, studentId: '', teacherId: '', email: row.email || '' };

  // Ensure type and disabled are strings
  userModel.value.type = String(row.type);
  userModel.value.disabled = String(row.disabled);

  console.log('原始 row 数据类型 - type:', typeof row.type, 'value:', row.type);
  console.log('原始 row 数据类型 - disabled:', typeof row.disabled, 'value:', row.disabled);
  console.log('转换后 userModel 数据类型 - type:', typeof userModel.value.type, 'value:', userModel.value.type);
  console.log('转换后 userModel 数据类型 - disabled:', typeof userModel.value.disabled, 'value:', userModel.value.disabled);

  visibleDrawer1.value = true;

  console.log('编辑用户时 userModel (initial):', userModel.value);

  // After a short delay, set the actual studentId/teacherId
  setTimeout(() => {
    userModel.value.studentId = tempStudentId;
    userModel.value.teacherId = tempTeacherId;
    console.log('编辑用户时 userModel (after delay):', userModel.value);
    console.log('studentList:', studentList.value);
    console.log('teacherList:', teacherList.value);
    console.log('userModel.studentId 数据类型:', typeof userModel.value.studentId);
    console.log('userModel.teacherId 数据类型:', typeof userModel.value.teacherId);
    console.log('studentList items and their types:', studentList.value.map(s => ({ id: s.studentId, name: s.studentName, idType: typeof s.studentId })));
    console.log('teacherList items and their types:', teacherList.value.map(t => ({ id: t.teacherId, name: t.teacherName, idType: typeof t.teacherId })));
  }, 100); // Adjust delay if needed
};

// 保存编辑后的用户
const saveEditedUser = async () => {
  try {
    const result = await userEditService(userModel.value);
    ElMessage.success(result.msg? result.msg : '编辑成功');
    visibleDrawer1.value = false;
    clearUserModel();
    userAllList();
  } catch (error) {
    ElMessage.error('编辑用户失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 删除用户
const handleDelete = async (row) => {
  try {
    const confirm = window.confirm('确定要删除该用户吗？');
    if (confirm) {
      const result = await userDeleteService(row.id);
      ElMessage.success(result.msg? result.msg : '删除成功');
      userAllList();
    }
  } catch (error) {
    ElMessage.error('删除用户失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 添加用户
const addUser = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 检查用户名是否已存在
        const checkParams = {
          pageNum: 1,
          pageSize: 1,
          params: {
            username: userModel.value.username
          }
        };
        console.log('检查用户名是否存在，请求参数:', checkParams); // Log request parameters
        const checkResult = await userGetAllService(checkParams);
        console.log('检查用户名是否存在，响应结果:', checkResult); // Log response result

        if (checkResult.data.records && checkResult.data.records.length > 0) {
          ElMessage.error('用户名已存在，请更换其他用户名');
          return;
        }

        const result = await userAddService(userModel.value);
        ElMessage.success(result.msg? result.msg : '添加成功');
        visibleDrawer.value = false;
        clearUserModel();
        userAllList();
      } catch (error) {
        ElMessage.error('添加用户失败，请检查网络或联系管理员');
        console.error(error);
      }
    } else {
      ElMessage.error('表单校验不通过，请检查输入内容');
    }
  });
};

// 重置搜索条件
const resetSearch = () => {
  username.value = '';
  teacherName.value = '';
  studentName.value = '';
  userAllList();
};

// 获取所有教师列表
const getAllTeachers = async () => {
  try {
    const result = await teacherGetAllService({
      pageSize: 999,
      pageNum: 1,
    });
    teacherList.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取教师列表失败');
    console.error(error);
  }
};

// 获取所有学生列表
const getAllStudents = async () => {
  try {
    const result = await studentGetAllService({
      pageSize: 999,
      pageNum: 1,
    });
    studentList.value = result.data.records;
    console.log('Fetched studentList:', studentList.value); // Log after fetching
  } catch (error) {
    ElMessage.error('获取学生列表失败');
    console.error(error);
  }
};

// 监听用户类型变化，清空另一个字段
watch(() => userModel.value.type, (newType) => {
  if (newType === '0') { // 如果选择学生用户
    userModel.value.teacherId = ''; // 清空教师ID
  } else if (newType === '1') { // 如果选择教师用户
    userModel.value.studentId = ''; // 清空学生ID
  } else { // 如果选择管理员
    userModel.value.studentId = ''; // 清空两个ID
    userModel.value.teacherId = '';
  }
});

// 在组件挂载时获取列表数据
onMounted(() => {
  console.log('User component mounted');
  getAllTeachers();
  getAllStudents();
  userAllList();

  // Temporary global click listener for debugging
  document.addEventListener('click', (event) => {
    console.log('Global click detected:', event.target);
    // You can add more specific checks here if needed
  });
});
</script>
<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>用户管理</span>
        <div class="extra">
          <el-button type="primary" @click="openAddDrawer">添加用户</el-button>
        </div>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="用户名">
        <el-input v-model="username"></el-input>
      </el-form-item>
      <el-form-item label="教师名">
        <el-select v-model="teacherName" placeholder="请选择教师" clearable>
          <el-option
            v-for="teacher in teacherList"
            :key="teacher.teacherId"
            :label="teacher.teacherName"
            :value="teacher.teacherName"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="学生名">
        <el-select v-model="studentName" placeholder="请选择学生" clearable>
          <el-option
            v-for="student in studentList"
            :key="student.studentId"
            :label="student.name"
            :value="student.name"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="userAllList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 用户列表 -->
    <el-table :data="userAll" style="width: 100%">
      <el-table-column label="序号" width="100" prop="id" align="center"></el-table-column>
      <el-table-column label="用户名" prop="username" align="center"></el-table-column>
      <el-table-column label="权限" width="100" align="center">
        <template #default="scope">
          {{ scope.row.type === 2 ? '管理员' : 
             scope.row.type === 1 ? '教师' : 
             scope.row.type === 0 ? '学生' : '未知' }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template #default="scope">
          {{ scope.row.disabled === 1 ? '已禁用' : '正常' }}
        </template>
      </el-table-column>
      <el-table-column label="对应学生姓名" align="center">
        <template #default="{ row }">
          {{ studentList.find(student => student.studentId === row.studentId)?.name || '' }}
        </template>
      </el-table-column>
      <el-table-column label="对应教师姓名" align="center">
        <template #default="{ row }">
          {{ teacherList.find(teacher => teacher.teacherId === row.teacherId)?.teacherName || '' }}
        </template>
      </el-table-column>
      <el-table-column label="邮箱" prop="email" align="center"></el-table-column>
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

    <!-- 添加用户抽屉 -->
    <el-drawer v-model="visibleDrawer" title="添加用户" direction="rtl" size="50%">
      <el-form ref="formRef" :model="userModel" label-width="100px" :rules="rules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userModel.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="userModel.password" placeholder="请输入密码" type="password"></el-input>
        </el-form-item>
        <el-form-item label="用户类型" prop="type">
          <el-select v-model="userModel.type" placeholder="请选择用户类型">
            <el-option label="管理员" value="2"></el-option>
            <el-option label="教师" value="1"></el-option>
            <el-option label="学生" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否禁用" prop="disabled">
          <el-select v-model="userModel.disabled" placeholder="请选择状态">
            <el-option label="已禁用" value="1"></el-option>
            <el-option label="未禁用" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userModel.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        
        <!-- 根据用户类型显示对应的选择框 -->
        <el-form-item 
          :label="userModel.type === '0' ? '选择学生' : '选择教师'" 
          :prop="userModel.type === '0' ? 'studentId' : 'teacherId'"
          v-if="userModel.type === '0' || userModel.type === '1'"
        >
          <el-select 
            v-if="userModel.type === '0'"
            v-model="userModel.studentId" 
            placeholder="请选择学生"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="student in studentList"
              :key="student.studentId"
              :label="student.name"
              :value="student.studentId"
            >
              <span>{{ student.name }}</span>
            </el-option>
          </el-select>
          <el-select 
            v-if="userModel.type === '1'"
            v-model="userModel.teacherId" 
            placeholder="请选择教师"
            style="width: 100%"
            filterable
            :label="userModel.type === '1' && userModel.teacherId ? (teacherList.find(t => t.teacherId === userModel.teacherId)?.teacherName || userModel.teacherId) : ''"
          >
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.teacherId"
              :label="teacher.teacherName"
              :value="teacher.teacherId"
            >
              <span>{{ teacher.teacherName }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="addUser">确定</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 编辑用户抽屉 -->
    <el-drawer v-model="visibleDrawer1" title="编辑用户" direction="rtl" size="50%">
      <el-form ref="formRef" :model="userModel" label-width="100px" :rules="rules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userModel.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="userModel.password" placeholder="请输入密码" type="password"></el-input>
        </el-form-item>
        <el-form-item label="用户类型" prop="type">
          <el-select v-model="userModel.type" placeholder="请选择用户类型">
            <el-option label="管理员" value="2"></el-option>
            <el-option label="教师" value="1"></el-option>
            <el-option label="学生" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否禁用" prop="disabled">
          <el-select v-model="userModel.disabled" placeholder="请选择状态">
            <el-option label="已禁用" value="1"></el-option>
            <el-option label="未禁用" value="0"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userModel.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>

        <!-- 根据用户类型显示对应的选择框 -->
        <el-form-item 
          :label="userModel.type === '0' ? '选择学生' : '选择教师'" 
          :prop="userModel.type === '0' ? 'studentId' : 'teacherId'"
          v-if="userModel.type === '0' || userModel.type === '1'"
        >
          <el-select 
            v-if="userModel.type === '0'"
            v-model="userModel.studentId" 
            placeholder="请选择学生"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="student in studentList"
              :key="student.studentId"
              :label="student.name"
              :value="student.studentId"
            >
              <span>{{ student.name }}</span>
            </el-option>
          </el-select>
          <el-select 
            v-if="userModel.type === '1'"
            v-model="userModel.teacherId" 
            placeholder="请选择教师"
            style="width: 100%"
            filterable
            :label="userModel.type === '1' && userModel.teacherId ? (teacherList.find(t => t.teacherId === userModel.teacherId)?.teacherName || userModel.teacherId) : ''"
          >
            <el-option
              v-for="teacher in teacherList"
              :key="teacher.teacherId"
              :label="teacher.teacherName"
              :value="teacher.teacherId"
            >
              <span>{{ teacher.teacherName }}</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="saveEditedUser">确定</el-button>
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