package com.astatin3.scoutingapp2025.SettingsVersionStack;

public class v2 extends v1 {
    @Override
    public int getVersion() {
        return 2;
    }
    @Override
    public void update(){
        if(get_file_version() < getVersion()){super.update();}
        set_file_version(getVersion());
//        writeTag("test1", "value_v2_1");
//        writeTag("test2", "value_v2_2");
        writeTag("test3", "value_v2 _3");
    }
    @Override
    public void defaultSettings() {
        forceWriteTag("test1", "value1");
        forceWriteTag("test2", "value2");
        forceWriteTag("test3", "value3");
    }
}
