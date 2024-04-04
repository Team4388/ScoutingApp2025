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
                binding.generatorLayout.start(binding, "Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism Antidisestablishmentarianism");
//                binding.generatorLayout.start(binding, "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99");
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