<script setup>
import { ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import useUserInfoStore from '@/stores/userInfo.js'
import { ElMessage } from 'element-plus'
import { userInfoUpdateService, uploadAvatarService } from '@/api/user.js'

const userInfoStore = useUserInfoStore();
const userInfo = ref({...userInfoStore.info})
const dialogVisible = ref(false)
const tempAvatar = ref('')
const uploadRef = ref(null)

// 获取token
const getToken = () => {
  try {
    return localStorage.getItem('token') || ''
  } catch (error) {
    console.error('获取token失败:', error)
    return ''
  }
}

// 头像上传前的验证
const beforeAvatarUpload = (file) => {
  console.log('开始上传文件:', file)
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 5

  if (!isJPG) {
    ElMessage.error('头像只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 5MB!')
    return false
  }
  return true
}

// 头像上传成功处理
const handleAvatarSuccess = (response) => {
  console.log('上传响应:', response)
  if (response.code === 200) {
    tempAvatar.value = response.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.msg || '上传失败')
  }
}

// 头像上传失败处理
const handleAvatarError = (error) => {
  console.error('上传失败:', error)
  ElMessage.error('上传失败，请重试')
}

// 确认上传头像
const confirmUpload = async () => {
  if (tempAvatar.value) {
    try {
      userInfo.value.avatar = tempAvatar.value
      const result = await userInfoUpdateService(userInfo.value)
      if (result.code === 200) {
        userInfoStore.setInfo(userInfo.value)
        ElMessage.success('头像更新成功')
        dialogVisible.value = false
      } else {
        ElMessage.error(result.msg || '更新失败')
      }
    } catch (error) {
      console.error('更新失败:', error)
      ElMessage.error('更新失败，请重试')
    }
  }
}

// 打开头像上传对话框
const handleAvatarUpload = () => {
  console.log('打开头像上传对话框')
  dialogVisible.value = true
  tempAvatar.value = '' // 清空临时头像
}

// 修改个人信息
const updateUserInfo = async () => {
  try {
    console.log('更新用户信息:', userInfo.value)
    const result = await userInfoUpdateService(userInfo.value)
    ElMessage.success(result.msg || '修改成功')
    // 修改pinia中的个人信息
    userInfoStore.setInfo(userInfo.value)
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error('修改失败')
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户昵称', trigger: 'blur' },
    {
      pattern: /^\S{2,10}$/,
      message: '昵称必须是2-15位的非空字符串',
      trigger: 'blur'
    }
  ],
  password: [
    { required: true, message: '请输入用户密码', trigger: 'blur' },
    {
      pattern: /^\S{2,10}$/,
      message: '昵称必须是2-15位的非空字符串',
      trigger: 'blur'
    }
  ],
}
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>基本资料</span>
      </div>
    </template>
    <el-row>
      <el-col :span="12">
        <el-form :model="userInfo" :rules="rules" label-width="100px" size="large">
          <!-- 头像上传部分 -->
          <el-form-item label="用户头像">
            <div class="avatar-uploader">
              <el-avatar 
                :size="100" 
                :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7e/3d6b9ba0d9f3d81f78a0c0850dfb5jpeg.jpeg'"
                @error="() => true"
              />
              <el-button 
                type="primary" 
                @click="handleAvatarUpload"
                style="margin-left: 20px"
              >
                更换头像
              </el-button>
            </div>
          </el-form-item>
          
          <el-form-item label="登录名称">
            <el-input v-model="userInfo.username" disabled></el-input>
          </el-form-item>
          <el-form-item label="用户昵称" prop="username">
            <el-input v-model="userInfo.username"></el-input>
          </el-form-item>
          <el-form-item label="用户密码" prop="password">
            <el-input v-model="userInfo.password" type="password"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="updateUserInfo">提交修改</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>

    <!-- 头像上传对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="更换头像"
      width="400px"
      :close-on-click-modal="false"
      :append-to-body="true"
      :destroy-on-close="true"
    >
      <div class="upload-container">
        <el-upload
          ref="uploadRef"
          class="avatar-uploader"
          :action="'/api/user/avatar'"
          :headers="{
            Authorization: 'Bearer ' + getToken()
          }"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :on-error="handleAvatarError"
          :before-upload="beforeAvatarUpload"
          :auto-upload="true"
          name="file"
          accept="image/jpeg,image/png"
        >
          <template #trigger>
            <div class="upload-area">
              <img v-if="tempAvatar" :src="tempAvatar" class="avatar-preview" />
              <div v-else class="upload-placeholder">
                <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
                <div class="upload-text">点击上传头像</div>
              </div>
            </div>
          </template>
        </el-upload>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmUpload" :disabled="!tempAvatar">确认</el-button>
        </span>
      </template>
    </el-dialog>
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

.upload-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.avatar-uploader {
  width: 178px;
  height: 178px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);

  &:hover {
    border-color: var(--el-color-primary);
  }

  .avatar-preview {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .upload-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;
    color: var(--el-text-color-secondary);
    font-size: 14px;

    .avatar-uploader-icon {
      font-size: 28px;
      margin-bottom: 8px;
    }

    .upload-text {
      font-size: 12px;
    }
  }
}

.upload-area {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  margin-bottom: 8px;
}

.upload-text {
  color: #8c939d;
  font-size: 14px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>