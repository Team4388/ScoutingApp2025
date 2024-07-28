package com.astatin3.scoutingapp2025.ui.data;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.R;
import com.astatin3.scoutingapp2025.databinding.FragmentDataFieldsChooserBinding;
import com.astatin3.scoutingapp2025.scoutingData.fields;

public class FieldsChooserFragment  extends Fragment {

    FragmentDataFieldsChooserBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentDataFieldsChooserBinding.inflate(inflater, container, false);

        binding.matchScoutingButton.setOnClickListener(v -> {
            FieldsFragment.set_filename(fields.matchFieldsFilename);
            findNavController(this).navigate(R.id.action_navigation_data_fields_chooser_to_navigation_data_fields);
        });

        binding.pitScoutingButton.setOnClickListener(v -> {
            FieldsFragment.set_filename(fields.pitsFieldsFilename);
            findNavController(this).navigate(R.id.action_navigation_data_fields_chooser_to_navigation_data_fields);
        });

        return binding.getRoot();
    }
}
