package com.ridgebotics.ridgescout.ui.data;

import static com.ridgebotics.ridgescout.SettingsVersionStack.latestSettings.settings;
import static com.ridgebotics.ridgescout.types.input.inputType.dropdownType;
import static com.ridgebotics.ridgescout.utility.DataManager.evcode;
import static com.ridgebotics.ridgescout.utility.DataManager.event;
import static com.ridgebotics.ridgescout.utility.DataManager.match_latest_values;
import static com.ridgebotics.ridgescout.utility.DataManager.pit_latest_values;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ridgebotics.ridgescout.databinding.FragmentDataReportBinding;
import com.ridgebotics.ridgescout.scoutingData.ScoutingDataWriter;
import com.ridgebotics.ridgescout.types.data.dataType;
import com.ridgebotics.ridgescout.types.frcMatch;
import com.ridgebotics.ridgescout.types.input.dropdownType;
import com.ridgebotics.ridgescout.types.input.inputType;
import com.ridgebotics.ridgescout.types.input.sliderType;
import com.ridgebotics.ridgescout.utility.DataManager;
import com.ridgebotics.ridgescout.utility.fileEditor;
import com.ridgebotics.ridgescout.utility.ollama.OllamaTest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ReportFragment extends Fragment {
    FragmentDataReportBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentDataReportBinding.inflate(inflater, container, false);

        DataManager.reload_event();
        DataManager.reload_pit_fields();
        DataManager.reload_match_fields();

        getReportMatches();
        AIDataOverview();

        return binding.getRoot();
    }

    public frcMatch[] getTeamMatches(int teamNum){
        List<frcMatch> teamMatches = new ArrayList<>();
        for(int i = 0; i < event.matches.size(); i++){
            frcMatch match = event.matches.get(i);
            boolean isTeamMatch = false;
            isTeamMatch = IntStream.of(match.redAlliance).anyMatch(x -> x == teamNum);
            isTeamMatch = isTeamMatch || IntStream.of(match.blueAlliance).anyMatch(x -> x == teamNum);
            if(isTeamMatch)
                teamMatches.add(match);
        }
        return teamMatches.toArray(new frcMatch[0]);
    }

    private int getMostRecentTeamMatch(int teamNum, int curMatch){
        frcMatch[] teamMatches = getTeamMatches(teamNum);
        int maxMatch = - 1;

        for(int i = 0; i < teamMatches.length; i++) {
            if (teamMatches[i].matchIndex < curMatch &&
                    teamMatches[i].matchIndex > maxMatch) {
                maxMatch = teamMatches[i].matchIndex;
            }

        }

        if(maxMatch == -1)
            return curMatch;
        else
            return maxMatch;
    }

    public void getReportMatches(){
//        String out = "";
        int ourTeamNum = settings.get_team_num();
        frcMatch[] teamMatches = getTeamMatches(ourTeamNum);

        TableRow tr = new TableRow(getContext());

        TextView tv = new TextView(getContext());
        tv.setText("Team match");
        tr.addView(tv);

        tv = new TextView(getContext());
        tv.setText("Most informed match");
        tr.addView(tv);

        binding.teamMatchesTable.addView(tr);
        binding.teamMatchesTable.setStretchAllColumns(true);

        for(int i = 0; i < teamMatches.length; i++){
            tr = new TableRow(getContext());

            tv = new TextView(getContext());
            tv.setText(String.valueOf(teamMatches[i].matchIndex));
            tr.addView(tv);

            int maxMatch = -1;
            for(int a = 0; a < 6; a++){
                int teamNum = 0;
                if(a < 3)
                    teamNum = teamMatches[i].redAlliance[a];
                else
                    teamNum = teamMatches[i].blueAlliance[a-3];

                if(teamNum == ourTeamNum)
                    continue;

                int matchNum = getMostRecentTeamMatch(teamNum, teamMatches[i].matchIndex);
                if(maxMatch < matchNum)
                    maxMatch = matchNum;
            }


            tv = new TextView(getContext());
            tv.setText(String.valueOf(maxMatch));
            tr.addView(tv);

            binding.teamMatchesTable.addView(tr);
        }
//        AlertManager.error(out);
    }

    private String fieldSummary(inputType field){
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

    private void AIDataOverview(){
        String prompt = "Below is a list of data collected from an FRC match. Generate a qualitative and concise summary of the teams listed in the data collected.\n\n";

        frcMatch curmatch = event.matches.get(0);

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

            frcMatch[] matchNums = getTeamMatches(teamNum);

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

        System.out.println(prompt);
//        binding.AyEyeBox.setText(prompt);


        OllamaTest.run(prompt, new OllamaTest.ollamaListener() {
            @Override
            public void onResponse(String response) {
//                System.out.println(response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.AyEyeBox.setText(binding.AyEyeBox.getText()+response);
                    }
                });
            }

            @Override
            public void onComplete() {
                System.out.println(binding.AyEyeBox.getText());
            }
        });
    }
}
