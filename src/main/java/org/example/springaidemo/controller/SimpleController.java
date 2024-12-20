package org.example.springaidemo.controller;

import org.example.springaidemo.impl.SimpleControllerImpl;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class SimpleController {


    private final SimpleControllerImpl simpleControllerimpl;

    @Autowired
    public SimpleController(OpenAiChatModel openAiChatModel, SimpleControllerImpl simpleControllerimpl) {
        this.simpleControllerimpl = simpleControllerimpl;
    }


    @GetMapping("/ai/generate")
    public String generate(@RequestParam(value = "message", defaultValue = "讲个笑话") String message,
                           @RequestHeader(value = "token") String token) {
        return simpleControllerimpl.generate(message, token);
    }

    @GetMapping("/ai/generateStream")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "讲个笑话") String message,
                                       @RequestHeader(value = "token") String token) {
        return simpleControllerimpl.generateStream(message, token);
    }

}
