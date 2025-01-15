package org.example.springaidemo.impl;

import org.example.springaidemo.config.AiMyFunction;
import org.example.springaidemo.config.knowledge.TextKnowledgeBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleControllerImpl {

    @Autowired
    private AiMyFunction aiMyFunction;

    @Autowired
    private TextKnowledgeBase textKnowledgeBase;

    public String chat(String message, String token) {
        // 首先搜索外挂知识库
        List<String> knowledgeResults = textKnowledgeBase.searchKnowledge(message);

        // 构建提示词
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

        // 调用AI接口获取回答
        String aiResponse = aiMyFunction.generate(promptBuilder.toString(), token);

        return aiResponse;
    }

    public Double priceAll(int count) {
        double price = 3.25;
        double re = price * count;
        System.out.println("打印这条内容，代表已经执行了priceAll该方法。");
        return re;
    }
}
