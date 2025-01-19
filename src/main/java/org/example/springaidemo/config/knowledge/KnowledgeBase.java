package org.example.springaidemo.config.knowledge;

import java.util.List;

public interface KnowledgeBase {
    void addKnowledge(String content);
    List<String> searchKnowledgeAll();
    boolean updateKnowledge(String oldContent, String newContent);
} 