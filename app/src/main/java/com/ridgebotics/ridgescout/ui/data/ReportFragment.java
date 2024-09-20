package com.ridgebotics.ridgescout.ui.data;

import static com.ridgebotics.ridgescout.utility.DataManager.evcode;
import static com.ridgebotics.ridgescout.utility.DataManager.event;
import static com.ridgebotics.ridgescout.utility.DataManager.match_latest_values;
import static com.ridgebotics.ridgescout.utility.DataManager.pit_latest_values;

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
import com.ridgebotics.ridgescout.utility.ollama.OllamaClient;
import com.ridgebotics.ridgescout.utility.ollama.PromptCreator;
import com.ridgebotics.ridgescout.utility.settingsManager;

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
//
//    public frcMatch[] getTeamMatches(int teamNum){
//        List<frcMatch> teamMatches = new ArrayList<>();
//        for(int i = 0; i < event.matches.size(); i++){
//            frcMatch match = event.matches.get(i);
//            boolean isTeamMatch = false;
//            isTeamMatch = IntStream.of(match.redAlliance).anyMatch(x -> x == teamNum);
//            isTeamMatch = isTeamMatch || IntStream.of(match.blueAlliance).anyMatch(x -> x == teamNum);
//            if(isTeamMatch)
//                teamMatches.add(match);
//        }
//        return teamMatches.toArray(new frcMatch[0]);
//    }


    public void getReportMatches(){
//        String out = "";
        int ourTeamNum = settingsManager.getTeamNum();
        frcMatch[] teamMatches = event.getTeamMatches(ourTeamNum);

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

                int matchNum = event.getMostRecentTeamMatch(teamNum, teamMatches[i].matchIndex);
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

    private void AIDataOverview(){
        String prompt = PromptCreator.genMatchPrompt(0);

//        System.out.println(prompt);
        
        OllamaClient.run(prompt, new OllamaClient.ollamaListener() {
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
