<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { userInfoService } from '@/api/user.js';

// 用户信息
const userInfo = ref({
  userId: '',
  username: '',
  realName: '',
  sex: '',
  age: '',
  phone: '',
  email: '',
  facultyName: '',
  className: '',
  studentId: '',
  role: ''
});

// 计算年龄
const calculateAge = (birthday) => {
  if (!birthday) return '';
  const today = new Date();
  const birthDate = new Date(birthday);
  let age = today.getFullYear() - birthDate.getFullYear();
  const monthDiff = today.getMonth() - birthDate.getMonth();
  
  // 如果还没过生日，年龄减1
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
    age--;
  }
  
  return age;
};

// 获取用户信息
const getUserInfo = async () => {
  try {
    const result = await userInfoService();
    if (result.data) {
      userInfo.value = result.data;
    }
  } catch (error) {
    ElMessage.error('获取用户信息失败');
    console.error(error);
  }
};

onMounted(() => {
  getUserInfo();
});
</script>

<template>
  <el-card class="info-container">
    <template #header>
      <div class="header">
        <div class="title">
          <el-icon><User /></el-icon>
          <span>个人信息</span>
        </div>
      </div>
    </template>

    <div class="info-content">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学号">{{ userInfo.studentId }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ userInfo.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ userInfo.sex === 1 ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="生日">{{ userInfo.birthday }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ calculateAge(userInfo.birthday) }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ userInfo.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
        <el-descriptions-item label="院系">{{ userInfo.facultyName }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ userInfo.classId }}</el-descriptions-item>
        <el-descriptions-item label="地址">{{ userInfo.address }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ userInfo.remark }}</el-descriptions-item>
      </el-descriptions>
    </div>
  </el-card>
</template>

<style lang="scss" scoped>
.info-container {
  min-height: 100%;
  box-sizing: border-box;

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 18px;
      font-weight: bold;
    }
  }

  .info-content {
    margin-top: 20px;
  }
}
</style> 