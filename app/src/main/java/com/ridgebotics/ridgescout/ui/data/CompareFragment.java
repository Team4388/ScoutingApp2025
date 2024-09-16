package com.ridgebotics.ridgescout.ui.data;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ridgebotics.ridgescout.databinding.FragmentDataCompareBinding;
import com.ridgebotics.ridgescout.databinding.FragmentDataReportBinding;

public class CompareFragment extends Fragment {
    FragmentDataCompareBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentDataCompareBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
