package com.crytalwjh.smartschedule_teachingmgmt.controller;

import com.crytalwjh.smartschedule_teachingmgmt.service.AiService;
import com.crytalwjh.smartschedule_teachingmgmt.vo.AiAnalyzeRequest;
import com.crytalwjh.smartschedule_teachingmgmt.vo.AiAnalyzeResponse;
import com.crytalwjh.smartschedule_teachingmgmt.vo.AiChatRequest;
import com.crytalwjh.smartschedule_teachingmgmt.vo.AiChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {
    @Autowired
    private AiService aiService;

    @PostMapping("/analyze")
    public AiAnalyzeResponse analyzeStudentData(@RequestBody AiAnalyzeRequest request) {
        return aiService.analyzeStudentData(request);
    }

    @PostMapping("/chat")
    public AiChatResponse chat(@RequestBody AiChatRequest request) {
        return aiService.chat(request);
    }
} 