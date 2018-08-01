package com.android.inputmethod.ebmStudy.keyStrokeLogging;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;

import com.android.inputmethod.ebmStudy.activities.StudyExplainTaskActivity;
import com.android.inputmethod.ebmStudy.activities.StudyGeneralExplanationActivity;
import com.android.inputmethod.ebmStudy.activities.StudyLauncherActivity;
import com.android.inputmethod.ebmStudy.activities.StudyTaskActivity;
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
     * Writes to CSV file if no study is active (determined by StudyTaskActivity.isIsStudyActive())
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
     * Writes log to CSV file if study is active (determined by StudyTaskActivity.isIsStudyActive())
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

    /**
     * Empties the KeyStrokeDataList
     */
    public void clearKeyStrokeList() {
        keyStrokeDataList.clear();
    }

    /**
     * This is only usefull for the ExplainTask activity where one wants to remove all the
     * delete key events while the training edittext is empty
     */
    public void clearKeyStrokeListExceptForLastEventPair() {
        if (keyStrokeDataList.size() > 2) {
            keyStrokeDataList.subList(0, keyStrokeDataList.size() - 2).clear();
            // TODO set flighttime to 0
        }
    }

    public void askForFilePermissions(Activity activity) {
        LogToFileHelper.askForFilePermissions(activity);
    }

    /**
     * Determines if one of the user study activities is running
     * @return true if one of the user study activities is running
     */
    private boolean isStudyActive() {
        return (StudyExplainTaskActivity.isActivityRunning() || StudyGeneralExplanationActivity.isActivityRunning()
                || StudyLauncherActivity.isActivityRunning() || StudyTaskActivity.isActivityRunning());
    }

    /**
     * Returns the last maxNumOfKeyStrokes (or less, if less have been recorded) key events in form of a KeyStrokeDataBean List
     * Returns empty list, if no down/up event has been recorded since the last clear action
     * @param maxNumOfKeyStrokes
     * @return
     */
    public List<KeyStrokeDataBean> getLastKeyStrokes(int maxNumOfKeyStrokes) {
        List<KeyStrokeDataBean> lastKeyStrokes;

        int maxNumOfUpDownEvents = maxNumOfKeyStrokes * 2;
        int numberOfKeyStrokeEvents = (keyStrokeDataList.size() < maxNumOfUpDownEvents) ? keyStrokeDataList.size() : maxNumOfUpDownEvents;
        lastKeyStrokes = new ArrayList<KeyStrokeDataBean>(keyStrokeDataList.subList(keyStrokeDataList.size() - numberOfKeyStrokeEvents, keyStrokeDataList.size()));

        return lastKeyStrokes;
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