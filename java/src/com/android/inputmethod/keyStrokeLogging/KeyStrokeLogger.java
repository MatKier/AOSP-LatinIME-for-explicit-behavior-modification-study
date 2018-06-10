package com.android.inputmethod.keyStrokeLogging;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.Toast;

import com.android.inputmethod.keyboard.Key;
import com.android.inputmethod.keyboard.KeyDetector;

import java.util.ArrayList;
import java.util.List;

public class KeyStrokeLogger {
    private static final KeyStrokeLogger instance = new KeyStrokeLogger();

    private List<KeyStrokeDataBean> keyStrokeDataList;
    private boolean isStudyActive = false;

    private KeyStrokeLogger() {
        keyStrokeDataList = new ArrayList<>();
    }

    public static KeyStrokeLogger getInstance() {
        return instance;
    }

    public void logKeyEvent(final MotionEvent me, final KeyDetector mKeyDetector) {
        KeyStrokeLoggingHelper.logKeyEvent(keyStrokeDataList, me, mKeyDetector);
    }

    public void logLongPress(final Key key) {
        KeyStrokeLoggingHelper.logLongPress(keyStrokeDataList, key);
    }

    /**
     * Writes to CSV file if no study is active (determined by StudyMainActivity.isIsStudyActive())
     *
     * @param context
     */
    public void writeToCSVFile(Context context) {
        if (!isStudyActive) {
            LogToFileHelper.logToFile(context, keyStrokeDataList, "/Demo");
            keyStrokeDataList.clear();
        }
    }

    /**
     * Writes log to CSV file if study is active (determined by StudyMainActivity.isIsStudyActive())
     *
     * @param context
     * @param path    the file path after Documents/KeyStrokeLog
     */
    public void writeToCSVFile(Context context, String path) {
        if (isStudyActive) {
            LogToFileHelper.logToFile(context, keyStrokeDataList, path);
            keyStrokeDataList.clear();
        }
    }

    public void clearKeyStrokeList() {
        keyStrokeDataList.clear();
    }

    public void askForFilePermissions(Activity activity) {
        LogToFileHelper.askForFilePermissions(activity);
    }

    public void setIsStudyActive(boolean isStudyActive) {
        this.isStudyActive = isStudyActive;
    }
}
