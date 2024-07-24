package com.astatin3.scoutingapp2025.types;

import com.astatin3.scoutingapp2025.utility.AlertManager;
import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.ByteBuilder;
import com.astatin3.scoutingapp2025.utility.fileEditor;

import java.util.ArrayList;
import java.util.Objects;

public class file {
    public static final int typecode = 255;
    public String filename;
    public byte[] data;


    public file(){}

    public file(String filename){
        this(filename, fileEditor.readFile(filename));
    }

    public file(String filename, byte[] data){
        this.filename = filename;
        this.data = data;
    }

    public byte[] encode(){
        try {
            ByteBuilder bb = new ByteBuilder()
                    .addString(filename)
                    .addRaw(255, Objects.requireNonNull(fileEditor.readFile(filename)));

            return bb.build();

        } catch (ByteBuilder.buildingException e) {
            AlertManager.error(e);
            return null;
        }
    }

    public static file decode(byte[] bytes){
        try{
            ArrayList<BuiltByteParser.parsedObject> objects = new BuiltByteParser(bytes).parse();

            file f = new file();

            f.filename = (String) objects.get(0).get();
            f.data = (byte[]) objects.get(1).get();

            return f;

        }catch (BuiltByteParser.byteParsingExeption e){
            AlertManager.error(e);
            return null;
        }
    }

    public boolean write(){
        if(data == null || filename == null) return false;
        return fileEditor.writeFile(filename, data);
    }
}
