package com.ridgebotics.ridgescout.SettingsVersionStack;

public class latestSettings {
    public static sv1 settings = new sv1();
    public static void update(){
        settings.init_settings();
        settings.update();
    }
}
