package com.collegeapp.chatbot;

public class ChatMessage
{
    private boolean isClient;
    public String message;

    public ChatMessage(boolean byClient, String message)
    {
        this.isClient = byClient;
        this.message = message;
    }

    public boolean byClient()
    {
        return isClient;
    }
    public void setByClient(boolean value)
    {
        isClient = value;
    }
}
