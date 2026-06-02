import request from '@/utils/request.js';
import { useTokenStore } from '@/stores/token.js'


//教师列表查询
/**
 * 获取所有教师列表数据
 * @param {Object} params - 查询参数对象
 * @returns {Promise} 返回教师列表数据的Promise对象
 */
export const teacherGetAllService = (params = {}) => {
  console.log('teacherGetAllService - 请求参数:', params);
  const requestData = {
    pageNum: params.pageNum || 1,
    pageSize: params.pageSize || 10,
    params: params.params || {}
  };
  console.log('teacherGetAllService - 处理后的请求数据:', requestData);
  
  return request({
    url: '/teacher/list',
    method: 'post',
    data: requestData
  });
};

//教室添加
export const teacherAddService = (classData)=>{
    return request.post('/teacher/insertTeacher',classData);
}

//教室列表修改
export const teacherEditService = (data)=>{
  return  request.put('/teacher/updateById',data)
}

//教室列表删除
export const teacherDeleteService = (id)=>{
    return request.delete('/teacher/deleteById?id='+id)
}