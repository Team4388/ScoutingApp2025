package com.astatin3.scoutingapp2025.SettingsVersionStack;

import com.astatin3.scoutingapp2025.utility.AlertManager;
import com.astatin3.scoutingapp2025.utility.fileEditor;

import java.nio.charset.StandardCharsets;

public abstract class settingsVersion {
    private static final String settingsFilename = "settings.txt";
    public abstract void defaultSettings();
    public abstract int getVersion();
    public abstract void update();

    public static String get_settings_file_content(){
        byte[] data = fileEditor.readFile(settingsFilename);
        if(data == null){return "";}
        return new String(data, StandardCharsets.UTF_8);
    }

    public int get_file_version(){
        String[] fileContent = get_settings_file_content().split("\n");
        try{
            return Integer.parseInt(fileContent[0]);
        }catch(Exception e){
            return -1;
        }
    }

    public void set_file_version(int version){
        String[] fileContent = get_settings_file_content().split("\n");
        String output = String.valueOf(version);
        for(int i = 0; i < fileContent.length; i++){
            output += ("\n" + fileContent[i]);
        }
        fileEditor.writeFile(settingsFilename, output.getBytes(StandardCharsets.UTF_8));
    }

    public String readTag(String search_tag){
        String[] fileContent = get_settings_file_content().split("\n");

        try{
            for(String line : fileContent){
                if(line.isEmpty()){
                    continue;
                }
                String[] split = line.split("=");
                if(split[0].equals(search_tag)){
                    if(split[1].equals("<empty>")){
                        return "";
                    }else if(split[1].equals("<null>")){
                        return null;
                    }else {
                        return split[1];
                    }
                }
            }
        }catch (Exception e){
            AlertManager.error(e);
        }

        return null;
    }

    public void init_settings(){
        if(!fileEditor.fileExist(settingsFilename)){
            fileEditor.createFile(settingsFilename);

            set_file_version(getVersion());
            defaultSettings();

        }
    }

    public String writeTag(String tag_name, String data){
        final boolean already_exists = readTag(tag_name) != null;

        if(data == null){
            data = "<null>";
        }else if(data.equals("")){
            data = "<empty>";
        }

        if(!already_exists){
            String fileContent = get_settings_file_content();
            String output = fileContent + "\n" + tag_name + "=" + data;
            fileEditor.writeFile(settingsFilename, output.getBytes(StandardCharsets.UTF_8));
            return output;
        }else{
            String[] fileContent = get_settings_file_content().split("\n");
            try{
                for(int i = 0; i < fileContent.length; i++){
                    if(fileContent[i].isEmpty()){
                        continue;
                    }
                    String[] split = fileContent[i].split("=");
                    if(split[0].equals(tag_name)){
                        fileContent[i] = tag_name + "=" + data;
                        String output = String.join("\n", fileContent);
                        fileEditor.writeFile(settingsFilename, output.getBytes(StandardCharsets.UTF_8));
                        return output;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return "No idea how this happened";
    }
}
