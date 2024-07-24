package com.astatin3.scoutingapp2025.ui.transfer.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.astatin3.scoutingapp2025.R;
import com.astatin3.scoutingapp2025.utility.AlertManager;
import com.astatin3.scoutingapp2025.utility.fileEditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class BluetoothSenderView extends LinearLayout {
    private BluetoothSender bluetoothSender;
    private ListView deviceListView;
    private Button sendFileButton;
    private ArrayAdapter<String> deviceArrayAdapter;
    private ArrayList<BluetoothDevice> deviceList;
    private byte[] data_to_send = new byte[0];

    public BluetoothSenderView(Context context) {
        super(context);
    }

    public BluetoothSenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void set_data(byte[] data){
        data_to_send = data;
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_bluetooth_sender, this, true);

        bluetoothSender = new BluetoothSender(getContext());
        deviceListView = findViewById(R.id.deviceListView);
        sendFileButton = findViewById(R.id.sendFileButton);

        deviceList = new ArrayList<>();
        deviceArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        deviceListView.setAdapter(deviceArrayAdapter);

        if (!bluetoothSender.isBluetoothSupported()) {
            AlertManager.toast("Bluetooth is not supported on this device");
            return;
        }

        if (!bluetoothSender.isBluetoothEnabled()) {
            AlertManager.toast("Please enable Bluetooth");
        } else {
            listPairedDevices();
        }

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice selectedDevice = deviceList.get(position);
                try {
                    bluetoothSender.connectToDevice(selectedDevice);
                    AlertManager.toast("Connected to " + selectedDevice.getName());
                    sendFileButton.setEnabled(true);
                } catch (IOException e) {
                    AlertManager.toast("Failed to connect: " + e.getMessage());
                }
            }
        });

        sendFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void listPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothSender.getPairedDevices();
        if (pairedDevices != null && !pairedDevices.isEmpty()) {
            deviceList.addAll(pairedDevices);
            for (BluetoothDevice device : pairedDevices) {
                deviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            AlertManager.toast("No paired devices found");
        }
    }

    private void sendData() {
        try {
            byte[] compressed = fileEditor.blockCompress(data_to_send);

            for(int i = 0; i < Math.ceil((double) compressed.length/1024); i++){
                bluetoothSender.sendData(fileEditor.getByteBlock(compressed, i*1024, (i+1)*1024));
            }

            bluetoothSender.close();
            sendFileButton.setEnabled(false);



            AlertManager.toast("File sent successfully");
        } catch (IOException e) {
            AlertManager.toast("Failed to send file: " + e.getMessage());
        }
    }

    public void onDestroy() {
        if(bluetoothSender != null)
            try {
                bluetoothSender.close();
            } catch (IOException e) {
                AlertManager.error(e);
            }
    }
}