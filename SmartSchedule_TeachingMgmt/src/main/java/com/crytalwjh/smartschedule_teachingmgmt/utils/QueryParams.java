package com.crytalwjh.smartschedule_teachingmgmt.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO: 封装前端传入的查询参数
 * @author yinbo
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryParams<T> {

    /**
     * 当前页数
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 其它查询参数，通常用它存储搜索表单传入的参数
     */
    private Map<String,T> params = new ConcurrentHashMap<>();
}