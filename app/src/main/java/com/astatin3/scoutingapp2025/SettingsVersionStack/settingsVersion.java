package com.astatin3.scoutingapp2025.SettingsVersionStack;

import com.astatin3.scoutingapp2025.fileEditor;

import java.nio.charset.StandardCharsets;

public abstract class settingsVersion {
    private static String settingsFilename = "settings.txt";
    public abstract int getVersion();
    public abstract void update();

    public static String readLine(int line){
        if(!fileEditor.fileExist("settings.txt")){return null;}

        String[] fileContent = new String(fileEditor.readFile("settings.txt"), StandardCharsets.UTF_8).split("\n");

        if(fileContent.length <= line){return null;}

        return fileContent[line];
    }

    public static String w riteLine(int line, String data){
        String[] fileContent;
        if(!fileEditor.fileExist(settingsFilename)){
            fileContent = new String[]{};
        }else{
            fileContent = new String(fileEditor.readFile(settingsFilename), StandardCharsets.UTF_8).split("\n");
        }

        String newFile = "";

        for(int i = 0; i < Math.max(fileContent.length-1, line); i++){

            if(i == line) {
                newFile += data + "\n";

            }else if(i > fileContent.length-1){
                newFile += fileContent[i];

            }

            if(i < Math.max(fileContent.length - 1, line) - 1){
                newFile += "\n";
            }
        }

        return newFile;

//        fileEditor.writeFile(settingsFilename, newFile.getBytes(StandardCharsets.UTF_8));
    }
}
