package com.ridgebotics.ridgescout.ui.transfer;

//import static com.ridgebotics.ridgescout.utility.DataManager.evcode;
import static com.ridgebotics.ridgescout.utility.fileEditor.baseDir;

import com.ridgebotics.ridgescout.utility.AlertManager;
import com.ridgebotics.ridgescout.utility.settingsManager;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class FTPSync extends Thread {
    public static final String remoteBasePath = "/RidgeScout/";
    public static final long timeTolerance = 60000; // One min
    public static long lastSyncTime = 0;

    public interface onResult {
        void onResult(boolean error, int upCount, int downCount);
    }

    public onResult onResult;

    public static void sync(onResult onResult){
//        DataManager.reload_event();
        FTPSync ftpSync = new FTPSync();
        ftpSync.onResult = onResult;
        ftpSync.start();

        lastSyncTime = new Date().getTime();
    }

    FTPClient ftpClient;

//    private class FileDate {
//        public String filename;
//        public Calendar lastModified;
//    }

    private int upCount = 0;
    private int downCount = 0;

    private void downloadFile(FTPFile remoteFile, File localFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(localFile)) {
            ftpClient.retrieveFile(remoteBasePath + remoteFile.getName(), fos);
        }
        Date d = getUtcTimestamp(remoteFile);
        System.out.println(d);
        setLocalFileTimestamp(localFile, d);
    }

    private void uploadFile(File localFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(localFile)) {
            ftpClient.storeFile(remoteBasePath + localFile.getName(), fis);
        }
        setLocalFileTimestamp(localFile, new Date());
    }

    private FTPFile findRemoteFile(FTPFile[] remoteFiles, String fileName) {
        for (FTPFile file : remoteFiles) {
            if (file.getName().equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    private Date getUtcTimestamp(FTPFile file) {
        return file.getTimestamp().getTime();
    }

    private Date getLocalFileUtcTimestamp(File file) {
        return new Date(file.lastModified());
    }

    private void setLocalFileTimestamp(File file, Date date) {
        file.setLastModified(date.getTime());
    }

//    private long longAbs(long n){
//        return n>0 ? n : -n;
//    }

    private boolean toleranceCompareAfter(Date d1, Date d2){
        long diff = d1.getTime() - d2.getTime();
        System.out.println(diff);
        return diff > timeTolerance;
    }


    public void run() {
        try {
//            localTimeZone = TimeZone.getDefault();
            ftpClient = new FTPClient();
            InetAddress address = InetAddress.getByName(settingsManager.getFTPServer());
            ftpClient.connect(address);
            ftpClient.login("anonymous", null);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            File localDir = new File(baseDir);
            File[] localFiles = localDir.listFiles();
            FTPFile[] remoteFiles = ftpClient.listFiles(remoteBasePath);

            if (localFiles != null) {
                for (File localFile : localFiles) {
                    if (localFile.isFile()) {
                        FTPFile remoteFile = findRemoteFile(remoteFiles, localFile.getName());
//
//                        Date t1 = getLocalFileUtcTimestamp(localFile);
//                        Date t2 = getUtcTimestamp(remoteFile);
////
//                        System.out.println("- " + t1.getTime() + (t1.after(t2) ? ">" : "<") + t2.getTime());

                        if (remoteFile == null || toleranceCompareAfter(getLocalFileUtcTimestamp(localFile), (getUtcTimestamp(remoteFile)))) {
                            uploadFile(localFile);
                            System.out.println("Uploaded " + localFile.getName());
                            upCount++;
                        }else{
                            System.out.println("Did not upload " + localFile.getName());
                        }
                    }
                }
            }

            for (FTPFile remoteFile : remoteFiles) {
                if (!remoteFile.isDirectory()) {
                    File localFile = new File(baseDir, remoteFile.getName());

//                    Date t1 = getLocalFileUtcTimestamp(localFile);
//                    Date t2 = getUtcTimestamp(remoteFile);
////
//                    System.out.println("- " + t1 + (t1.after(t2) ? ">" : "<") + t2);

                    if (!localFile.exists() || toleranceCompareAfter(getUtcTimestamp(remoteFile), (getLocalFileUtcTimestamp(localFile)))) {
                        downloadFile(remoteFile, localFile);
                        System.out.println("Downloaded " + localFile.getName());
                        downCount++;
                    }else{
                        System.out.println("Did not download " + remoteFile.getName());
                    }
                }
            }

        } catch (Exception e) {
            AlertManager.error(e);
            onResult.onResult(true, upCount, downCount);
        } finally {
            onResult.onResult(false, upCount, downCount);
        }
    }
}
