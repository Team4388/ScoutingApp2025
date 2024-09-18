package com.ridgebotics.ridgescout.ui.data;

import static com.ridgebotics.ridgescout.SettingsVersionStack.latestSettings.settings;
import static com.ridgebotics.ridgescout.utility.DataManager.event;

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
import com.ridgebotics.ridgescout.types.frcMatch;
import com.ridgebotics.ridgescout.utility.AlertManager;
import com.ridgebotics.ridgescout.utility.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class ReportFragment extends Fragment {
    FragmentDataReportBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentDataReportBinding.inflate(inflater, container, false);

        getReportMatches();

        return binding.getRoot();
    }

    public frcMatch[] getTeamMatches(int teamNum){
        DataManager.reload_event();
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
}
