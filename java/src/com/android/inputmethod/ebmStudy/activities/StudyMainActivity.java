package com.android.inputmethod.ebmStudy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.inputmethod.ebmStudy.keyStrokeLogging.KeyStrokeLogger;
import com.android.inputmethod.ebmStudy.etc.StudyConstants;
import com.android.inputmethod.latin.R;


public class StudyMainActivity extends StudyAbstractActivity implements View.OnClickListener {
    private final int numberOfRepsPerTask = 3;

    private int entryCounter = 0;

    private String pid;
    private int taskId;

    private Button btn_nextTask;
    private EditText et_password;
    private Button btn_savePassword;
    private ProgressBar pb_taskProgress;
    private TextView tv_taskProgress;
    private TextView tv_currentTask;
    private TextView tv_taskDescription;

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
        tv_currentTask = findViewById(R.id.tv_currentTask);
        tv_taskDescription = findViewById(R.id.tv_taskDescription);

        initializeEnabledState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setProgressCounter();
        getStudyInfoFromExtras();
        setUiElementsToCurrentTask();
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

    private void setUiElementsToCurrentTask() {
        tv_currentTask.setText(getResources().getIdentifier("task_title_" + taskId, "string", this.getPackageName()));
        tv_taskDescription.setText(getResources().getIdentifier("password_behavior_encoding_" + taskId, "string", this.getPackageName()));
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btn_nextTask)) {
            if(taskId < StudyConstants.TASK_ID_LAST) {
                initializeEnabledState();
                entryCounter = 0;
                launchNextExplainTaskActivity();
            } else {
                // TODO End Study
            }
        } else if (v.equals(btn_savePassword)) {
            int actualNumberOfTouchEvents = KeyStrokeLogger.getInstance().getNumberOfEventsSinceLastClear();
            int numberOfDesiredTouchEvents = getResources().getInteger(getResources().getIdentifier("number_of_events_" + taskId, "integer", this.getPackageName()));

            String actualPasswordStringValue = et_password.getText().toString();
            String desiredPasswordStringValue = getResources().getString(getResources().getIdentifier("password_string_value_" + taskId, "string", this.getPackageName()));

            if (actualPasswordStringValue.equals(desiredPasswordStringValue)) {
                if ((actualNumberOfTouchEvents == numberOfDesiredTouchEvents)) {
                    savePasswordAction();
                } else {
                    KeyStrokeLogger.getInstance().clearKeyStrokeList();
                    et_password.setText("");
                    Toast.makeText(this, "Zu viele Tastenanschläge, bitte wiederholen", Toast.LENGTH_LONG).show();
                }
            } else {
                KeyStrokeLogger.getInstance().clearKeyStrokeList();
                et_password.setText("");
                Toast.makeText(this, "Passwort falsch, bitte wiederholen", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void launchNextExplainTaskActivity() {
        Intent intent = new Intent(this, StudyExplainTaskActivity.class);
        intent.putExtra(StudyConstants.INTENT_PID, pid);
        intent.putExtra(StudyConstants.INTENT_TASK_ID, (++taskId));
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void savePasswordAction() {
        String path = "/" + "ID_" + pid + "/" + "TASK_" + taskId;
        KeyStrokeLogger.getInstance().writeToCSVFile(this, path);
        et_password.setText("");

        entryCounter += 1;
        setProgressCounter();

        if (entryCounter >= numberOfRepsPerTask) {
            et_password.setEnabled(false);
            btn_savePassword.setEnabled(false);
            btn_nextTask.setEnabled(true);
        }
    }

    private void setProgressCounter() {
        tv_taskProgress.setText("Aufgabenfortschritt: (" + entryCounter + "/" + numberOfRepsPerTask + ")");
        pb_taskProgress.setProgress(entryCounter);
    }

    private void initializeEnabledState() {
        btn_nextTask.setEnabled(false);
        et_password.setEnabled(true);
        btn_savePassword.setEnabled(true);
        pb_taskProgress.setEnabled(true);
    }
}