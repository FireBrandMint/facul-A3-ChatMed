package com.collegeapp.chatbot.msgtree;

import java.util.ArrayList;

public class ChatChoice
{
    public String ChoiceDescription;
    public MessageChoiceNode NodeNext;

    Action[] OnTrigger;

    public ChatChoice(String choiceDescription, MessageChoiceNode nextNode)
    {
        ChoiceDescription = choiceDescription;
        NodeNext = nextNode;
        OnTrigger = null;
    }

    public ChatChoice(String choiceDescription, MessageChoiceNode nextNode, Action[] onTrigger)
    {
        ChoiceDescription = choiceDescription;
        NodeNext = nextNode;
        OnTrigger = onTrigger;
    }

    public MessageChoiceNode Select()
    {
        if(OnTrigger != null)
        {
            for(int i = 0; i < OnTrigger.length; ++i)
            {
                OnTrigger[i].apply();
            }
        }

        return NodeNext;
    }
}
