package com.android.inputmethod.keyStrokeLogging.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.inputmethod.keyStrokeLogging.KeyStrokeLogger;
import com.android.inputmethod.keyStrokeLogging.etc.StudyConstants;
import com.android.inputmethod.latin.R;


public class StudyMainActivity extends StudyAbstractActivity implements View.OnClickListener {
    private final int numberOfRepsPerTask = 10;

    private int entryCounter = 0;

    private String pid;
    private int taskId;

    private Button btn_nextTask;
    private EditText et_password;
    private Button btn_savePassword;
    private ProgressBar pb_taskProgress;
    private TextView tv_taskProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_main);

        btn_nextTask = findViewById(R.id.btn_nextTask);
        btn_nextTask.setOnClickListener(this);
        et_password = findViewById(R.id.et_password);
        btn_savePassword = findViewById(R.id.btn_savePassword);
        btn_savePassword.setOnClickListener(this);
        pb_taskProgress = findViewById(R.id.pb_TaskProgress);
        pb_taskProgress.setMax(numberOfRepsPerTask);
        tv_taskProgress = findViewById(R.id.tv_taskProgress);
        setProgressCounter();

        btn_nextTask.setEnabled(false);
        et_password.setEnabled(true);
        btn_savePassword.setEnabled(true);
        pb_taskProgress.setEnabled(true);
    }

    private void getStudyInfoFromExtras() {
        Intent intent = getIntent();
        this.pid = intent.getStringExtra(StudyConstants.INTENT_PID);
        if (this.pid == null || pid.equals("")) {
            Toast.makeText(this, "Keine ID gesetzt", Toast.LENGTH_LONG).show();
            pid = "noPid";
        }
        this.taskId = intent.getIntExtra(StudyConstants.INTENT_TASK_ID, -1);
        if (this.taskId == -1) {
            Toast.makeText(this, "Keine Task-ID gesetzt", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStudyInfoFromExtras();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btn_nextTask)) {
            launchNextTask();
        } else if (v.equals(btn_savePassword)) {
            savePasswordAction();
        }
    }

    private void launchNextTask() {
        // TODO launch next ExplainTaskActivity

        setProgressCounter();
    }

    private void savePasswordAction() {
        String path = "/" + "ID_" + pid + "/" + "TASK_" + taskId;
        KeyStrokeLogger.getInstance().writeToCSVFile(this, path);
        et_password.setText("");

        entryCounter += 1;
        setProgressCounter();

        if (entryCounter >= numberOfRepsPerTask) {
            entryCounter = 0;

            et_password.setEnabled(false);
            btn_savePassword.setEnabled(false);
            btn_nextTask.setEnabled(true);
        }
    }

    private void setProgressCounter() {
        tv_taskProgress.setText("Aufgabenfortschritt: (" + entryCounter + "/" + numberOfRepsPerTask + ")");
        pb_taskProgress.setProgress(entryCounter);
    }
}