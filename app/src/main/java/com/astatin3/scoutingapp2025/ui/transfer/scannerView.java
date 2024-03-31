package com.astatin3.scoutingapp2025.ui.transfer;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.astatin3.scoutingapp2025.databinding.FragmentTransferBinding;
import com.astatin3.scoutingapp2025.qrPointsOverlayView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class scannerView extends ConstraintLayout {
    private QRCodeReaderView qrCodeReaderView;
    private qrPointsOverlayView pointsOverlayView;

    private class codeReadListener implements QRCodeReaderView.OnQRCodeReadListener {
        @Override
        public void onQRCodeRead(String text, PointF[] points) {
            pointsOverlayView.setPoints(points);
        }
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
}
