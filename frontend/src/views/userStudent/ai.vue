<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from 'vue';
import { ElCard, ElLoading, ElMessage, ElDialog, ElButton, ElDivider, ElInput } from 'element-plus';

// Import AI API service
import { analyzeData, chatWithAI } from '@/api/ai';
import { curriculumGetAllService } from '@/api/curriculum.js';
import { scoreGetByStudentService } from '@/api/score.js';
import useUserInfoStore from '@/stores/userInfo.js';
import { courseGetAllService, getCompleteCourseSchedule } from '@/api/course.js';
import { teacherGetAllService } from '@/api/teacher.js';
import { locationGetAllService } from '@/api/location.js';
import { classRoomGetAllService } from '@/api/classRoom.js';

// TODO: Import functions to get student's raw data from your backend
// import { getStudentSchedule } from '@/api/studentSchedule.js'; // Replace with your actual path
// import { getStudentScores } from '@/api/studentScore.js'; // Replace with your actual path

// --- View State ---
const currentView = ref('analysis'); // 'analysis' or 'chat'

const switchToAnalysisView = () => {
    currentView.value = 'analysis';
    // You might want to stop any ongoing chat activities if necessary
};

const switchToChatView = () => {
    currentView.value = 'chat';
    nextTick(() => {
        scrollToBottom(); // Scroll to bottom when switching to chat view
    });
};

// --- Analysis Feature State and Logic ---
const analysisResult = ref(null); // Stores the complete fetched analysis data
const hasAnalysisData = computed(() => !!localStorage.getItem('aiAnalysisData'));

const isLoading = ref(false); // Renamed, controlled by dialog
const analysisDialogVisible = ref(false);
const dialogMessage = ref('正在生成分析报告，请耐心等待...');

const currentDisplayedText = ref('');
let fullAnalysisText = '';
let intervalId = null;
const typingSpeed = 30; // milliseconds per character

// Function to fetch analysis data from backend AI service
const fetchAnalysis = async () => {
    try {
        const userStore = useUserInfoStore();
        const userInfo = userStore.info;

        if (!userInfo || !userInfo.studentId) {
            throw new Error('未获取到学生信息，请重新登录');
        }

        // 使用新的接口获取完整的课程信息
        const scheduleResult = await getCompleteCourseSchedule(userInfo.classId);
        
        // 获取学生成绩数据
        const scoreResult = await scoreGetByStudentService(userInfo.studentId);

        if (!scheduleResult.data || !scoreResult.data) {
            throw new Error('获取学生数据失败');
        }

        // 格式化数据用于AI分析
        const rawUserDataForAnalysis = {
            schedule: scheduleResult.data.map(course => ({
                courseName: course.courseName,
                weekday: course.weekday,
                section: course.section,
                locationName: course.locationName,
                teacherName: course.teacherName,
                startWeek: course.startWeek,
                endWeek: course.endWeek,
                schoolYear: course.schoolYear,
                term: course.term
            })),
            scores: scoreResult.data.map(score => {
                // 从课程表中找到对应的课程信息
                const courseInfo = scheduleResult.data.find(c => c.courseId === score.courseId);
                return {
                    courseName: courseInfo ? courseInfo.courseName : '未知课程',
                    score: score.score,
                    examType: score.examType,
                    examDate: score.examDate,
                    teacherName: courseInfo ? courseInfo.teacherName : '未知教师'
                };
            })
        };

        // 构建更清晰的提示词
        const prompt = `请分析以下学生的课程安排和成绩数据，并提供建议：

课程安排：
${rawUserDataForAnalysis.schedule.map(course => 
    `- ${course.courseName}：
  * 上课时间：第${course.weekday}周 第${course.section}节
  * 上课地点：${course.locationName}
  * 授课教师：${course.teacherName}
  * 开课周次：第${course.startWeek}周到第${course.endWeek}周
  * 学年学期：${course.schoolYear}学年 第${course.term}学期`
).join('\n')}

成绩情况：
${rawUserDataForAnalysis.scores.map(score => 
    `- ${score.courseName}：
  * 考试成绩：${score.score}分
  * 考试类型：${score.examType === 1 ? '期中考试' : score.examType === 2 ? '期末考试' : '其他考试'}
  * 考试日期：${score.examDate}
  * 授课教师：${score.teacherName}`
).join('\n')}

请提供：
1. 对课程安排的分析
2. 对成绩情况的分析
3. 具体的改进建议`;

        // 添加调试日志
        console.log('Schedule Data:', scheduleResult.data);
        console.log('Score Data:', scoreResult.data);
        console.log('Formatted Data for AI:', rawUserDataForAnalysis);
        console.log('AI Prompt:', prompt);

        // 调用后端 AI 分析接口
        const response = await analyzeData({
            ...rawUserDataForAnalysis,
            prompt: prompt
        });
        console.log('AI Analysis Response:', response);

        if (response.code === 0) {
            // 成功获取分析结果
            const finalAnalysisData = {
                scheduleAnalysis: response.data.scheduleAnalysis || '',
                scoreAnalysis: response.data.scoreAnalysis || '',
                recommendations: response.data.recommendations || []
            };
            return finalAnalysisData;
        } else {
            // 后端返回错误
            throw new Error(response.message || '后端返回分析错误');
        }

    } catch (error) {
        console.error('Error fetching analysis:', error);
        throw new Error(`加载 AI 分析报告失败: ${error.message || '未知错误'}`);
    }
};

// Function to start the analysis process
const startAnalysis = async () => {
    analysisDialogVisible.value = true;
    dialogMessage.value = '正在生成分析报告，请耐心等待...';
    isLoading.value = true; // For the dialog loading spinner
    currentDisplayedText.value = ''; // Clear previous text
    fullAnalysisText = '';
    stopTypingAnimation(); // Clear any existing animation
    analysisResult.value = null; // Clear previous result

    try {
        const data = await fetchAnalysis(); // Call the updated fetchAnalysis
        analysisResult.value = data;
        localStorage.setItem('aiAnalysisData', JSON.stringify(data));

        // Combine analysis parts into a single string for typing animation
        let combinedText = '分析报告:\n\n';
        if(data.scheduleAnalysis) {
            // 处理日程分析部分
            combinedText += `日程分析:\n${data.scheduleAnalysis
                .replace(/^####\s*(.*?)$/gm, '<h4>$1</h4>') // 将 #### 开头的行转换为 h4 标题
                .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') // 将 **text** 转换为 <strong>text</strong>
                .replace(/\*/g, '') // 移除单个 *
                .replace(/^(\s*)-/gm, '<li>$1') // 将行首的 - 转换为 <li>
                .replace(/^(\s*)(\d+)\./gm, '<li>$1') // 将行首的数字编号转换为 <li>
                .replace(/^--$/gm, '<hr>') // 将单独的 -- 转换为水平线
                .replace(/^-$/gm, '') // 删除单独的 -
                .replace(/\n\s*\n\s*\n/g, '\n\n') // 删除多余的空行
            }\n\n`;
        }
        if(data.scoreAnalysis) {
            // 处理成绩分析部分
            combinedText += `成绩分析:\n${data.scoreAnalysis
                .replace(/^####\s*(.*?)$/gm, '<h4>$1</h4>') // 将 #### 开头的行转换为 h4 标题
                .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') // 将 **text** 转换为 <strong>text</strong>
                .replace(/\*/g, '') // 移除单个 *
                .replace(/^(\s*)-/gm, '<li>$1') // 将行首的 - 转换为 <li>
                .replace(/^(\s*)(\d+)\./gm, '<li>$1') // 将行首的数字编号转换为 <li>
                .replace(/^--$/gm, '<hr>') // 将单独的 -- 转换为水平线
                .replace(/^-$/gm, '') // 删除单独的 -
                .replace(/\n\s*\n\s*\n/g, '\n\n') // 删除多余的空行
            }\n\n`;
        }
        if(data.recommendations && data.recommendations.length > 0) {
            combinedText += '建议:\n';
            data.recommendations.forEach((rec, index) => {
                // 处理建议部分
                combinedText += `${index + 1}. ${rec
                    .replace(/^####\s*(.*?)$/gm, '<h4>$1</h4>') // 将 #### 开头的行转换为 h4 标题
                    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') // 将 **text** 转换为 <strong>text</strong>
                    .replace(/\*/g, '') // 移除单个 *
                    .replace(/^(\s*)-/gm, '<li>$1') // 将行首的 - 转换为 <li>
                    .replace(/^(\s*)(\d+)\./gm, '<li>$1') // 将行首的数字编号转换为 <li>
                    .replace(/^--$/gm, '<hr>') // 将单独的 -- 转换为水平线
                    .replace(/^-$/gm, '') // 删除单独的 -
                    .replace(/\n\s*\n\s*\n/g, '\n\n') // 删除多余的空行
                }\n`;
            });
        }

        fullAnalysisText = combinedText;
        isLoading.value = false; // Hide dialog spinner
        analysisDialogVisible.value = false; // Close the dialog
        startTypingAnimation(); // Start typing on the main page

    } catch (error) {
        console.error(error);
        dialogMessage.value = error.message || '分析失败';
        isLoading.value = false; // Hide dialog spinner
        // Keep dialog open briefly to show error, or change dialog type
        // For now, let\'s just show an ElMessage error and close dialog
        ElMessage.error(dialogMessage.value);
        analysisDialogVisible.value = false;
    }
};

// Function to start the typing animation
const startTypingAnimation = () => {
    stopTypingAnimation(); // Ensure no multiple intervals are running
    let i = 0;
    intervalId = setInterval(() => {
        if (i < fullAnalysisText.length) {
            currentDisplayedText.value += fullAnalysisText.charAt(i);
            i++;
        } else {
            stopTypingAnimation();
        }
    }, typingSpeed);
};

// Function to stop the typing animation
const stopTypingAnimation = () => {
    if (intervalId) {
        clearInterval(intervalId);
        intervalId = null;
    }
};

// On component mount, check local storage and load data if available
onMounted(() => {
    const savedAnalysis = localStorage.getItem('aiAnalysisData');
    if (savedAnalysis) {
        try {
            analysisResult.value = JSON.parse(savedAnalysis);
            // Pre-render the full text if data is loaded from local storage
            let combinedText = '分析报告:\n\n';
            if(analysisResult.value.scheduleAnalysis) {
                // 处理日程分析部分
                combinedText += `日程分析:\n${analysisResult.value.scheduleAnalysis
                    .replace(/^####\s*(.*?)$/gm, '<h4>$1</h4>') // 将 #### 开头的行转换为 h4 标题
                    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') // 将 **text** 转换为 <strong>text</strong>
                    .replace(/\*/g, '') // 移除单个 *
                    .replace(/^(\s*)-/gm, '<li>$1') // 将行首的 - 转换为 <li>
                    .replace(/^(\s*)(\d+)\./gm, '<li>$1') // 将行首的数字编号转换为 <li>
                    .replace(/^--$/gm, '<hr>') // 将单独的 -- 转换为水平线
                    .replace(/^-$/gm, '') // 删除单独的 -
                    .replace(/\n\s*\n\s*\n/g, '\n\n') // 删除多余的空行
                }\n\n`;
            }
            if(analysisResult.value.scoreAnalysis) {
                // 处理成绩分析部分
                combinedText += `成绩分析:\n${analysisResult.value.scoreAnalysis
                    .replace(/^####\s*(.*?)$/gm, '<h4>$1</h4>') // 将 #### 开头的行转换为 h4 标题
                    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') // 将 **text** 转换为 <strong>text</strong>
                    .replace(/\*/g, '') // 移除单个 *
                    .replace(/^(\s*)-/gm, '<li>$1') // 将行首的 - 转换为 <li>
                    .replace(/^(\s*)(\d+)\./gm, '<li>$1') // 将行首的数字编号转换为 <li>
                    .replace(/^--$/gm, '<hr>') // 将单独的 -- 转换为水平线
                    .replace(/^-$/gm, '') // 删除单独的 -
                    .replace(/\n\s*\n\s*\n/g, '\n\n') // 删除多余的空行
                }\n\n`;
            }
            if(analysisResult.value.recommendations && analysisResult.value.recommendations.length > 0) {
                combinedText += '建议:\n';
                analysisResult.value.recommendations.forEach((rec, index) => {
                    // 处理建议部分
                    combinedText += `${index + 1}. ${rec
                        .replace(/^####\s*(.*?)$/gm, '<h4>$1</h4>') // 将 #### 开头的行转换为 h4 标题
                        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>') // 将 **text** 转换为 <strong>text</strong>
                        .replace(/\*/g, '') // 移除单个 *
                        .replace(/^(\s*)-/gm, '<li>$1') // 将行首的 - 转换为 <li>
                        .replace(/^(\s*)(\d+)\./gm, '<li>$1') // 将行首的数字编号转换为 <li>
                        .replace(/^--$/gm, '<hr>') // 将单独的 -- 转换为水平线
                        .replace(/^-$/gm, '') // 删除单独的 -
                        .replace(/\n\s*\n\s*\n/g, '\n\n') // 删除多余的空行
                    }\n`;
                });
            }
            currentDisplayedText.value = combinedText; // Display immediately

        } catch (e) {
            console.error("Failed to parse local storage data:", e);
            localStorage.removeItem('aiAnalysisData'); // Clear invalid data
            // If parsing fails, treat as no data exists, button will show '分析'
            analysisResult.value = null;
            currentDisplayedText.value = '';
        }
    }
    // Initial view is analysis
    currentView.value = 'analysis';
});

// Cleanup interval on component unmount
onUnmounted(() => {
    stopTypingAnimation();
});

// --- Chat Feature State and Logic ---
const messages = ref([]); // Array to store chat messages { type: 'user' | 'ai', text: string }
const userInput = ref('');
const isSendingMessage = ref(false);
const chatBoxRef = ref(null); // Reference to the chat box element for auto-scrolling

// Function to get AI response from backend chat interface
const getAIResponse = async (message) => {
    isSendingMessage.value = true;
    try {
        // TODO: 调用后端 /api/ai/chat 接口
        const response = await chatWithAI(message);

         if (response.code === 0) {
            // 成功获取聊天回复
            isSendingMessage.value = false;
            return response.data.reply || ''; // 确保字段存在
        } else {
            // 后端返回错误
            isSendingMessage.value = false;
            throw new Error(response.message || '后端返回聊天错误');
        }

    } catch (error) {
        console.error('Error getting AI response from backend:', error);
        isSendingMessage.value = false;
        // Throw error to be caught by sendMessage
        throw new Error(`AI 助手响应失败: ${error.message || '未知错误'}`);
    }
};

// Function to send user message
const sendMessage = async () => {
    if (!userInput.value.trim() || isSendingMessage.value) {
        return;
    }

    const userMessage = userInput.value.trim();
    const timestamp = new Date().getTime();
    
    messages.value.push({
        type: 'user',
        text: userMessage,
        timestamp,
        status: 'sending'
    });
    
    userInput.value = '';

    if (currentView.value !== 'chat') {
        switchToChatView();
    }

    scrollToBottom();

    try {
        const aiResponse = await getAIResponse(userMessage);
        // 更新用户消息状态为已发送
        const lastUserMessage = messages.value[messages.value.length - 1];
        if (lastUserMessage.type === 'user') {
            lastUserMessage.status = 'sent';
        }
        
        messages.value.push({
            type: 'ai',
            text: aiResponse,
            timestamp: new Date().getTime()
        });
        
        scrollToBottom();
    } catch (error) {
        console.error('Error getting AI response:', error);
        // 更新用户消息状态为发送失败
        const lastUserMessage = messages.value[messages.value.length - 1];
        if (lastUserMessage.type === 'user') {
            lastUserMessage.status = 'error';
        }
        
        messages.value.push({
            type: 'ai',
            text: error.message || '抱歉，AI 助手当前无法响应。',
            timestamp: new Date().getTime()
        });
        scrollToBottom();
    }
};

// Function to scroll the chat box to the bottom
const scrollToBottom = () => {
    nextTick(() => {
        if (chatBoxRef.value) {
            // 确保滚动到底部，留出足够的空间给输入区域
            chatBoxRef.value.scrollTop = chatBoxRef.value.scrollHeight;
        }
    });
};

// Initial message from AI when component mounts (optional)
onMounted(() => {
    // You can add an initial AI message here if you like
    // messages.value.push({ type: 'ai', text: '你好！我是你的AI助手，有什么可以帮你？' });
    // scrollToBottom(); // Scroll to bottom after adding initial message
});

// 添加时间格式化函数
const formatTime = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  return date.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit'
  });
};

</script>

<template>
  <div class="ai-analysis-container">
    <!-- Analysis Section -->
    <div v-if="currentView === 'analysis'" class="analysis-section">
        <h2>AI 日程与成绩分析</h2>
        <div class="button-container">
            <el-button type="primary" @click="startAnalysis" :disabled="isLoading">
                {{ hasAnalysisData ? '重新分析' : '分析' }}
            </el-button>
            <el-button type="info" plain @click="switchToChatView" :disabled="isSendingMessage">
                开始聊天
            </el-button>
        </div>

        <!-- Analysis Results Area -->
        <div v-if="currentDisplayedText" class="analysis-results">
          <el-card shadow="hover" class="analysis-card">
             <!-- 使用 v-html 来渲染带有 HTML 标签的文本 -->
            <div class="analysis-text" v-html="currentDisplayedText"></div>
          </el-card>
        </div>

        <!-- Error Area (if analysisResult has error property and no text is displayed) -->
         <div v-else-if="analysisResult?.error && !currentDisplayedText" class="error-container">
             <el-card shadow="hover">
                <div class="error-content">
                    <p>无法加载分析报告. {{ analysisResult.error }}</p>
                </div>
             </el-card>
         </div>
    </div>

    <!-- Chat Section -->
    <div v-else-if="currentView === 'chat'" class="chat-section">
        <div class="chat-header">
            <el-button type="info" plain @click="switchToAnalysisView" size="small">
                 < 返回分析
            </el-button>
            <h2>AI 智能问答</h2>
            <div></div> <!-- Placeholder for centering title -->
        </div>

         <el-card shadow="hover" class="chat-card">
            <div ref="chatBoxRef" class="chat-box">
                <div v-for="(message, index) in messages" :key="index" :class="['message', message.type]">
                    <div :class="[
                        'message-content',
                        message.text.length < 20 ? 'short' : 'long'
                    ]">
                        {{ message.text }}
                    </div>
                    <div class="message-time">
                        {{ formatTime(message.timestamp) }}
                    </div>
                    <div v-if="message.type === 'user'" class="message-status">
                        <span class="icon" v-if="message.status === 'sending'">发送中...</span>
                        <span class="icon" v-else-if="message.status === 'sent'">已发送</span>
                        <span class="icon" v-else-if="message.status === 'error'">发送失败</span>
                    </div>
                </div>
            </div>
            <div class="input-area">
                 <el-input
                    v-model="userInput"
                    placeholder="向AI发送消息..."
                    @keyup.enter="sendMessage"
                    :disabled="isSendingMessage"
                 ></el-input>
                 <el-button type="primary" @click="sendMessage" :disabled="isSendingMessage || !userInput.trim()"
                    >
                    发送
                </el-button>
            </div>
         </el-card>
    </div>

    <!-- Analysis Dialog -->
    <el-dialog
        v-model="analysisDialogVisible"
        :title="dialogMessage"
        :close-on-click-modal="false"
        :close-on-press-escape="false"
        :show-close="false"
        width="30%"
        center
    >
        <div style="text-align: center;">
             <el-loading v-if="isLoading" v-model="isLoading" :text="dialogMessage" size="large"></el-loading>
             <p v-else>{{ dialogMessage }}</p>
        </div>
    </el-dialog>

  </div>
</template>

<style scoped>
.ai-analysis-container {
  padding: 20px;
  max-width: 1200px; /* 增加最大宽度，从 800px 改为 1200px */
  margin: 0 auto;
  min-height: 80vh;
  display: flex;
  flex-direction: column;
}

h1, h2 {
  color: #333;
  text-align: center;
  margin-bottom: 20px; /* Adjusted margin */
}

.analysis-section, .chat-section {
    margin-bottom: 30px;
}

.chat-section {
    height: calc(100vh - 170px);
    display: flex;
    flex-direction: column;
    background: #f6f8fa;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: none;
}

.chat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 24px;
    background: linear-gradient(to right, #409EFF, #36cfc9);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.chat-header h2 {
    color: #ffffff;
    font-size: 18px;
    font-weight: 600;
    margin: 0;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.chat-header .el-button {
    padding: 8px 16px;
    font-size: 14px;
    border-radius: 8px;
    background-color: rgba(255, 255, 255, 0.2);
    border: 1px solid rgba(255, 255, 255, 0.3);
    color: #ffffff;
    transition: all 0.3s ease;
}

.chat-header .el-button:hover {
    background-color: rgba(255, 255, 255, 0.3);
    transform: translateY(-1px);
}

.chat-header div:last-child {
    width: 0; /* Placeholder to help center title */
}

.button-container {
    text-align: center;
    margin-bottom: 30px; /* 增加底部间距 */
}

.button-container .el-button {
    padding: 12px 24px; /* 增加按钮大小 */
    font-size: 16px; /* 增加按钮字体大小 */
}

.loading-container, .error-container {
    text-align: center;
    margin-top: 50px;
}

.loading-content, .error-content {
    padding: 30px;
}

.analysis-results {
    margin-top: 20px;
    width: 100%; /* 确保结果区域占满容器宽度 */
}

.analysis-card {
  margin-bottom: 20px;
  width: 100%; /* 确保卡片占满容器宽度 */
}

.analysis-text {
  font-size: 16px;
  line-height: 1.8;
  color: #444;
  padding: 20px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.analysis-text h4 {
  font-size: 18px;
  font-weight: bold;
  color: #0056b3;
  margin: 20px 0 15px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e0e0e0;
}

.analysis-text li {
  margin: 8px 0;
  padding-left: 20px;
  position: relative;
  list-style-type: none;
}

.analysis-text li::before {
  content: "•";
  position: absolute;
  left: 0;
  color: #0056b3;
}

.analysis-text hr {
  border: none;
  border-top: 1px solid #e0e0e0;
  margin: 20px 0;
}

.analysis-text ul {
  list-style: none;
  padding-left: 0;
  margin: 10px 0;
}

.analysis-text strong {
  font-weight: bold;
  color: #0056b3;
}

/* 调整卡片标题样式 */
.card-header {
  font-size: 20px; /* 增加标题字体大小 */
  font-weight: bold;
  color: #0056b3;
  padding: 15px 20px; /* 增加标题内边距 */
}

h3 {
  color: #555;
  margin-top: 15px;
  margin-bottom: 10px;
}

p, li {
  color: #666;
  line-height: 1.6;
}

ul {
    padding-left: 20px;
}

li {
    margin-bottom: 8px;
}

/* --- Chat Specific Styles --- */
.chat-card {
    display: flex;
    flex-direction: column;
    height: calc(100vh - 270px);
    position: relative;
    background: #fff;
    border-radius: 0 0 16px 16px;
    box-shadow: none;
    overflow: hidden;
    padding: 0;
}

.chat-box {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 70px;
    overflow-y: auto;
    padding: 24px 16px;
    scroll-behavior: smooth;
    box-sizing: border-box;
}

/* 优化滚动条样式 */
.chat-box::-webkit-scrollbar {
    width: 8px;
    background-color: transparent;
}

.chat-box::-webkit-scrollbar-thumb {
    background-color: #c0c4cc;
    border-radius: 4px;
    border: 2px solid #f6f8fa;
}

.chat-box::-webkit-scrollbar-thumb:hover {
    background-color: #909399;
}

.chat-box::-webkit-scrollbar-track {
    background-color: #f6f8fa;
    border-radius: 4px;
}

/* 消息样式 */
.message {
    display: flex;
    align-items: flex-end;
    margin-bottom: 18px;
    padding: 0 8px;
    animation: slideIn 0.3s ease-out;
    width: 100%;
    box-sizing: border-box;
    min-height: 0;
}

.message.user {
    flex-direction: row-reverse;
    justify-content: flex-start;
}

.message.ai {
    flex-direction: row;
    justify-content: flex-start;
}

.avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    background: #e0e7ef;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: bold;
    color: #409EFF;
    margin: 0 8px;
    font-size: 18px;
}

.message-content {
    max-width: 70%;
    min-width: 48px;
    padding: 14px 18px;
    border-radius: 16px;
    font-size: 15px;
    line-height: 1.7;
    word-break: break-word;
    white-space: pre-wrap;
    box-shadow: 0 2px 8px rgba(0,0,0,0.04);
    margin: 0 8px;
    flex-grow: 0;
    flex-shrink: 1;
    flex-basis: auto;
    width: auto;
    box-sizing: border-box;
    height: auto;
    min-height: 0;
}

.message.user .message-content {
    background: linear-gradient(135deg, #409EFF, #36cfc9);
    color: #fff;
    border-bottom-right-radius: 4px;
}

.message.ai .message-content {
    background: #fff;
    color: #222;
    border-bottom-left-radius: 4px;
    border: 1px solid #e8eaed;
}

.message-time {
    font-size: 12px;
    color: #b0b3b8;
    margin: 0 8px;
    margin-top: 2px;
}

.message-status {
    font-size: 12px;
    color: #b0b3b8;
    margin-left: 8px;
}

/* 输入区域样式 */
.input-area {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    padding: 14px 20px;
    background: #fff;
    border-top: 1px solid #e8eaed;
    display: flex;
    align-items: center;
    gap: 12px;
    box-shadow: 0 -2px 8px rgba(0,0,0,0.03);
    z-index: 10;
    box-sizing: border-box;
    height: 70px;
}

.input-area .el-input {
    flex: 1;
}

.input-area .el-button {
    padding: 12px 24px;
    font-size: 15px;
    border-radius: 12px;
    background: linear-gradient(135deg, #409EFF, #36cfc9);
    border: none;
    color: #fff;
    transition: all 0.3s;
}

/* 消息动画 */
@keyframes slideIn {
    from {
        opacity: 0;
        transform: translateY(20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>

