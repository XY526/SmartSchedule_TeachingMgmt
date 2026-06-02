//导入request.js请求工具
import request from '@/utils/request.js'

//提供调用注册接口的函数
export const userRegisterService = (registerData)=>{
    return request({
        url: '/user/register',
        method: 'post',
        data: JSON.stringify({
            username: registerData.username,
            email: registerData.email,
            password: registerData.password,
            code: registerData.verificationCode
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    });
}

//提供调用登录接口的函数
export const userLoginService = (loginData)=>{
    // 将登录数据转换为URLSearchParams格式
    const params = new URLSearchParams();
    for(let key in loginData){
        params.append(key, loginData[key]);
    }
    
    // 设置请求头
    const headers = {
        'Content-Type': 'application/x-www-form-urlencoded'
    };
    
    return request({
        url: '/user/login',
        method: 'post',
        data: params,
        headers: headers
    });
}


//获取用户详细信息
export const userInfoService = ()=>{
    return request.get('/user/userInfo')
}


//获取用户详细信息
/**
 * 获取所有用户列表
 * @returns {Promise} 返回用户列表数据的 Promise 对象
 */
export const userGetAllService  = (params)=>{
    return request.post('/user/list',params)
}

//修改个人信息
export const userInfoUpdateService = (userInfoData) => {
  return request.put('/user/updateById', userInfoData)
}

//用户添加
export const userAddService = (classData)=>{
    return request.post('/user/insertUser',classData);
}

//用户列表修改
export const userEditService = (data)=>{
    return  request.put('/user/updateById',data)
}

//用户列表删除
export const userDeleteService = (id)=>{
    return request.delete('/user/deleteById?id='+id)
}

// 上传头像
export const uploadAvatarService = (formData) => {
  return request({
    url: '/user/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 提供调用发送注册验证码接口的函数
export const sendRegisterCodeService = (emailData) => {
    return request({
        url: '/user/sendRegisterCode',
        method: 'post',
        data: JSON.stringify({
            username: emailData.username,
            email: emailData.email
        }),
        headers: {
            'Content-Type': 'application/json'
        }
    });
};