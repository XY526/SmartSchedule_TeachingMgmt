import {defineStore} from 'pinia'
import {ref} from 'vue'

const useUserInfoStore = defineStore('userInfo',()=>{
    // 从localStorage中获取初始值
    const getStoredInfo = () => {
        try {
            const storedInfo = localStorage.getItem('userInfo');
            return storedInfo ? JSON.parse(storedInfo) : {};
        } catch (e) {
            console.error('Error getting stored user info:', e);
            return {};
        }
    }

    //定义状态相关的内容
    const info = ref(getStoredInfo())

    const setInfo = (newInfo)=>{
        info.value = newInfo
        // 保存到localStorage
        try {
            localStorage.setItem('userInfo', JSON.stringify(newInfo));
        } catch (e) {
            console.error('Error saving user info:', e);
        }
    }

    const removeInfo = ()=>{
        info.value = {}
        // 从localStorage中移除
        try {
            localStorage.removeItem('userInfo');
        } catch (e) {
            console.error('Error removing user info:', e);
        }
    }

    return {info,setInfo,removeInfo}
},{
    persist: {
        key: 'userInfo',
        storage: localStorage,
        paths: ['info']
    }
})

export default useUserInfoStore;