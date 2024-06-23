package com.astatin3.scoutingapp2025.SettingsVersionStack;

public class v0 extends settingsVersion {
    public int getVersion() {
        return 0;
    }
    public void update(){
        set_file_version(getVersion());
    }

    @Override
    public void defaultSettings() {
        forceWriteTag("test1", "value1");
        forceWriteTag("test2", "value2");
        forceWriteTag("test3", "value3");
    }
}
