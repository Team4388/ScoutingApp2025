package com.astatin3.scoutingapp2025.SettingsVersionStack;

import java.util.UUID;

public class sv1 extends sv0 {
    @Override
    public int getVersion() {
        return 0;
    }
    @Override
    public void update(){
        int file_version = get_file_version();
        if(file_version == getVersion()) {
            return;
        }else if(file_version < getVersion()){
            super.update();
        }
        set_file_version(getVersion());
    }

    @Override
    public void defaultSettings() {
        writeTag("username", "Username");
        writeTag("selected_event_code", "unset");
//        writeTag("practice_mode", "false");
        writeTag("wifi_mode", "false");
        writeTag("team_num", "4388");

        writeTag("match_num", "0");
        writeTag("alliance_pos", "red-1");
        writeTag("compiled_mode", "false");
        writeTag("bt_uuid", UUID.randomUUID().toString());
    }

    public int get_match_num(){
        return Integer.parseInt(readTag("match_num"));
    }

    public void set_match_num(int num){
        writeTag("match_num", String.valueOf(num));
    }

    @Override
    public void set_evcode(String evcode){
        set_match_num(0);
        writeTag("selected_event_code", evcode);
    }

    public String get_alliance_pos(){
        return readTag("alliance_pos");
    }

    public void set_alliance_pos(String pos){
        writeTag("alliance_pos", pos);
    }

    public int get_team_num(){return Integer.parseInt(readTag("team_num"));}
    public void set_team_num(String str){

        if(str.isEmpty()) {
            set_team_num(0);
            return;
        }

        set_team_num(Integer.parseInt(str));
    }
    public void set_team_num(int num){writeTag("team_num", String.valueOf(num));}

    public boolean get_compiled_mode(){
        return readTag("compiled_mode").equals("true");
    }

    public void set_compiled_mode(boolean state){
        writeTag("compiled_mode", state ? "true" : "false");
    }

    public void setUUID(UUID uuid){
        writeTag("bt_uuid", uuid.toString());
    }

    public UUID getUUID(){
        return UUID.fromString(readTag("bt_uuid"));
    }
}
