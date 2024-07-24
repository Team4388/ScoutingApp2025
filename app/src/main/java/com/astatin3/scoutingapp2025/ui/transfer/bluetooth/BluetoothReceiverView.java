package com.astatin3.scoutingapp2025.ui.transfer.bluetooth;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astatin3.scoutingapp2025.MainActivity;
import com.astatin3.scoutingapp2025.R;
import com.astatin3.scoutingapp2025.types.file;
import com.astatin3.scoutingapp2025.utility.AlertManager;
import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.fileEditor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DataFormatException;

public class BluetoothReceiverView extends LinearLayout {
    private BluetoothReceiver bluetoothReceiver;
    private Button startListeningButton;
    private Button stopListeningButton;
    private TextView statusTextView;

    public BluetoothReceiverView(Context context) {
        super(context);
//        init(context);
    }

    public BluetoothReceiverView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init(context);
    }

//    private void alert(String title, String content) {
//        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
//        dialog.setCancelable(true);
//        dialog.setTitle(title);
//        dialog.setMessage(content);
//
//        final AlertDialog alert = dialog.create();
//        alert.show();
//
//    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_bluetooth_receiver, this, true);

//        bluetoothReceiver = new BluetoothReceiver(context);

        bluetoothReceiver = new BluetoothReceiver(getContext(), new BluetoothReceiver.receivedData() {
            @Override
            public void processReceivedData(byte[] data, int bytes) {
                receiveData(data, bytes);
            }

            @Override
            public void onConnectionStop() {
                finished_recieve();
            }
        });

        startListeningButton = findViewById(R.id.startListeningButton);
        stopListeningButton = findViewById(R.id.stopListeningButton);
        statusTextView = findViewById(R.id.statusTextView);

        if (!bluetoothReceiver.isBluetoothSupported()) {
            AlertManager.error("Bluetooth is not supported on this device");
            return;
        }

        if (!bluetoothReceiver.isBluetoothEnabled()) {
            AlertManager.error("Please enable Bluetooth");
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

            recievedBytes = new ArrayList<>();

        } catch (IOException e) {
            AlertManager.error("Failed to start listening: " + e.getMessage());
        }
    }

    private void stopListening() {
        try {
            bluetoothReceiver.stopListening();
            statusTextView.setText("Not listening");
            startListeningButton.setEnabled(true);
            stopListeningButton.setEnabled(false);
        } catch (IOException e) {
            AlertManager.error("Failed to stop listening: " + e.getMessage());
        }
    }

    private List<byte[]> recievedBytes;

    private void receiveData(byte[] data, int bytes) {
        byte[] newBytes = fileEditor.getByteBlock(data, 0, bytes);
        System.out.println("Recieved " + bytes + " Bytes over bluetooth!");
        recievedBytes.add(newBytes);
    }


    private void finished_recieve() {
        String result_filenames = "";
        try {

            byte[] resultBytes = fileEditor.combineByteArrays(recievedBytes);
            resultBytes = fileEditor.blockUncompress(resultBytes);


            BuiltByteParser bbp = new BuiltByteParser(resultBytes);
            ArrayList<BuiltByteParser.parsedObject> result = bbp.parse();

            for (int i = 0; i < result.size(); i++) {
                if (result.get(i).getType() != file.typecode) continue;
                file f = file.decode((byte[]) result.get(i).get());

                if (f != null) {
                    System.out.println(f.filename);
                    if (f.write())
                        result_filenames += f.filename + "\n";
                }
            }

        } catch (DataFormatException e) {
            AlertManager.error(e);
        } catch (BuiltByteParser.byteParsingExeption e) {
            AlertManager.error(e);
        }

        AlertManager.alert("Completed!", result_filenames);
    }


    public void onDestroy() {
        if (bluetoothReceiver != null)
            try {
                bluetoothReceiver.stopListening();
            } catch (IOException e) {
                AlertManager.error(e);
            }
    }
}