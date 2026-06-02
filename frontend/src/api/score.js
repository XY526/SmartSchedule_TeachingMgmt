import request from '@/utils/request';

// 分页查询成绩列表
export const scoreListService = (params) => {
  return request({
    url: '/score/list',
    method: 'post',
    data: params
  });
};

// 添加成绩
export const scoreAddService = (data) => {
  return request({
    url: '/score/add',
    method: 'post',
    data
  });
};

// 删除成绩
export const scoreDeleteService = (id) => {
  return request({
    url: `/score/delete/${id}`,
    method: 'delete'
  });
};

// 更新成绩
export const scoreUpdateService = (data) => {
  return request({
    url: '/score/update',
    method: 'put',
    data
  });
};

// 根据学生ID查询成绩
export const scoreGetByStudentService = (studentId) => {
  return request({
    url: `/score/student/${studentId}`,
    method: 'get'
  });
};

// 根据课程ID查询成绩
export const scoreGetByCourseService = (courseId) => {
  return request({
    url: `/score/course/${courseId}`,
    method: 'get'
  });
};

// 根据ID查询成绩
export const scoreGetByIdService = (id) => {
  return request({
    url: `/score/${id}`,
    method: 'get'
  });
}; 