package com.crytalwjh.smartschedule_teachingmgmt.controller;


import com.crytalwjh.smartschedule_teachingmgmt.entities.Location;
import com.crytalwjh.smartschedule_teachingmgmt.service.LocationService;
import com.crytalwjh.smartschedule_teachingmgmt.utils.QueryParams;
import com.crytalwjh.smartschedule_teachingmgmt.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/location")
public class LocationController {
    @Autowired
    private LocationService locationService;

    @PostMapping("/list")
    Result selectAll(@RequestBody QueryParams queryParams){
        return Result.success(locationService.selectAll(queryParams));
    }

    @GetMapping("/selectById")
    Location selectById(@RequestParam int id){
        return locationService.selectLocationAndClassRoomById(id);
    }

    @PostMapping("/insertLocation")
    Result insertLocation(@RequestBody Location location){
        try{
            int rows = locationService.insertLocation(location);
            if (rows>0){
                return Result.success("插入成功");
            }else{
                return Result.fail("插入失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("插入过程出现异常"+e.getMessage());
        }

    }

    @DeleteMapping("/deleteById")
    Result deleteLocation(@RequestParam int id){
        try{
            int rows = locationService.deleteById(id);
            if (rows>0){
                return Result.success("删除成功");
            }else{
                return Result.fail("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("删除过程出现异常"+e.getMessage());
        }

    }
    @PutMapping("/updateById")
    Result updateById(@RequestBody Location location){
        try{
            int rows = locationService.updateById(location);
            if (rows>0){
                return Result.success("更新成功");
            }else{
                return Result.fail("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("更新过程出现异常"+e.getMessage());
        }

    }
}
