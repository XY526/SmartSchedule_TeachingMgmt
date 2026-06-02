package com.crytalwjh.smartschedule_teachingmgmt.exception;


import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {//异常处理器

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        e.printStackTrace();
        return Result.fail(StringUtils.hasLength(e.getMessage())? e.getMessage() : "操作失败");
    }
}
