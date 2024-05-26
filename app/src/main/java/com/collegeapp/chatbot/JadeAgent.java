package com.collegeapp.chatbot;

import android.util.Log;

import com.collegeapp.chatbot.chatmed.ChatMed;
import com.collegeapp.chatbot.chatmed.CommandOnClient;
import com.collegeapp.chatbot.chatmed.DoctorAccess;
import com.collegeapp.chatbot.chatmed.DoctorInfo;
import com.collegeapp.chatbot.chatmed.DoctorRate;
import com.collegeapp.chatbot.chatmed.test.TestChatClientInfo;

import java.util.ArrayList;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class JadeAgent extends Agent
{
    JadeManager Owner;

    static boolean init = true;

    static TestChatClientInfo sampleUser = new TestChatClientInfo("Booris");

    static DoctorInfo[] doctors =
            {
                    new DoctorInfo("André", 6, "oculista"),
                    new DoctorInfo("George", 7, "oculista")
            };

    static ChatMed<TestChatClientInfo> chatMed;

    @Override
    protected void setup() {
        Owner = JadeManager.Singleton;

        addBehaviour(new TickerBehaviour(this, 90) {
            @Override
            protected void onTick() {
                ((JadeAgent)myAgent).onTick();
            }
        });
    }

    static void initialize()
    {
        //função para o sistema mandar a mensagem pro cliente
        CommandOnClient<TestChatClientInfo> a = JadeAgent::sendMsg;
        //função para o sistema ver doutores que existem
        DoctorAccess b = () -> doctors;
        //função para quando o cliente avalia o medico
        DoctorRate<TestChatClientInfo> c = JadeAgent::onClientRate;

        //conecta estas funções ao sistema
        chatMed = new ChatMed<TestChatClientInfo>(a, b, c);
    }

    public static void onTick()
    {
        if(init)
        {
            init = false;
            initialize();
        }

        chatMed.tick();

        JadeManager owner = JadeManager.Singleton;
        String cmsg = owner.popClientMessage();
        //enquanto mensagens existem, responder a elas
        while (cmsg != null)
        {
            //mande mensagem para o sistema
            chatMed.receiveMessage(sampleUser, cmsg);
            cmsg = owner.popClientMessage();
        }
    }
    static void sendMsg(TestChatClientInfo c, String msg)
    {
        JadeManager.Singleton.notifyBotMessage(msg);
    }

    static boolean onClientRate(TestChatClientInfo client, DoctorInfo subject, int rating)
    {
        //avg rating = (this rating + avg rating) / 2
        subject.rating = (subject.rating + rating) >> 1;

        return true;
    }

    private static void loopBackup()
    {
        while(true)
        {
            onTick();
            try {
                Thread.sleep(90);
            }
            catch (Exception e)
            {

            }
        }
    }

    public static void backupPlan()
    {
        Thread thread = new Thread(JadeAgent::loopBackup);
        thread.start();
    }
}
