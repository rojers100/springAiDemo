package org.example.springaidemo.config;

import org.example.springaidemo.config.knowledge.KnowledgeBase;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天过程中会自动给记录，我们重写了记录的方法，就是让按照我们的设想进行历史记录的保存
 * 0 利用 Spring AI Chat 框架，通过会话 ID（token）管理不同用户的上下文和对话。
 */
@Component
public class MychatMemory implements ChatMemory {

    Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    @Autowired
    @Qualifier("textKnowledgeBase")
    private KnowledgeBase textKnowledgeBase;

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

    public void addToTextKnowledge(String content) {
        textKnowledgeBase.addKnowledge(content);
    }

    public List<String> searchTextKnowledge(String query) {
        return textKnowledgeBase.searchKnowledgeAll();
    }

    public boolean updateTextKnowledge(String oldContent, String newContent) {
        return textKnowledgeBase.updateKnowledge(oldContent, newContent);
    }
}
