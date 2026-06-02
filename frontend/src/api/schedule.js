import request from '@/utils/request'

// 分页查询日程列表
export const scheduleListService = (params) => {
  return request({
    url: '/schedule/list',
    method: 'post',
    data: params
  })
}

// 添加日程
export const scheduleAddService = (data) => {
  return request({
    url: '/schedule/add',
    method: 'post',
    data
  })
}

// 删除日程
export const scheduleDeleteService = (id) => {
  return request({
    url: `/schedule/delete/${id}`,
    method: 'delete'
  })
}

// 更新日程
export const scheduleUpdateService = (data) => {
  return request({
    url: '/schedule/updateById',
    method: 'put',
    data: {
      scheduleId: data.scheduleId,
      title: data.title,
      description: data.description,
      startTime: data.startTime,
      endTime: data.endTime,
      status: data.status,
      userId: data.userId
    }
  })
}

// 根据用户ID查询日程
export const scheduleGetByUserIdService = (userId) => {
  return request({
    url: `/schedule/user/${userId}`,
    method: 'get'
  })
}

// 根据ID查询日程
export const scheduleGetByIdService = (id) => {
  return request({
    url: `/schedule/${id}`,
    method: 'get'
  })
}

// 生成课程表
export const scheduleGenerateService = (params) => {
  return request({
    url: '/schedule/generation/generate',
    method: 'post',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    data: {
      classId: params.classId,
      schoolYear: params.schoolYear,
      term: params.term
    }
  });
};

// 保存单个课程表记录
export const curriculumInsertService = (data) => {
  return request({
    url: '/curriculum/insertCurriculum',
    method: 'post',
    headers: {
      'Content-Type': 'application/json'
    },
    data: {
      courseId: data.courseId,
      classId: data.classId,
      teacherId: data.teacherId,
      schoolYear: data.schoolYear,
      term: data.term,
      startWeek: data.startWeek,
      endWeek: data.endWeek,
      weekday: data.weekday,
      section: data.section,
      locationId: data.locationId,
      classRoomId: data.classRoomId
    }
  });
};

// 批量保存课程表记录
export const curriculumBatchInsertService = (data) => {
  return request({
    url: '/curriculum/batchInsert',
    method: 'post',
    headers: {
      'Content-Type': 'application/json'
    },
    data: data.map(item => ({
      courseId: item.courseId,
      classId: item.classId,
      teacherId: item.teacherId,
      schoolYear: item.schoolYear,
      term: item.term,
      startWeek: item.startWeek,
      endWeek: item.endWeek,
      weekday: item.weekday,
      section: item.section,
      locationId: item.locationId,
      classRoomId: item.classRoomId
    }))
  });
}; 