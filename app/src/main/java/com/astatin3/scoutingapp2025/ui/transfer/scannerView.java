package com.astatin3.scoutingapp2025.ui.transfer;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.astatin3.scoutingapp2025.databinding.FragmentTransferBinding;
import com.astatin3.scoutingapp2025.fileEditor;
import com.astatin3.scoutingapp2025.qrPointsOverlayView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.DataFormatException;

public class scannerView extends ConstraintLayout {
    public static class fixedQRCodeReaderView extends QRCodeReaderView {
        public fixedQRCodeReaderView(Context context) {
            super(context, null);
        }
    }

    private QRCodeReaderView qrCodeReaderView;
    private qrPointsOverlayView pointsOverlayView;
    private String[] qrDataArr;

    private class codeReadListener implements QRCodeReaderView.OnQRCodeReadListener {
        @Override
        public void onQRCodeRead(String text, PointF[] points) {
            pointsOverlayView.setPoints(points);

            compileData(
                fileEditor.fromChar(text.charAt(0)),
                fileEditor.fromChar(text.charAt(1)),
                (fileEditor.fromChar(text.charAt(2))+1),
                text.substring(3)
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

        pointsOverlayView = new qrPointsOverlayView(getContext());
        pointsOverlayView.bringToFront();
        this.addView(pointsOverlayView);
        ConstraintLayout.LayoutParams pointsOverlayViewParams = (ConstraintLayout.LayoutParams) qrCodeReaderView.getLayoutParams();
        pointsOverlayViewParams.width = ActionBar.LayoutParams.MATCH_PARENT;
        pointsOverlayViewParams.height = ActionBar.LayoutParams.MATCH_PARENT;
        pointsOverlayView.setLayoutParams(pointsOverlayViewParams);


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

    private void compileData(int dataVersion, int qrIndex, int qrCount, String qrData){
        if(dataVersion != fileEditor.internalDataVersion){
            alert("Error", "Incorrect data version");
            return;
        }
        if(qrDataArr == null || qrDataArr.length != qrCount){
            qrDataArr = new String[qrCount];
        }

        if(qrDataArr[qrIndex] == null) {
            qrDataArr[qrIndex] = qrData;
            alert((qrIndex+1)+"/"+qrCount, qrData);
        }

        int count = 0;
        for(int i =0;i<qrCount;i++){
            if(qrDataArr[i] != null){
                count++;
            }
        }

        if(count >= qrCount){

            // I guess String.join does not like non-ascii text
            String compiledData = "";
            for(int i=0;i<qrCount;i++){
                compiledData += qrDataArr[i];
            }


            try {
                byte[] compiledBytes = compiledData.getBytes(StandardCharsets.ISO_8859_1);
                alert("completed", new String(fileEditor.decompress(compiledBytes), StandardCharsets.ISO_8859_1));
//                alert(""+compiledBytes.length, fileEditor.binaryVisualize(compiledBytes));
//                Log.i("Info", fileEditor.binaryVisualize(compiledBytes));
            }catch (Exception e){
//                alert("completed", compiledData);
                e.printStackTrace();
            }
        }
    }
}
