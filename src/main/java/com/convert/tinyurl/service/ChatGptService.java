package com.convert.tinyurl.service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.convert.tinyurl.util.ChatGptUtility;
import com.convert.tinyurl.model.ChatGptPromptRequest;

import okhttp3.*;

@Service
public class ChatGptService {
    
    @Value("${chatgpt.secret_key}")
    private String apiKey;

    @Value("${chatgpt.url}")
    private String apiUrl;

    @Value("${chatgpt.model}")
    private String model;

    @Value("${okhttp.timeout}")
    private Integer timeOut;

    public String getChatGptResponse(ChatGptPromptRequest promptRequest) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(timeOut, TimeUnit.SECONDS)
            .readTimeout(timeOut, TimeUnit.SECONDS)
            .writeTimeout(timeOut, TimeUnit.SECONDS)
            .build();

        String prompt = ChatGptUtility.formatChatGptPromptRequest(promptRequest);
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create("{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}", mediaType);

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        
        return ChatGptUtility.extractMessageFromJSONResponse(response.body().string());
    }
}