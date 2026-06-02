package com.crytalwjh.smartschedule_teachingmgmt.vo;

import lombok.Data;

@Data
public class AiChatResponse {
    private Integer code;
    private String message;
    private ChatData data;

    @Data
    public static class ChatData {
        private String reply;
    }
} 