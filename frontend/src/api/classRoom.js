import request from '@/utils/request.js';
import { useTokenStore } from '@/stores/token.js'


//教室查询
export const classRoomGetAllService = (params)=>{
    return  request.post('/classRoom/list',params)
 }

//班级添加
export const classRoomAddService = (classData)=>{
    return request.post('/classRoom/insertClassRoom',classData);

}

 //班级列表修改
 export const classRoomEditService = (data)=>{
   return  request.put('/classRoom/updateById',data)
}

//班级列表删除
export const classRoomDeleteService = (id)=>{
    return request.delete('/classRoom/deleteById?id='+id)
}