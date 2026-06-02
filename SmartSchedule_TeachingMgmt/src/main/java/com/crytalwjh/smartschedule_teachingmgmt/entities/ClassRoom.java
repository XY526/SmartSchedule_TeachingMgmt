package com.crytalwjh.smartschedule_teachingmgmt.entities;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 教室实体类
 * @author crytalwjh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("classroom")
public class ClassRoom {
    @TableId
    private Integer classRoomId; // 教室编号
    private Integer capacity; // 教室容量
    private Integer isMultimedia; // 是否为多媒体教室，0 表示否，1 表示是
    private Integer state; // 教室使用状态
    private Integer locationId; // 教室位置编号

    @TableField(exist = false)
    private String locationName;

}