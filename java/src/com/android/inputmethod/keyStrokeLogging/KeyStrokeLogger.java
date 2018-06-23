package com.android.inputmethod.keyStrokeLogging;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.android.inputmethod.keyStrokeLogging.activities.StudyExplainTaskActivity;
import com.android.inputmethod.keyStrokeLogging.activities.StudyGeneralExplanationActivity;
import com.android.inputmethod.keyStrokeLogging.activities.StudyLauncherActivity;
import com.android.inputmethod.keyStrokeLogging.activities.StudyMainActivity;
import com.android.inputmethod.keyboard.Key;

import java.util.ArrayList;
import java.util.List;

public class KeyStrokeLogger {
    private static final KeyStrokeLogger instance = new KeyStrokeLogger();

    private List<KeyStrokeDataBean> keyStrokeDataList;

    private KeyStrokeLogger() {
        keyStrokeDataList = new ArrayList<>();
    }

    public static KeyStrokeLogger getInstance() {
        return instance;
    }

    public void logKeyEvent(final MotionEvent me, final Key key) {
        KeyStrokeLoggingHelper.logKeyEvent(keyStrokeDataList, me, key);
    }

    public void logLongPress(final Key key) {
        KeyStrokeLoggingHelper.logLongPress(keyStrokeDataList, key);
    }

    // TODO Remove for actual study
    /**
     * Writes to CSV file if no study is active (determined by StudyMainActivity.isIsStudyActive())
     *
     * @param context
     */
    //public void writeToCSVFile(Context context) {
    //if (!(isStudyActive())) {
    //    LogToFileHelper.logToFile(context, keyStrokeDataList, "/Demo");
    //    keyStrokeDataList.clear();
    //}
    //}

    /**
     * Writes log to CSV file if study is active (determined by StudyMainActivity.isIsStudyActive())
     *
     * @param context
     * @param path    the file path after Documents/KeyStrokeLog
     */
    public void writeToCSVFile(Context context, String path) {
        //if (isStudyActive()) {
        LogToFileHelper.logToFile(context, keyStrokeDataList, path);
        keyStrokeDataList.clear();
        //}
    }

    public void clearKeyStrokeList() {
        keyStrokeDataList.clear();
    }

    public void askForFilePermissions(Activity activity) {
        LogToFileHelper.askForFilePermissions(activity);
    }

    private boolean isStudyActive() {
        return (StudyExplainTaskActivity.isActivityRunning() || StudyGeneralExplanationActivity.isActivityRunning()
                || StudyLauncherActivity.isActivityRunning() || StudyMainActivity.isActivityRunning());
    }

    /**
     * Returns the last key down/up combo in form of a KeyStrokeDataBean List
     * Returns null if no down/up event has been recorded since the last clear action
     * @return
     */
    public List<KeyStrokeDataBean> getLastKeyStroke() {
        List<KeyStrokeDataBean> lastKeyStroke = new ArrayList<>();
        if (keyStrokeDataList.size() >= 2) {
            lastKeyStroke.add(keyStrokeDataList.get(keyStrokeDataList.size() - 2));
            lastKeyStroke.add(keyStrokeDataList.get(keyStrokeDataList.size() - 1));
        }
        return lastKeyStroke;
    }

    /**
     * Returns number of up and down events since last clear /save action
     *
     * @return
     */
    public int getNumberOfEventsSinceLastClear() {
        return keyStrokeDataList.size();
    }
}