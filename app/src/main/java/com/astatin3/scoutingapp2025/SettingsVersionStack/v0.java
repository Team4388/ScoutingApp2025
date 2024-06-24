package com.astatin3.scoutingapp2025.SettingsVersionStack;

public class v0 extends settingsVersion {
    @Override
    public int getVersion() {
        return 0;
    }
    @Override
    public void update(){
//        int file_version = get_file_version();
//        if(file_version == getVersion()) {
//            return;
//        }else if(file_version < getVersion()){
//            super.update();
//        }
//        set_file_version(getVersion());
    }

    @Override
    public void defaultSettings() {
        writeTag("username", "Username");
        writeTag("selected_event_code", "unset");
//        writeTag("practice_mode", "false");
        writeTag("wifi_mode", "false");
    }

    public void set_username(String name){
        writeTag("username", name);
    }
    public String get_username(){
        return readTag("username");
    }
    public void set_evcode(String evcode){
        writeTag("selected_event_code", evcode);
    }
    public String get_evcode(){
        return readTag("selected_event_code");
    }
//    public void set_practice_mode(boolean value) {
//        writeTag("practice_mode", value ? "true" : "false");
//    }
//    public boolean get_practice_mode(){
//        return readTag("practice_mode").equals("true");
//    }
    public void set_wifi_mode(boolean value){
        writeTag("wifi_mode", value ? "true" : "false");
    }
    public boolean get_wifi_mode(){
        return readTag("wifi_mode").equals("true");
    }
}
