package com.crytalwjh.smartschedule_teachingmgmt.vo;

import lombok.Data;
import java.util.List;

@Data
public class AiAnalyzeRequest {
    private List<ScheduleItem> schedule;
    private List<ScoreItem> scores;
    private String prompt;

    @Data
    public static class ScheduleItem {
        private String courseName;
        private Integer weekday;
        private Integer section;
        private String locationName;
        private String teacherName;
        private Integer startWeek;
        private Integer endWeek;
        private Integer schoolYear;
        private Integer term;
    }

    @Data
    public static class ScoreItem {
        private String courseName;
        private Integer score;
        private Integer examType;
        private String examDate;
        private String teacherName;
    }
} 