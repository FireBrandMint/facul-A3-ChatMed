package com.collegeapp.chatbot.msgtree;

public class MessageChoiceNode
{

    public String FrontMessage;
    public ChatChoice[] Choices;
    public MessageChoiceNode(String frontMessage, ChatChoice[] choices)
    {
        FrontMessage = frontMessage;
        Choices = choices;
    }

    public MessageChoiceNode selectChoice(int what)
    {
        int truewhat = what - 1;
        if(truewhat < 0 || Choices.length >= truewhat) return null;
        return Choices[truewhat].Select();
    }

    public String getDisplayMessage()
    {
        String result = FrontMessage + "\n\n" + "Por favor selecione uma das escolhas:\n";
        for(int i = 0; i < Choices.length; ++i)
        {
            result += "\n\n" + (i+1) + ". " + Choices[i].ChoiceDescription;
        }

        return result;
    }
}
