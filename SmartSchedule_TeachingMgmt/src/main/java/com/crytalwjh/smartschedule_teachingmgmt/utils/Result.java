package com.crytalwjh.smartschedule_teachingmgmt.utils;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private int code;
    private String message;
    private Object data;

    // 成功时的静态方法
    public static Result success(Object data ) {
        Result result = new Result();
        result.code = 0;
        result.message = "操作成功";
        result.data = data;
        return result;
    }

    // 失败时的静态方法
    public static Result fail(String message) {
        Result result = new Result();
        result.code = 1;
        result.message = message;
        return result;
    }



}