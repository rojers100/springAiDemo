package org.example.springaidemo.impl;

import org.example.springaidemo.config.MychatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * SimpleControllerImpl 是一个服务类，用于处理基于 AI 的对话。
 * 它利用 Spring AI Chat 框架，通过会话 ID（token）管理不同用户的上下文和对话。
 */
@Service
public class SimpleControllerImpl {

    // AI 对话客户端实例
    private final ChatClient client;

    // 自定义的对话存储实现，用于保存用户会话上下文
    private final MychatMemory mychatMemory;

    /**
     * 构造方法，初始化 ChatClient 和自定义的对话存储。
     *
     * @param clientBuilder 用于构建 ChatClient 的构建器
     * @param mychatMemory  自定义的对话存储实现
     */
    @Autowired
    public SimpleControllerImpl(ChatClient.Builder clientBuilder, MychatMemory mychatMemory) {
        this.mychatMemory = mychatMemory;
        // 初始化 ChatClient，并设置默认系统提示和对话存储
        this.client = clientBuilder.defaultSystem(
                """
        你是一家名叫“Rojer”的淘宝客服。
        当用户语句中包含“退款”时，你需要回复“不好意思，本店不支持7天无理由退款”
        """
        )
                .defaultAdvisors(new PromptChatMemoryAdvisor(mychatMemory))
                .build();
    }

    /**
     * 生成基于用户消息和会话 token 的 AI 回复。
     *
     * @param msg   用户输入的消息
     * @param token 表示会话唯一标识，用于区分不同用户的上下文
     * @return AI 的回复内容
     */
    public String generate(String msg, String token) {
        return this.client.prompt()
                .user(msg) // 用户的输入
                .advisors(adv -> adv
                        // 设置检索的上下文记录条数
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
                        // 指定会话唯一标识，用于区分不同的用户对话
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, token))
                .call() // 调用 AI 服务，生成回复
                .content(); // 获取生成的文本内容
    }

    /**
     * 以流式方式生成基于用户消息和会话 token 的 AI 回复。
     * 适用于需要逐步接收回复内容的场景，例如聊天应用中的实时响应。
     *
     * @param msg   用户输入的消息
     * @param token 表示会话唯一标识，用于区分不同用户的上下文
     * @return Flux<String> 流式的回复内容
     */
    public Flux<String> generateStream(String msg, String token) {
        return this.client.prompt()
                .user(msg) // 用户的输入
                .advisors(adv -> adv
                        // 设置检索的上下文记录条数
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
                        // 指定会话唯一标识，用于区分不同的用户对话
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, token))
                .functions("getPrice")// 指定需要调用的功能
                .stream() // 以流式模式调用 AI 服务
                .content(); // 获取生成的文本流内容
    }


    public Double priceAll(int count) {
        double price = 3.25;
        double re = price * count;
        System.out.println("打印这条内容，代表已经执行了priceAll该方法。");
        return re;
    }
}
