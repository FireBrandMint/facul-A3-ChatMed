package com.collegeapp.chatbot;

import android.util.Log;

import com.collegeapp.chatbot.chatmed.ChatMed;
import com.collegeapp.chatbot.chatmed.CommandOnClient;
import com.collegeapp.chatbot.chatmed.DoctorAccess;
import com.collegeapp.chatbot.chatmed.DoctorInfo;
import com.collegeapp.chatbot.chatmed.DoctorRate;
import com.collegeapp.chatbot.chatmed.test.TestChatClientInfo;

import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Calendar;

import jade.core.Agent;

import android.util.Log;

import com.collegeapp.chatbot.chatmed.ChatMed;
import com.collegeapp.chatbot.chatmed.CommandOnClient;
import com.collegeapp.chatbot.chatmed.DoctorAccess;
import com.collegeapp.chatbot.chatmed.DoctorInfo;
import com.collegeapp.chatbot.chatmed.DoctorRate;
import com.collegeapp.chatbot.chatmed.test.TestChatClientInfo;

import java.util.ArrayList;
import java.util.Calendar;

import jade.core.Agent;

public class JadeAgent extends Agent {
    JadeManager Owner;

    static boolean init = true;

    static TestChatClientInfo sampleUser = new TestChatClientInfo("Booris");

    static DoctorInfo[] doctors = {
            new DoctorInfo("Andre Marques", 6, "Oftalmologista"),
            new DoctorInfo("George Michaels", 7, "Oftalmologista"),
            new DoctorInfo("Maria Joana", 7, "Dermatologista"),
            new DoctorInfo("Joao Reginaldo", 9, "Dermatologista"),
            new DoctorInfo("Alexandre Mateus", 4, "Cardiologista"),
            new DoctorInfo("Maria Casanova", 7, "Cardiologista"),
            new DoctorInfo("Anakin Esteves", 2, "Psiquiatra"),
            new DoctorInfo("Sabrina Violeta", 10, "Psiquiatra"),
    };

    static ChatMed<TestChatClientInfo> chatMed;

    @Override
    protected void setup() {
        Owner = JadeManager.Singleton;
        if (init) {
            init = false;
            initialize();
            addSampleAppointments();  // Chama o mÃ©todo para adicionar consultas vagas
        }
    }

    static void initialize() {
        CommandOnClient<TestChatClientInfo> a = JadeAgent::sendMsg;
        DoctorAccess b = () -> doctors;
        DoctorRate<TestChatClientInfo> c = JadeAgent::onClientRate;

        chatMed = new ChatMed<>(a, b, c);
    }

    public static void onTick() {
        if (init) {
            init = false;
            initialize();
            addSampleAppointments();  // Chama o mÃ©todo para adicionar consultas vagas
        }

        chatMed.tick();

        JadeManager owner = JadeManager.Singleton;
        String cmsg = owner.popClientMessage();
        while (cmsg != null) {
            chatMed.receiveMessage(sampleUser, cmsg);
            cmsg = owner.popClientMessage();
        }
    }

    static void sendMsg(TestChatClientInfo c, String msg) {
        JadeManager.Singleton.notifyBotMessage(msg);
    }

    static boolean onClientRate(TestChatClientInfo client, DoctorInfo subject, int rating) {
        subject.rating = (subject.rating + rating) >> 1;
        return true;
    }

    public static void addSampleAppointments() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);  // Adiciona um dia Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[0], calendar);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);  // Adiciona dois dias Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[1], calendar);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 60);  // Adiciona dois dias Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[0], calendar);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2);  // Adiciona dois dias Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[1], calendar);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);  // Adiciona dois dias Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[0], calendar);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 2);  // Adiciona dois dias Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[1], calendar);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2);  // Adiciona dois dias Ã  data atual
        calendar.add(Calendar.HOUR, 4);  // Adiciona dois dias Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[7], calendar);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 4);  // Adiciona dois dias Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[7], calendar);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 4);  // Adiciona dois dias Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[2], calendar);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2);  // Adiciona dois dias Ã  data atual
        calendar.add(Calendar.HOUR, 2);  // Adiciona dois dias Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[3], calendar);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2);  // Adiciona dois dias Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[4], calendar);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 3);  // Adiciona dois dias Ã  data atual
        calendar.add(Calendar.HOUR, 2);  // Adiciona dois dias Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[5], calendar);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);  // Adiciona dois dias Ã  data atual
        calendar.add(Calendar.HOUR_OF_DAY, 2);  // Adiciona dois dias Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[6], calendar);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 2);  // Adiciona dois dias Ã  data atual
        calendar.add(Calendar.HOUR, 10);  // Adiciona dois dias Ã  data atual
        chatMed.insertAppointmentVacancy(doctors[6], calendar);
    }

    private static void loopBackup() {
        while (true) {
            onTick();
            try {
                Thread.sleep(90);
            } catch (Exception e) {
                // Handle exception
            }
        }
    }

    public static void backupPlan() {
        Thread thread = new Thread(JadeAgent::loopBackup);
        thread.start();
    }
}

