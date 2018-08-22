package com.android.inputmethod.ebmStudy.ui;

import android.app.Activity;
import android.content.Intent;

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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            setIntent(intent);
        }
    }

    public static boolean isActivityRunning() {
        return isActivityRunning;
    }

    @Override
    public void onBackPressed() { }
}