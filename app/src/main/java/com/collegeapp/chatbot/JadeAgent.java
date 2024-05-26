package com.collegeapp.chatbot;

import com.collegeapp.chatbot.chatmed.ChatMed;
import com.collegeapp.chatbot.chatmed.CommandOnClient;
import com.collegeapp.chatbot.chatmed.DoctorAccess;
import com.collegeapp.chatbot.chatmed.DoctorInfo;
import com.collegeapp.chatbot.chatmed.DoctorRate;
import com.collegeapp.chatbot.chatmed.test.TestChatClientInfo;
import com.collegeapp.chatbot.msgtree.ChatOptionManager;

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
        CommandOnClient<TestChatClientInfo> a = JadeAgent::sendMsg;
        DoctorAccess b = () -> doctors;
        DoctorRate<TestChatClientInfo> c = JadeAgent::onClientRate;
        
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
        while (cmsg != null)
        {

            chatMed.receiveMessage(sampleUser, cmsg);
            /*
            if(cmsg.equals("oi"))
                owner.notifyBotMessage("Olá, atendimento do fast food aqui.");
            else if(cmsg.contains("google"))
                owner.notifyBotMessage("Não tenho nenhuma função relacionada ao Google.");
            else if(cmsg.equals("olá"))
                owner.notifyBotMessage("Oi! :] Atendimento do fast food aqui.");
            else if(cmsg.equals("cardapio"))
            {
                String send = "";
                if(Items.length > 0)
                {
                    for(int i = 0; i < Items.length; ++i)
                    {
                        send += (i + 1) +  ". " + Items[i];
                        if(i != Items.length - 1) send += "\n";
                    }
                    owner.notifyBotMessage(send);
                }
            }
            else if(cmsg.contains("eu quero"))
            {
                for(int i = 0; i < Items.length; ++i)
                {
                    String curr = Items[i];

                    if(cmsg.contains(curr.toLowerCase()))
                    {
                        Selected.add(curr);
                        owner.notifyBotMessage("Sua escolha foi adicionada.");
                    }
                }
            }
            else if(cmsg.contains("minhas compras"))
            {
                String send = "";
                if(Selected.size() > 0)
                {
                    for(int i = 0; i < Selected.size(); ++i)
                    {
                        send += (i + 1) +  ". " + Selected.get(i);
                        if(i != Selected.size() - 1) send += "\n";
                    }
                }
                else send = "Nenhum items encontrado.";

                owner.notifyBotMessage(send);
            }
            else if(cmsg.equals("comfirmar minha compra"))
            {
                confirmingChoices = true;
                owner.notifyBotMessage("Você tem certeza?");
            }
            else if(confirmingChoices && cmsg.equals("sim"))
            {
                confirmingChoices = false;
                Selected.clear();
                owner.notifyBotMessage("Sua compra foi confirmada e esta sendo enviada para você.");
            }
            else if(confirmingChoices && cmsg.equals("não"))
            {
                confirmingChoices = false;
                owner.notifyBotMessage("Confirmação de compra abortada.");
            }

            */
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
