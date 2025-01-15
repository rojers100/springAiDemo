package org.example.springaidemo.controller;

import org.example.springaidemo.impl.SimpleControllerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class SimpleController {

    private final SimpleControllerImpl simpleControllerImpl;

    @Autowired
    public SimpleController(SimpleControllerImpl simpleControllerImpl) {
        this.simpleControllerImpl = simpleControllerImpl;
    }

    @PostMapping
    public String chat(@RequestBody String message, @RequestHeader(value = "token") String token) {
        return simpleControllerImpl.chat(message, token);
    }
}
