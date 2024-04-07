package com.astatin3.scoutingapp2025.ui.transfer;

import static android.view.Surface.ROTATION_0;
import static androidx.core.math.MathUtils.clamp;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Handler;
import android.os.SystemClock;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.CameraFilters;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.astatin3.scoutingapp2025.YuvConvertor;
import com.astatin3.scoutingapp2025.databinding.FragmentTransferBinding;
import com.astatin3.scoutingapp2025.fileEditor;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.DataFormatException;

//public class scannerView extends androidx.appcompat.widget.AppCompatImageView {
public class scannerView extends ConstraintLayout {
//    public static class fixedQRCodeReaderView extends QRCodeReaderView {
//        public fixedQRCodeReaderView(Context context) {
//            super(context, null);
//        }
//    }

    private QRCodeReaderView qrCodeReaderView;
    private qrOverlayView qrOverlayView;
    private Handler uiHandler;
    private ScriptIntrinsicYuvToRGB script;
    private YuvConvertor yuvConvertor;

//    private class codeReadListener implements QRCodeReaderView.OnQRCodeReadListener {
//        @Override
//        public void onQRCodeRead(String text, PointF[] points) {
//            qrOverlayView.setPoints(points);
//
////            alert("Info", ""+(fileEditor.byteFromChar(text.charAt(3))+1));
//
//            compileData(
//                fileEditor.byteFromChar(text.charAt(0)),
//                fileEditor.byteFromChar(text.charAt(1)),
//                fileEditor.byteFromChar(text.charAt(2)),
//                (fileEditor.byteFromChar(text.charAt(3))+1),
//                text.substring(4)
//            );
//        }
//    }

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

    private float scale = 0;
    private double threshhold = 0.5;
    private FragmentTransferBinding binding;
    private LifecycleOwner lifecycle;

    private void setImage(Bitmap bmp){
        if(scale == 0) {
            scale = ((float) ((View) getParent()).getWidth() / bmp.getWidth()) * ((float) 16 / 9);
            setScaleX(scale);
            setScaleY(scale);
        }
        binding.scannerImage.setImageBitmap(bmp);
        binding.scannerThreshold.bringToFront();
    }

    private Bitmap toGreyscale(Bitmap originalBitmap){
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        Bitmap oneBitBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(oneBitBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(new float[] {
                0.299f, 0.587f, 0.114f, 0, 0,
                0.299f, 0.587f, 0.114f, 0, 0,
                0.299f, 0.587f, 0.114f, 0, 0,
                0,      0,      0,      1, 0
        })));

        canvas.drawBitmap(originalBitmap, 0, 0, paint);

        int[] pixels = new int[width * height];
        oneBitBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] oneBitPixels = new int[width * height];
        int threshold = 128; // Adjust this value to change the threshold

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);
            int average = (red + green + blue) / 3;
            oneBitPixels[i] = (average > threshold) ? Color.WHITE : Color.BLACK;
        }

        oneBitBitmap.setPixels(oneBitPixels, 0, width, 0, 0, width, height);

        return oneBitBitmap;
    }

    public void start(FragmentTransferBinding binding, LifecycleOwner lifecycle){
        this.binding = binding;
        this.lifecycle = lifecycle;
        yuvConvertor = new YuvConvertor(getContext(), 1280, 720);

        uiHandler = new Handler();

//        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(TransferFragment);
//        integrator.setPrompt("Scan a QR code");
//        integrator.setBeepEnabled(true);
//        integrator.setOrientationLocked(true);
//        integrator.setCaptureActivity(CaptureActivity.class);
//        integrator.initiateScan();


//        ScanOptions options = new ScanOptions();
//        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
//        options.setPrompt("Scan a barcode");
//        options.setCameraId(0);  // Use a specific camera of the device
//        options.setBeepEnabled(false);
//        options.setBarcodeImageEnabled(true);
//        barcodeLauncher.launch(options);

//        qrCodeReaderView = new QRCodeReaderView(getContext());
//        this.addView(qrCodeReaderView);
//        ConstraintLayout.LayoutParams qrCodeReaderViewParams = (ConstraintLayout.LayoutParams) qrCodeReaderView.getLayoutParams();
//        qrCodeReaderViewParams.width = ActionBar.LayoutParams.MATCH_PARENT;
//        qrCodeReaderViewParams.height = ActionBar.LayoutParams.MATCH_PARENT;
//        qrCodeReaderView.setLayoutParams(qrCodeReaderViewParams);
//
//        qrOverlayView = new qrOverlayView(getContext());
//        qrOverlayView.bringToFront();
//        this.addView(qrOverlayView);
//        ConstraintLayout.LayoutParams pointsOverlayViewParams = (ConstraintLayout.LayoutParams) qrCodeReaderView.getLayoutParams();
//        pointsOverlayViewParams.width = ActionBar.LayoutParams.MATCH_PARENT;
//        pointsOverlayViewParams.height = ActionBar.LayoutParams.MATCH_PARENT;
//        qrOverlayView.setLayoutParams(pointsOverlayViewParams);
//
//        Map<DecodeHintType, Object> hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
//        hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
//        hints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
////        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
//
//        qrCodeReaderView.setDecodeHints(hints);
//
////        qrCodeReaderView = (QRCodeReaderView) binding.qrdecoderview;
//        qrCodeReaderView.setOnQRCodeReadListener(new codeReadListener());
////        qrCodeReaderView.setQRDecodingEnabled(true);
//        qrCodeReaderView.setAutofocusInterval(2000L);
////        qrCodeReaderView.setFrontCamera();
//        qrCodeReaderView.setBackCamera();
//        qrCodeReaderView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                qrCodeReaderView.forceAutoFocus();
//            }
//        });
//        qrCodeReaderView.startCamera();

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture
                = ProcessCameraProvider.getInstance(this.getContext());

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(this.getContext()));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setTargetRotation(Surface.ROTATION_180)
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                .addCameraFilter(CameraFilters.NON)
                .build();

        ExecutorService executor = Executors.newSingleThreadExecutor();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
//                .setTargetResolution(new Size(224, 224))
                .setOutputImageRotationEnabled(false)
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();


        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
            @OptIn(markerClass = ExperimentalGetImage.class) @Override
            public void analyze(@NonNull ImageProxy image) {
                Image img = Objects.requireNonNull(image.getImage());
                Log.i("test", img.getWidth() + ", " + img.getHeight());
                final Bitmap bmp = yuvConvertor.toBitmap(img);
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setImage(toGreyscale(bmp));
                    }
                });
                image.close();
            }
        });

        cameraProvider.unbindAll();

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)lifecycle,
                cameraSelector, imageAnalysis, preview);

//        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

    }

    private String[] qrDataArr;
    private int qrScannedCount;
    private int[] barColors;
    private int randID;
    private int prevQrIndex;
    private void compileData(int dataVersion, int randID, int qrIndex, int qrCount, String qrData){
        if(dataVersion != fileEditor.internalDataVersion){
            alert("Error", "Incorrect data version");
            return;
        }

        // Reset code array if ID Changes
        if(randID != this.randID){
            this.randID = randID;
            qrDataArr = new String[qrCount];
            Log.i("title", ""+qrCount);
            barColors = new int[qrCount];
            prevQrIndex = qrIndex;
        }

        final boolean updated;

        if(qrDataArr[qrIndex] == null) {
            qrDataArr[qrIndex] = qrData;
            updated = true;
            qrScannedCount += 1;
        }else{
            updated = false;
        }

        barColors[prevQrIndex] = 2;
        barColors[qrIndex] = 1;
        qrOverlayView.setBar(barColors);

        if(updated && qrScannedCount >= qrCount){

            // I guess String.join does not like non-ascii text
            String compiledData = "";
            for(int i=0;i<qrCount;i++){
                compiledData += qrDataArr[i];
            }

            try {
                byte[] compiledBytes = compiledData.getBytes(StandardCharsets.ISO_8859_1);
//                alert(count+", "+compiledData.length()+", "+compiledBytes.length, ""+fileEditor.fromBytes(fileEditor.getByteBlock(compiledBytes, 0,2),2));
//                alert("completed", new String(fileEditor.decompress(compiledBytes), StandardCharsets.ISO_8859_1));
                alert("completed", blockUncompress(compiledBytes));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        prevQrIndex = qrIndex;
    }

    private static String blockUncompress(byte[] data) throws DataFormatException {
        String uncompressedData = "";
        int curIndex = 0;
        while(curIndex < data.length){

            final int blockLength = fileEditor.fromBytes(fileEditor.getByteBlock(data, curIndex, curIndex+2), 2);

            uncompressedData += new String(
                    fileEditor.decompress(
                            fileEditor.getByteBlock(data, curIndex+2, curIndex+blockLength+2)
                    ), StandardCharsets.ISO_8859_1);

            curIndex += blockLength+2;
        }
        return uncompressedData;
    }
}
