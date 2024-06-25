package com.astatin3.scoutingapp2025.MatchScoutingVersionStack;

import com.astatin3.scoutingapp2025.BuiltByteParser;

import java.util.ArrayList;

public class msv1 extends msv0 {
    public static int version_num = 1;
    @Override
    public int getVersion(){return version_num;}
    public dataType[] types = {
            new stringData("name", "Unset-Username"),
            new intData("team", 0),
            new intData("scoreArea", -1),
            new intData("robotCondition", -1),
            new intData("autoPerformance", -1),
            new intData("teleopPerformance", -1),
            new intData("overallPerformance", -1),
            new intData("endState", -1),
            new intData("autoNotes", -1),
            new intData("teleopNotes", -1),
            new stringData("notes", "<No notes>")
    };
    @Override
    public dataType[] getTypes(){
        return types;
    }

    public static msv1 update(msv0 prev) {
        msv1 post = new msv1();
        post.types = prev.types;

        return post;
    }

    public static msv1 parse(int version, ArrayList<BuiltByteParser.parsedObject> parsedObjects) {
        if(version < version_num){
            msv0 msdata = msv0.parse(version, parsedObjects);
            return update(msdata);
        }

        msv1 msdata = new msv1();
        dataType[] types = msdata.getTypes();

        for(int i = 1; i < types.length; i++){
            types[i].set(parsedObjects.get(i));
        }

        return msdata;
    }
}
