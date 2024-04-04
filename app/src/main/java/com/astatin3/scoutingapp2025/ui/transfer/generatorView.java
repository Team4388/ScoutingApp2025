package com.astatin3.scoutingapp2025.ui.transfer;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.astatin3.scoutingapp2025.R;
import com.astatin3.scoutingapp2025.databinding.FragmentTransferBinding;
import com.astatin3.scoutingapp2025.fileEditor;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class generatorView extends ConstraintLayout {
    private FragmentTransferBinding binding;
    private ImageView qrImage;
    private SeekBar qrSpeedSlider;
    private SeekBar qrSizeSlider;
    private TextView qrIndexN;
    private TextView qrIndexD;

    private final int maxQrCount = 256; //The max number that can be stored in a byte

    private final int maxQrSpeed = 20;
    private final int minQrSpeed = 300 + maxQrSpeed - 1;

    private int minQrSize = 0;
    private final int maxQrSize = 600;
    private int qrSize = 200;


    private int curCodeIndex = 0;
    private final int defaultQrDelay = 419;
    private int qrDelay = 0;
    private int qrIndex = 0;

    private CountDownTimer timer;
    private int qrCount = 0;

    private ArrayList<Bitmap> qrBitmaps;

    public generatorView(Context context) {
        super(context);
    }

    public generatorView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    public static Bitmap generateQrCode(String myCodeText) throws WriterException {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // H = 30% damage
//        hints.put(EncodeHintType.QR_COMPACT, true);
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

    public void start(FragmentTransferBinding binding, String inputData){
        qrImage = binding.qrImage;
        qrSpeedSlider = binding.qrSpeedSlider;
        qrSizeSlider = binding.qrSizeSlider;
        qrIndexN = binding.qrIndexN;
        qrIndexD = binding.qrIndexD;

        String compiledData = null;
//        try {
        byte[] tempData = fileEditor.compress(inputData.getBytes(StandardCharsets.ISO_8859_1));
        compiledData = new String(tempData, StandardCharsets.ISO_8859_1);
//            alert(""+tempData.length, fileEditor.binaryVisualize(tempData));
//            Log.i("Info", fileEditor.binaryVisualize(tempData));

//        }catch (UnsupportedEncodingException e){
//            e.printStackTrace();
//        }

        alert(""+compiledData.length(), compiledData);

        if(compiledData == null || inputData.length() < compiledData.length()){
            sendData(inputData);
        }else{
            sendData(compiledData);
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

    private void sendData(String data){

        minQrSize = Math.round(data.length()/maxQrCount);

        qrSizeSlider.setMax(maxQrSize-minQrSize);
        qrSpeedSlider.setMax((minQrSpeed-maxQrSpeed)*2);

        qrCount = (data.length()/qrSize)+1;
        qrIndexD.setText(String.valueOf(qrCount));

//        alert("size", ""+binding.qrSizeSlider.getProgress()+"\n"+binding.qrSizeSlider.getMax());

        qrSpeedSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                qrDelay = -(minQrSpeed - progress - maxQrSpeed + 1);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        qrSizeSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                qrSize = seekBar.getProgress() + minQrSpeed;
                qrCount = (data.length()/qrSize)+1;
                qrIndexD.setText(String.valueOf(qrCount));
                sendData(data);
            }
        });

        qrSpeedSlider.setProgress(defaultQrDelay+5);

        qrBitmaps = new ArrayList<Bitmap>();
        for(int i=0;i<=((data.length()+1)/qrSize);i++){
            final int start = i*qrSize;
            int end = (i+1)*qrSize;
            if(end >= data.length()){
                end = data.length();
            }
            try {
//                alert("test", ""+Math.ceil((double)data.length()/(double)qrSize));
                qrBitmaps.add(generateQrCode(
                        String.valueOf(fileEditor.toChar(fileEditor.internalDataVersion)) +
                                String.valueOf(fileEditor.toChar(i)) +
                                String.valueOf(fileEditor.toChar(qrCount-1)) +
                                data.substring(start, end)
                ));
            }catch (WriterException e){
                e.printStackTrace();
            }
        }
        qrIndex = 0;
        if(timer != null){
            timer.cancel();
        }
        qrLoop();
    }

    private void updateQr(){
        qrImage.setImageBitmap(qrBitmaps.get(qrIndex));
        if(qrDelay > 0) {
            this.qrIndex += 1;
            if (this.qrIndex >= this.qrCount) {
                this.qrIndex = 0;
            }
        }else{
            this.qrIndex -= 1;
            if (this.qrIndex < 0) {
                this.qrIndex = this.qrCount-1;
            }
        }

        qrIndexN.setText(String.valueOf(qrIndex+1));
    }

    private void qrLoop(){
        timer = new CountDownTimer(minQrSpeed-Math.abs(qrDelay)+1, 1000) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                updateQr();
                qrLoop();
            }
        }.start();
    }
}
