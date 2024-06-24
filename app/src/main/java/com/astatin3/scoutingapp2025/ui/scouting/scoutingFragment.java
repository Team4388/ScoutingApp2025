package com.astatin3.scoutingapp2025.ui.scouting;

import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentScoutingBinding;
import com.astatin3.scoutingapp2025.fileEditor;

public class scoutingFragment extends Fragment {

    private FragmentScoutingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentScoutingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if(latestSettings.settings.get_evcode().equals("unset")){
            binding.noEventError.setVisibility(View.VISIBLE);
        }

        return root;
    }
}