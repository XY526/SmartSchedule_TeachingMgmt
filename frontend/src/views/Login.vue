<template>
    <el-row class="login-page">
      <!-- Add dialog component -->
      <el-dialog
        v-model="showExampleDialog"
        title="示例账号信息"
        width="30%"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :show-close="false"
      >
        <div class="example-account">
          <h1>管理员账号</h1>
          <p><strong>用户名：</strong>admin</p>
          <p><strong>密码：</strong>123456</p>
          <h1>学生账号</h1>
          <p><strong>用户名：</strong>student03</p>
          <p><strong>密码：</strong>123456</p>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button type="primary" @click="showExampleDialog = false">
              我知道了
            </el-button>
          </span>
        </template>
      </el-dialog>
      
      <el-col :span="14" class="bg"></el-col>
      
      <el-col :span="6" :offset="2" class="form">
        <div class ="title">智慧日程教务系统</div>
        <!-- 注册表单 -->
        <el-form ref="registerFormRef" size="large" autocomplete="off" v-if="isRegister" :model="registerData" :rules="rules">
          <el-form-item>
            <h1>注册</h1>
          </el-form-item>
          <el-form-item prop="username">
            <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="registerData.username"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input :prefix-icon="Lock" type="password" placeholder="请输入密码" v-model="registerData.password"></el-input>
          </el-form-item>
          <el-form-item prop="rePassword">
            <el-input :prefix-icon="Lock" type="password" placeholder="请输入再次密码" v-model="registerData.rePassword"></el-input>
          </el-form-item>
          <el-form-item prop="email">
            <el-input :prefix-icon="User" placeholder="请输入邮箱" v-model="registerData.email"></el-input>
          </el-form-item>
          <el-form-item prop="verificationCode">
            <div style="display: flex; width: 100%;">
              <el-input :prefix-icon="Lock" placeholder="请输入验证码" v-model="registerData.verificationCode" style="flex-grow: 1; margin-right: 10px;"></el-input>
              <el-button type="primary" @click="sendVerificationCode" :disabled="isSendingCode">{{ isSendingCode ? '发送中...' : '发送验证码' }}</el-button>
            </div>
          </el-form-item>
          <!-- 注册按钮 -->
          <el-form-item>
            <div style="display: flex; gap: 10px;">
              <el-button class="button" type="primary" auto-insert-space @click="handleRegister" :loading="isRegisterLoading">
                注册
              </el-button>
              <el-button class="button" type="info" @click="showExampleDialog = true">
                示例账号
              </el-button>
            </div>
          </el-form-item>
          <el-form-item class="flex">
            <el-link type="info" :underline="false" @click="switchToLogin">
              ← 返回
            </el-link>
          </el-form-item>
        </el-form>
        <!-- 登录表单 -->
        <el-form ref="loginFormRef" size="large" autocomplete="off" v-else :model="loginData" :rules="rules">
   
          <el-form-item>
            <h1>登录</h1>
          </el-form-item>
          <el-form-item prop="username">
            <el-input :prefix-icon="User" placeholder="请输入用户名" v-model="loginData.username"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input name="password" :prefix-icon="Lock" type="password" placeholder="请输入密码" v-model="loginData.password"></el-input>
          </el-form-item>
          <el-form-item class="flex">
            <div class="flex">
              <el-checkbox v-model="loginData.remember">记住我</el-checkbox>
              <el-link type="primary" :underline="false">忘记密码？</el-link>
            </div>
          </el-form-item>
          <!-- 登录按钮 -->
          <el-form-item>
            <el-button class="button" type="primary" auto-insert-space @click="handleLogin" :loading="isLoginLoading">
              登录
            </el-button>
          </el-form-item>
          <el-form-item class="flex">
            <div class="flex" style="width: 100%;">
              <el-link type="info" :underline="false" @click="switchToRegister">
                注册 →
              </el-link>
              <el-link type="info" :underline="false" @click="showExampleDialog = true">
                示例账号
              </el-link>
            </div>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </template>
  
  <script setup>
  import { User, Lock } from '@element-plus/icons-vue';
  import { ref, onMounted } from 'vue';
  import { ElMessage } from 'element-plus';
  import { userRegisterService, userLoginService, sendRegisterCodeService } from '@/api/user.js';
  import { useTokenStore } from '@/stores/token.js';
  import { useRouter } from 'vue-router';
  
  // 控制注册与登录表单的显示， 默认显示登录
  const isRegister = ref(false);
  
  // 定义登录数据模型
  const loginData = ref({
    username: '',
    password: '',
    remember: false
  });
  
  // 定义注册数据模型
  const registerData = ref({
    username: '',
    password: '',
    rePassword: '',
    email: '',
    verificationCode: '',
  });
  
  // 校验密码的函数
  const checkRePassword = (rule, value, callback) => {
    if (value === '') {
      callback(new Error('请再次确认密码'));
    } else if (value!== registerData.value.password) {
      callback(new Error('请确保两次输入的密码一样'));
    } else {
      callback();
    }
  };
  
  // 定义表单校验规则
  const rules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 5, max: 16, message: '长度为5~16位非空字符', trigger: 'blur' },
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 5, max: 16, message: '长度为5~16位非空字符', trigger: 'blur' },
    ],
    rePassword: [
      { validator: checkRePassword, trigger: 'blur' },
    ],
    email: [
      { required: true, message: '请输入邮箱地址', trigger: 'blur' },
      { type: 'email', message: '请输入有效的邮箱地址', trigger: ['blur', 'change'] },
    ],
    verificationCode: [
      { required: true, message: '请输入验证码', trigger: 'blur' },
      { min: 4, max: 6, message: '验证码长度通常为4-6位', trigger: 'blur' },
    ],
  };
  
  // 表单引用
  const registerFormRef = ref(null);
  const loginFormRef = ref(null);
  
  // 加载状态
  const isRegisterLoading = ref(false);
  const isLoginLoading = ref(false);
  const isSendingCode = ref(false);
  const codeSent = ref(false);
  const countdown = ref(0);
  let countdownTimer = null;
  
  // 路由和 token 存储
  const router = useRouter();
  const tokenStore = useTokenStore();
  
  // Add dialog control
  const showExampleDialog = ref(true);
  
  // 处理注册
  const handleRegister = async () => {
    registerFormRef.value.validate(async (valid) => {
      if (valid) {
        isRegisterLoading.value = true;
        try {
          const result = await userRegisterService({
            username: registerData.value.username,
            password: registerData.value.password,
            email: registerData.value.email,
            code: registerData.value.verificationCode
          });
          if (result.code === 0) {
            ElMessage.success(result.msg? result.msg : '注册成功');
            switchToLogin();
          } else {
            ElMessage.error(result.msg? result.msg : '注册失败，请检查验证码或稍后重试');
          }
        } catch (error) {
          console.error('注册请求出错:', error);
          ElMessage.error('注册请求出错，请检查网络或稍后重试');
        } finally {
          isRegisterLoading.value = false;
        }
      } else {
        ElMessage.warning('请检查表单信息是否填写完整或符合要求');
      }
    });
  };
  
  // 处理登录
  const handleLogin = async () => {
    loginFormRef.value.validate(async (valid) => {
      if (valid) {
        isLoginLoading.value = true;
        try {
          console.log('开始登录请求，数据：', loginData.value);
          const result = await userLoginService(loginData.value);
          console.log('登录响应完整数据：', JSON.stringify(result));
          
          if (result.code === 0 || result.code === 200) {
            // 检查用户状态
            if (result.data.status === 0) { // 假设0表示禁用状态
              ElMessage.error('该用户已被禁用，请联系管理员');
              isLoginLoading.value = false;
              return;
            }
            
            ElMessage.success(result.message || '登录成功');
            
            // 检查返回数据结构
            console.log('登录响应数据结构：', {
              code: result.code,
              message: result.message,
              data: result.data
            });
            
            // 获取token，支持多种可能的数据结构
            let token = null;
            if (typeof result.data === 'string') {
              token = result.data;
            } else if (result.data && result.data.token) {
              token = result.data.token;
            } else if (result.data && result.data.accessToken) {
              token = result.data.accessToken;
            }
            
            console.log('提取的token:', token);
            
            // 检查token格式
            if (!token) {
              console.error('Token为空，返回数据:', result);
              ElMessage.error('登录失败：未获取到有效的登录凭证');
              isLoginLoading.value = false;
              return;
            }
            
            // 保存token
            console.log('开始保存token，记住我状态:', loginData.value.remember);
            tokenStore.setToken(token, loginData.value.remember);
            
            // 验证token是否保存成功
            const savedToken = tokenStore.getToken();
            console.log('验证保存的token:', savedToken);
            
            // 验证存储中的token
            const storedToken = localStorage.getItem('token') || sessionStorage.getItem('token');
            console.log('存储中的token:', storedToken);
            
            if (savedToken && storedToken) {
              console.log('Token保存成功，准备跳转到首页');
              // 跳转到首页
              router.push('/');
            } else {
              console.error('Token保存失败:', {
                savedToken,
                storedToken,
                remember: loginData.value.remember
              });
              ElMessage.error('登录状态保存失败，请重试');
            }
          } else if (result.code === 400) {
            // 处理多个用户记录的情况
            ElMessage.error('系统检测到多个相同用户名的账号，请联系管理员处理');
          } else {
            console.error('登录失败:', result);
            ElMessage.error(result.message || '登录失败，请稍后重试');
          }
        } catch (error) {
          console.error('登录请求出错:', error);
          ElMessage.error('登录请求出错，请检查网络或稍后重试');
        } finally {
          isLoginLoading.value = false;
        }
      } else {
        ElMessage.warning('请检查表单信息是否填写完整或符合要求');
      }
    });
  };
  
  // 切换到登录
  const switchToLogin = () => {
    isRegister.value = false;
    clearRegisterData();
    stopCountdown();
  };
  
  // 切换到注册
  const switchToRegister = () => {
    isRegister.value = true;
    clearRegisterData();
    codeSent.value = false;
    countdown.value = 0;
    stopCountdown();
  };
  
  // 定义函数,清空数据模型的数据
  const clearRegisterData = () => {
    registerData.value = {
      username: '',
      password: '',
      rePassword: '',
      email: '',
      verificationCode: '',
    };
    loginData.value = {
      username: '',
      password: '',
      remember: false
    };
  };
  
  // Function to send verification code
  const sendVerificationCode = async () => {
    if (!registerData.value.email) {
      ElMessage.warning('请先填写邮箱地址');
      return;
    }

    // Basic email format validation before sending
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(registerData.value.email)) {
      ElMessage.warning('请输入有效的邮箱地址');
      return;
    }

    isSendingCode.value = true;
    codeSent.value = false;
    countdown.value = 60;

    try {
      const result = await sendRegisterCodeService({ email: registerData.value.email });

      if (result.code === 0) {
        ElMessage.success(result.msg ? result.msg : '验证码发送成功，请查收邮件');
        codeSent.value = true;
        startCountdown();
      } else {
        ElMessage.error(result.msg ? result.msg : '验证码发送失败，请稍后重试');
        countdown.value = 0;
      }
    } catch (error) {
      console.error('发送验证码请求出错:', error);
      ElMessage.error('发送验证码请求出错，请检查网络或稍后重试');
      countdown.value = 0;
    } finally {
      isSendingCode.value = false;
    }
  };

  // Function to start countdown timer
  const startCountdown = () => {
    stopCountdown();
    countdownTimer = setInterval(() => {
      if (countdown.value > 0) {
        countdown.value--;
      } else {
        stopCountdown();
        codeSent.value = false;
      }
    }, 1000);
  };

  // Function to stop countdown timer
  const stopCountdown = () => {
    if (countdownTimer) {
      clearInterval(countdownTimer);
      countdownTimer = null;
    }
  };

  // Add onMounted hook to show dialog
  onMounted(() => {
    showExampleDialog.value = true;
  });
  </script>
  
  <style lang="scss" scoped>
  /* 样式 */
  .login-page {
    height: 100vh;
    background-color: #fff;
  
    .title {
  font-size: 50px;
  color: #8b4513; /* 棕色系，类似复古色调 */
  text-align: center;
  font-family: "Georgia", serif; /* 使用衬线字体，增添复古感 */
  border-bottom: 2px solid #8b4513; /* 底部添加边框 */
  padding-bottom: 10px; /* 增加底部内边距，让边框和文字有间隔 */
  margin-top: -20px; /* 设置一个负的上外边距，使元素往上移动，数值可根据实际情况调整 */
}
    @keyframes glow {
      from {
        text-shadow: 0 0 5px #0099ff, 0 0 10px #0099ff, 0 0 15px #0099ff;
      }
      to {
        text-shadow: 0 0 10px #ff66cc, 0 0 20px #ff66cc, 0 0 30px #ff66cc;
      }
    }
    .bg {
      background: url('@/assets/logo2.png') no-repeat 60% center / 240px auto,
        url('@/assets/login_bg.jpg') no-repeat center / cover;
      border-radius: 0 20px 20px 0;
    }
  
    .form {
      display: flex;
      flex-direction: column;
      justify-content: center;
      user-select: none;
  
      .title {
        margin: 0 auto;
      }
  
      .button {
        width: 100%;
      }
  
      .flex {
        width: 100%;
        display: flex;
        justify-content: space-between;
      }
    }
  }

  /* Add dialog styles */
  .example-account {
    text-align: center;
    font-size: 16px;
    line-height: 1.8;
    
    p {
      margin: 10px 0;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: center;
    width: 100%;
  }
  </style>