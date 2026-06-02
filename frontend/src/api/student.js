import request from '@/utils/request.js';
import { useTokenStore } from '@/stores/token.js'


//学生列表查询
/**
 * 获取所有学生列表数据
 * @param {Object} params - 查询参数对象
 * @returns {Promise} 返回学生列表数据的Promise对象
 */
export const studentGetAllService = (params = {}) => {
  const requestData = {
    pageNum: params.pageNum || 1,
    pageSize: params.pageSize || 10,
    params: {
      name: params.name || undefined,
      facultyName: params.facultyName || undefined
    }
  };
  return request({
    url: '/student/list',
    method: 'post',
    data: requestData
  });
};

//学生添加
export const studentAddService = (classData)=>{
    return request.post('/student/insertStudent',classData);
}

//学生列表修改
export const studentEditService = (data)=>{
  return  request.put('/student/updateById',data)
}

//学生列表删除
export const studentDeleteService = (id)=>{
    return request.delete('/student/deleteById?id='+id)
}