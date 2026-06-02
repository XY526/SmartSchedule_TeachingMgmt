import request from '@/utils/request.js';
import { useTokenStore } from '@/stores/token.js'


//班级列表查询
/**
 * 获取所有班级列表数据
 * @param {Object} params - 查询参数对象
 * @returns {Promise} 返回班级列表数据的Promise对象
 */
export const classGetAllService = (params)=>{
    return  request.post('/classAll/list',params)
 }

 //班级添加
 export const classAddService = (classData)=>{
     return request.post('/classAll/insertClass',classData);
 
 }

  //班级列表修改
  export const classEditService = (data)=>{
    return  request.put('/classAll/updateById',data)
 }
 
 //班级列表删除
 export const classDeleteService = (id)=>{
     return request.delete('/classAll/deleteById?id='+id)
 }