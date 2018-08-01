package com.android.inputmethod.ebmStudy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.inputmethod.ebmStudy.etc.keyStrokeVisualizer.KeyStrokeVisualizerView;
import com.android.inputmethod.ebmStudy.etc.StudyConfigBean;
import com.android.inputmethod.ebmStudy.keyStrokeLogging.KeyStrokeLogger;
import com.android.inputmethod.ebmStudy.etc.StudyConstants;
import com.android.inputmethod.latin.R;

import java.util.ArrayList;

public class StudyTaskActivity extends StudyAbstractActivity implements View.OnClickListener {
    private int entryCounter = 0;

    private String pid;
    private ArrayList<StudyConfigBean> studyConfig;

    private Button btn_nextTask;
    private EditText et_password;
    private Button btn_savePassword;
    private ProgressBar pb_taskProgress;
    private TextView tv_taskProgress;
    private TextView tv_currentTask;
    private KeyStrokeVisualizerView taskVisualizer;

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
        tv_taskProgress = findViewById(R.id.tv_taskProgress);
        tv_currentTask = findViewById(R.id.tv_currentTask);
        taskVisualizer = findViewById(R.id.pwTaskDisplay);

        initializeEnabledState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStudyInfoFromExtras();
        setProgressCounter();
        setUiElementsToCurrentTask();
    }

    private void getStudyInfoFromExtras() {
        Intent intent = getIntent();
        this.pid = intent.getStringExtra(StudyConstants.INTENT_PID);
        if (this.pid == null || pid.equals("")) {
            Toast.makeText(this, "Keine ID gesetzt", Toast.LENGTH_LONG).show();
            pid = "noPid";
        }
        Bundle bundle = getIntent().getExtras();
        this.studyConfig = bundle.getParcelableArrayList(StudyConstants.INTENT_CONFIG);
        if (this.studyConfig == null || this.studyConfig.size() == 0) {
            Toast.makeText(this, "Keine oder leere Config", Toast.LENGTH_LONG).show();
        }
    }

    private void setUiElementsToCurrentTask() {
        pb_taskProgress.setMax(studyConfig.get(0).getNumberOfReps());
        tv_currentTask.setText("Aufgabe " + studyConfig.get(0).getTaskId());
        taskVisualizer.setKeyStrokeList(studyConfig.get(0).getPwTask());
        taskVisualizer.invalidate();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btn_nextTask)) {
            if(studyConfig.size() > 1) {
                initializeEnabledState();
                entryCounter = 0;
                launchNextExplainTaskActivity();
            } else {
                // TODO End Study
                Toast.makeText(this, "Ende", Toast.LENGTH_LONG).show();
            }
        } else if (v.equals(btn_savePassword)) {
            int actualNumberOfTouchEvents = KeyStrokeLogger.getInstance().getNumberOfEventsSinceLastClear();
            int numberOfDesiredTouchEvents = studyConfig.get(0).getPwTask().size();

            String actualPasswordStringValue = et_password.getText().toString();

            String desiredPasswordStringValue = "";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < studyConfig.get(0).getPwTask().size(); i = i+2) {
                sb.append(studyConfig.get(0).getPwTask().get(i).getKeyValue());
            }
            desiredPasswordStringValue = sb.toString();

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

        // Remove finished task from list
        studyConfig.remove(0);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(StudyConstants.INTENT_CONFIG, studyConfig);
        intent.putExtras(bundle);

        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void savePasswordAction() {
        String path = "/" + "ID_" + pid + "/" + "TASK_" + studyConfig.get(0).getTaskId();
        KeyStrokeLogger.getInstance().writeToCSVFile(this, path);
        et_password.setText("");

        entryCounter += 1;
        setProgressCounter();

        if (entryCounter >= studyConfig.get(0).getNumberOfReps()) {
            et_password.setEnabled(false);
            btn_savePassword.setEnabled(false);
            btn_nextTask.setEnabled(true);
        }
    }

    private void setProgressCounter() {
        tv_taskProgress.setText("Aufgabenfortschritt: (" + entryCounter + "/" + studyConfig.get(0).getNumberOfReps() + ")");
        pb_taskProgress.setProgress(entryCounter);
    }

    private void initializeEnabledState() {
        btn_nextTask.setEnabled(false);
        et_password.setEnabled(true);
        btn_savePassword.setEnabled(true);
        pb_taskProgress.setEnabled(true);
    }
}