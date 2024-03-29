package com.astatin3.scoutingapp2025.ui.home;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.astatin3.scoutingapp2025.databinding.FragmentHomeBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private final int maxQrCount = 256; //The max number that can be stored in a byte

    private final int maxQrSpeed = 20;
    private final int minQrSpeed = 300;

    private int minQrSize = 0;
    private final int maxQrSize = 600;
    private int qrSize = 100;


    private int curCodeIndex = 0;
    private int qrDelay = 100;
    private int qrIndex = 0;
    private int qrCount = 0;

    private ArrayList<Bitmap> qrBitmaps = new ArrayList<Bitmap>();

    private void alert(String title, String content) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(content);
        alert.setTitle(title);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(true);
        alert.create().show();
    }

    public static Bitmap generateQrCode(String myCodeText) throws WriterException {
//        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // H = 30% damage
        hints.put(EncodeHintType.MARGIN, 0); /* default = 4 */

        int size = 200;

        BitMatrix bitMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size, size, hints);

        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();

        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
               pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.qrSpeedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                qrDelay = progress + minQrSpeed;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        binding.qrSpeedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                qrDelay = maxQrSpeed - progress + 1;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sendData("Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, DisestablishmenjAWHRJGQWEhugQWHKJEtarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism, Disestablishmentarianism");

        return root;
    }

    private void getQr(int index){
        byte[] bytes = ByteBuffer.allocate(1).putInt(index).array();
//        new byte;
    }

    private void sendData(String data){
        minQrSize = Math.round(data.length()/maxQrCount);

        binding.qrSizeSlider.setMax(maxQrSize);
        binding.qrSpeedSlider.setMax(maxQrSpeed-minQrSpeed);

        binding.qrSizeSlider.setProgress(qrSize-minQrSize);
        binding.qrSpeedSlider.setProgress(qrDelay-minQrSpeed);

        qrCount = (data.length()/qrSize);

//        alert("ee", ""+ qrDelay + "\n" + minQrSpeed);

        for(int i=0;i<=(data.length()/qrSize);i++){
            final int start = i*qrSize;
            int end = (i+1)*qrSize;
            if(end > data.length()){
                end = data.length()-1;
            }
//            alertstr += "\n" + start + ", " + end;
            try {
                qrBitmaps.add(generateQrCode(
                    data.substring(start, end)
                ));
            }catch (WriterException e){
                e.printStackTrace();
            }
        }
        qrIndex = 0;
        qrLoop();
    }

    private void updateQr(){
//        alert("qr", ""+qrIndex);
        binding.qrImage.setImageBitmap(qrBitmaps.get(qrIndex));
        this.qrIndex += 1;
        if(this.qrIndex >= this.qrCount+1){
            this.qrIndex = 0;
        }
    }

    private void qrLoop(){
        new CountDownTimer(qrDelay, 1000) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                updateQr();
                qrLoop();
            }
        }.start();


    }

//    private void setQR(int curIndex){
//        binding.qrImage.setImageBitmap(qrBitmaps.get(curIndex));
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(qrIndex+1 > qrCount){
//                    qrIndex = 0;
//                }
//                setQR(curIndex+1);
//            }
//        }, qrDelay);
//
//
//    }
}