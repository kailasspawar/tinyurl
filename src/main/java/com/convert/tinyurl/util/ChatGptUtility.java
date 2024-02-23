package com.convert.tinyurl.util;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import com.convert.tinyurl.model.ChatGptPromptRequest;

public class ChatGptUtility {
    
    public static String formatChatGptPromptRequest(ChatGptPromptRequest promptRequest) {
        String reqString = "";
        if (!StringUtils.isEmpty(promptRequest.getFromVersion()) && !StringUtils.isEmpty(promptRequest.getToVersion())) {
            reqString = "Please convert following code snippet from java " +promptRequest.getFromVersion() + " to java version " + promptRequest.getToVersion();
        }
        
        reqString = reqString + " " + StringEscapeUtils.escapeEcmaScript(promptRequest.getCodeSnippet());

        return reqString;
    }

    public static String extractMessageFromJSONResponse(String response) {
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray choices = jsonResponse.getJSONArray("choices");
        
        return choices.getJSONObject(0)
                .getJSONObject("message")
                .getString("content");
    }

}
