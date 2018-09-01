package com.android.inputmethod.ebmStudy.keyStrokeLogging;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class LogToFileHelper {
    private static final String BASEDIRECTRORY = "KeyStrokeLog";

    private static boolean isExternalStorageWritable(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private static void createDirectory(String subPath) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), BASEDIRECTRORY + subPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    static void writeLikertAnswersToCSV(String path, String identifier, String csvContent, Context context) {
        if (isExternalStorageWritable(context)) {
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS) + "/" + BASEDIRECTRORY + path, "likertAnswers" + identifier + ".csv");
                FileOutputStream stream = new FileOutputStream(file, true);
                stream.write(csvContent.getBytes());
                stream.flush();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Exception while writing file", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Can't access external storage (not mounted or no permission)", Toast.LENGTH_LONG).show();
        }
    }

    private static void writeDataListToFile(List<? extends SimpleKeyStrokeDataBean> keyStrokeDataList, String path, String errorPrefix) throws IOException {
        createDirectory(path);
        String filename = errorPrefix + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS) + "/" + BASEDIRECTRORY + path, filename + ".csv");

        FileOutputStream stream;
        if (!file.exists()) {
            stream = new FileOutputStream(file, true);
            stream.write(keyStrokeDataList.get(0).getCSVHeader().getBytes());
            // stream.flush();
        } else {
            stream = new FileOutputStream(file, true);
        }


        Log.d("writeData", "" + file.toString());
        StringBuilder buff = new StringBuilder();
        for (SimpleKeyStrokeDataBean bean : keyStrokeDataList) {
            buff.append(bean.toCSVString());
        }

        stream.write(buff.toString().getBytes());
        stream.flush();
        stream.close();
    }

    static void logToFile(Context context, List<? extends SimpleKeyStrokeDataBean> keyStrokeDataList, String path, String errorPrefix) {
        if (keyStrokeDataList != null && keyStrokeDataList.size() != 0) {
            if (isExternalStorageWritable(context)) {
                try {
                    writeDataListToFile(keyStrokeDataList, path, errorPrefix);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Exception while writing file", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Can't access external storage (not mounted or no permission)", Toast.LENGTH_LONG).show();
            }
        }
    }

    static void askForFilePermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                0);
    }
}