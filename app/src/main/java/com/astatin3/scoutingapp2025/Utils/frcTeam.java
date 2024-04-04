package com.astatin3.scoutingapp2025.Utils;

public class frcTeam {
    public int teamNumber = 0;
    public String teamName = null;
    public String city = null;
    public String stateOrProv = null;
    public String school = null;
    public String country = null;
    public int startingYear = 0;

    public String getDescription(){
        return teamName + " Started in " + startingYear + ", and is from " + school + " in " + city + ", " + stateOrProv + ", " + country;
    }

    public frcTeam encode(){
        return this;
    }
    public frcTeam decode(){
        return this;
    }
}