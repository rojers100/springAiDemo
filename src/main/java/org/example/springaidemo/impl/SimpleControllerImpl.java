package org.example.springaidemo.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.springaidemo.config.AiMyFunction;
import org.example.springaidemo.config.knowledge.TextKnowledgeBase;
import org.example.springaidemo.context.UserContext;
import org.example.springaidemo.dao.mapper.Mapper1;
import org.example.springaidemo.moudle.UserModel;
import org.example.springaidemo.moudle.UserRestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class SimpleControllerImpl {

    @Autowired
    private AiMyFunction aiMyFunction;

    @Autowired
    private TextKnowledgeBase textKnowledgeBase;

    @Autowired
    private Mapper1 mapper1;

    public String chat(String message, String token) {
        // 如果是休假相关的请求，使用特殊处理
        if (message.contains("休假") || message.contains("请假")) {
            return handleRestRequest(message, token);
        }

        // 普通对话处理
        List<String> knowledgeResults = textKnowledgeBase.searchKnowledgeAll();
        StringBuilder promptBuilder = new StringBuilder();
        if (!knowledgeResults.isEmpty()) {
            promptBuilder.append("根据以下参考知识回答问题：\n\n");
            for (String knowledge : knowledgeResults) {
                promptBuilder.append("- ").append(knowledge).append("\n");
            }
            promptBuilder.append("\n问题是：").append(message);
        } else {
            promptBuilder.append(message);
        }

        return aiMyFunction.generateNormal(promptBuilder.toString(), token);
    }

    @Async
    public CompletableFuture<Void> processRestAsync(String userId, LocalDateTime startTime, LocalDateTime endTime, String restType, String status, String reason) {
        return CompletableFuture.runAsync(() -> {
            try {
                UserRestModel restModel = new UserRestModel();
                restModel.setUserid(userId);
                restModel.setStartTime(startTime);
                restModel.setEndTime(endTime);
                restModel.setRestType(restType);
                restModel.setStatus(status);
                if ("REJECTED".equals(status)) {
                    restModel.setRejectReason(reason);
                }
                mapper1.addUserRest(restModel);
                log.info("Successfully processed rest request for user: {}", userId);
            } catch (Exception e) {
                log.error("Error processing rest request for user: {}", userId, e);
            }
        });
    }

    private String handleRestRequest(String message, String token) {
        UserModel user = UserContext.getCurrentUser();
        if (user == null) {
            return "未找到用户信息";
        }

        // 构建提示词，包含用户角色和规则信息
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("当前用户角色：").append(user.getRoles()).append("\n");
        promptBuilder.append("请根据以下规则分析休假申请：\n");
        List<String> rules = textKnowledgeBase.searchKnowledgeAll();
        for (String rule : rules) {
            promptBuilder.append("- ").append(rule).append("\n");
        }
        promptBuilder.append("\n请分析以下休假申请，并从中提取开始时间、结束时间和休假类型：\n");
        promptBuilder.append(message).append("\n");
        promptBuilder.append("\n如果信息不完整或格式不正确，请直接回复提示用户正确的格式。");
        promptBuilder.append("\n如果信息完整，请按以下格式回复：");
        promptBuilder.append("\n批准：[分析理由]|[开始时间]|[结束时间]|[休假类型]");
        promptBuilder.append("\n或");
        promptBuilder.append("\n拒绝：[拒绝原因]|[开始时间]|[结束时间]|[休假类型]");

        // 调用AI处理休假请求
        String aiResponse = aiMyFunction.generateNormal(promptBuilder.toString(), token);
        log.info("AI Response for rest request: {}", aiResponse);

        // 解析AI响应并异步处理数据库操作
        if (aiResponse.startsWith("批准：")) {
            String[] parts = aiResponse.substring(3).split("\\|");
            if (parts.length != 4) {
                return "AI响应格式错误，请重试";
            }
            String reason = parts[0];
            LocalDateTime startTime = LocalDateTime.parse(parts[1].trim() + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endTime = LocalDateTime.parse(parts[2].trim() + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String restType = parts[3].trim();

            processRestAsync(user.getUserid(), startTime, endTime, restType, "PENDING", null);
            return String.format("批准：%s\n请确认以下休假信息：\n开始时间：%s\n结束时间：%s\n休假类型：%s", 
                reason, startTime.toLocalDate(), endTime.toLocalDate(), restType);
        } else if (aiResponse.startsWith("拒绝：")) {
            String[] parts = aiResponse.substring(3).split("\\|");
            if (parts.length != 4) {
                return "AI响应格式错误，请重试";
            }
            String reason = parts[0];
            LocalDateTime startTime = LocalDateTime.parse(parts[1].trim().replace("[", "").replace("]", "")
                            + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endTime = LocalDateTime.parse(parts[2].trim().replace("[", "").replace("]", "")
                            + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String restType = parts[3].trim();

            processRestAsync(user.getUserid(), startTime, endTime, restType, "REJECTED", reason);
            return String.format("拒绝：%s", reason);
        }
        
        return aiResponse;
    }

    public Double priceAll(int count) {
        double price = 3.25;
        return price * count;
    }
}
