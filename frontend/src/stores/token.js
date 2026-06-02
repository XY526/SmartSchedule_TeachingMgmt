//定义store
import {defineStore} from 'pinia'
import {ref} from 'vue'

// 从存储中获取token
const getStoredToken = () => {
    try {
        // 优先从localStorage获取，如果没有则从sessionStorage获取
        const token = localStorage.getItem('token') || sessionStorage.getItem('token') || '';
        console.log('getStoredToken - 从存储中获取token:', token);
        return token;
    } catch (e) {
        console.error('getStoredToken - 获取token失败:', e);
        return '';
    }
}

// 保存token到存储
const saveToken = (token, remember = false) => {
    try {
        console.log('saveToken - 保存token:', token, '记住我:', remember);
        // 先清除所有存储的token
        localStorage.removeItem('token');
        sessionStorage.removeItem('token');
        
        if (token) {
            // 确保token格式正确
            const tokenValue = token.startsWith('Bearer ') ? token : `Bearer ${token}`;
            console.log('saveToken - 格式化后的token:', tokenValue);
            
            if (remember) {
                // 如果选择记住我，使用localStorage存储（长期保存）
                localStorage.setItem('token', tokenValue);
                console.log('saveToken - token已保存到localStorage:', tokenValue);
            } else {
                // 否则使用sessionStorage存储（会话期间有效）
                sessionStorage.setItem('token', tokenValue);
                console.log('saveToken - token已保存到sessionStorage:', tokenValue);
            }
            
            // 验证token是否保存成功
            const savedToken = localStorage.getItem('token') || sessionStorage.getItem('token');
            console.log('saveToken - 验证保存的token:', savedToken);
            
            if (!savedToken) {
                console.error('saveToken - token保存失败');
            }
        }
    } catch (e) {
        console.error('saveToken - 保存token失败:', e);
    }
}

/* 
    第一个参数:名字,唯一性
    第二个参数:函数,函数的内部可以定义状态的所有内容

    返回值: 函数
*/
export const useTokenStore = defineStore('token', () => {
    // 初始化token
    const token = ref(getStoredToken());
    console.log('useTokenStore - 初始化token:', token.value);

    // 设置token
    const setToken = (newToken, remember = false) => {
        console.log('setToken - 设置新token:', newToken, '记住我:', remember);
        
        // 检查token是否为空
        if (!newToken) {
            console.error('setToken - token为空');
            return;
        }
        
        // 确保token格式正确
        const tokenValue = newToken.startsWith('Bearer ') ? newToken : `Bearer ${newToken}`;
        console.log('setToken - 格式化后的token:', tokenValue);
        token.value = tokenValue;
        saveToken(tokenValue, remember);
        
        // 验证token是否设置成功
        const currentToken = getToken();
        console.log('setToken - 验证设置后的token:', currentToken);
        
        if (!currentToken) {
            console.error('setToken - token设置失败');
        }
    }

    // 移除token
    const removeToken = () => {
        console.log('removeToken - 移除token');
        token.value = '';
        localStorage.removeItem('token');
        sessionStorage.removeItem('token');
        
        // 验证token是否移除成功
        const currentToken = getToken();
        console.log('removeToken - 验证移除后的token:', currentToken);
    }

    // 获取token
    const getToken = () => {
        const currentToken = token.value;
        console.log('getToken - 获取当前token:', currentToken);
        return currentToken;
    }

    return {
        token,
        setToken,
        removeToken,
        getToken
    }
});