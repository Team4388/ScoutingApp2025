package com.astatin3.scoutingapp2025;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.media.Image;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import androidx.annotation.Nullable;

import java.nio.ByteBuffer;



public class YuvConvertor {
    private final Allocation in, out;
    private final ScriptIntrinsicYuvToRGB script;

    public YuvConvertor(Context context, int width, int height) {
        RenderScript rs = RenderScript.create(context);
        this.script = ScriptIntrinsicYuvToRGB.create(
                rs, Element.U8_4(rs));

        // NV21 YUV image of dimension 4 X 4 has following packing:
        // YYYYYYYYYYYYYYYYVUVUVUVU
        // With each pixel (of any channel) taking 8 bits.
        int yuvByteArrayLength = (int) (width * height * 1.5f);
        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs))
                .setX(yuvByteArrayLength);
        this.in = Allocation.createTyped(
                rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs))
                .setX(width)
                .setY(height);
        this.out = Allocation.createTyped(
                rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
    }


    public Bitmap toBitmap(Image image) {
        if (image.getFormat() != ImageFormat.YUV_420_888) {
            throw new IllegalArgumentException("Only supports YUV_420_888.");
        }

        byte[] yuvByteArray = toNv21(image);
        in.copyFrom(yuvByteArray);
        script.setInput(in);
        script.forEach(out);

        // Allocate memory for the bitmap to return. If you have a reusable Bitmap
        // I recommending using that.
        Bitmap bitmap = Bitmap.createBitmap(
                image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        out.copyTo(bitmap);

        return bitmap;
    }

    private byte[] toNv21(Image image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Order of U/V channel guaranteed, read more:
        // https://developer.android.com/reference/android/graphics/ImageFormat#YUV_420_888
        Image.Plane yPlane = image.getPlanes()[0];
        Image.Plane uPlane = image.getPlanes()[1];
        Image.Plane vPlane = image.getPlanes()[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        // Full size Y channel and quarter size U+V channels.
        int numPixels = (int) (width * height * 1.5f);
        byte[] nv21 = new byte[numPixels];
        int idY = 0;
        int idUV = width * height;
        int uvWidth = width / 2;
        int uvHeight = height / 2;

        // Copy Y & UV channel.
        // NV21 format is expected to have YYYYVU packaging.
        // The U/V planes are guaranteed to have the same row stride and pixel stride.
        int uvRowStride = uPlane.getRowStride();
        int uvPixelStride = uPlane.getPixelStride();
        int yRowStride = yPlane.getRowStride();
        int yPixelStride = yPlane.getPixelStride();
        for(int y = 0; y < height; ++y) {
            int yOffset = y * yRowStride;
            int uvOffset = y * uvRowStride;

            for (int x = 0; x < width; ++x) {
                nv21[idY++] = yBuffer.get(yOffset + x * yPixelStride);

                if (y < uvHeight && x < uvWidth) {
                    int bufferIndex = uvOffset + (x * uvPixelStride);
                    // V channel.
                    nv21[idUV++] = vBuffer.get(bufferIndex);
                    // U channel.
                    nv21[idUV++] = uBuffer.get(bufferIndex);
                }
            }
        }

        return nv21;
    }

    YuvImage toYuvImage(Image image) {
        if (image.getFormat() != ImageFormat.YUV_420_888) {
            throw new IllegalArgumentException("Invalid image format");
        }

        int width = image.getWidth();
        int height = image.getHeight();

        // Order of U/V channel guaranteed, read more:
        // https://developer.android.com/reference/android/graphics/ImageFormat#YUV_420_888
        Image.Plane yPlane = image.getPlanes()[0];
        Image.Plane uPlane = image.getPlanes()[1];
        Image.Plane vPlane = image.getPlanes()[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        // Full size Y channel and quarter size U+V channels.
        int numPixels = (int) (width * height * 1.5f);
        byte[] nv21 = new byte[numPixels];
        int index = 0;

        // Copy Y channel.
        int yRowStride = yPlane.getRowStride();
        int yPixelStride = yPlane.getPixelStride();
        for(int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                nv21[index++] = yBuffer.get(y * yRowStride + x * yPixelStride);
            }
        }

        // Copy VU data; NV21 format is expected to have YYYYVU packaging.
        // The U/V planes are guaranteed to have the same row stride and pixel stride.
        int uvRowStride = uPlane.getRowStride();
        int uvPixelStride = uPlane.getPixelStride();
        int uvWidth = width / 2;
        int uvHeight = height / 2;

        for(int y = 0; y < uvHeight; ++y) {
            for (int x = 0; x < uvWidth; ++x) {
                int bufferIndex = (y * uvRowStride) + (x * uvPixelStride);
                // V channel.
                nv21[index++] = vBuffer.get(bufferIndex);
                // U channel.
                nv21[index++] = uBuffer.get(bufferIndex);
            }
        }
        return new YuvImage(
                nv21, ImageFormat.NV21, width, height, /* strides= */ null);
    }
}