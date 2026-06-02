//定制请求的实例

//导入axios  npm install axios
import axios from 'axios';
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useTokenStore } from '@/stores/token.js'

//定义一个变量,记录公共的前缀  ,  baseURL
const baseURL = 'http://localhost:8093/api';  // 添加/api前缀
// const baseURL = 'https://www.zh.lovetmj.cn/api';  // 添加/api前缀
// const baseURL = '';  // 移除 /api 前缀
const instance = axios.create({ 
  baseURL,
  timeout: 30000,  // 增加超时时间到30秒
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
})

//添加请求拦截器
instance.interceptors.request.use(
    (config)=>{
        //请求前的回调
        //添加token
        const tokenStore = useTokenStore();
        const token = tokenStore.getToken();
        console.log('请求拦截器 - 当前请求URL:', config.url);
        console.log('请求拦截器 - 当前请求方法:', config.method);
        console.log('请求拦截器 - 当前请求头:', config.headers);
        console.log('请求拦截器 - 当前token:', token);
        
        // 从localStorage获取用户信息
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
        console.log('请求拦截器 - 用户信息:', userInfo);
        
        if(token){
            // token已经在存储时确保了格式，直接使用
            config.headers.Authorization = token;
            // 添加username到请求头
            if (userInfo.username) {
                config.headers['X-Username'] = userInfo.username;
            }
            console.log('请求拦截器 - 设置请求头:', config.headers);
        } else {
            // 尝试从存储中直接获取token
            const storedToken = localStorage.getItem('token') || sessionStorage.getItem('token');
            if (storedToken) {
                config.headers.Authorization = storedToken;
                // 添加username到请求头
                if (userInfo.username) {
                    config.headers['X-Username'] = userInfo.username;
                }
                console.log('请求拦截器 - 从存储中获取token并设置:', storedToken);
            } else {
                console.log('请求拦截器 - 没有token，跳过添加Authorization头');
            }
        }
        return config;
    },
    (err)=>{
        //请求错误的回调
        console.error('请求拦截器错误:', err);
        return Promise.reject(err)
    }
)

//添加响应拦截器
instance.interceptors.response.use(
    result => {
        console.log('响应拦截器 - 响应数据:', result.data);
        //判断业务状态码
        if(result.data.code === 0 || result.data.code === 200){
            return result.data;
        }

        //操作失败
        ElMessage.error(result.data.message || '服务异常')
        return Promise.reject(result.data)
    },
    err => {
        console.error('响应拦截器 - 错误:', err);
        //判断响应状态码
        if(err.response){
            console.log('响应拦截器 - 错误响应状态:', err.response.status);
            console.log('响应拦截器 - 错误响应数据:', err.response.data);
            console.log('响应拦截器 - 错误请求配置:', err.config);
            
            switch(err.response.status){
                case 401:
                    console.log('响应拦截器 - 401错误，清除token并跳转到登录页');
                    // 清除token
                    const tokenStore = useTokenStore();
                    tokenStore.removeToken();
                    ElMessage.error('请先登录')
                    router.push('/login')
                    break;
                case 403:
                    ElMessage.error('没有权限')
                    break;
                case 404:
                    ElMessage.error('请求的资源不存在')
                    break;
                case 500:
                    ElMessage.error('服务器错误')
                    break;
                default:
                    ElMessage.error('服务异常')
            }
        }else{
            ElMessage.error('网络错误，请检查网络连接')
        }
        return Promise.reject(err);
    }
)

export default instance;