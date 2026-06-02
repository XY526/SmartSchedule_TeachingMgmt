package com.crytalwjh.smartschedule_teachingmgmt.vo;

import com.crytalwjh.smartschedule_teachingmgmt.entities.Student;
import com.crytalwjh.smartschedule_teachingmgmt.entities.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 教师表的拓展
 * @author 王晶辉
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TeacherVo extends Teacher implements Serializable {

    private String facultyName;

}
