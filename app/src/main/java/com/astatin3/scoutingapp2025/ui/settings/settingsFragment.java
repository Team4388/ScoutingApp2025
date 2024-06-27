package com.astatin3.scoutingapp2025.ui.settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.databinding.FragmentSettingsBinding;
import com.astatin3.scoutingapp2025.fileEditor;
import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;

import com.google.android.material.textfield.TextInputEditText;
import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.List;


public class settingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private android.widget.ScrollView ScrollArea;
    private android.widget.TableLayout Table;

    private void alert(String title, String content) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(content);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    private void setDropdownItems(Spinner dropdown, String[] items){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        EditText username = binding.username;
        username.setText(latestSettings.settings.get_username());
        username.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                latestSettings.settings.set_username(username.getText().toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });




        PowerSpinnerView spinnerView = binding.eventDropdown;

        List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();

        String target_event_name = latestSettings.settings.get_evcode();
        int target_index = -1;

        ArrayList<String> evlist = fileEditor.getEventList();
        for(int i = 0; i < evlist.size(); i++){
            if(evlist.get(i).equals(target_event_name)){
                target_index = i;
            }
            iconSpinnerItems.add(new IconSpinnerItem(evlist.get(i)));
        }

        IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(spinnerView);
        spinnerView.setSpinnerAdapter(iconSpinnerAdapter);
        spinnerView.setItems(iconSpinnerItems);
        spinnerView.setLifecycleOwner(this);

        if(!iconSpinnerItems.isEmpty() && target_index != -1){
            spinnerView.selectItemByIndex(target_index);
        }

        spinnerView.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<IconSpinnerItem>() {
            @Override
            public void onItemSelected(int oldIndex, @Nullable IconSpinnerItem oldItem, int newIndex,
                                       IconSpinnerItem newItem) {
                latestSettings.settings.set_evcode(newItem.getText().toString());
            }
        });










        PowerSpinnerView alliance_pos_spinnerView = binding.alliancePosDropdown;

        List<IconSpinnerItem> alliance_pos_iconSpinnerItems = new ArrayList<>();

        String target_alliance_pos = latestSettings.settings.get_alliance_pos();
        int alliance_pos_target_index = -1;

        String[] alliance_pos_list = new String[]{"red-1", "red-2", "red-3",
                                                  "blue-1", "blue-2", "blue-3"};

        for(int i = 0; i < alliance_pos_list.length; i++){
            if(alliance_pos_list[i].equals(target_alliance_pos)){
                alliance_pos_target_index = i;
            }
            alliance_pos_iconSpinnerItems.add(new IconSpinnerItem(alliance_pos_list[i]));
        }

        IconSpinnerAdapter alliance_pos_iconSpinnerAdapter = new IconSpinnerAdapter(alliance_pos_spinnerView);
        alliance_pos_spinnerView.setSpinnerAdapter(alliance_pos_iconSpinnerAdapter);
        alliance_pos_spinnerView.setItems(alliance_pos_iconSpinnerItems);
        alliance_pos_spinnerView.setLifecycleOwner(this);

        if(alliance_pos_target_index != -1){
            alliance_pos_spinnerView.selectItemByIndex(alliance_pos_target_index);
        }

        alliance_pos_spinnerView.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<IconSpinnerItem>() {
            @Override
            public void onItemSelected(int oldIndex, @Nullable IconSpinnerItem oldItem, int newIndex,
                                       IconSpinnerItem newItem) {
                latestSettings.settings.set_alliance_pos(newItem.getText().toString());
            }
        });









//
//        CheckBox practice_mode = binding.practiceMode;
//        practice_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                latestSettings.settings.set_practice_mode(isChecked);
//            }
//
//        });
//
//        practice_mode.setChecked(latestSettings.settings.get_practice_mode());





        CheckBox wifi_mode = binding.wifiMode;
        wifi_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                latestSettings.settings.set_wifi_mode(isChecked);
            }

        });

        wifi_mode.setChecked(latestSettings.settings.get_wifi_mode());






        Button reset_button = binding.resetButton;
        reset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Warning");
                alert.setMessage("Do you really want to reset settings?");
                alert.setCancelable(true);

                alert.setPositiveButton("Ok", (dialog, which) -> {
                    latestSettings.settings.defaultSettings();
                    username.setText(latestSettings.settings.get_username());
                    spinnerView.clearSelectedItem();
//                    practice_mode.setChecked(latestSettings.settings.get_practice_mode());
                    wifi_mode.setChecked(latestSettings.settings.get_wifi_mode());
                    alliance_pos_spinnerView.selectItemByIndex(0);
                });

                alert.setNegativeButton("Cancel", null);
                alert.create().show();
            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}