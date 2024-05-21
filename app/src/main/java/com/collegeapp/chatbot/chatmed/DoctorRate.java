package com.collegeapp.chatbot.chatmed;

@FunctionalInterface
public interface DoctorRate<ChatClientInfo>
{
    boolean apply(ChatClientInfo client, DoctorInfo subject, int rating);
}
