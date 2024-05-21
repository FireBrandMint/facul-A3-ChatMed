package com.collegeapp.chatbot.chatmed.test;

import androidx.annotation.Nullable;

public class TestChatClientInfo
{
    public String name;

    public TestChatClientInfo(String name)
    {
        this.name = name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof TestChatClientInfo)
        {
            return this.name.equals(((TestChatClientInfo) obj).name);
        }

        return false;
    }
}
