package org.example.springaidemo.config.knowledge;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class TextKnowledgeBase implements KnowledgeBase {
    private final List<String> textKnowledge = new CopyOnWriteArrayList<>();
    
    @Value("${knowledge.text.base-path:knowledge}")
    private String basePath;
    
    @Value("${knowledge.text.file-name:knowledge.txt}")
    private String fileName;
    
    private Path getFilePath() {
        try {
            Path directory = Paths.get(basePath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            Path filePath = directory.resolve(fileName);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("无法创建知识库文件", e);
        }
    }

    @Override
    public void addKnowledge(String content) {
        textKnowledge.add(content);
        // 将新知识写入文件
        try {
            Files.write(getFilePath(), 
                       (content + System.lineSeparator()).getBytes(), 
                       StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("写入知识库失败", e);
        }
    }

    @Override
    public List<String> searchKnowledge(String query) {
        try {
            // 从文件读取所有内容               ·
            return Files.readAllLines(getFilePath());
        } catch (IOException e) {
            throw new RuntimeException("读取知识库失败", e);
        }
    }

    @Override
    public boolean updateKnowledge(String oldContent, String newContent) {
        try {
            Path filePath = getFilePath();
            List<String> lines = Files.readAllLines(filePath);
            
            boolean found = false;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).equals(oldContent)) {
                    lines.set(i, newContent);
                    found = true;
                }
            }
            
            if (found) {
                // 更新文件
                Files.write(filePath, lines, StandardOpenOption.TRUNCATE_EXISTING);
                
                // 更新内存中的数据
                textKnowledge.removeIf(content -> content.equals(oldContent));
                textKnowledge.add(newContent);
                
                return true;
            }
            
            return false;
        } catch (IOException e) {
            throw new RuntimeException("更新知识库失败", e);
        }
    }
} 