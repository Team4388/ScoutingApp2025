package com.astatin3.scoutingapp2025.ui.data;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.astatin3.scoutingapp2025.databinding.FragmentDataBinding;
import com.astatin3.scoutingapp2025.scoutingData.ScoutingDataWriter;
import com.astatin3.scoutingapp2025.scoutingData.ScoutingVersion;
import com.astatin3.scoutingapp2025.scoutingData.fields;
import com.astatin3.scoutingapp2025.types.frcEvent;
import com.astatin3.scoutingapp2025.types.frcTeam;
import com.astatin3.scoutingapp2025.utility.fileEditor;

import java.util.ArrayList;
import java.util.Arrays;

public class searchView extends ConstraintLayout {
    public searchView(@NonNull Context context) {
        super(context);
    }
    public searchView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    FragmentDataBinding binding;
    String evcode;
    frcEvent event;

    ScoutingVersion.inputType[][] match_values;
    ScoutingVersion.transferType[][] match_transferValues;
    ScoutingVersion.inputType[][] pit_values;
    ScoutingVersion.transferType[][] pit_transferValues;

    public void init(FragmentDataBinding binding, frcEvent event){
        this.binding = binding;
        this.evcode = event.eventCode;
        this.event = event;


        match_values = fields.load(fields.matchFieldsFilename);
        match_transferValues = fields.sv.get_transfer_values(match_values);
        pit_values = fields.load(fields.pitsFieldsFilename);
        pit_transferValues = fields.sv.get_transfer_values(pit_values);


        binding.searchArea.removeAllViews();

        TableLayout table = new TableLayout(getContext());
        table.setStretchAllColumns(true);
        binding.searchArea.addView(table);

//        binding.searchTable.addView(table);

        int[] teams = new int[event.teams.size()];

        for(int i = 0 ; i < event.teams.size(); i++){
            teams[i] = event.teams.get(i).teamNumber;
        }

        Arrays.sort(teams);

        for(int i = 0; i < event.teams.size(); i++){
            frcTeam team = null;
            for(int a = 0 ; a < event.teams.size(); a++){
                if(event.teams.get(a).teamNumber == teams[i]){
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

            tr.setBackgroundColor(0x1000ff00);

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
                loadTeam(finalTeam);
            });
        }
    }

    public void loadTeam(frcTeam team){
        binding.searchArea.removeAllViews();

        LinearLayout ll = new LinearLayout(getContext());
        ll.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        ll.setOrientation(LinearLayout.VERTICAL);
        binding.searchArea.addView(ll);

        TextView tv = new TextView(getContext());
        tv.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setText(team.teamName);
        tv.setTextSize(28);
        ll.addView(tv);

        tv = new TextView(getContext());
        tv.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setText(team.getDescription());
        tv.setTextSize(16);
        ll.addView(tv);

        String[] files = fileEditor.getMatchesByTeamNum(team.teamNumber);

        for(int i = 0; i < files.length; i++){
            String[] split = files[i].split("-");
            int match_num = Integer.parseInt(split[1]);

            ScoutingDataWriter.ParsedScoutingDataResult psda = ScoutingDataWriter.load(files[i], match_values, match_transferValues);

            tv = new TextView(getContext());
            tv.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            tv.setPadding(0,20,0,5);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText("Match " + (match_num) + " by " + psda.username);
            tv.setTextSize(30);
            ll.addView(tv);

            for(int a = 0 ; a < psda.data.array.length; a++){
                tv = new TextView(getContext());
                tv.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setText(psda.data.array[a].name);
                tv.setTextSize(25);
                ll.addView(tv);


                match_values[match_values.length-1][a].add_individual_view(ll, psda.data.array[a]);
            }
        }

        System.out.println(ll.getChildCount());

//        ScoutingDataWriter.ParsedScoutingDataResult[] scoutingData;
    }

    public void display_uncompiled_data(){

    }
}
