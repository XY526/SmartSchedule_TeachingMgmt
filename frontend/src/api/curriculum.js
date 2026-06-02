import request from '@/utils/request.js';
import { useTokenStore } from '@/stores/token.js'


//课程表查询
/**
 * 获取所有课程表数据
 * @param {Object} params - 查询参数对象 (contains pageNum, pageSize, and nested params for filtering)
 * @returns {Promise} 返回教室列表数据的Promise对象
 */
export const curriculumGetAllService = (params)=>{
    // 创建一个新的对象，避免修改原始参数
    const requestData = {
        pageNum: Number(params.pageNum),
        pageSize: Number(params.pageSize),
        params: {
            schoolYear: params.params.schoolYear || null,
            term: params.params.term ? Number(params.params.term) : null,
            classId: params.params.classId ? Number(params.params.classId) : null,
            week: params.params.week === '' ? null : Number(params.params.week)
        }
    };
    console.log('发送到后端的参数:', requestData); // 添加日志
    return request.post('/curriculum/list', requestData);
}

//课程表添加
export const curriculumAddService = (data)=>{
    return request.post('/curriculum/insertCurriculum',data)
}

//课程表批量添加
export const curriculumBatchInsertService = (data)=>{
    return request.post('/curriculum/batchInsert',data)
}

//课程表修改
export const curriculumUpdateService = (data)=>{
    return  request.put('/curriculum/updateById',data)
}

//课程表删除
export const curriculumDeleteService = (id)=>{
    return request.delete('/curriculum/deleteById?id='+id)
}