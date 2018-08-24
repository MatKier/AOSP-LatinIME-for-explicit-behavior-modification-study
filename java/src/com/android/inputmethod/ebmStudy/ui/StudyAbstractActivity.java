package com.android.inputmethod.ebmStudy.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public abstract class StudyAbstractActivity extends Activity {

    private static boolean isActivityRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
    }

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