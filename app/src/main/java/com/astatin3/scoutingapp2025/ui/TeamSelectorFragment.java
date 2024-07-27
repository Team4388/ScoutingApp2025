package com.astatin3.scoutingapp2025.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentMatchScoutDataEnterBinding;
import com.astatin3.scoutingapp2025.databinding.FragmentTeamSelectorBinding;
import com.astatin3.scoutingapp2025.types.frcEvent;
import com.astatin3.scoutingapp2025.types.frcTeam;
import com.astatin3.scoutingapp2025.utility.AlertManager;
import com.astatin3.scoutingapp2025.utility.fileEditor;

import java.util.Arrays;

public class TeamSelectorFragment extends Fragment {
    private FragmentTeamSelectorBinding binding;

    private String evcode;
    private frcEvent event;
    private static onTeamSelected onSelect = new onTeamSelected() {@Override public void onSelect(TeamSelectorFragment self, frcTeam team) {}};

    public interface onTeamSelected {
        void onSelect(TeamSelectorFragment self, frcTeam team);
    }
    public static void setOnSelect(onTeamSelected tmponSelect){
        onSelect = tmponSelect;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTeamSelectorBinding.inflate(inflater, container, false);

//        event = fileEditor.g
        evcode = latestSettings.settings.get_evcode();

        if(evcode == null || evcode.equals("unset")){
            AlertManager.error("You somehow have not loaded an event!");
            return binding.getRoot();
        }

        load_teams();


        return binding.getRoot();
    }

    public void load_teams(){
//        binding.pitFileIndicator.setVisibility(View.GONE);
//        binding.pitTeamName.setVisibility(View.GONE);
//        binding.pitTeamDescription.setVisibility(View.GONE);
//
//        clear_fields();


        int[] teamNums = new int[event.teams.size()];

        for(int i = 0 ; i < event.teams.size(); i++){
            teamNums[i] = event.teams.get(i).teamNumber;
        }

        Arrays.sort(teamNums);

        TableLayout table = new TableLayout(getContext());
        table.setStretchAllColumns(true);
        binding.teams.addView(table);


        for(int i = 0; i < event.teams.size(); i++){
            frcTeam team = null;
            for(int a = 0 ; a < event.teams.size(); a++){
                if(event.teams.get(a).teamNumber == teamNums[i]){
                    team = event.teams.get(a);
                    break;
                }
            }

            TableRow tr = new TableRow(getContext());
            TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(20,20,20,20);
            tr.setLayoutParams(rowParams);
            tr.setPadding(20,20,20,20);
            table.addView(tr);

            if(fileEditor.fileExist(evcode + "-" + team.teamNumber + ".pitscoutdata")){
                tr.setBackgroundColor(0x3000FF00);
            }else{
                tr.setBackgroundColor(0x30FF0000);
            }

            TextView tv = new TextView(getContext());
            tv.setText(String.valueOf(team.teamNumber));
            tv.setTextSize(20);
            tr.addView(tv);

            tv = new TextView(getContext());
            tv.setText(team.teamName);
            tv.setTextSize(16);
            tr.addView(tv);

            frcTeam finalTeam = team;
            tr.setOnClickListener(v -> {
                onSelect.onSelect(this, finalTeam);
            });
        }
    }

}
