package com.astatin3.scoutingapp2025.ui.transfer;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.R;
import com.astatin3.scoutingapp2025.SettingsVersionStack.latestSettings;
import com.astatin3.scoutingapp2025.databinding.FragmentTransferBinding;
import com.astatin3.scoutingapp2025.ui.transfer.bluetooth.BluetoothSenderFragment;
import com.astatin3.scoutingapp2025.ui.transfer.codes.CodeGeneratorView;

public class TransferFragment extends Fragment {
    private FragmentTransferBinding binding;

//    private enum TransferTypes {
//        CAMERA,
//        BLUETOOTH,
//        LOCAL_WIFI,
//        SCOUTING_SERVER
//    }

    String evcode;

    private static final int background_color = 0x5000ff00;
    private static final int unselected_background_color = 0x2000ff00;

//    private Bundle b;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

//        b = savedInstanceState;

        binding = FragmentTransferBinding.inflate(inflater, container, false);

        evcode = latestSettings.settings.get_evcode();

        binding.downloadButton.setOnClickListener(v -> {
            start_download();
        });

        binding.TBAButton.setOnClickListener(v -> {
            binding.noEventError.setVisibility(View.GONE);
            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle("Warning");
            alert.setMessage("This action requires internet.");
            alert.setCancelable(true);

            alert.setPositiveButton("Ok", (dialog, which) -> {
                findNavController(this).navigate(R.id.action_navigation_transfer_to_navigation_tba);
            });

            alert.setNegativeButton("Cancel", null);
            alert.create().show();
        });

        if(evcode.equals("unset")){
            binding.noEventError.setVisibility(View.VISIBLE);
            binding.uploadButton.setVisibility(View.GONE);
            binding.CSVButton.setVisibility(View.GONE);
            binding.downloadButton.setVisibility(View.VISIBLE);
            return binding.getRoot();
        }

        binding.uploadButton.setOnClickListener(v -> {
            start_upload();
        });

        binding.CSVButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Chose data");

            builder.setNegativeButton("Pit data", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CSVExport.exportPits(getContext());
                }
            });

            builder.setPositiveButton("Match data", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CSVExport.exportMatches(getContext());
                }
            });

            builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        });

        if(!latestSettings.settings.get_wifi_mode())
            binding.TBAButton.setVisibility(View.GONE);

        return binding.getRoot();
    }



    private void start_upload() {
        FileSelectorFragment.setOnSelect(data -> {
            TransferSelectorFragment.setOnSelect(new TransferSelectorFragment.onSelect() {
                @Override
                public void onSelectCodes(TransferSelectorFragment self) {
                    CodeGeneratorView.setData(data);
                    findNavController(self).navigate(R.id.action_navigation_transfer_selector_to_navigation_code_generator);
                }
                @Override
                public void onSelectBluetooth(TransferSelectorFragment self) {
                    BluetoothSenderFragment.set_data(data);
                    findNavController(self).navigate(R.id.action_navigation_transfer_selector_to_navigation_bluetooth_sender);
                }
                @Override
                public void onSelectFileBundle(TransferSelectorFragment self) {
                    FileBundle.send(data, getContext());
                }
            });
            findNavController(this).navigate(R.id.action_navigation_file_selector_to_navigation_transfer_selector);
        });
        findNavController(this).navigate(R.id.action_navigation_transfer_to_navigation_file_selector);
    }




    private void start_download(){

        TransferSelectorFragment.setOnSelect(new TransferSelectorFragment.onSelect() {
            @Override
            public void onSelectCodes(TransferSelectorFragment self) {
                findNavController(self).navigate(R.id.action_navigation_transfer_selector_to_navigation_code_scanner);
            }

            @Override
            public void onSelectBluetooth(TransferSelectorFragment self) {
                findNavController(self).navigate(R.id.action_navigation_transfer_selector_to_navigation_bluetooth_receiver);
            }

            @Override
            public void onSelectFileBundle(TransferSelectorFragment self) {
                FileBundle.receive(getActivity());
            }
        });
        findNavController(this).navigate(R.id.action_navigation_transfer_to_navigation_transfer_selector);
    }

}