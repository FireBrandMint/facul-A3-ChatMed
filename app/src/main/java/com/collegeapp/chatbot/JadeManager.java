package com.collegeapp.chatbot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import jade.imtp.leap.sms.Boot;

public class JadeManager
{
    public static JadeManager Singleton;
    private CollegeappBotscreen owner;
    private Thread jadeThread;
    private List<String> received;

    private ReentrantLock mutex = new ReentrantLock();

    public JadeManager(CollegeappBotscreen owner)
    {
        Singleton = this;
        this.owner = owner;
    }
    public void Initialize()
    {
        jadeThread = new Thread(this::JadeProcess);
        received = Collections.synchronizedList(new ArrayList<>());
        jadeThread.start();
    }

    public void notifyClientMessage(String msg)
    {
        try
        {
            mutex.lock();
            received.add(msg.toLowerCase());
        }
        finally {
            mutex.unlock();
        }
    }

    public String popClientMessage()
    {
        try
        {
            mutex.lock();
            int size = received.size();
            if(size == 0) return null;

            return received.remove(size - 1);
        }
        finally {
            mutex.unlock();
        }
    }

    public void notifyBotMessage(String msg)
    {
        owner.addToChat(msg, false);
    }

    private void JadeProcess()
    {
        String[] args = new String[]
                {
                        "-name", "goodspace", "-agents",
                    "Andrew:com.collegeapp.chatbot.JadeAgent"
                };

            JadeAgent.backupPlan();

    }
}
