package com.collegeapp.chatbot.chatmed;

@FunctionalInterface
public interface DoctorRate<ChatClientInfo>
{
    boolean apply(ChatClientInfo client, ChatMed.DoctorInfo subject, int rating);
}
