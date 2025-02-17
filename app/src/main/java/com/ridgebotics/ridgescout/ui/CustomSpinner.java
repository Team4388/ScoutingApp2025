package com.ridgebotics.ridgescout.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.ridgebotics.ridgescout.R;

import java.util.ArrayList;
import java.util.List;

public class CustomSpinner extends DialogFragment {
    private List<String> options;
    private OnOptionSelectedListener listener;
    private RecyclerView recyclerView;
    private CustomSpinnerOptionsAdapter adapter;

    public interface OnOptionSelectedListener {
        void onOptionSelected(String option);
    }

    public static CustomSpinner newInstance(ArrayList<String> options) {
        CustomSpinner dialog = new CustomSpinner();
        Bundle args = new Bundle();
        args.putStringArrayList("options", options);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
        if (getArguments() != null) {
            options = getArguments().getStringArrayList("options");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_custom_spinner, container, false);

        // Setup toolbar
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("Select an Option");

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CustomSpinnerOptionsAdapter(options, option -> {
            if (listener != null) {
                listener.onOptionSelected(option);
            }
            dismiss();
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void setOnOptionSelectedListener(OnOptionSelectedListener listener) {
        this.listener = listener;
    }



}

