package com.crytalwjh.smartschedule_teachingmgmt.vo;

import lombok.Data;
import java.util.List;

@Data
public class AiAnalyzeResponse {
    private Integer code;
    private String message;
    private AnalyzeData data;

    @Data
    public static class AnalyzeData {
        private String scheduleAnalysis;
        private String scoreAnalysis;
        private List<String> recommendations;
    }
} 