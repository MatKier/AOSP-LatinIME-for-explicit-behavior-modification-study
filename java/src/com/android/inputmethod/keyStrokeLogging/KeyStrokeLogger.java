package com.android.inputmethod.keyStrokeLogging;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.android.inputmethod.keyStrokeLogging.activities.StudyExplainTaskActivity;
import com.android.inputmethod.keyStrokeLogging.activities.StudyGeneralExplanationActivity;
import com.android.inputmethod.keyStrokeLogging.activities.StudyLauncherActivity;
import com.android.inputmethod.keyStrokeLogging.activities.StudyMainActivity;
import com.android.inputmethod.keyboard.Key;
import com.android.inputmethod.keyboard.KeyDetector;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KeyStrokeLogger {
    private static final KeyStrokeLogger instance = new KeyStrokeLogger();

    private List<KeyStrokeDataBean> keyStrokeDataList;

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
        // TODO Remove for actual study
        //if (!(isStudyActive())) {
        //    LogToFileHelper.logToFile(context, keyStrokeDataList, "/Demo");
        //    keyStrokeDataList.clear();
        //}
    }

    /**
     * Writes log to CSV file if study is active (determined by StudyMainActivity.isIsStudyActive())
     *
     * @param context
     * @param path    the file path after Documents/KeyStrokeLog
     */
    public void writeToCSVFile(Context context, String path) {
        if (isStudyActive()) {
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

    private boolean isStudyActive() {
        return (StudyExplainTaskActivity.isActivityRunning() || StudyGeneralExplanationActivity.isActivityRunning()
                || StudyLauncherActivity.isActivityRunning() || StudyMainActivity.isActivityRunning());
    }

    public String getInfoForLastKeyEvent() {
        if (keyStrokeDataList.size() >= 1) {
            KeyStrokeDataBean lastKeyEvent = keyStrokeDataList.get(keyStrokeDataList.size() - 1);
            Log.d("getInfoForLastKeyEvent", "eventType: " + lastKeyEvent.getEventType());
            String offsetLR = lastKeyEvent.getOffsetX() > 25 ? "rechts" : (lastKeyEvent.getOffsetX() < -25 ? "links" : "");
            String offsetOU = lastKeyEvent.getOffsetY() > 25 ? "unten" : (lastKeyEvent.getOffsetY() < -25 ? "oben" : "");
            String info = "Letzte Taste: " + lastKeyEvent.getKeyValue() + ", Offset: " + offsetLR + " " + offsetOU
                    + ", Haltedauer: " + lastKeyEvent.getHoldTime() + ", Druck: " + (lastKeyEvent.getPressure() < 0.25f ? "normal" : "fest");
            return info;
        } else {
            return "";
        }
    }
}
