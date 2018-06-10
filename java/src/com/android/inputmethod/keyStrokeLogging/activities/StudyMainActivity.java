package com.android.inputmethod.keyStrokeLogging.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.inputmethod.keyStrokeLogging.KeyStrokeLogger;
import com.android.inputmethod.latin.R;

import java.util.ArrayList;
import java.util.List;

public class StudyMainActivity extends Activity implements View.OnClickListener {
    private final int numberOfRepsPerTask = 10;
    private final int numberOfTasks = 5;

    private int entryCounter = 0;

    private Spinner sp_studyTaskSelector;
    private Button btn_saveSelectedTask;
    private EditText et_password;
    private Button btn_savePassword;
    private ProgressBar pb_taskProgress;
    private TextView tv_taskProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_main);

        sp_studyTaskSelector = findViewById(R.id.sp_studyTaskSelector);
        btn_saveSelectedTask = findViewById(R.id.btn_saveSelectedTask);
        btn_saveSelectedTask.setOnClickListener(this);
        et_password = findViewById(R.id.et_password);
        btn_savePassword = findViewById(R.id.btn_savePassword);
        btn_savePassword.setOnClickListener(this);
        pb_taskProgress = findViewById(R.id.pb_TaskProgress);
        pb_taskProgress.setMax(numberOfRepsPerTask);
        tv_taskProgress = findViewById(R.id.tv_taskProgress);
        setProgressCounter();

        fillSpinner();

        sp_studyTaskSelector.setEnabled(false);
        btn_saveSelectedTask.setEnabled(false);
        et_password.setEnabled(false);
        btn_savePassword.setEnabled(false);
        pb_taskProgress.setEnabled(false);
    }

    private void fillSpinner() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= numberOfTasks; i++) {
            list.add("Aufgabe " + i);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_studyTaskSelector.setAdapter(arrayAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btn_saveSelectedTask)) {
            saveSelectedTaskAction();
        } else if (v.equals(btn_savePassword)) {
            savePasswordAction();
        }
    }

    private void saveSelectedTaskAction() {
        et_password.setEnabled(true);
        btn_savePassword.setEnabled(true);
        sp_studyTaskSelector.setEnabled(false);
        btn_saveSelectedTask.setEnabled(false);

        setProgressCounter();
    }

    private void savePasswordAction() {
        // TODO add partId
        String path = "/" + "ID_" + "PART_ID" + "/" + sp_studyTaskSelector.getSelectedItem().toString();
        KeyStrokeLogger.getInstance().writeToCSVFile(this, path);
        et_password.setText("");

        entryCounter += 1;
        setProgressCounter();

        if (entryCounter >= numberOfRepsPerTask) {
            entryCounter = 0;

            et_password.setEnabled(false);
            btn_savePassword.setEnabled(false);
            sp_studyTaskSelector.setEnabled(true);
            btn_saveSelectedTask.setEnabled(true);
        }
    }

    private void setProgressCounter() {
        tv_taskProgress.setText("Aufgabenfortschritt: (" + entryCounter + "/" + numberOfRepsPerTask + ")");
        pb_taskProgress.setProgress(entryCounter);
    }

    @Override
    public void onBackPressed() { }
}