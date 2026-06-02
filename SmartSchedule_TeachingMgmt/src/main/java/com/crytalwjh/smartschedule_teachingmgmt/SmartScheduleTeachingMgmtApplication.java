package com.crytalwjh.smartschedule_teachingmgmt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.crytalwjh.smartschedule_teachingmgmt", "com.crytalwjh.smartschedule_teachingmgmt.config"})
@MapperScan(basePackages = "com.crytalwjh.smartschedule_teachingmgmt.mapper")
public class SmartScheduleTeachingMgmtApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartScheduleTeachingMgmtApplication.class, args);
    }

}
