package com.ridgebotics.ridgescout.ui.transfer;

import static com.ridgebotics.ridgescout.utility.DataManager.evcode;
import static com.ridgebotics.ridgescout.utility.fileEditor.baseDir;

import android.content.Context;
import android.net.Uri;

import com.ridgebotics.ridgescout.utility.AlertManager;
import com.ridgebotics.ridgescout.utility.DataManager;
import com.ridgebotics.ridgescout.utility.fileEditor;
import com.ridgebotics.ridgescout.utility.settingsManager;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.checkerframework.checker.units.qual.C;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FTPSync extends Thread {
    private static final String remoteBasePath = "/RidgeScout/";

    public interface onResult {
        void onResult(boolean error);
    }

    public onResult onResult;

    public static void sync(onResult onResult){
        DataManager.reload_event();
        FTPSync ftpSync = new FTPSync();
        ftpSync.onResult = onResult;
        ftpSync.start();
    }

    private class FileDate {
        public String filename;
        public Calendar lastModified;
    }

    public boolean sendFile(FTPClient ftpClient, String filename) throws Exception{
        FileInputStream fin = new FileInputStream(baseDir + filename);
        boolean worked = ftpClient.storeFile(remoteBasePath + filename, fin);
        fin.close();
        return worked;
    }

    public void run() {
        try {
            FTPClient ftpClient = new FTPClient();
            InetAddress address = InetAddress.getByName(settingsManager.getFTPServer());
            ftpClient.connect(address);
            ftpClient.login("anonymous", null);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
//            ftpClient.setFileTransferMode(FTP.BLOCK_TRANSFER_MODE)

            FTPFile[] remoteFilestmp = ftpClient.listFiles(remoteBasePath);
            String[] localFilestmp = fileEditor.getEventFiles(evcode);

            FileDate[] remoteFiles = new FileDate[remoteFilestmp.length];
            for(int i = 0; i < remoteFilestmp.length; i++){
//                System.out.println(remoteFilestmp[i].getName());
                remoteFiles[i] = new FileDate();
                remoteFiles[i].filename = remoteFilestmp[i].getName();
                System.out.println(remoteFiles[i].filename);
                remoteFiles[i].lastModified = remoteFilestmp[i].getTimestamp();
            }

            FileDate[] localFiles = new FileDate[localFilestmp.length];
            for(int i = 0; i < localFilestmp.length; i++){
//                sendFile(localFilestmp[i]);
            String filename = "matches.fields";
            File f = new File(baseDir + filename);
//                File f = new File(baseDir + localFilestmp[i]);
//                System.out.println(f.exists());
//                FileInputStream fin = new FileInputStream(f);
//                System.out.println(f.getName() + ", " + f.exists() + ", " + sendFile(ftpClient, f));
//                fin.close();

//                System.out.println(ftpClient.getStatus());


                localFiles[i] = new FileDate();
                localFiles[i].filename = localFilestmp[i];
                localFiles[i].lastModified = fileEditor.getLastModified(localFilestmp[i]);
            }

            FileAction[] localActions = compareDates(localFiles, fd2map(remoteFiles), false);
            System.out.println(localActions.length);
            for(int i = 0 ; i < localActions.length; i++){

                System.out.println(localActions[i].filename + ", " + localActions[i].action);
                switch (localActions[i].action){
                    case SEND:
                        sendFile(ftpClient, localActions[i].filename);
                        fileEditor.setLastModified(localActions[i].filename, localActions[i].otherTimestamp);
                        break;
                    case RECIEVE:

                        break;
                }
            }


        } catch (Exception e) {
            AlertManager.error(e);
            onResult.onResult(true);
            AlertManager.toast("Error Syncing!");
        }
//      } finally {
//            AlertManager.toast("Synced!");
//            onResult.onResult(false);
//        }
    }

    private Map<String, Calendar> fd2map(FileDate[] fdarr){
        Map<String, Calendar> map = new HashMap<>();
        for(int i = 0; i < fdarr.length; i++)
            map.put(fdarr[i].filename, fdarr[i].lastModified);
        return map;
    }

    private enum SyncAction {
        SEND,
        RECIEVE,
        NONE
    }

    private class FileAction {
        String filename;
        SyncAction action;
        Calendar otherTimestamp;
        public FileAction(String filename, SyncAction action, Calendar otherTimestamp){
            this.filename = filename;
            this.action = action;
            this.otherTimestamp = otherTimestamp;
        }
    }

    private FileAction[] compareDates(FileDate[] files, Map<String, Calendar> refrence, boolean reverse){
        FileAction[] actions = new FileAction[files.length];
        for(int i = 0; i < files.length; i++){
            Calendar ref = refrence.get(files[i].filename);

            System.out.println(ref);

            if(ref == null || files[i].lastModified.after(ref)) {
                actions[i] = new FileAction(files[i].filename, !reverse ? SyncAction.SEND : SyncAction.RECIEVE, ref);
            }else if(files[i].lastModified.before(ref)){
                actions[i] = new FileAction(files[i].filename, !reverse ? SyncAction.RECIEVE : SyncAction.SEND, ref);
            }else {
                actions[i] = new FileAction(files[i].filename, SyncAction.NONE, ref);
            }
        }
        return actions;
    }
}
