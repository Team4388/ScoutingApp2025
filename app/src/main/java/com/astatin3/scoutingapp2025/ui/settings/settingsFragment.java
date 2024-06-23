package com.astatin3.scoutingapp2025.ui.settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.databinding.FragmentSettingsBinding;
import com.astatin3.scoutingapp2025.fileEditor;
import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;

import com.skydoves.powerspinner.IconSpinnerAdapter;
import com.skydoves.powerspinner.IconSpinnerItem;
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

        PowerSpinnerView spinnerView = binding.eventDropdown;

        List<IconSpinnerItem> iconSpinnerItems = new ArrayList<>();

        for(String name : fileEditor.getEventList()){
            iconSpinnerItems.add(new IconSpinnerItem(name));
        }

        IconSpinnerAdapter iconSpinnerAdapter = new IconSpinnerAdapter(spinnerView);
        spinnerView.setSpinnerAdapter(iconSpinnerAdapter);
        spinnerView.setItems(iconSpinnerItems);
        spinnerView.setLifecycleOwner(this);

        if(!iconSpinnerItems.isEmpty()){
            spinnerView.selectItemByIndex(0);
        }

        alert("test", latestSettings.settings.readTag("test2"));

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}