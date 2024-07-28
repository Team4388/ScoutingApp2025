package com.astatin3.scoutingapp2025.ui.transfer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.databinding.FragmentTransferSelectorBinding;

public class TransferSelectorFragment extends Fragment {

    // Declaring three blank funcs in one line lol
    private static onSelect onselect  = new onSelect() {@Override public void onSelectCodes(TransferSelectorFragment self) {}@Override public void onSelectBluetooth(TransferSelectorFragment self) {} @Override public void onSelectWifi(TransferSelectorFragment self) {}};

    public static void setOnSelect(onSelect tmp) {
        onselect = tmp;
    }

    public interface onSelect {
        void onSelectCodes(TransferSelectorFragment self);
        void onSelectBluetooth(TransferSelectorFragment self);
        void onSelectWifi(TransferSelectorFragment self);
    }

    FragmentTransferSelectorBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTransferSelectorBinding.inflate(inflater, container, false);

        binding.codesButton.setOnClickListener(view -> {
            onselect.onSelectCodes(this);
        });

        binding.bluetoothButton.setOnClickListener(view -> {
            onselect.onSelectBluetooth(this);
        });

        binding.wifiButton.setOnClickListener(view -> {
            onselect.onSelectWifi(this);
        });

        return binding.getRoot();
    }
}
