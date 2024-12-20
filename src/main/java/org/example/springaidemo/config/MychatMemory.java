package org.example.springaidemo.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MychatMemory implements ChatMemory {

    Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    @Override
    public void add(String conversationId, List<Message> messages) {
        this.conversationHistory.computeIfAbsent(conversationId, id -> Collections.synchronizedList(new ArrayList<>()))
                .addAll(messages);
    }

    @Override
    public void add(String conversationId, Message message) {
        this.conversationHistory.computeIfAbsent(conversationId, id -> Collections.synchronizedList(new ArrayList<>()))
                .add(message);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        List<Message> allMessages = conversationHistory.get(conversationId);
        if (allMessages == null || allMessages.isEmpty()) {
            return List.of(); // 如果没有历史记录，返回空列表
        }

        // 计算获取的起始位置
        int start = Math.max(0, allMessages.size() - lastN);
        return new ArrayList<>(allMessages.subList(start, allMessages.size())); // 返回一个新列表，避免外部修改

    }

    @Override
    public void clear(String conversationId) {
        conversationHistory.remove(conversationId); // 移除该会话的历史记录
    }
}
