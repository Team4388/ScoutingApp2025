package com.astatin3.scoutingapp2025;

import android.content.Context;

import com.astatin3.scoutingapp2025.Utils.frcMatch;
import com.astatin3.scoutingapp2025.Utils.frcTeam;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.BufferOverflowException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class fileEditor {
//    private final static String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final byte internalDataVersion = 0x01;

    public static String binaryVisualize(byte[] bytes){
        String returnStr = "";
        for(int a=0;a<bytes.length;a++){
            for(int b=0;b<8;b++){
                returnStr += String.valueOf((bytes[a] >> b) & 1);
            }
            returnStr += " (" + (int)bytes[a] + ")\n";
        }
        return returnStr;
    }

    public static char toChar(int num){
        if(num < 0 || num > 255){
            throw new BufferOverflowException();
        }
        byte[] bytes = new byte[1];
        bytes[0] = (byte) num;
        return new String(bytes, Charset.defaultCharset()).charAt(0);
    }

    public static int fromChar(char c){
        byte[] bytes = (String.valueOf(c)).getBytes(Charset.defaultCharset());
        return Byte.toUnsignedInt(bytes[0]);
    }

    public static byte[] compress(byte[] input) {
        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        while (!deflater.finished()) {
            int compressedSize = deflater.deflate(buffer);
            outputStream.write(buffer, 0, compressedSize);
        }

        return outputStream.toByteArray();
    }


    public static byte[] decompress(byte[] input) throws DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(input);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        while (!inflater.finished()) {
            int decompressedSize = inflater.inflate(buffer);
            outputStream.write(buffer, 0, decompressedSize);
        }

        return outputStream.toByteArray();
    }

    private static boolean writeToFile(Context context, String filepath, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filepath, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String intSplit(int[] intArr, String splitStr){
        String returnStr = "";
        for(int i=0;i<intArr.length;i++){
            returnStr += String.valueOf(intArr[i]);
            if(i != intArr.length-1){
                returnStr += splitStr;
            }
        }
        return returnStr;
    }

    public static boolean setMatches(Context context, String key, String matchName, ArrayList<frcMatch> matches){
        final String filename = (key + "-matches.csv");

        String csvData = "";

        csvData += key + "\n";
        csvData += matchName + "\n";

        for(int i=0;i<matches.size();i++){
            final frcMatch match = matches.get(i);
            csvData += String.valueOf(match.matchIndex)
                       + "," + intSplit(match.redAlliance, ",")
                       + "," + intSplit(match.blueAlliance, ",") + "\n";
        }

        return writeToFile(context, filename, csvData);
    }

    public static boolean setTeams(Context context, String key, ArrayList<frcTeam> teams){
        return true;
    }
}

