package com.android.inputmethod.ebmStudy.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.inputmethod.ebmStudy.ui.dialogs.ExplainNotationDialog;
import com.android.inputmethod.ebmStudy.etc.keyStrokeVisualizer.KeyStrokeVisualizerView;
import com.android.inputmethod.ebmStudy.etc.StudyConfigBean;
import com.android.inputmethod.ebmStudy.keyStrokeLogging.KeyStrokeLogger;
import com.android.inputmethod.ebmStudy.etc.StudyConstants;
import com.android.inputmethod.ebmStudy.ui.dialogs.LikertQuestionDialog;
import com.android.inputmethod.latin.R;

import java.util.ArrayList;

public class StudyTaskActivity extends StudyAbstractActivity implements View.OnClickListener {
    private static final String ERROR_PREFIX_VALID = "valid";
    private static final String ERROR_PREFIX_WRONG_TOUCH = "touchError";
    private static final String ERROR_PREFIX_WRONG_KEYS = "keyError";

    private int entryCounter = 0;

    private String pid;
    private ArrayList<StudyConfigBean> studyConfig;

    private Button btn_nextTask;
    private EditText et_password;
    private Button btn_savePassword;
    private ProgressBar pb_taskProgress;
    private TextView tv_taskProgress;
    private TextView tv_currentTask;
    private TextView tv_taskTitle;
    private KeyStrokeVisualizerView taskVisualizer;
    private TextView tv_taskGroupDescription;

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
        tv_taskTitle = findViewById(R.id.tv_taskTitle);
        taskVisualizer = findViewById(R.id.pwTaskDisplay);
        taskVisualizer.setOnClickListener(this);
        tv_taskGroupDescription = findViewById(R.id.tv_taskGroupDescription);

        initializeEnabledState();

        et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doSaveAction();
                }
                return true;
            }
        });
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

    @SuppressLint("SetTextI18n")
    private void setUiElementsToCurrentTask() {
        StudyConfigBean currentTask = studyConfig.get(0);

        pb_taskProgress.setMax(currentTask.getNumberOfReps());
        tv_currentTask.setText("Aufgabe: " + currentTask.getTaskId());
        tv_taskGroupDescription.setText("Aufgabengruppe: " + currentTask.getGroupName());

        if (currentTask.isIntroductionGroup() && currentTask.getPwTask().size() != 0) {
            taskVisualizer.setVisibility(View.GONE);
            tv_taskTitle.setText("Aufgaben Passwort: \n" + currentTask.getTaskPWString());
        } else if (!currentTask.isIntroductionGroup() && currentTask.getPwTask().size() != 0) {
            taskVisualizer.setVisibility(View.VISIBLE);
            tv_taskTitle.setText("Aufgaben Passwort:");
            taskVisualizer.setKeyStrokeList(currentTask.getPwTask());
            taskVisualizer.invalidate();
        }  else if (currentTask.getPwTask().size() == 0) {
            taskVisualizer.setVisibility(View.GONE);
            tv_taskTitle.setText("Geben Sie Ihr selbst ausgedachtes Passwort ein:");
        }
    }

    @Override
    public void onClick(View v) {
        final StudyConfigBean currentTask = studyConfig.get(0);
        if (v == btn_nextTask) {
            // Case: We are not in the last task
            if(studyConfig.size() > 1) {
                final StudyConfigBean nextTask = studyConfig.get(1);
                initializeEnabledState();
                entryCounter = 0;
                // if (currentTask.getGroupId() != nextTask.getGroupId()) {
                // TODO launch likert question dialkog after evrey task
                if ((!currentTask.isIntroductionGroup())) {
                    LikertQuestionDialog lqd = new LikertQuestionDialog(this, currentTask, pid);
                    lqd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            launchNextExplainTaskActivity();
                        }
                    });
                    lqd.show();
                } else {
                    launchNextExplainTaskActivity();
                }
            } else {
                // Case: We are in the last task
                showStudyEndDialog();
            }
        } else if (v == btn_savePassword) {
            doSaveAction();
        } else if (v == taskVisualizer) {
            ExplainNotationDialog end = new ExplainNotationDialog(this, currentTask.getPwTask());
            end.show();
        }
    }

    private void showStudyEndDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Studie beendet")
                .setMessage("Danke für die Teilnahme")
                .setCancelable(false)
                .setPositiveButton("App beenden", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAndRemoveTask();
                    }
                })
                .create()
                .show();
    }

    private void doSaveAction() {
        final StudyConfigBean currentTask = studyConfig.get(0);

        int numberOfDesiredTouchEvents = currentTask.getPwTask().size();

        // Case: currentTask is not the 'user created pw task'
        if (numberOfDesiredTouchEvents != 0) {
            int actualNumberOfTouchEvents = KeyStrokeLogger.getInstance().getNumberOfEventsSinceLastClear();

            String desiredPasswordStringValue = currentTask.getTaskPWString();
            String actualPasswordStringValue = et_password.getText().toString();

            if (actualPasswordStringValue.equals(desiredPasswordStringValue)) {
                if ((actualNumberOfTouchEvents == numberOfDesiredTouchEvents)) {
                    // TODO flag in der methode für identifizierung nutzten (valid, touchError, keyError)
                    incrementProgressbar();
                    savePasswordAction(ERROR_PREFIX_VALID);
                } else {
//                    KeyStrokeLogger.getInstance().clearKeyStrokeList();
//                    et_password.setText("");
                    savePasswordAction(ERROR_PREFIX_WRONG_TOUCH);
                    Toast.makeText(this, "Zu viele Tastenanschläge, bitte wiederholen", Toast.LENGTH_LONG).show();
                }
            } else {
//                KeyStrokeLogger.getInstance().clearKeyStrokeList();
//                et_password.setText("");
                savePasswordAction(ERROR_PREFIX_WRONG_KEYS);
                Toast.makeText(this, "Passwort falssch \nDeine Eingabe: " + actualPasswordStringValue, Toast.LENGTH_LONG).show();
            }
        } else {
            // Case: currentTask is the 'user created pw task' => no validation
            incrementProgressbar();
            savePasswordAction(ERROR_PREFIX_VALID);
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

    private void savePasswordAction(String errorPrefix) {
        final StudyConfigBean currentStudyTask = studyConfig.get(0);
        String path = KeyStrokeLogger.getTaskPath(currentStudyTask, pid);

        KeyStrokeLogger.getInstance().writeToCSVFile(this, path, errorPrefix);
        et_password.setText("");

        if (entryCounter >= currentStudyTask.getNumberOfReps()) {
            et_password.setEnabled(false);
            btn_savePassword.setEnabled(false);
            btn_nextTask.setEnabled(true);
        }
    }

    private void incrementProgressbar() {
        entryCounter += 1;
        setProgressCounter();
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