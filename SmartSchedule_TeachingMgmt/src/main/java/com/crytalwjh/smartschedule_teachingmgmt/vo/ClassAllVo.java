package com.crytalwjh.smartschedule_teachingmgmt.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassAll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 班级表的拓展
 * @author 王晶辉
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ClassAllVo extends ClassAll implements Serializable {

    private String facultyName;
    private String teacherName;
}
