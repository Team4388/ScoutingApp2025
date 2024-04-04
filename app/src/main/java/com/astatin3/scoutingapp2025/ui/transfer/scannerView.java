package com.astatin3.scoutingapp2025.ui.transfer;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.astatin3.scoutingapp2025.databinding.FragmentTransferBinding;
import com.astatin3.scoutingapp2025.fileEditor;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.nio.charset.StandardCharsets;
import java.util.zip.DataFormatException;

public class scannerView extends ConstraintLayout {
    public static class fixedQRCodeReaderView extends QRCodeReaderView {
        public fixedQRCodeReaderView(Context context) {
            super(context, null);
        }
    }

    private QRCodeReaderView qrCodeReaderView;
    private qrOverlayView qrOverlayView;
    private String[] qrDataArr;

    private int randID;

    private class codeReadListener implements QRCodeReaderView.OnQRCodeReadListener {
        @Override
        public void onQRCodeRead(String text, PointF[] points) {
            qrOverlayView.setPoints(points);

            compileData(
                fileEditor.byteFromChar(text.charAt(0)),
                fileEditor.byteFromChar(text.charAt(1)),
                fileEditor.byteFromChar(text.charAt(2)),
                (fileEditor.byteFromChar(text.charAt(3))+1),
                text.substring(4)
            );
        }
    }

    private void alert(String title, String content) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(content);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    public scannerView(Context context) {
        super(context);
    }

    public scannerView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public void start(FragmentTransferBinding binding){
        qrCodeReaderView = new QRCodeReaderView(getContext());
        this.addView(qrCodeReaderView);
        ConstraintLayout.LayoutParams qrCodeReaderViewParams = (ConstraintLayout.LayoutParams) qrCodeReaderView.getLayoutParams();
        qrCodeReaderViewParams.width = ActionBar.LayoutParams.MATCH_PARENT;
        qrCodeReaderViewParams.height = ActionBar.LayoutParams.MATCH_PARENT;
        qrCodeReaderView.setLayoutParams(qrCodeReaderViewParams);

        qrOverlayView = new qrOverlayView(getContext());
        qrOverlayView.bringToFront();
        this.addView(qrOverlayView);
        ConstraintLayout.LayoutParams pointsOverlayViewParams = (ConstraintLayout.LayoutParams) qrCodeReaderView.getLayoutParams();
        pointsOverlayViewParams.width = ActionBar.LayoutParams.MATCH_PARENT;
        pointsOverlayViewParams.height = ActionBar.LayoutParams.MATCH_PARENT;
        qrOverlayView.setLayoutParams(pointsOverlayViewParams);


        qrCodeReaderView.startCamera();

//        qrCodeReaderView = (QRCodeReaderView) binding.qrdecoderview;
        qrCodeReaderView.setOnQRCodeReadListener(new codeReadListener());
//        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setAutofocusInterval(2000L);
//        qrCodeReaderView.setFrontCamera();
        qrCodeReaderView.setBackCamera();
        qrCodeReaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrCodeReaderView.forceAutoFocus();
            }
        });
    }

    private void compileData(int dataVersion, int randID, int qrIndex, int qrCount, String qrData){
        if(dataVersion != fileEditor.internalDataVersion){
            alert("Error", "Incorrect data version");
            return;
        }

        // Reset code array if ID Changes
        if(randID != this.randID){
            this.randID = randID;
            qrDataArr = new String[qrCount];
        }

        final boolean updated;

        if(qrDataArr[qrIndex] == null) {
            qrDataArr[qrIndex] = qrData;
            updated = true;
        }else{
            updated = false;
        }

        int count = 0;
        int[] barColors = new int[qrCount];

        for(int i =0;i<qrCount;i++){
            if(qrDataArr[i] != null){
                barColors[i] = 2;
                count++;
            }

            if(i == qrIndex){
                barColors[i] = 1;
            }else if(qrDataArr[i] == null) {
                barColors[i] = 0;
            }
        }

        qrOverlayView.setBar(barColors);

        if(updated && count >= qrCount){

            // I guess String.join does not like non-ascii text
            String compiledData = "";
            for(int i=0;i<qrCount;i++){
                compiledData += qrDataArr[i];
            }


            try {
                byte[] compiledBytes = compiledData.getBytes(StandardCharsets.ISO_8859_1);
//                alert("completed", new String(fileEditor.decompress(compiledBytes), StandardCharsets.ISO_8859_1));
                alert("completed", blockUncompress(compiledBytes));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static String blockUncompress(byte[] data) throws DataFormatException {
        String uncompressedData = "";
        int curIndex = 0;
        while(curIndex < data.length){
            final int blockLength = fileEditor.fromBytes(fileEditor.getByteBlock(data, curIndex, curIndex+2), 2);

            Log.i("test", ""+blockLength);

            uncompressedData += new String(
                    fileEditor.decompress(
                            fileEditor.getByteBlock(
                                    data, curIndex+2, curIndex+blockLength+2)
                    ), StandardCharsets.ISO_8859_1
            );

            curIndex += blockLength+2;
        }
        return uncompressedData;
    }
}
