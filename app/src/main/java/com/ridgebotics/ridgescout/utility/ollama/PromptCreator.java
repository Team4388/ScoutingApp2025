package com.ridgebotics.ridgescout.utility.ollama;

import static com.ridgebotics.ridgescout.utility.DataManager.evcode;
import static com.ridgebotics.ridgescout.utility.DataManager.event;
import static com.ridgebotics.ridgescout.utility.DataManager.match_latest_values;
import static com.ridgebotics.ridgescout.utility.DataManager.pit_latest_values;

import com.ridgebotics.ridgescout.scoutingData.ScoutingDataWriter;
import com.ridgebotics.ridgescout.types.data.dataType;
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
                summary += "A the index of a dropdown with the possible options: [" +String.join(", ", ((dropdownType) field).text_options) + "]";
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
        String prompt = "Below is a list of data collected from an FRC match. Generate a qualitative and concise summary of the teams listed in the data collected.\n\n";

        frcMatch curmatch = event.matches.get(matchIndex);

        prompt += "## Pit scouting\n";
        prompt += "This is a list of the different fields that are present in the pit scouting data:\n";


        prompt += "1) Team number\n";
        for(int i = 0; i < pit_latest_values.length; i++){
            prompt += (i+2) + ") " + fieldSummary(pit_latest_values[i]) + "\n";
        }
        prompt += ("\nData:\n");

        for(int a = 0; a < 6; a++){
            int teamNum = 0;
            if(a < 3)
                teamNum = curmatch.redAlliance[a];
            else
                teamNum = curmatch.blueAlliance[a-3];

            prompt += teamNum + ",";

            String filename = evcode+"-"+teamNum+".pitscoutdata";
            if(!fileEditor.fileExist(filename)){
                prompt += ("null,".repeat(pit_latest_values.length));
            }else{
                ScoutingDataWriter.ParsedScoutingDataResult psdr = ScoutingDataWriter.load(filename, DataManager.pit_values, DataManager.pit_transferValues);
                dataType[] types = psdr.data.array;
                for(int i = 0; i < types.length; i++) {
                    prompt += (types[i].get() + ",");
                }
            }
            prompt += "\n";
        }


        prompt += "\n## Match scouting\n";
        prompt += "This is a list of the different fields that are present in the match scouting data:\n";

        prompt += "1) Match number\n";
        for(int i = 0; i < match_latest_values.length; i++){
            prompt += (i+2) + ") " + fieldSummary(match_latest_values[i]) + "\n";
        }

        prompt += ("\nData:\n");

        for(int a = 0; a < 6; a++){
            int teamNum = 0;
            if(a < 3)
                teamNum = curmatch.redAlliance[a];
            else
                teamNum = curmatch.blueAlliance[a-3];

            prompt += "Team " + teamNum + " Match scout data:\n";

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
                prompt += match.matchIndex + ",";
                for (int i = 0; i < types.length; i++) {
                    prompt += (types[i].get() + ",");
                }
                prompt += "\n";

            }
        }
        return prompt;
    }
}
