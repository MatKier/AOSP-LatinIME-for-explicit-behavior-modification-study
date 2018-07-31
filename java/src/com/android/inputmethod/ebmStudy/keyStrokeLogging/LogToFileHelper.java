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
                Environment.DIRECTORY_DOCUMENTS), "KeyStrokeLog" + subPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private static void writeDataListToFile(List<KeyStrokeDataBean> keyStrokeDataList, String path) throws IOException {
        createDirectory(path);
        String filename = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS) + "/KeyStrokeLog" + path, filename + ".csv");

        FileOutputStream stream;
        if (!file.exists()) {
            stream = new FileOutputStream(file, true);
            stream.write(KeyStrokeDataBean.getCSVHeader().getBytes());
            // stream.flush();
        } else {
            stream = new FileOutputStream(file, true);
        }


        Log.d("writeData", "" + file.toString());
        StringBuilder buff = new StringBuilder();
        for (KeyStrokeDataBean bean : keyStrokeDataList) {
            buff.append(bean.toCSVString());
        }

        stream.write(buff.toString().getBytes());
        stream.flush();
        stream.close();
    }

    static void logToFile(Context context, List<KeyStrokeDataBean> keyStrokeDataList, String path) {
        if (keyStrokeDataList != null && keyStrokeDataList.size() != 0) {
            if (isExternalStorageWritable(context)) {
                try {
                    writeDataListToFile(keyStrokeDataList, path);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Exception while writing file", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Can't access external storage (not mounted or no permission)", Toast.LENGTH_LONG).show();
            }
            Log.d("closing", keyStrokeDataList.toString());
        }
    }

    static void askForFilePermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                0);
    }
}