package com.collegeapp.chatbot.chatmed;

@FunctionalInterface
public interface DoctorAccess
{
    ChatMed.DoctorInfo[] apply();
}
