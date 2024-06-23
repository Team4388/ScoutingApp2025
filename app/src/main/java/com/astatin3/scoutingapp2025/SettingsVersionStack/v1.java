package com.astatin3.scoutingapp2025.SettingsVersionStack;

public class v1 extends v0 {
    @Override
    public int getVersion() {
        return 1;
    }
    @Override
    public void update(){
        if(get_file_version() < getVersion()){super.update();}
        set_file_version(getVersion());
//        writeTag("test1", "value_v1_1");
        writeTag("test2", "value_v1_2");
        writeTag("test3", "value_v1_3");
    }

    @Override
    public void defaultSettings() {
        forceWriteTag("test1", "value1");
        forceWriteTag("test2", "value2");
        forceWriteTag("test3", "value3");
    }
}
