package com.android.inputmethod.keyStrokeLogging.activities;

import android.app.Activity;

public abstract class StudyAbstractActivity extends Activity {

    private static boolean isActivityRunning;

    @Override
    protected void onResume() {
        super.onResume();
        isActivityRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityRunning = false;
    }

    public static boolean isActivityRunning() {
        return isActivityRunning;
    }

    @Override
    public void onBackPressed() { }
}
