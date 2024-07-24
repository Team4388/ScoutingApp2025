package com.astatin3.scoutingapp2025.scoutingData;

import com.astatin3.scoutingapp2025.scoutingData.transfer.transferType;
import com.astatin3.scoutingapp2025.types.ScoutingArray;
import com.astatin3.scoutingapp2025.types.data.dataType;
import com.astatin3.scoutingapp2025.types.data.stringType;
import com.astatin3.scoutingapp2025.types.input.inputType;
import com.astatin3.scoutingapp2025.types.data.intType;
import com.astatin3.scoutingapp2025.utility.AlertManager;
import com.astatin3.scoutingapp2025.utility.fileEditor;
import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.ByteBuilder;

import java.util.ArrayList;

public class ScoutingDataWriter {
//    private static final int int_type_id = 255;
//    private static final int string_type_id = 254;

    public static boolean save(int version, String username, String filename, dataType[] data){
        ByteBuilder bb = new ByteBuilder();
        try {
            bb.addInt(version);
            bb.addString(username);
            for(int i = 0; i < data.length; i++){
                switch (data[i].getValueType()){
                    case NUM:
                        bb.addInt((int) data[i].get());
                        break;
                    case STRING:
                        bb.addString((String) data[i].get());
                        break;
                }
            }
            byte[] bytes = bb.build();
            fileEditor.writeFile(filename, bytes);
            return true;
        } catch (ByteBuilder.buildingException e) {
            AlertManager.error(e);
            return false;
        }
    }

    public static class ParsedScoutingDataResult {
        public String filename;
        public String username;
        public int version;
        public ScoutingArray data;
    }

    public static ParsedScoutingDataResult load(String filename, inputType[][] values , transferType[][] transferValues){
        byte[] bytes = fileEditor.readFile(filename);
        BuiltByteParser bbp = new BuiltByteParser(bytes);
        try {
            ArrayList<BuiltByteParser.parsedObject> objects = bbp.parse();
            dataType[] dataTypes = new dataType[objects.size()-2];

            int version = ((int)objects.get(0).get());
//            System.out.println(version);
            String username = (String) objects.get(1).get();

            for(int i = 0; i < values[version].length; i++){
                switch (objects.get(i+2).getType()){
                    case 1:
                        dataTypes[i] = new intType(values[version][i].name, (int) objects.get(i+2).get());
                        break;
                    case 2:
                        String name = values[version][i].name;
                        String value = (String) objects.get(i+2).get();
                        dataTypes[i] = new stringType(name, value);
                        break;
                }
            }

            ScoutingArray msa = new ScoutingArray(version, dataTypes, values, transferValues);
            msa.update();

            ParsedScoutingDataResult psda = new ParsedScoutingDataResult();

            psda.filename = filename;
            psda.username = username;
            psda.version = version;
            psda.data = msa;

            return psda;

        } catch (BuiltByteParser.byteParsingExeption e){
            AlertManager.error(e);
            return null;
        }
    }

}
