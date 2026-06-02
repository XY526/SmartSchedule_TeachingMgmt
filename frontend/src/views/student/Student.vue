<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>学生管理</span>
        <div class="extra">
          <el-button type="primary" @click="openAddDrawer">添加学生</el-button>
        </div>
      </div>
    </template>
    <!-- 搜索表单 -->
    <el-form inline>
      <el-form-item label="学生姓名：">
        <el-input v-model="name"></el-input>
      </el-form-item>
      <el-form-item label="院系名称：">
        <el-input v-model="facultyName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="studentAllList">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 学生列表 -->
    <el-table :data="studentAll" style="width: 100%">
      <el-table-column label="序号" width="100" prop="studentId" align="center"></el-table-column>
      <el-table-column label="姓名" prop="name" align="center"></el-table-column>
      <el-table-column label="性别" prop="sex" align="center" width="80">
        <template #default="{ row }" >
          {{ row.sex === 1? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column label="生日" prop="birthday" align="center"></el-table-column>
      <el-table-column label="院系名称" prop="facultyName" align="center"></el-table-column>
      <el-table-column label="班级ID" prop="classId" align="center"></el-table-column>
      <el-table-column label="地址" prop="address" align="center"></el-table-column>
      <el-table-column label="电话" prop="phone" align="center"></el-table-column>
      <el-table-column label="备注" prop="remark" align="center"></el-table-column>
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

    <!-- 添加学生抽屉 -->
    <el-drawer v-model="visibleDrawer" title="添加学生" direction="rtl" size="50%">
      <el-form ref="formRef" :model="studentModel" label-width="100px" :rules="rules">
        <el-form-item label="学生姓名" prop="name">
          <el-input v-model="studentModel.name" placeholder="请输入学生姓名"></el-input>
        </el-form-item>
        <el-form-item label="院系" prop="facultyId">
          <el-select 
            v-model="studentModel.facultyId" 
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
        <el-form-item label="班级" prop="classId">
          <el-select 
            v-model="studentModel.classId" 
            placeholder="请选择班级"
            style="width: 100%"
          >
            <el-option
              v-for="class_ in classList"
              :key="class_.classId"
              :label="class_.className"
              :value="class_.classId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="生日" prop="birthday">
          <el-date-picker 
            v-model="studentModel.birthday" 
            type="date" 
            placeholder="请选择生日" 
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-cascader
            v-model="studentModel.addressCascader"
            :options="regionOptions"
            :props="regionProps"
            @change="handleAddressChange"
            placeholder="请选择地区"
            style="width: 100%"
          />
          <el-input 
            v-model="studentModel.addressDetail" 
            placeholder="请输入详细地址"
            style="margin-top: 10px"
          />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="studentModel.phone" placeholder="请输入电话号码"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="studentModel.remark" 
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          ></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="studentModel.sex">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="addClass">确定</el-button>
          <el-button @click="visibleDrawer = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>

    <!-- 编辑学生抽屉 -->
    <el-drawer v-model="visibleDrawer1" title="编辑学生" direction="rtl" size="50%">
      <el-form ref="formRef" :model="studentModel" label-width="100px" :rules="rules">
        <el-form-item label="学生姓名" prop="name">
          <el-input v-model="studentModel.name" placeholder="请输入学生姓名"></el-input>
        </el-form-item>
        <el-form-item label="院系" prop="facultyId">
          <el-select 
            v-model="studentModel.facultyId" 
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
        <el-form-item label="班级" prop="classId">
          <el-select 
            v-model="studentModel.classId" 
            placeholder="请选择班级"
            style="width: 100%"
          >
            <el-option
              v-for="class_ in classList"
              :key="class_.classId"
              :label="class_.className"
              :value="class_.classId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="生日" prop="birthday">
          <el-date-picker 
            v-model="studentModel.birthday" 
            type="date" 
            placeholder="请选择生日" 
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-cascader
            v-model="studentModel.addressCascader"
            :options="regionOptions"
            :props="regionProps"
            @change="handleAddressChange"
            placeholder="请选择地区"
            style="width: 100%"
          />
          <el-input 
            v-model="studentModel.addressDetail" 
            placeholder="请输入详细地址"
            style="margin-top: 10px"
          />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="studentModel.phone" placeholder="请输入电话号码"></el-input>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="studentModel.remark" 
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          ></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="studentModel.sex">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveEditedClass">确定</el-button>
          <el-button @click="visibleDrawer1 = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
  </el-card>
</template>

<script setup>
import { Edit, Delete } from '@element-plus/icons-vue';
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { studentGetAllService, studentAddService, studentEditService, studentDeleteService } from '@/api/student.js';
import { facultyGetAllService } from '@/api/faculty.js';
import { classGetAllService } from '@/api/classAll.js';

// 班级列表数据
const studentAll = ref([]);

// 分页数据
const pageNum = ref(1); // 当前页
const total = ref(0); // 总条数
const pageSize = ref(3); // 每页条数

// 搜索条件
const name = ref('');
const facultyName = ref('');

// 抽屉控制
const visibleDrawer = ref(false); // 添加抽屉
const visibleDrawer1 = ref(false); // 编辑抽屉

// 表单数据模型
const studentModel = ref({
  studentId: '',
  name: '',
  birthday: '',
  facultyId: '',
  classId: '',
  address: '',
  addressCascader: [],
  addressDetail: '',
  phone: '',
  remark: '',
  sex: '',
});

// 添加院系和班级列表数据
const facultyList = ref([]);
const classList = ref([]);

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

// 获取所有班级列表
const getAllClasses = async () => {
  try {
    const result = await classGetAllService({
      pageSize: 999,
      pageNum: 1,
    });
    classList.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取班级列表失败');
    console.error(error);
  }
};

// 在组件挂载时获取列表数据
onMounted(() => {
  getAllFaculties();
  getAllClasses();
  studentAllList();
});

const regionOptions = [
  {
    value: '湖北省',
    label: '湖北省',
    children: [
      {
        value: '武汉市',
        label: '武汉市',
        children: [
          { value: '江岸区', label: '江岸区' },
          { value: '江汉区', label: '江汉区' },
          { value: '硚口区', label: '硚口区' },
          { value: '汉阳区', label: '汉阳区' },
          { value: '武昌区', label: '武昌区' },
          { value: '青山区', label: '青山区' },
          { value: '洪山区', label: '洪山区' },
          { value: '东西湖区', label: '东西湖区' },
          { value: '汉南区', label: '汉南区' },
          { value: '蔡甸区', label: '蔡甸区' },
          { value: '江夏区', label: '江夏区' },
          { value: '黄陂区', label: '黄陂区' },
          { value: '新洲区', label: '新洲区' }
        ]
      },
      {
        value: '黄石市',
        label: '黄石市',
        children: [
          { value: '黄石港区', label: '黄石港区' },
          { value: '西塞山区', label: '西塞山区' },
          { value: '下陆区', label: '下陆区' },
          { value: '铁山区', label: '铁山区' },
          { value: '阳新县', label: '阳新县' },
          { value: '大冶市', label: '大冶市' }
        ]
      },
      {
        value: '十堰市',
        label: '十堰市',
        children: [
          { value: '茅箭区', label: '茅箭区' },
          { value: '张湾区', label: '张湾区' },
          { value: '郧阳区', label: '郧阳区' },
          { value: '郧西县', label: '郧西县' },
          { value: '竹山县', label: '竹山县' },
          { value: '竹溪县', label: '竹溪县' },
          { value: '房县', label: '房县' },
          { value: '丹江口市', label: '丹江口市' }
        ]
      },
      {
        value: '宜昌市',
        label: '宜昌市',
        children: [
          { value: '西陵区', label: '西陵区' },
          { value: '伍家岗区', label: '伍家岗区' },
          { value: '点军区', label: '点军区' },
          { value: '猇亭区', label: '猇亭区' },
          { value: '夷陵区', label: '夷陵区' },
          { value: '远安县', label: '远安县' },
          { value: '兴山县', label: '兴山县' },
          { value: '秭归县', label: '秭归县' },
          { value: '长阳土家族自治县', label: '长阳土家族自治县' },
          { value: '五峰土家族自治县', label: '五峰土家族自治县' },
          { value: '宜都市', label: '宜都市' },
          { value: '当阳市', label: '当阳市' },
          { value: '枝江市', label: '枝江市' }
        ]
      },
      {
        value: '襄阳市',
        label: '襄阳市',
        children: [
          { value: '襄城区', label: '襄城区' },
          { value: '樊城区', label: '樊城区' },
          { value: '襄州区', label: '襄州区' },
          { value: '南漳县', label: '南漳县' },
          { value: '谷城县', label: '谷城县' },
          { value: '保康县', label: '保康县' },
          { value: '老河口市', label: '老河口市' },
          { value: '枣阳市', label: '枣阳市' },
          { value: '宜城市', label: '宜城市' }
        ]
      },
      {
        value: '鄂州市',
        label: '鄂州市',
        children: [
          { value: '梁子湖区', label: '梁子湖区' },
          { value: '华容区', label: '华容区' },
          { value: '鄂城区', label: '鄂城区' }
        ]
      },
      {
        value: '荆门市',
        label: '荆门市',
        children: [
          { value: '东宝区', label: '东宝区' },
          { value: '掇刀区', label: '掇刀区' },
          { value: '京山市', label: '京山市' },
          { value: '沙洋县', label: '沙洋县' },
          { value: '钟祥市', label: '钟祥市' }
        ]
      },
      {
        value: '孝感市',
        label: '孝感市',
        children: [
          { value: '孝南区', label: '孝南区' },
          { value: '孝昌县', label: '孝昌县' },
          { value: '大悟县', label: '大悟县' },
          { value: '云梦县', label: '云梦县' },
          { value: '应城市', label: '应城市' },
          { value: '安陆市', label: '安陆市' },
          { value: '汉川市', label: '汉川市' }
        ]
      },
      {
        value: '荆州市',
        label: '荆州市',
        children: [
          { value: '沙市区', label: '沙市区' },
          { value: '荆州区', label: '荆州区' },
          { value: '公安县', label: '公安县' },
          { value: '监利市', label: '监利市' },
          { value: '江陵县', label: '江陵县' },
          { value: '石首市', label: '石首市' },
          { value: '洪湖市', label: '洪湖市' },
          { value: '松滋市', label: '松滋市' }
        ]
      },
      {
        value: '黄冈市',
        label: '黄冈市',
        children: [
          { value: '黄州区', label: '黄州区' },
          { value: '团风县', label: '团风县' },
          { value: '红安县', label: '红安县' },
          { value: '罗田县', label: '罗田县' },
          { value: '英山县', label: '英山县' },
          { value: '浠水县', label: '浠水县' },
          { value: '蕲春县', label: '蕲春县' },
          { value: '黄梅县', label: '黄梅县' },
          { value: '麻城市', label: '麻城市' },
          { value: '武穴市', label: '武穴市' }
        ]
      },
      {
        value: '咸宁市',
        label: '咸宁市',
        children: [
          { value: '咸安区', label: '咸安区' },
          { value: '嘉鱼县', label: '嘉鱼县' },
          { value: '通城县', label: '通城县' },
          { value: '崇阳县', label: '崇阳县' },
          { value: '通山县', label: '通山县' },
          { value: '赤壁市', label: '赤壁市' }
        ]
      },
      {
        value: '随州市',
        label: '随州市',
        children: [
          { value: '曾都区', label: '曾都区' },
          { value: '随县', label: '随县' },
          { value: '广水市', label: '广水市' }
        ]
      },
      {
        value: '恩施土家族苗族自治州',
        label: '恩施土家族苗族自治州',
        children: [
          { value: '恩施市', label: '恩施市' },
          { value: '利川市', label: '利川市' },
          { value: '建始县', label: '建始县' },
          { value: '巴东县', label: '巴东县' },
          { value: '宣恩县', label: '宣恩县' },
          { value: '咸丰县', label: '咸丰县' },
          { value: '来凤县', label: '来凤县' },
          { value: '鹤峰县', label: '鹤峰县' }
        ]
      },
      {
        value: '仙桃市',
        label: '仙桃市'
      },
      {
        value: '潜江市',
        label: '潜江市'
      },
      {
        value: '天门市',
        label: '天门市'
      },
      {
        value: '神农架林区',
        label: '神农架林区'
      }
    ]
  }
];
const regionProps = {
  expandTrigger: 'click',
  value: 'value',
  label: 'label',
  children: 'children',
};

// 处理地址选择变化
const handleAddressChange = (value) => {
  // 这里可以根据选择的地区数据拼接地址，或者直接使用
  // 示例：studentModel.value.address = value.join('-');
};

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入学生姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  facultyId: [
    { required: true, message: '请选择院系', trigger: 'change' }
  ],
  classId: [
    { required: true, message: '请选择班级', trigger: 'change' }
  ],
  birthday: [
    { required: true, message: '请选择生日', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入电话号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  sex: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ]
};

// 表单引用
const formRef = ref(null);

// 打开添加抽屉
const openAddDrawer = () => {
  visibleDrawer.value = true;
  clearClassModel();
};

// 清空表单数据
const clearClassModel = () => {
  studentModel.value = {
    studentId: '',
    name: '',
    birthday: '',
    facultyId: '',
    classId: '',
    address: '',
    addressCascader: [],
    addressDetail: '',
    phone: '',
    remark: '',
    sex: '',
  };
};

// 获取学生列表
const studentAllList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      name: name.value,
      facultyName: facultyName.value
    };
    console.log('搜索参数:', params); // 添加日志
    const result = await studentGetAllService(params);
    total.value = result.data.total;
    studentAll.value = result.data.records;
  } catch (error) {
    ElMessage.error('获取学生列表失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 重置搜索
const resetSearch = () => {
  name.value = '';
  facultyName.value = '';
  pageNum.value = 1; // 重置页码
  studentAllList();
};

// 分页大小变化
const onSizeChange = (size) => {
  pageSize.value = size;
  studentAllList();
};

// 当前页码变化
const onCurrentChange = (num) => {
  pageNum.value = num;
  studentAllList();
};

// 编辑学生
const handleEdit = (row) => {
  studentModel.value = {
    ...row,
    addressCascader: [], // 这里需要根据实际存储的地址数据，转换为地区选择器的格式
    addressDetail: '',
  };
  visibleDrawer1.value = true; // 打开编辑抽屉
};

// 保存编辑后的学生
const saveEditedClass = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        studentModel.value.address = studentModel.value.addressCascader.join('-') + studentModel.value.addressDetail;
        const result = await studentEditService(studentModel.value);
        ElMessage.success(result.msg? result.msg : '编辑成功');
        visibleDrawer1.value = false;
        clearClassModel();
        studentAllList();
      } catch (error) {
        ElMessage.error('编辑学生失败，请检查网络或联系管理员');
        console.error(error);
      }
    } else {
      ElMessage.error('表单校验不通过，请检查输入内容');
    }
  });
};

// 删除学生
const handleDelete = async (row) => {
  try {
    const confirm = window.confirm('确定要删除该学生吗？');
    if (confirm) {
      const result = await studentDeleteService(row.studentId);
      ElMessage.success(result.msg? result.msg : '删除成功');
      studentAllList();
    }
  } catch (error) {
    ElMessage.error('删除学生失败，请检查网络或联系管理员');
    console.error(error);
  }
};

// 添加学生
const addClass = async () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        studentModel.value.address = studentModel.value.addressCascader.join('-') + studentModel.value.addressDetail;
        const result = await studentAddService(studentModel.value);
        ElMessage.success(result.msg? result.msg : '添加成功');
        visibleDrawer.value = false;
        clearClassModel();
        studentAllList();
      } catch (error) {
        ElMessage.error('添加学生失败，请检查网络或联系管理员');
        console.error(error);
      }
    } else {
      ElMessage.error('表单校验不通过，请检查输入内容');
    }
  });
};
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