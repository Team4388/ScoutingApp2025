package com.ridgebotics.ridgescout.utility.ollama;

import static com.ridgebotics.ridgescout.utility.DataManager.evcode;
import static com.ridgebotics.ridgescout.utility.DataManager.event;
import static com.ridgebotics.ridgescout.utility.DataManager.match_latest_values;
import static com.ridgebotics.ridgescout.utility.DataManager.pit_latest_values;

import com.ridgebotics.ridgescout.scoutingData.ScoutingDataWriter;
import com.ridgebotics.ridgescout.types.data.dataType;
import com.ridgebotics.ridgescout.types.data.intType;
import com.ridgebotics.ridgescout.types.data.stringType;
import com.ridgebotics.ridgescout.types.frcMatch;
import com.ridgebotics.ridgescout.types.input.dropdownType;
import com.ridgebotics.ridgescout.types.input.inputType;
import com.ridgebotics.ridgescout.types.input.sliderType;
import com.ridgebotics.ridgescout.utility.DataManager;
import com.ridgebotics.ridgescout.utility.fileEditor;

public class PromptCreator {
    private static String fieldSummary(inputType field){
        String summary = field.name + ": ";
        switch (field.getInputType()){
            case DROPDOWN:
                summary += "The index of a dropdown with the possible options: [" +String.join(", ", ((dropdownType) field).text_options) + "]";
                break;
            case SLIDER:
                sliderType slider = (sliderType) field;
                summary += "A slider with the range ["+slider.min+","+slider.max+"]";
                break;
            case TALLY:
                summary += "A tally counter";
                break;
            case NOTES_INPUT:
                summary += "Raw text input";
                break;
        }
        return summary;
    }

    public static String genMatchPrompt(int matchIndex){
        String prompt = "Below is a list of data collected from an FRC match. Generate a qualitative and concise summary of the teams listed, using both numerical and textual data collected in the summary. Additionally, rank the teams in order of their performance.\n\n";

        frcMatch curmatch = event.matches.get(matchIndex);

        prompt += "## Pit scouting\n";        prompt += "This is a list of the different fields that are present in the pit scouting data:\n";

        for(int i = 0; i < pit_latest_values.length; i++){
            prompt += (i+1) + ") " + fieldSummary(pit_latest_values[i]) + "\n";
        }
        prompt += ("\nData:\n");

        for(int a = 0; a < 6; a++){
            int teamNum = 0;
            if(a < 3)
                teamNum = curmatch.redAlliance[a];
            else
                teamNum = curmatch.blueAlliance[a-3];

            prompt += "\nTeam " + teamNum + " pit scout data:\n";

            String filename = evcode+"-"+teamNum+".pitscoutdata";

            if (!fileEditor.fileExist(filename)) continue;

            ScoutingDataWriter.ParsedScoutingDataResult psdr = ScoutingDataWriter.load(filename, DataManager.pit_values, DataManager.pit_transferValues);
            dataType[] types = psdr.data.array;
            for(int i = 0; i < types.length; i++) {
                boolean isNull = true;
                switch (types[i].getValueType()){
                    case NUM:
                        isNull = intType.isNull((int) types[i].get());
                        break;
                    case STRING:
                        isNull = stringType.isNull((String) types[i].get());
                        break;
                }
                if(isNull){
                    prompt += match_latest_values[i].name + ": null\n";
                }else{
                    prompt += match_latest_values[i].name + ": " + types[i].get() + "\n";
                }
            }
        }

        prompt += "\n## Match scouting\n";
        prompt += "This is a list of the different fields that are present in the match scouting data:\n";

        for(int i = 0; i < match_latest_values.length; i++){
            prompt += (i+1) + ") " + fieldSummary(match_latest_values[i]) + "\n";
        }

        prompt += ("\nData:\n");

        for(int a = 0; a < 6; a++){
            int teamNum = 0;
            if(a < 3)
                teamNum = curmatch.redAlliance[a];
            else
                teamNum = curmatch.blueAlliance[a-3];

            prompt += "\nTeam " + teamNum + " Match scout data for match " + curmatch.matchIndex +":\n";

            frcMatch[] matchNums = event.getTeamMatches(teamNum);

            for(int b = 0; b < matchNums.length; b++) {
                frcMatch match = matchNums[b];

                String alliance = "";
                int alliancePos = 0;

                for(int c = 0; c < 6; c++) {
                    if(c<3){
                        if(match.redAlliance[c] != teamNum) continue;
                        alliance = "red";
                        alliancePos = c+1;
                        break;
                    }else{
                        if(match.blueAlliance[c-3] != teamNum) continue;
                        alliance = "blue";
                        alliancePos = c-2;
                        break;
                    }
                }

                String filename = evcode + "-" + match.matchIndex + "-" + alliance + "-" + alliancePos + "-" + teamNum + ".matchscoutdata";

                if (!fileEditor.fileExist(filename)) continue;

                ScoutingDataWriter.ParsedScoutingDataResult psdr = ScoutingDataWriter.load(filename, DataManager.match_values, DataManager.match_transferValues);
                dataType[] types = psdr.data.array;
                for (int i = 0; i < types.length; i++) {
                    boolean isNull = true;
                    switch (types[i].getValueType()){
                        case NUM:
                            isNull = intType.isNull((int) types[i].get());
                            break;
                        case STRING:
                            isNull = stringType.isNull((String) types[i].get());
                            break;
                    }
                    if(isNull){
                        prompt += match_latest_values[i].name + ": null\n";
                    }else{
                        prompt += match_latest_values[i].name + ": " + types[i].get() + "\n";
                    }
                }
                prompt += "\n";
            }
        }
        return prompt;
    }
}
