package com.astatin3.scoutingapp2025.ui.transfer.codes;

import static androidx.core.math.MathUtils.clamp;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.astatin3.scoutingapp2025.databinding.FragmentTransferBinding;
import com.astatin3.scoutingapp2025.types.file;
import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.fileEditor;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.DataFormatException;

//public class scannerView extends androidx.appcompat.widget.AppCompatImageView {
public class scannerView extends ConstraintLayout {
    private qrOverlayView qrOverlayView;
    private Handler uiHandler;

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
    private final int downscale = 1;
    private FragmentTransferBinding binding;
    private LifecycleOwner lifecycle;

    private void setImage(Bitmap bmp){
        if(scale == 0) {
            scale = ((float) getWidth() / bmp.getWidth()) * ((float) 16 / 9);
            binding.scannerImage.setTranslationX(0);
            binding.scannerImage.setTranslationY(0);
        }
        scanQRCode(bmp);
        binding.scannerImage.setImageBitmap(bmp);
        binding.scannerThreshold.bringToFront();
//        alert("test", getChildCount()+"");
    }

//    private Bitmap img

    int[] levelMap = new int[256];
    private void recalcMap(){
        for (int i = 0; i < 256; i++) {
            levelMap[i] = clamp(
                (clamp(
                    i-thresholdOffset, 0, 255) / (256 / numColors)) * (256 / numColors
                )+brightness, 0, 255
            );
        }
    }
    private Bitmap toGreyscale(Image image){
        // Turns out the "Y" In YUV is the Luminance of the pixel.
        // Makes converting to greyscale 1000x easier
        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
        final int width = image.getWidth();
        final int height = image.getHeight();

        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int L = levelMap[yBuffer.get() & 0xff];
                pixels[y * width + x] = 0xff000000 | (L << 16) | (L << 8) | L;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }
    public void scanQRCode(Bitmap bitmap) {

        codeScanTask async = new codeScanTask();
        async.setImage(bitmap);
        async.onResult(data -> {
            if(data != null){
//                    alert("test", ""+fileEditor.byteFromChar(data.charAt(0)));
                compileData(
                    fileEditor.byteFromChar(data.charAt(0)),
                    fileEditor.byteFromChar(data.charAt(1)),
                    fileEditor.byteFromChar(data.charAt(2)),
                    (fileEditor.byteFromChar(data.charAt(3))+1),
                    data.substring(4)
                );
            }
            return null;
        });
        async.execute();



//        return contents;
    }
    private int numColors = 3;
    private int thresholdOffset = 128;
    private int brightness = 128;
    public void start(FragmentTransferBinding binding, LifecycleOwner lifecycle){
        this.binding = binding;
        this.lifecycle = lifecycle;



        uiHandler = new Handler();


        binding.scannerThreshold.setProgress(thresholdOffset);
        binding.scannerThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                thresholdOffset = 127-progress;
                recalcMap();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        binding.scannerThreshold.setMax(255);

        binding.scannerColors.setProgress(numColors);
        binding.scannerColors.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                numColors = 18-(progress-2);
                recalcMap();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        binding.scannerColors.setMax(18);
        binding.scannerBrightness.setProgress(brightness);
        binding.scannerBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightness = progress-128;
                recalcMap();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        binding.scannerBrightness.setMax(256);


        recalcMap();

        qrOverlayView = new qrOverlayView(getContext());
        qrOverlayView.bringToFront();
        ConstraintLayout.LayoutParams pointsOverlayViewParams = new ConstraintLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT
        );

        qrOverlayView.setLayoutParams(pointsOverlayViewParams);
        this.addView(qrOverlayView);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture
                = ProcessCameraProvider.getInstance(this.getContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
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
                uiHandler.post(new Runnable() {
                    final Bitmap bmp = toGreyscale(img);

                    @Override
                    public void run() {
//                        setImage(toGreyscale(bmp));
                        setImage(bmp);
                    }
                });
                image.close();
            }
        });

        cameraProvider.unbindAll();

        cameraProvider.bindToLifecycle(lifecycle,
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
            alert("Error", "Incorrect data version ("+dataVersion+" != "+fileEditor.internalDataVersion+")");
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

            String compiledString = "";
            for(int i=0;i<qrCount;i++){
                compiledString += qrDataArr[i];
            }

            try {
                byte[] compiledBytes = compiledString.getBytes(StandardCharsets.ISO_8859_1);
                byte[] resultBytes = blockUncompress(compiledBytes);

                String result_filenames = "";

                BuiltByteParser bbp = new BuiltByteParser(resultBytes);
                ArrayList<BuiltByteParser.parsedObject> result = bbp.parse();

                for(int i = 0; i < result.size(); i++){
                    if(result.get(i).getType() != file.typecode) continue;
                    file f = file.decode((byte[]) result.get(i).get());

                    if(f != null)
                        if(f.write())
                            result_filenames += f.filename + "\n";
                }

                alert("Completed!", result_filenames);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        prevQrIndex = qrIndex;
    }

    private static byte[] blockUncompress(byte[] data) throws DataFormatException {
        List<byte[]> uncompressedData = new ArrayList<>();
        int curIndex = 0;
        while(curIndex < data.length){

            final int blockLength = fileEditor.fromBytes(fileEditor.getByteBlock(data, curIndex, curIndex+2), 2);

            uncompressedData.add(
                    fileEditor.decompress(
                            fileEditor.getByteBlock(data, curIndex+2, curIndex+blockLength+2)
                    )
            );

            curIndex += blockLength+2;
        }
        return fileEditor.combineByteArrays(uncompressedData);
    }
}
