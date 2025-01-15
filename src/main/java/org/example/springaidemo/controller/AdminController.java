package org.example.springaidemo.controller;

import org.example.springaidemo.config.MychatMemory;
import org.example.springaidemo.config.knowledge.KnowledgeBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/knowledge")
public class AdminController {

    @Autowired
    private MychatMemory mychatMemory;
    
    @Autowired
    @Qualifier("textKnowledgeBase")
    private KnowledgeBase textKnowledgeBase;

    // 添加文本知识
    @PostMapping("/text")
    public ResponseEntity<String> addTextKnowledge(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("内容不能为空");
        }
        
        try {
            mychatMemory.addToTextKnowledge(content.trim());
            return ResponseEntity.ok("添加成功");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("添加失败：" + e.getMessage());
        }
    }

    // 搜索文本知识
    @GetMapping("/text/search")
    public ResponseEntity<?> searchTextKnowledge(@RequestParam String query) {
        try {
            List<String> results = mychatMemory.searchTextKnowledge(query);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("搜索失败：" + e.getMessage());
        }
    }

    // 修改文本知识
    @PutMapping("/text")
    public ResponseEntity<String> updateTextKnowledge(
            @RequestBody Map<String, String> request) {
        String oldContent = request.get("oldContent");
        String newContent = request.get("newContent");
        
        if (oldContent == null || newContent == null || 
            oldContent.trim().isEmpty() || newContent.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("原内容和新内容都不能为空");
        }
        
        try {
            boolean updated = textKnowledgeBase.updateKnowledge(oldContent.trim(), newContent.trim());
            return updated ? 
                ResponseEntity.ok("修改成功") : 
                ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("修改失败：" + e.getMessage());
        }
    }
} 