package com.astatin3.scoutingapp2025.ui.data;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.scoutingData.ScoutingDataWriter;
import com.astatin3.scoutingapp2025.scoutingData.fields;
import com.astatin3.scoutingapp2025.scoutingData.transfer.transferType;
import com.astatin3.scoutingapp2025.types.data.dataType;
import com.astatin3.scoutingapp2025.types.frcEvent;
import com.astatin3.scoutingapp2025.types.frcTeam;
import com.astatin3.scoutingapp2025.types.input.inputType;
import com.astatin3.scoutingapp2025.utility.AlertManager;
import com.astatin3.scoutingapp2025.utility.fileEditor;
import com.google.android.material.divider.MaterialDivider;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.skydoves.powerspinner.SpinnerGravity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class teamsView extends ConstraintLayout {
    public teamsView(@NonNull Context context) {
        super(context);
    }
    public teamsView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    com.astatin3.scoutingapp2025.databinding.FragmentDataBinding binding;

    private static final int background_color = 0x5000ff00;
    private static final int unsaved_background_color = 0x2000ff00;


    String evcode;
    frcEvent event;

    inputType[][] match_values;
    inputType[] latest_match_values;
    transferType[][] match_transferValues;
    inputType[][] pit_values;
    inputType[] latest_pit_values;
    transferType[][] pit_transferValues;

    public void init(com.astatin3.scoutingapp2025.databinding.FragmentDataBinding binding, frcEvent event){
        this.binding = binding;
        this.evcode = event.eventCode;
        this.event = event;


        match_values = fields.load(fields.matchFieldsFilename);
        latest_match_values = match_values[match_values.length-1];
        match_transferValues = transferType.get_transfer_values(match_values);
        pit_values = fields.load(fields.pitsFieldsFilename);
        latest_pit_values = pit_values[pit_values.length-1];
        pit_transferValues = transferType.get_transfer_values(pit_values);


        binding.teamsArea.removeAllViews();

        TableLayout table = new TableLayout(getContext());
        table.setStretchAllColumns(true);
        binding.teamsArea.addView(table);

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

            tr.setBackgroundColor(background_color);

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
                loadTeam(finalTeam, latestSettings.settings.get_data_view_mode());
            });
        }
    }

    public void loadTeam(frcTeam team, int mode) {
        binding.teamsArea.removeAllViews();

        LinearLayout ll = new LinearLayout(getContext());
        ll.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        ll.setOrientation(LinearLayout.VERTICAL);
        binding.teamsArea.addView(ll);



        PowerSpinnerView dropdown = new PowerSpinnerView(getContext());

        List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();

        iconSpinnerItems.add(new IconSpinnerItem("Individual"));
        iconSpinnerItems.add(new IconSpinnerItem("Compiled"));
        iconSpinnerItems.add(new IconSpinnerItem("History"));

        IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(dropdown);
        dropdown.setSpinnerAdapter(iconSpinnerAdapter);
        dropdown.setItems(iconSpinnerItems);

        dropdown.selectItemByIndex(0);

        dropdown.setPadding(15,15,15,15);
        dropdown.setBackgroundColor(0xf0000000);
        dropdown.setTextColor(0xff00ff00);
        dropdown.setTextSize(15);
        dropdown.setArrowGravity(SpinnerGravity.END);
        dropdown.setArrowPadding(8);
        dropdown.setSpinnerItemHeight(46);
        dropdown.setSpinnerPopupElevation(14);


        dropdown.selectItemByIndex(mode);


        dropdown.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<IconSpinnerItem>() {
            @Override
            public void onItemSelected(int oldIndex, @Nullable IconSpinnerItem oldItem, int newIndex,
                                       IconSpinnerItem newItem) {

                latestSettings.settings.set_data_view_mode(newIndex);
                loadTeam(team, newIndex);
            }
        });

        ll.addView(dropdown);







        TextView tv = new TextView(getContext());
        tv.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setText(String.valueOf(team.teamNumber));
        tv.setTextSize(28);
        ll.addView(tv);

        tv = new TextView(getContext());
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

        add_pit_data(ll, team);
        add_match_data(ll, team, mode);
    }

    public void add_pit_data(LinearLayout ll, frcTeam team){
        final String filename = evcode+"-"+team.teamNumber+".pitscoutdata";

        ll.addView(new MaterialDivider(getContext()));

        TextView tv = new TextView(getContext());
        tv.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setPadding(0,10,0,10);
        tv.setText("----- Pit data -----");
        tv.setTextSize(30);
        ll.addView(tv);

        ll.addView(new MaterialDivider(getContext()));

        if(!fileEditor.fileExist(filename)){
            tv = new TextView(getContext());
            tv.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText("No pit data has been collected!");
            tv.setTextSize(23);
            ll.addView(tv);
            return;
        }

        ScoutingDataWriter.ParsedScoutingDataResult psda = ScoutingDataWriter.load(filename, pit_values, pit_transferValues);

        tv = new TextView(getContext());
        tv.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tv.setPadding(0, 20, 0, 5);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setText("Pit scouting by " + psda.username);
        tv.setTextSize(30);
        ll.addView(tv);

        for (int a = 0; a < psda.data.array.length; a++) {
            tv = new TextView(getContext());
            tv.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText(psda.data.array[a].name);
            tv.setTextSize(25);

            if(psda.data.array[a].isNull()){
                tv.setBackgroundColor(0xffff0000);
                tv.setTextColor(0xff000000);
            }


            ll.addView(tv);


            latest_pit_values[a].add_individual_view(ll, psda.data.array[a]);
        }
    }




    public void add_match_data(LinearLayout ll, frcTeam team, int mode){
        String[] files = fileEditor.getMatchesByTeamNum(evcode, team.teamNumber);

        ll.addView(new MaterialDivider(getContext()));

        TextView tv = new TextView(getContext());
        tv.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setText("----- Match data -----");
        tv.setPadding(0,10,0,10);
        tv.setTextSize(30);
        ll.addView(tv);

        ll.addView(new MaterialDivider(getContext()));



        if(files.length == 0){
            tv = new TextView(getContext());
            tv.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText("No match data has been collected!");
            tv.setTextSize(23);
            ll.addView(tv);
            return;
        }

        switch (mode){
            case 0:
                add_individual_views(ll,files);
                break;
            case 1:
                add_compiled_views(ll,files);
                break;
            case 2:
                add_history_views(ll,files);
                break;
        }
    }




    public void add_individual_views(LinearLayout ll, String[] files) {
        for (int i = 0; i < files.length; i++) {
            String[] split = files[i].split("-");
            int match_num = Integer.parseInt(split[1]);

            ScoutingDataWriter.ParsedScoutingDataResult psda = ScoutingDataWriter.load(files[i], match_values, match_transferValues);

            TextView tv = new TextView(getContext());
            tv.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            tv.setPadding(0, 40, 0, 5);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText("M" + (match_num) + " " + split[2] + "-" + split[3] + " by " + psda.username);
            tv.setTextSize(30);
            ll.addView(tv);

            for (int a = 0; a < psda.data.array.length; a++) {
                tv = new TextView(getContext());
                tv.setLayoutParams(new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setText(psda.data.array[a].name);
                tv.setTextSize(25);

                if (psda.data.array[a].isNull()) {
                    tv.setBackgroundColor(0xffff0000);
                    tv.setTextColor(0xff000000);
                }

                ll.addView(tv);


                latest_match_values[a].add_individual_view(ll, psda.data.array[a]);
            }
        }
    }






    public void add_compiled_views(LinearLayout ll, String[] files){
        dataType[][] data = new dataType[latest_match_values.length][files.length];
        for (int i = 0; i < files.length; i++) {

            ScoutingDataWriter.ParsedScoutingDataResult psda = ScoutingDataWriter.load(files[i], match_values, match_transferValues);
            for (int a = 0; a < data.length; a++) {
                data[a][i] = psda.data.array[a];
            }
        }

        for(int i = 0; i < latest_match_values.length; i++){
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            tv.setPadding(0, 20, 0, 5);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText(latest_match_values[i].name);
            tv.setTextSize(30);
            ll.addView(tv);

            latest_match_values[i].add_compiled_view(ll, data[i]);
        }
    }





    public void add_history_views(LinearLayout ll, String[] files){
        dataType[][] data = new dataType[latest_match_values.length][files.length];
        for (int i = 0; i < files.length; i++) {

            ScoutingDataWriter.ParsedScoutingDataResult psda = ScoutingDataWriter.load(files[i], match_values, match_transferValues);
            for (int a = 0; a < data.length; a++) {
                data[a][i] = psda.data.array[a];
            }
        }

        for(int i = 0; i < latest_match_values.length; i++){
            TextView tv = new TextView(getContext());
            tv.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            tv.setPadding(0, 20, 0, 5);
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
            tv.setText(latest_match_values[i].name);
            tv.setTextSize(30);
            ll.addView(tv);

            latest_match_values[i].add_history_view(ll, data[i]);
        }
    }
}
