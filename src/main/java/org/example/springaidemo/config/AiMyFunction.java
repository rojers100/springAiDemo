package org.example.springaidemo.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class AiMyFunction {
    // AI 对话客户端实例
    private final ChatClient client;

    // 自定义的对话存储实现，用于保存用户会话上下文
    private final MychatMemory mychatMemory;

    public AiMyFunction(ChatClient.Builder clientBuilder, MychatMemory mychatMemory) {
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
     * 根据外挂知识库，聊天历史记录搜索回答问题
     *
     * @param msg
     * @param token
     * @return
     */
    public String generate(String msg, String token) {
        String content = client.prompt()
                .user(msg) // 用户的输入
                .advisors(adv -> adv
                        // 设置检索的上下文记录条数
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
                        // 指定会话唯一标识，用于区分不同的用户对话
                        .param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, token))
                .call() // 调用 AI 服务，生成回复
                .content();// 获取生成的文本内容
        return content;
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
}
