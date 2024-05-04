z1package com.astatin3.scoutingapp2025.ui.scouting;


import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.databinding.FragmentScoutingBinding;
import com.astatin3.scoutingapp2025.fileEditor;

public class scoutingFragment extends Fragment {

    private FragmentScoutingBinding binding;

    private void setDropdownItems(Spinner dropdown, String[] items){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentScoutingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Spinner dropdown = binding.eventDropdown;

        setDropdownItems(dropdown, fileEditor.getEventList().toArray(new String[0]));

        return root;
    }
}