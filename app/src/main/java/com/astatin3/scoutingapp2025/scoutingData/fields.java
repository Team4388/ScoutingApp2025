package com.astatin3.scoutingapp2025.scoutingData;

import com.astatin3.scoutingapp2025.fileEditor;
import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.ByteBuilder;

import java.util.ArrayList;

public class fields {
    public static ScoutingVersion sv = new ScoutingVersion();
    public static final String fieldsFilename = "data.fields";

//    public static ScoutingVersion.inputType[][] values = new ScoutingVersion.inputType[][]{};

    public static ScoutingVersion.inputType[][] values = new ScoutingVersion.inputType[][] {
        {
            sv.new notesType("name", "Unset-Username"),
            sv.new sliderType("How good is robot", 5, 0, 10)
        }, {
            sv.new notesType("name", "Unset-Username"),
            sv.new sliderType("How good is robot", 5, 0, 10),
            sv.new notesType("notes", "No-Notes")
        }, {
            sv.new notesType("name", "Unset-Username"),
            sv.new notesType("notes", "No-Notes")
        }, {
            sv.new notesType("name", "Unset-Username")
        }, {
            sv.new notesType("name", "Unset-Username"),
            sv.new sliderType("How good is robot", 5, 0, 10)
        }
    };

    public static boolean save(){
        try {
            ByteBuilder bb = new ByteBuilder();
            for (int i = 0; i < values.length; i++) {
                bb.addRaw(127, save_version(values[i]));
            }
            fileEditor.writeFile(fieldsFilename, bb.build());
            return true;
        }catch (ByteBuilder.buildingException e) {
            e.printStackTrace();
            return false;
//            throw new RuntimeException(e);
        }
    }

    private static byte[] save_version(ScoutingVersion.inputType[] values) throws ByteBuilder.buildingException {
        ByteBuilder bb = new ByteBuilder();
        for(int i =0; i < values.length; i++){
            bb.addRaw(values[i].get_byte_id(), values[i].encode());
        }
        return bb.build();
    }

    public static boolean load(){
        byte[] bytes = fileEditor.readFile(fieldsFilename);

        System.out.println(bytes);

        try {
            BuiltByteParser bbp = new BuiltByteParser(bytes);
            ArrayList<BuiltByteParser.parsedObject> objects = bbp.parse();
            values = new ScoutingVersion.inputType[objects.size()][];

            for(int i = 0 ; i < objects.size(); i++){
                values[i] = load_version((byte[]) objects.get(i).get());
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static ScoutingVersion.inputType[] load_version(byte[] bytes) throws BuiltByteParser.byteParsingExeption{
        BuiltByteParser bbp = new BuiltByteParser(bytes);
        ArrayList<BuiltByteParser.parsedObject> objects = bbp.parse();
        ScoutingVersion.inputType[] output = new ScoutingVersion.inputType[objects.size()];

        for(int i = 0 ; i < objects.size(); i++){
            BuiltByteParser.parsedObject obj = objects.get(i);
            ScoutingVersion.inputType t = null;
            switch (obj.getType()){
                case ScoutingVersion.slider_type_id:
                    t = sv.new sliderType();
                    break;
                case ScoutingVersion.dropdownType:
                    t = sv.new dropdownType();
                    break;
                case ScoutingVersion.notesType:
                    t = sv.new notesType();
                    break;
            }

            t.decode((byte[]) obj.get());
            output[i] = t
            ;
        }

        return output;
    }

    public static void test(){
        ScoutingVersion.transferType[][] transferValues = sv.get_transfer_values(values);

        ScoutingVersion.ScoutingArray msa = sv.new ScoutingArray(0, new ScoutingVersion.dataType[]{
                sv.new stringType("name", "test-username"),
                sv.new intType("How good is robot", 12)
        }, values, transferValues);

        msa.update();

        for(ScoutingVersion.dataType dt : msa.array){
            if(dt == null) continue;
            switch (dt.getValueType()){
                case NUM:
                    System.out.println(dt.name + " " + (int) dt.get());
                    break;
                case STRING:
                    System.out.println(dt.name + " " + (String) dt.get());
                    break;
            }

        }
    }
}
