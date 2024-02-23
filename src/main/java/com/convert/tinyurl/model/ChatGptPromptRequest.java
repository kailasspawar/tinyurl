package com.convert.tinyurl.model;

public class ChatGptPromptRequest {
    
    private String fromVersion;
    private String toVersion;
    private String lineNumber;
    private String codeSnippet;

    @Override
    public String toString() {
        return "ChatGptPromptRequest [fromVersion=" + fromVersion + ", toVersion=" + toVersion + ", lineNumber="
                + lineNumber + ", codeSnippet=" + codeSnippet + "]";
    }
    public String getFromVersion() {
        return fromVersion;
    }
    public void setFromVersion(String fromVersion) {
        this.fromVersion = fromVersion;
    }
    public String getToVersion() {
        return toVersion;
    }
    public void setToVersion(String toVersion) {
        this.toVersion = toVersion;
    }
    public String getLineNumber() {
        return lineNumber;
    }
    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }
    public String getCodeSnippet() {
        return codeSnippet;
    }
    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    
}
