package com.astatin3.scoutingapp2025.ui.transfer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.RequestTask;
import com.astatin3.scoutingapp2025.databinding.FragmentTransferBinding;

import java.util.function.Function;

public class TransferFragment extends Fragment {
    private FragmentTransferBinding binding;


    private void alert(String title, String content) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(content);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = FragmentTransferBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.selectLayout.setVisibility(View.GONE);
                binding.generatorLayout.setVisibility(View.VISIBLE);
                binding.generatorLayout.start(binding, "Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmenjAWHRJGQWEhugQWHKJEtarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmenjAWHRJGQWEhugQWHKJEtarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmenjAWHRJGQWEhugQWHKJEtarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmenjAWHRJGQWEhugQWHKJEtarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmenjAWHRJGQWEhugQWHKJEtarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism,Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmenjAWHRJGQWEhugQWHKJEtarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmentarianismDisestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmenjAWHRJGQWEhugQWHKJEtarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmentarianismDisestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmenjAWHRJGQWEhugQWHKJEtarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmentarianismDisestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmenjAWHRJGQWEhugQWHKJEtarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmentarianismDisestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmenjAWHRJGQWEhugQWHKJEtarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmentarianismDisestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmenjAWHRJGQWEhugQWHKJEtarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism");
            }
        });

        binding.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.selectLayout.setVisibility(View.GONE);
                binding.scannerLayout.setVisibility(View.VISIBLE);
                binding.scannerLayout.start(binding);
            }
        });
        binding.TBAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Warning");
                alert.setMessage("This action requires internet.");
                alert.setCancelable(true);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        binding.selectLayout.setVisibility(View.GONE);
                        binding.TBAView.setVisibility(View.VISIBLE);
                        binding.TBAView.start(binding, "2024");
                    }
                });

                alert.setNegativeButton("Cancel", null);
                alert.create().show();
            }
        });

        return root;
    }
}