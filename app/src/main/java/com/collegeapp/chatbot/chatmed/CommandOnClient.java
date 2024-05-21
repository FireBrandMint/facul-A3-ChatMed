package com.collegeapp.chatbot.chatmed;

@FunctionalInterface
public interface CommandOnClient<ChatClientInfo>
{
    void apply(ChatClientInfo info, String command);
}
