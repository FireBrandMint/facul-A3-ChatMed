package com.collegeapp.chatbot;

import java.util.ArrayList;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class JadeAgent extends Agent
{
    JadeManager owner;

    static String[] Items = new String[]
    {
        "Burger",
        "Fritas",
        "Cola",
        "Sorvete"
    };

    static ArrayList<String> Selected = new ArrayList<String>();

    static boolean confirmingChoices = false;

    @Override
    protected void setup() {
        owner = JadeManager.Singleton;

        addBehaviour(new TickerBehaviour(this, 90) {
            @Override
            protected void onTick() {
                ((JadeAgent)myAgent).onTick();
            }
        });
    }

    public static void onTick()
    {
        JadeManager owner = JadeManager.Singleton;
        String cmsg = owner.popClientMessage();
        while (cmsg != null)
        {
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


            cmsg = owner.popClientMessage();
        }
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
