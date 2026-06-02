package com.crytalwjh.smartschedule_teachingmgmt.entities;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 地点实体类
 * @author crytalwjh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("location")
public class Location {
    @TableId
    private Integer locationId; // 地点编号
    private String locationName; // 地点名称
    private String isMultimedia; // 是否多媒体教室
    private String state; // 状态
    @TableField(exist = false)
    private List<ClassRoom> ClassRoom;

}