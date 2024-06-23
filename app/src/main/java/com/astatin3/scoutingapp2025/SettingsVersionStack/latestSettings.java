package com.astatin3.scoutingapp2025.SettingsVersionStack;

public class latestSettings {
    public static v2 settings = new v2();
    public latestSettings(){
        settings.init_settings();
        settings.update();
    }
}
