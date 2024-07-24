package com.astatin3.scoutingapp2025.ui.transfer.bluetooth;

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
import android.widget.Toast;

import com.astatin3.scoutingapp2025.R;
import com.astatin3.scoutingapp2025.utility.fileEditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
        init(context);
    }

    public BluetoothSenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void set_data(byte[] data){
        data_to_send = data;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_bluetooth_sender, this, true);

        bluetoothSender = new BluetoothSender(context);
        deviceListView = findViewById(R.id.deviceListView);
        sendFileButton = findViewById(R.id.sendFileButton);

        deviceList = new ArrayList<>();
        deviceArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1);
        deviceListView.setAdapter(deviceArrayAdapter);

        if (!bluetoothSender.isBluetoothSupported()) {
            Toast.makeText(context, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothSender.isBluetoothEnabled()) {
            Toast.makeText(context, "Please enable Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            listPairedDevices();
        }

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice selectedDevice = deviceList.get(position);
                try {
                    bluetoothSender.connectToDevice(selectedDevice);
                    Toast.makeText(context, "Connected to " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
                    sendFileButton.setEnabled(true);
                } catch (IOException e) {
                    Toast.makeText(context, "Failed to connect: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        sendFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // You would typically launch a file picker here
                // For this example, we'll just send a dummy file
                sendDummyFile();
            }
        });
    }

    private void listPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothSender.getPairedDevices();
        if (pairedDevices != null && !pairedDevices.isEmpty()) {
            deviceList.addAll(pairedDevices);
            for (BluetoothDevice device : pairedDevices) {
                deviceArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            Toast.makeText(getContext(), "No paired devices found", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendDummyFile() {
        try {
            for(int i = 0; i < Math.floor((double) data_to_send.length /1024); i++){
                bluetoothSender.sendData(fileEditor.getByteBlock(data_to_send, (i*1024), (i+1)*1024));
//                System.out.println(Arrays.toString(buffer));
            }


            Toast.makeText(getContext(), "File sent successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to send file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onDestroy() {
        try {
            bluetoothSender.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}