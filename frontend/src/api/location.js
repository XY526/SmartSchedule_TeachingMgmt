import request from '@/utils/request.js';
import { useTokenStore } from '@/stores/token.js'


//教学楼列表查询
export const locationGetAllService = (params)=>{
    return  request.post('/location/list',params)
 }

 //教学楼列表添加
 export const locationAddService = (data)=>{
     return request.post('/location/insertLocation',data)
 }                      
 
 //教学楼列表修改
 export const locationUpdateService = (data)=>{
    return  request.put('/location/updateById',data)
 }
 
 //教学楼列表删除
 export const locationDeleteService = (id)=>{
     return request.delete('/location/deleteById?id='+id)
 }