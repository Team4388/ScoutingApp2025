package com.astatin3.scoutingapp2025.ui.transfer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.astatin3.scoutingapp2025.MainActivity;
import com.astatin3.scoutingapp2025.types.file;
import com.astatin3.scoutingapp2025.utility.AlertManager;
import com.astatin3.scoutingapp2025.utility.BuiltByteParser;
import com.astatin3.scoutingapp2025.utility.ByteBuilder;
import com.astatin3.scoutingapp2025.utility.DataManager;
import com.astatin3.scoutingapp2025.utility.SharePrompt;
import com.astatin3.scoutingapp2025.utility.fileEditor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FileBundle {
    private static final Intent FILE_SELECT_CODE = new Intent();

    public static void send(String[] files, Context c){
        try {
            ByteBuilder b = new ByteBuilder();

            for(int i = 0; i < files.length; i++){
                if(!fileEditor.fileExist(files[i])) continue;
    //            byte[] data = fileEditor.readFile(files[i]);
                file f = new file(files[i]);
                b.addRaw(file.typecode, f.encode());
            }

            byte[] data = b.build();
            send(data, c);

        } catch (ByteBuilder.buildingException e) {
            AlertManager.error(e);
        }
    }

    public static void send(byte[] data, Context c){
        String filename = DataManager.getevcode() + "-" + System.currentTimeMillis() + ".scoutbundle";
        SharePrompt.shareContent(c, filename, data, "application/ridgescout");
    }


    public static void receive(Activity b){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        MainActivity.setResultRelay(new MainActivity.activityResultRelay() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {
                Uri uri = data.getData();
                if(uri == null) return;

                try (InputStream is = b.getContentResolver().openInputStream(uri)) {
                    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];
                    int len = 0;
                    while ((len = is.read(buffer)) != -1) {
                        byteBuffer.write(buffer, 0, len);
                    }
                    byte[] bytes =  byteBuffer.toByteArray();
                    saveFiles(bytes);
//                    AlertManager.error(""+(bytes.length));
                } catch (IOException e) {
                    // Handle the exception
                }
            }
        });
        b.startActivityForResult(intent, 1);
    }


    private static void saveFiles(byte[] data){
        BuiltByteParser bbp = new BuiltByteParser(data);
        try{
            List<BuiltByteParser.parsedObject> parsedObjectList = bbp.parse();

            ArrayList<String> filenames = new ArrayList<>();

            for(int i = 0; i < parsedObjectList.size(); i++){
                BuiltByteParser.parsedObject pa = parsedObjectList.get(i);
                if(pa.getType() != file.typecode) continue;
                file f = file.decode((byte[]) pa.get());
                if(f == null) continue;
                filenames.add(f.filename);
                fileEditor.writeFile(f.filename, f.data);
            }

            AlertManager.alert("Saved",
                    String.join("\n", filenames));

        }catch (BuiltByteParser.byteParsingExeption e){
            AlertManager.error(e);
        }
    }
}