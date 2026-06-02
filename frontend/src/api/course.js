import request from '@/utils/request.js';
import { useTokenStore } from '@/stores/token.js'


//获取课程列表
export const courseGetAllService = (params = {}) => {
  console.log('courseGetAllService - 请求参数:', params);
  const requestData = {
    pageNum: params.pageNum || 1,
    pageSize: params.pageSize || 10,
    params: params.params || {}
  };
  console.log('courseGetAllService - 处理后的请求数据:', requestData);
  
  return request({
    url: '/course/list',
    method: 'post',
    headers: {
      'Content-Type': 'application/json'
    },
    data: requestData
  });
};

// 根据班级ID获取课程列表
export const courseGetByClassIdService = (classId) => {
  return request({
    url: `/course/getByClassId?classId=${classId}`,
    method: 'get'
  });
};

// 获取完整的课程信息（包括教师、地点、教室等信息）
export const getCompleteCourseSchedule = (classId) => {
  return request({
    url: `/course/getCompleteSchedule?classId=${classId}`,
    method: 'get'
  });
};

//课程列表添加
export const courseAddService = (data)=>{
    return request.post('/course/insertCourse',data)
}                      

//课程列表修改
export const courseUpdateService = (data)=>{
   return  request.put('/course/updateById',data)
}

//课程列表删除
export const courseDeleteService = (id)=>{
    return request.delete('/course/deleteById?id='+id)
}