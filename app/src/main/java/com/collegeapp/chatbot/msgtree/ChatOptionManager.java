package com.collegeapp.chatbot.msgtree;

public class ChatOptionManager
{
    public MessageChoiceNode Main;

    public ChatOptionManager(MessageChoiceNode main)
    {
        Main = main;
    }

    public boolean selectChoice (int what)
    {
        MessageChoiceNode fodder = Main.selectChoice(what);
        if(fodder == null) return false;

        Main = fodder;
        return true;
    }

    public String getDisplayMessage()
    {
        return Main.getDisplayMessage();
    }
}
