package com.collegeapp.chatbot.chatmed;

import java.util.ArrayList;

public class DoctorInfo
{

    public String name;
    public int rating = 1;
    public ArrayList<String> professions = new ArrayList<String>();

    public DoctorInfo(String name, int rating, String... professions)
    {
        this.name = name;
        this.rating = rating;

        for(int i = 0; i < professions.length; ++i)
        {
            this.professions.add(professions[i]);
        }
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
