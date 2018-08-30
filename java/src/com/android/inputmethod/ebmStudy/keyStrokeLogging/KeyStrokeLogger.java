package com.android.inputmethod.ebmStudy.keyStrokeLogging;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.android.inputmethod.ebmStudy.etc.StudyConfigBean;
import com.android.inputmethod.ebmStudy.ui.StudyExplainTaskActivity;
import com.android.inputmethod.ebmStudy.ui.StudyGeneralExplanationActivity;
import com.android.inputmethod.ebmStudy.ui.StudyLauncherActivity;
import com.android.inputmethod.ebmStudy.ui.StudyTaskActivity;
import com.android.inputmethod.keyboard.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class for remembering, logging and persisting keystroke data
 */
public class KeyStrokeLogger {
    private static final KeyStrokeLogger instance = new KeyStrokeLogger();

    private List<KeyStrokeDataBean> keyStrokeDataList;

    private KeyStrokeLogger() {
        keyStrokeDataList = new ArrayList<>();
    }

    public static KeyStrokeLogger getInstance() {
        return instance;
    }

    /**
     *
     * @param me
     * @param key
     */
    public void logKeyEvent(final MotionEvent me, final Key key) {
        if ((key != null && !key.isActionKey()) || key == null) {
            KeyStrokeLoggingHelper.logKeyEvent(keyStrokeDataList, me, key);
        } else {
            Log.d("KeyStrokeLogger", "No KeyEvent logged for ActionKey");
        }
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
     * Writes log to CSV file
     *
     * @param context
     * @param path    the file path after Documents/KeyStrokeLog
     */
    public void writeToCSVFile(Context context, String path, String errorPrefix) {
        //if (isStudyActive()) {
        LogToFileHelper.logToFile(context, keyStrokeDataList, path, errorPrefix);
        keyStrokeDataList.clear();
        //}
    }

    /**
     *
     * @param currentStudyTask
     * @param pid
     * @return
     */
    public static String getTaskGroupPath(StudyConfigBean currentStudyTask, String pid) {
        return "/" + "ID_" + pid + "/" + currentStudyTask.getSortingGroupId() + "_" + currentStudyTask.getGroupName();
    }

    /**
     *
     * @param currentStudyTask
     * @param pid
     * @return
     */
    public static String getTaskPath(StudyConfigBean currentStudyTask, String pid) {
        return getTaskGroupPath(currentStudyTask, pid) + "/TASK_" + currentStudyTask.getSortingTaskId();
    }

    /**
     * Empties the KeyStrokeDataList
     */
    public void clearKeyStrokeList() {
        keyStrokeDataList.clear();
    }

    /**
     * This is only useful for the ExplainTask activity where one wants to remove all the
     * delete key events while the training edittext is empty
     */
    public void clearKeyStrokeListExceptForLastEventPair() {
        if (keyStrokeDataList.size() > 2) {
            keyStrokeDataList.subList(0, keyStrokeDataList.size() - 2).clear();
            // Resets the Flight time for the First downEvent (has to be -1)
            keyStrokeDataList.get(0).setFlightTime(-1);
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

    /**
     * Writes the String csvContet to a CSV file in /Documents/KeyStrokeLog/<path>
     * This should probably be placed somewhere else
     * @param path
     * @param identifier
     * @param csvContent
     * @param context
     */
    public static void writeLikertAnswersToCSVFile(String path, String identifier, String csvContent, Context context) {
        LogToFileHelper.writeLikertAnswersToCSV(path, identifier, csvContent, context);
    }
}