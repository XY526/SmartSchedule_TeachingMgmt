package com.crytalwjh.smartschedule_teachingmgmt.service;

import com.crytalwjh.smartschedule_teachingmgmt.config.AiConfig;
import com.crytalwjh.smartschedule_teachingmgmt.vo.AiAnalyzeRequest;
import com.crytalwjh.smartschedule_teachingmgmt.vo.AiAnalyzeResponse;
import com.crytalwjh.smartschedule_teachingmgmt.vo.AiChatRequest;
import com.crytalwjh.smartschedule_teachingmgmt.vo.AiChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiService {
    private static final Logger logger = LoggerFactory.getLogger(AiService.class);
    private static final int MAX_RETRIES = 3;
    private static final int TIMEOUT = 30000; // 30 seconds

    @Autowired
    private AiConfig aiConfig;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiAnalyzeResponse analyzeStudentData(AiAnalyzeRequest request) {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                // Prepare the prompt for analysis
                String prompt = buildAnalysisPrompt(request);
                
                // Call Tongyi API for analysis
                String analysisResult = callTongyiApi(prompt);
                logger.info("Received analysis result: {}", analysisResult);
                
                // Parse and structure the response
                AiAnalyzeResponse response = new AiAnalyzeResponse();
                response.setCode(0);
                response.setMessage("分析成功");
                
                AiAnalyzeResponse.AnalyzeData data = new AiAnalyzeResponse.AnalyzeData();
                
                // 使用正则表达式提取各个部分
                Pattern schedulePattern = Pattern.compile("###\\s*1\\.\\s*对课程安排的分析\\s*(.*?)(?=###\\s*2\\.\\s*对成绩情况的分析|$)", Pattern.DOTALL);
                Pattern scorePattern = Pattern.compile("###\\s*2\\.\\s*对成绩情况的分析\\s*(.*?)(?=###\\s*3\\.\\s*具体的改进建议|$)", Pattern.DOTALL);
                Pattern recommendationsPattern = Pattern.compile("###\\s*3\\.\\s*具体的改进建议\\s*(.*?)(?=###|$)", Pattern.DOTALL);
                
                Matcher scheduleMatcher = schedulePattern.matcher(analysisResult);
                Matcher scoreMatcher = scorePattern.matcher(analysisResult);
                Matcher recommendationsMatcher = recommendationsPattern.matcher(analysisResult);
                
                // 提取课程安排分析
                if (scheduleMatcher.find()) {
                    String scheduleAnalysis = scheduleMatcher.group(1).trim();
                    // 移除多余的换行符和分隔符，但保留所有标记
                    scheduleAnalysis = scheduleAnalysis.replaceAll("\\n\\s*\\n", "\n")
                        .replaceAll("\\n\\s*---\\s*\\n", "\n")
                        .trim();
                    data.setScheduleAnalysis(scheduleAnalysis);
                    logger.info("Extracted schedule analysis: {}", scheduleAnalysis);
                } else {
                    logger.warn("No schedule analysis found in response");
                }
                
                // 提取成绩分析
                if (scoreMatcher.find()) {
                    String scoreAnalysis = scoreMatcher.group(1).trim();
                    // 移除多余的换行符和分隔符，但保留所有标记
                    scoreAnalysis = scoreAnalysis.replaceAll("\\n\\s*\\n", "\n")
                        .replaceAll("\\n\\s*---\\s*\\n", "\n")
                        .trim();
                    data.setScoreAnalysis(scoreAnalysis);
                    logger.info("Extracted score analysis: {}", scoreAnalysis);
                } else {
                    logger.warn("No score analysis found in response");
                }
                
                // 提取建议
                if (recommendationsMatcher.find()) {
                    String recommendations = recommendationsMatcher.group(1).trim();
                    List<String> recommendationList = new ArrayList<>();
                    
                    // 使用正则表达式匹配所有列表项，包括带括号的序号和子标题
                    Pattern listItemPattern = Pattern.compile("(?:^|\\n)(?:(?:\\d+\\.|（\\d+）)\\s*\\*\\*|[-*])\\s*(.*?)(?=\\n(?:(?:\\d+\\.|（\\d+）)\\s*\\*\\*|[-*]|$)|$)", Pattern.DOTALL);
                    Matcher listItemMatcher = listItemPattern.matcher(recommendations);
                    
                    while (listItemMatcher.find()) {
                        String item = listItemMatcher.group(1).trim();
                        // 移除单行分隔符和子标题，但保留内容
                        item = item.replaceAll("\\n\\s*---\\s*\\n", "\n")
                            .replaceAll("\\n\\s*####\\s*（\\d+）.*?：\\s*\\n", "\n")
                            .trim();
                        if (!item.isEmpty()) {
                            recommendationList.add(item);
                        }
                    }
                    
                    data.setRecommendations(recommendationList);
                    logger.info("Extracted recommendations: {}", recommendationList);
                } else {
                    logger.warn("No recommendations found in response");
                }
                
                response.setData(data);
                return response;
            } catch (ResourceAccessException e) {
                retryCount++;
                logger.error("网络请求超时，正在进行第{}次重试", retryCount, e);
                if (retryCount == MAX_RETRIES) {
                    return createErrorResponse("网络连接超时，请稍后重试");
                }
                try {
                    Thread.sleep(1000 * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return createErrorResponse("请求被中断");
                }
            } catch (HttpClientErrorException e) {
                logger.error("API调用失败: {}", e.getStatusCode(), e);
                return createErrorResponse("AI服务暂时不可用，请稍后重试");
            } catch (Exception e) {
                logger.error("分析过程中发生错误", e);
                return createErrorResponse("分析失败：" + e.getMessage());
            }
        }
        return createErrorResponse("超过最大重试次数");
    }

    public AiChatResponse chat(AiChatRequest request) {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                // Call Tongyi API for chat
                String reply = callTongyiApi(request.getMessage());
                
                AiChatResponse response = new AiChatResponse();
                response.setCode(0);
                response.setMessage("回复成功");
                
                AiChatResponse.ChatData data = new AiChatResponse.ChatData();
                data.setReply(reply);
                
                response.setData(data);
                return response;
            } catch (ResourceAccessException e) {
                retryCount++;
                logger.error("网络请求超时，正在进行第{}次重试", retryCount, e);
                if (retryCount == MAX_RETRIES) {
                    return createChatErrorResponse("网络连接超时，请稍后重试");
                }
                try {
                    Thread.sleep(1000 * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return createChatErrorResponse("请求被中断");
                }
            } catch (HttpClientErrorException e) {
                logger.error("API调用失败: {}", e.getStatusCode(), e);
                return createChatErrorResponse("AI服务暂时不可用，请稍后重试");
            } catch (Exception e) {
                logger.error("聊天过程中发生错误", e);
                return createChatErrorResponse("回复失败：" + e.getMessage());
            }
        }
        return createChatErrorResponse("超过最大重试次数");
    }

    private String buildAnalysisPrompt(AiAnalyzeRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请分析以下学生的课程安排和成绩数据，并提供建议。\n\n");
        
        prompt.append("课程安排：\n");
        for (AiAnalyzeRequest.ScheduleItem item : request.getSchedule()) {
            prompt.append(String.format("- %s：\n", item.getCourseName()));
            prompt.append(String.format("  * 上课时间：第%d周 第%d节\n", item.getStartWeek(), item.getSection()));
            prompt.append(String.format("  * 上课地点：%s\n", item.getLocationName()));
            prompt.append(String.format("  * 授课教师：%s\n", item.getTeacherName()));
            prompt.append(String.format("  * 开课周次：第%d周到第%d周\n", item.getStartWeek(), item.getEndWeek()));
            prompt.append(String.format("  * 学年学期：%d学年 第%d学期\n", item.getSchoolYear(), item.getTerm()));
        }
        
        prompt.append("\n成绩情况：\n");
        for (AiAnalyzeRequest.ScoreItem item : request.getScores()) {
            prompt.append(String.format("- %s：\n", item.getCourseName()));
            prompt.append(String.format("  * 考试成绩：%d分\n", item.getScore()));
            prompt.append(String.format("  * 考试类型：%s\n", getExamTypeName(item.getExamType())));
            prompt.append(String.format("  * 考试日期：%s\n", item.getExamDate()));
            prompt.append(String.format("  * 授课教师：%s\n", item.getTeacherName()));
        }
        
        prompt.append("\n请提供：\n");
        prompt.append("#### 1. 对课程安排的分析\n");
        prompt.append("（请分析上课时间、地点、教师等是否合理）\n\n");
        prompt.append("#### 2. 对成绩情况的分析\n");
        prompt.append("（请分析成绩表现、优势和不足）\n\n");
        prompt.append("#### 3. 具体的改进建议\n");
        prompt.append("（请提供针对性的改进建议）\n");
        
        return prompt.toString();
    }

    private String getExamTypeName(Integer examType) {
        if (examType == null) return "未知类型";
        switch (examType) {
            case 1: return "期中考试";
            case 2: return "期末考试";
            case 3: return "其他考试";
            default: return "未知类型";
        }
    }

    private String callTongyiApi(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + aiConfig.getTongyiApiKey());

        // 构建符合通义千问 API 格式的请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen-turbo");
        
        Map<String, Object> input = new HashMap<>();
        List<Map<String, String>> messages = List.of(
            Map.of("role", "user", "content", prompt)
        );
        input.put("messages", messages);
        requestBody.put("input", input);

        // 添加其他必要的参数
        requestBody.put("parameters", Map.of(
            "temperature", 0.7,
            "top_p", 0.8,
            "result_format", "message"
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            logger.info("Sending request to Tongyi API: {}", objectMapper.writeValueAsString(requestBody));
            
            ResponseEntity<Map> response = restTemplate.exchange(
                aiConfig.getTongyiApiUrl(),
                HttpMethod.POST,
                request,
                Map.class
            );

            logger.info("Received response from Tongyi API: {}", objectMapper.writeValueAsString(response.getBody()));

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                Map<String, Object> output = (Map<String, Object>) responseBody.get("output");
                if (output != null) {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) output.get("choices");
                    if (choices != null && !choices.isEmpty()) {
                        Map<String, Object> choice = choices.get(0);
                        Map<String, Object> message = (Map<String, Object>) choice.get("message");
                        if (message != null && message.containsKey("content")) {
                            return message.get("content").toString();
                        }
                    }
                }
                throw new RuntimeException("API响应格式不正确");
            } else {
                throw new RuntimeException("API返回异常状态码: " + response.getStatusCode());
            }
        } catch (ResourceAccessException e) {
            logger.error("API调用超时", e);
            throw e;
        } catch (HttpClientErrorException e) {
            logger.error("API调用失败: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            logger.error("API调用发生错误", e);
            throw new RuntimeException("API调用失败: " + e.getMessage());
        }
    }

    private AiAnalyzeResponse createErrorResponse(String message) {
        AiAnalyzeResponse response = new AiAnalyzeResponse();
        response.setCode(1);
        response.setMessage(message);
        return response;
    }

    private AiChatResponse createChatErrorResponse(String message) {
        AiChatResponse response = new AiChatResponse();
        response.setCode(1);
        response.setMessage(message);
        return response;
    }
} 