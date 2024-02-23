package com.convert.tinyurl.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.convert.tinyurl.model.ChatGptPromptRequest;
import com.convert.tinyurl.service.ChatGptService;

@RestController
@RequestMapping("chatgpt/v1")
public class ChatGptRequestController {
    
    @Autowired
    private ChatGptService chatGptService;

    @GetMapping("/query")
    public String getChatGPTResponseForRequest(@RequestBody ChatGptPromptRequest prompt) throws IOException {
        return chatGptService.getChatGptResponse(prompt);
    }
}
