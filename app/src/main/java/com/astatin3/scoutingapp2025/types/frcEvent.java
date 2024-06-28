package com.astatin3.scoutingapp2025.types;

import androidx.annotation.NonNull;

import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.ByteBuilder;

import java.util.ArrayList;

public class frcEvent {
    public static final int typecode = 254;
    public String eventCode;
    public String name;
    public ArrayList<frcMatch> matches;
    public ArrayList<frcTeam> teams;

    public byte[] encode(){
        try {
            ByteBuilder bb = new ByteBuilder()
            .addString(eventCode)
            .addString(name);

            for(frcTeam teams : teams){
                bb.addRaw(frcTeam.typecode, teams.encode());
            }

            for(frcMatch match : matches){
                bb.addRaw(frcMatch.typecode, match.encode());
            }

            return bb.build();

        } catch (ByteBuilder.buildingException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static frcEvent decode(byte[] bytes){
        try{
            ArrayList<BuiltByteParser.parsedObject> objects = new BuiltByteParser(bytes).parse();

            frcEvent frc = new frcEvent();

            frc.eventCode = (String) objects.get(0).get();
            frc.name = (String) objects.get(1).get();

            frc.matches = new ArrayList<>();
            frc.teams = new ArrayList<>();

            for(BuiltByteParser.parsedObject object : objects){
                if(object.getType() == frcTeam.typecode){
                    frc.teams.add(frcTeam.decode((byte[]) object.get()));
                }else if(object.getType() == frcMatch.typecode){
                    frc.matches.add(frcMatch.decode((byte[]) object.get()));
                }
            }

            return frc;

        }catch (BuiltByteParser.byteParsingExeption e){
            e.printStackTrace();
            return null;
        }
    }
    @NonNull
    public String toString(){
        return "frcEvent Name: " + name + ", Code: " + eventCode + " numTeams: " + teams.size() + " numMatches: " + matches.size();
    }
}
