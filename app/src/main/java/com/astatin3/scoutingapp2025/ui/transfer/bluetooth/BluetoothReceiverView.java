package com.astatin3.scoutingapp2025.ui.transfer.bluetooth;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astatin3.scoutingapp2025.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class BluetoothReceiverView extends LinearLayout {
    private BluetoothReceiver bluetoothReceiver;
    private Button startListeningButton;
    private Button stopListeningButton;
    private TextView statusTextView;

    public BluetoothReceiverView(Context context) {
        super(context);
        init(context);
    }

    public BluetoothReceiverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_bluetooth_receiver, this, true);

//        bluetoothReceiver = new BluetoothReceiver(context);

        bluetoothReceiver = new BluetoothReceiver(context, new BluetoothReceiver.receivedData() {
            @Override
            public void processReceivedData(byte[] data, int bytes) {
                receiveFile(data,bytes);
            }
        });

        startListeningButton = findViewById(R.id.startListeningButton);
        stopListeningButton = findViewById(R.id.stopListeningButton);
        statusTextView = findViewById(R.id.statusTextView);

        if (!bluetoothReceiver.isBluetoothSupported()) {
            Toast.makeText(context, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothReceiver.isBluetoothEnabled()) {
            Toast.makeText(context, "Please enable Bluetooth", Toast.LENGTH_SHORT).show();
        }

        startListeningButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startListening();
            }
        });

        stopListeningButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopListening();
            }
        });
    }

    private void startListening() {
        try {
            bluetoothReceiver.startListening();
            statusTextView.setText("Listening for incoming connections...");
            startListeningButton.setEnabled(false);
            stopListeningButton.setEnabled(true);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to start listening: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopListening() {
        try {
            bluetoothReceiver.stopListening();
            statusTextView.setText("Not listening");
            startListeningButton.setEnabled(true);
            stopListeningButton.setEnabled(false);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Failed to stop listening: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void receiveFile(byte[] data, int bytes) {
        System.out.println(Arrays.toString(data) + ", " + bytes);
    }

    public void onDestroy() {
        try {
            bluetoothReceiver.stopListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}