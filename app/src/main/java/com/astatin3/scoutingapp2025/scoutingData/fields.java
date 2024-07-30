package com.astatin3.scoutingapp2025.scoutingData;

import com.astatin3.scoutingapp2025.types.input.dropdownType;
import com.astatin3.scoutingapp2025.types.input.inputType;
import com.astatin3.scoutingapp2025.types.input.tallyType;
import com.astatin3.scoutingapp2025.types.input.textType;
import com.astatin3.scoutingapp2025.types.input.sliderType;
import com.astatin3.scoutingapp2025.types.input.textType;
import com.astatin3.scoutingapp2025.ui.scouting.TallyCounterView;
import com.astatin3.scoutingapp2025.utility.AlertManager;
import com.astatin3.scoutingapp2025.utility.fileEditor;
import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.ByteBuilder;

import java.util.ArrayList;

public class fields {
//    public static ScoutingVersion sv = new ScoutingVersion();

    public static final String matchFieldsFilename = "matches.fields";
    public static final String pitsFieldsFilename = "pits.fields";

    public static final inputType[][] default_match_fields = new inputType[][] {
        {
            new sliderType("How good is robot", 5, 1, 10),
            new textType("notes", "<no-notes>"),
        },{
            new sliderType("How good is robot", 5, 1, 10),
            new sliderType("Test", 128, 64, 256),
            new textType("notes", "<no-notes>"),
        },{
            new sliderType("How good is robot", 5, 5, 10),
            new sliderType("Test", 128, 64, 256),
            new dropdownType("test-dropdown", new String[]{"Test1", "test2", "Three"}, 1),
            new textType("notes", "<no-notes>"),
        },{
            new tallyType("Test Tally", 0),
            new sliderType("Test2", 30, 25, 50),
            new dropdownType("test-dropdown", new String[]{"Test1", "test2", "Three"}, 1),
            new textType("notes", "<no-notes>"),
    }
    };

    public static final inputType[][] default_pit_fields = new inputType[][] {
        {
            new sliderType("How good is robot", 5, 0, 10),
            new textType("notes", "<no-notes>"),
        },{
            new sliderType("How good is robot", 5, 0, 10),
            new sliderType("Test", 1, 0, 10),
            new textType("notes", "<no-notes>"),
        }
    };


    public static boolean save(String filename, inputType[][] values){
        try {
            ByteBuilder bb = new ByteBuilder();
            for (int i = 0; i < values.length; i++) {
                bb.addRaw(127, save_version(values[i]));
            }
            fileEditor.writeFile(filename, bb.build());
            return true;
        }catch (ByteBuilder.buildingException e) {
            AlertManager.error(e);
            return false;
//            throw new RuntimeException(e);
        }
    }

    private static byte[] save_version(inputType[] values) throws ByteBuilder.buildingException {
        ByteBuilder bb = new ByteBuilder();
        for(int i =0; i < values.length; i++){
            bb.addRaw(values[i].get_byte_id(), values[i].encode());
        }
        return bb.build();
    }

    public static inputType[][] load(String filename){
        byte[] bytes = fileEditor.readFile(filename);

//        System.out.println(bytes);

        try {
            BuiltByteParser bbp = new BuiltByteParser(bytes);
            ArrayList<BuiltByteParser.parsedObject> objects = bbp.parse();
            inputType[][] values = new inputType[objects.size()][];

            for(int i = 0 ; i < objects.size(); i++){
                values[i] = load_version((byte[]) objects.get(i).get());
            }


            return values;
//            return true;
        } catch (Exception e) {
            AlertManager.error(e);
            return null;
//            return false;
        }
    }

    private static inputType[] load_version(byte[] bytes) throws BuiltByteParser.byteParsingExeption{
        BuiltByteParser bbp = new BuiltByteParser(bytes);
        ArrayList<BuiltByteParser.parsedObject> objects = bbp.parse();
        inputType[] output = new inputType[objects.size()];

        for(int i = 0 ; i < objects.size(); i++){
            BuiltByteParser.parsedObject obj = objects.get(i);
            inputType t = null;
            switch (obj.getType()){
                case inputType.slider_type_id:
                    t = new sliderType();
                    break;
                case inputType.dropdownType:
                    t = new dropdownType();
                    break;
                case inputType.notesType:
                    t = new textType();
                    break;
                case inputType.tallyType:
                    t = new tallyType();
                    break;
            }

            t.decode((byte[]) obj.get());
            output[i] = t;
        }

        return output;
    }

//    public static void test(){
//        ScoutingVersion.transferType[][] transferValues = sv.get_transfer_values(values);
//
//        ScoutingVersion.ScoutingArray msa = sv.new ScoutingArray(0, new ScoutingVersion.dataType[]{
//                sv.new stringType("name", "test-username"),
//                sv.new intType("How good is robot", 12)
//        }, values, transferValues);
//
//        msa.update();
//
//        for(ScoutingVersion.dataType dt : msa.array){
//            if(dt == null) continue;
//            switch (dt.getValueType()){
//                case NUM:
//                    System.out.println(dt.name + " " + (int) dt.get());
//                    break;
//                case STRING:
//                    System.out.println(dt.name + " " + (String) dt.get());
//                    break;
//            }
//
//        }
//    }
}
