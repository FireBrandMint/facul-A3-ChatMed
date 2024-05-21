package com.collegeapp.chatbot.chatmed;

import java.util.ArrayList;
import java.util.Calendar;

//checklist
//
//feitos: agendar consulta, ver as 'notas' dos medicos junto com a profissão deles
//
//sistema recomenda médicos quando a pessoa pede uma recomendação
public class ChatMed<ChatClientInfo>
{
    CommandOnClient<ChatClientInfo> sendMsg;
    DoctorAccess getDoctors;

    DoctorRate<ChatClientInfo> rateDoctor;

    DoctorInfo[] doctorCache;

    ArrayList<UnassignedAppointment> vacantAppointments = new ArrayList<UnassignedAppointment>();

    ArrayList<Appointment<ChatClientInfo>> appointments = new ArrayList<Appointment<ChatClientInfo>>();

    public ChatMed(CommandOnClient<ChatClientInfo> funcSendMsg, DoctorAccess funcGetDoctors, DoctorRate<ChatClientInfo> onTryRate)
    {
        sendMsg = funcSendMsg;
        getDoctors = funcGetDoctors;
        rateDoctor = onTryRate;
    }

    public void insertAppointmentVacancy(DoctorInfo who, Calendar when)
    {
        vacantAppointments.add(new UnassignedAppointment(who, when));
    }

    public ArrayList<UnassignedAppointment> getVacantAppointments ()
    {
        return vacantAppointments;
    }

    public void removeVacantAppointment (int idx)
    {
        vacantAppointments.remove(idx);
    }

    public void tick()
    {
        for(int i = appointments.size(); i > 0; ++i)
        {
            int idx = i-1;
            Appointment<ChatClientInfo> curr = appointments.get(i);

            if(curr.when.after(Calendar.getInstance()))
            {
                appointments.remove(idx);
                continue;
            }

            //Uma hora antes da consulta, o sistema vai lembrar a pessoa que a consulta
            //existe.
            if(!curr.reminded && curr.when.getTimeInMillis() + 3600000L > Calendar.getInstance().getTimeInMillis())
            {
                int hora = curr.when.get(Calendar.HOUR);
                int minutos = curr.when.get(Calendar.MINUTE);
                String parteddia = curr.when.get(Calendar.AM_PM) == Calendar.AM ? "manhã" : "tarde";
                sendMsg.apply
                (
                    curr.cinfo,
                    "Aviso: você tem uma consulta com o doutor \""
                    + curr.withwho.name + "\" ás " + hora + ":" + minutos
                    + " da " + parteddia + "."
                );
                curr.reminded = true;
            }
        }
    }

    ///Method that should be called when receiving messages from a client.
    public void receiveMessage(ChatClientInfo responsible, String msg)
    {
        try
        {
            msg = msg.toLowerCase();

            String[] msgs = msg.split(" ");

            if(msgs.length == 0) return;

            if(msg.equals("ajuda"))
            {
                String result = "Mensagens conhecidas:";
                result += "\n\"quais são os doutores\"";
                result += "\n\"consultas disponíveis\"";
                result += "\n\"avaliar <nome do doutor> <pontos>\"";
                result += "\n\"agendar <numero da consulta disponivel>\"";

                sendMsg.apply(responsible, result);
            }
            else if(msg.equals("quais são os doutores"))
            {
                doctorCache = getDoctors.apply();

                String result = "";

                for(int i = 0; i < doctorCache.length; ++i)
                {
                    if(i != 0) result += "\n";
                    result += doctorCache[i].toString();
                }

                doctorCache = null;

                sendMsg.apply(responsible, msg);
            }
            else if(msg.equals("consultas disponíveis") || msg.equals("consultas disponiveis"))
            {
                String result = "";

                for(int i = 0; i < vacantAppointments.size(); ++i)
                {
                    UnassignedAppointment curr = vacantAppointments.get(i);

                    if(i == 0) result += i + ". " + curr.withwho.toString() + ". Horario: " + curr.when;
                    else result += '\n' + i + ". " + curr.withwho.toString() + ". Horario: " + curr.when;
                }

                if(vacantAppointments.size() > 0) sendMsg.apply(responsible, result);
                else sendMsg.apply(responsible, "Não há consultas disponíveis no momento. Verifique mais tarde.");
            }
            else if(msgs[0].equals("avaliar"))
            {
                String name = msgs[1];
                String pontos = null;
                for(int i = 2; i < msgs.length; ++i)
                {
                    if (i != msgs.length - 1) name += ' ' + msgs[i];
                    else
                    {
                        Integer.parseInt(msgs[i]);
                        pontos = msgs[i];
                    }
                }

                doctorCache = getDoctors.apply();

                DoctorInfo subject = null;

                for(int i = 0; i < doctorCache.length; ++i)
                {
                    DoctorInfo curr = doctorCache[i];

                    if(curr.name.equals(name))
                    {
                        subject = curr;
                        break;
                    }
                }
                doctorCache = null;

                if(subject == null) sendMsg.apply(responsible, "Não á um doutor com o nome exato \"" + name + "\".");
                else
                {
                    if(rateDoctor.apply(responsible, subject, Integer.parseInt(pontos)))
                        sendMsg.apply(responsible, "Sua avaliação foi registrada.");
                    else
                        sendMsg.apply(responsible, "Sua avaliação não pode ser registrada.");
                }
            }
            else if(msgs[0].equals("agendar"))
            {
                String num = msg.replace("agendar ", "");

                doctorCache = getDoctors.apply();

                int choice = Integer.parseInt(num);

                if(choice >= 0 && choice < vacantAppointments.size())
                {
                    UnassignedAppointment curr = vacantAppointments.get(choice);

                    appointments.add(new Appointment<ChatClientInfo>(responsible, curr.when, curr.withwho));

                    vacantAppointments.remove(choice);
                }
                else
                {
                    sendMsg.apply(responsible, "Consulta não existe.");
                }

                doctorCache = null;
            }
            else if(msgs[0].equals("recomendar"))
            {
                String profession = msg.replace("recomendar ", "");

                doctorCache = getDoctors.apply();

                DoctorInfo chosen = null;
                int unassignedAppointmentIndex = -1;

                for(int i = 0; i < vacantAppointments.size(); ++i)
                {
                    DoctorInfo curr = vacantAppointments.get(i).withwho;

                    boolean desired = false;

                    for(int i2 = 0; i2 < curr.professions.size(); ++i2)
                    {
                        if(profession.equals(curr.professions.get(i2)))
                        {
                            desired = true;
                            break;
                        }
                    }

                    if(desired && (chosen == null || chosen.rating < curr.rating))
                    {
                        chosen = curr;
                        unassignedAppointmentIndex = i;
                    }
                }

                if(chosen == null) sendMsg.apply(responsible, "Não á um médico esta profissão (" + profession + ") disponível no momento.");
                else
                {
                    UnassignedAppointment appointment = vacantAppointments.get(unassignedAppointmentIndex);

                    sendMsg.apply(responsible, "A melhor consulta disponível é a consulta " + unassignedAppointmentIndex + " uma com o doltor " + chosen.name + " com avaliação de " + chosen.rating + " pontos, se selecionada esta consulta vai ocorrer na data " + appointment.when + '.');
                }

                doctorCache = null;
            }
            else
            {
                sendMsg.apply(responsible, "Mensagem não reconhecida. Digite 'ajuda' para ver as mensagens conhecidas");
            }
        }
        catch (Exception e)
        {
            sendMsg.apply(responsible, "Ouve um erro com sua mensagem.");
        }
    }

    public class UnassignedAppointment
    {
        public DoctorInfo withwho;
        public Calendar when;

        public UnassignedAppointment(DoctorInfo withwho, Calendar when)
        {
            this.withwho = withwho;
            this.when = when;
        }
    }

    private class Appointment<ChatClientInfo>
    {
        public ChatClientInfo cinfo;
        public Calendar when;
        public DoctorInfo withwho;
        public boolean reminded = false;
        public Appointment(ChatClientInfo cinfo, Calendar when, DoctorInfo withwho)
        {
            this.cinfo = cinfo;
            this.when = when;
            this.withwho = withwho;
        }
    }

    public class DoctorInfo
    {

        public String name;
        public int rating = 1;
        public ArrayList<String> professions = new ArrayList<>();

        public DoctorInfo(String name)
        {
            this.name = name;
        }

        public boolean hasProfession(String profession)
        {
            boolean result = false;

            for(int i = professions.size(); i > 0; --i)
            {
                if(profession.equals(professions.get(i)))
                {
                    result = true;
                    break;
                }
            }

            return result;
        }

        public String toString()
        {
            String result = name + "[avaliação " + rating + "] - ";
            for(int i = 0; i < professions.size(); ++i)
            {
                if(i == 0)
                    result += professions.get(i);
                else
                    result += ", " + professions.get(i);
            }

            return result;
        }
    }
}
