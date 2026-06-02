<script setup>
import { Edit, Delete } from '@element-plus/icons-vue';
import { ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { facultyGetAllService, facultyAddService, facultyUpdateService, facultyDeleteService } from '@/api/faculty.js';

// 院系列表数据
const facultys = ref([]);

// 分页数据
const pageNum = ref(1); // 当前页
const total = ref(0); // 总条数
const pageSize = ref(5); // 每页条数

// 搜索条件
const facultyName = ref('');

// 抽屉控制
const visibleDrawer = ref(false); // 添加抽屉
const visibleDrawer1 = ref(false); // 编辑抽屉

// 表单数据模型
const facultyModel = ref({
  facultyName: '',
});

// 表单校验规则
const rules = {
  facultyName: [
    { required: true, message: '请输入院系名称', trigger: 'blur' },
    { min: 2, max: 10, message: '院系名称长度在 2 到 10 个字符之间', trigger: 'blur' },
  ],
};

const facultyList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      params:{facultyName: facultyName.value },
    };
    // console.log(facultyName.value);
    console.log('请求参数:', params); // 打印请求参数
    const result = await facultyGetAllService(params);
    console.log('API 返回结果:', result); // 打印 API 返回的完整结果
    total.value = result.data.total;
    facultys.value = result.data.records;
    // ElMessage.success('获取院系列表成功');
  } catch (error) {
    ElMessage.error('获取院系列表失败，请检查网络或联系管理员');
    console.error(error);
  }
};
// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  facultyList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  facultyList();
};

// 编辑院系
const handleEdit = (row) => {
  facultyModel.value = { ...row }; // 填充表单数据
  visibleDrawer1.value = true; // 打开编辑抽屉
};

// 保存编辑后的院系
const saveEditedCourse = async () => {
  try {
    // 触发表单校验
    await formRef1.value.validate();
    const result = await facultyUpdateService(facultyModel.value);
    ElMessage.success(result.msg ? result.msg : '编辑成功');
    visibleDrawer1.value = false;
    clearCourseModel();
    facultyList();
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('编辑院系失败，请检查网络或联系管理员');
      console.error(error);
    }
    // 校验失败时，不执行后续逻辑
  }
};

// 删除院系
const handleDelete = async (row) => {
  try {
    const confirm = await ElMessageBox.confirm(
      '确定要删除该院系吗？',
      '温馨提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    if (confirm) {
      const result = await facultyDeleteService(row.facultyId);
      ElMessage.success(result.msg ? result.msg : '删除成功');
      facultyList();
    }
  } catch (error) {
    ElMessage.error('删除院系失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 添加院系
const addCourse = async () => {
  try {
    // 触发表单校验
    await formRef.value.validate();
    const result = await facultyAddService(facultyModel.value);
    ElMessage.success(result.msg ? result.msg : '添加成功');
    visibleDrawer.value = false;
    clearCourseModel();
    facultyList();
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error('添加院系失败，请检查网络或联系管理员');
      console.error(error);
    }
    // 校验失败时，不执行后续逻辑
  }
};

// 清空表单数据
const clearCourseModel = () => {
  facultyModel.value = {
    facultyName: '',
  };
};

// 表单引用
const formRef = ref(null); // 添加表单引用
const formRef1 = ref(null); // 编辑表单引用

// 初始化加载院系列表
facultyList();
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>院系管理</span>
        <div class="extra">
          <el-button type="primary" @click="visibleDrawer = true; clearCourseModel()">添加院系</el-button>
        </div>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="院系名称：">
        <el-input v-model="facultyName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="facultyList">搜索</el-button>
        <el-button @click="facultyName = ''; facultyList()">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 院系列表 -->
    <el-table :data="facultys" style="width: 100%">
      <el-table-column label="序号" width="100" prop="facultyId" align="center"></el-table-column>
      <el-table-column label="院系名称" prop="facultyName" align="center"></el-table-column>
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

    <!-- 添加院系抽屉 -->
    <el-drawer v-model="visibleDrawer" title="添加院系" direction="rtl" size="50%">
      <el-form ref="formRef" :model="facultyModel" label-width="100px" :rules="rules">
        <el-form-item label="院系名称" prop="facultyName">
          <el-input v-model="facultyModel.facultyName" placeholder="请输入院系名称"></el-input>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="addCourse">确定</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 编辑院系抽屉 -->
    <el-drawer v-model="visibleDrawer1" title="编辑院系" direction="rtl" size="50%">
      <el-form ref="formRef1" :model="facultyModel" label-width="100px" :rules="rules">
        <el-form-item label="院系名称" prop="facultyName">
          <el-input v-model="facultyModel.facultyName" placeholder="请输入院系名称"></el-input>
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