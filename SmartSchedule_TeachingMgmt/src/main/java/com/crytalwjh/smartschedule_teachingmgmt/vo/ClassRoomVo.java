package com.crytalwjh.smartschedule_teachingmgmt.vo;

import com.crytalwjh.smartschedule_teachingmgmt.entities.ClassRoom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 教室表的拓展
 * @author
 */
import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ClassRoomVo extends ClassRoom implements Serializable {

    private String locationName;
}
