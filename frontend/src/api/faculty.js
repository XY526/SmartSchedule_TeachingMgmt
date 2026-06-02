import request from '@/utils/request.js';
import { useTokenStore } from '@/stores/token.js'


//院系列表查询
export const facultyGetAllService = (params)=>{
    return  request.post('/faculty/list',params)
 }

 //院系列表添加
 export const facultyAddService = (data)=>{
     return request.post('/faculty/insertFaculty',data)
 }                      
 
 //院系列表修改
 export const facultyUpdateService = (data)=>{
    return  request.put('/faculty/updateById',data)
 }
 
 //院系列表删除
 export const facultyDeleteService = (id)=>{
     return request.delete('/faculty/deleteById?id='+id)
 }