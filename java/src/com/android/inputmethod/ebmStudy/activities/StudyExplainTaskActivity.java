package com.android.inputmethod.ebmStudy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.inputmethod.ebmStudy.etc.StudyConfigBean;
import com.android.inputmethod.ebmStudy.keyStrokeLogging.KeyStrokeDataBean;
import com.android.inputmethod.ebmStudy.keyStrokeLogging.KeyStrokeLogger;
import com.android.inputmethod.ebmStudy.etc.KeyStrokeVisualizerView;
import com.android.inputmethod.ebmStudy.etc.StudyConstants;
import com.android.inputmethod.latin.R;

import java.util.ArrayList;
import java.util.List;

public class StudyExplainTaskActivity extends StudyAbstractActivity implements View.OnClickListener {

    private Button btnStartTask;
    private Button btnClearField;
    private EditText et_trainingField;
    private TextView tv_description;
    private TextView tv_title;

    private KeyStrokeVisualizerView pwTaskView;
    private KeyStrokeVisualizerView ksvView;

    private String pid;
    private ArrayList<StudyConfigBean> studyConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_explain_task);

        btnStartTask = findViewById(R.id.btn_startTask);
        btnStartTask.setOnClickListener(this);
        btnClearField = findViewById(R.id.btn_clearField);
        btnClearField.setOnClickListener(this);
        et_trainingField = findViewById(R.id.et_trainingField);
        tv_description = findViewById(R.id.tv_description);
        tv_title = findViewById(R.id.tv_title);

        pwTaskView = findViewById(R.id.pwTaskDisplay);
        pwTaskView.setOnClickListener(this);
        ksvView = findViewById(R.id.keyStrokeBiometricsDisplay);

        et_trainingField.addTextChangedListener(new TextWatcher() {
            int charCount = 0;
            boolean isFieldResettable = true;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                charCount = et_trainingField.getText().length();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (et_trainingField.getText().length() > charCount) {
                    List<KeyStrokeDataBean> lastKeyStrokes = KeyStrokeLogger.getInstance().getLastKeyStrokes(8);
                    ksvView.setKeyStrokeList(lastKeyStrokes);
                    ksvView.invalidate();
                    isFieldResettable = true;
                } else {
                    if(isFieldResettable) {
                        isFieldResettable = false;
                        et_trainingField.setText("");
                    }
                    charCount = 0;
                    KeyStrokeLogger.getInstance().clearKeyStrokeList();
                    ksvView.setKeyStrokeList(null);
                    ksvView.invalidate();
                }
            }
        });
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
        tv_title.setText("Aufgabe: " + studyConfig.get(0).getTaskId());
        tv_description.setText("Geben Sie das untenstehende Passwort " + studyConfig.get(0).getNumberOfReps() + "-mal entsprechend seiner Notation ein.");
        btnStartTask.setText("Aufgabe " + studyConfig.get(0).getTaskId() + " starten");
        pwTaskView.setKeyStrokeList(studyConfig.get(0).getPwTask());
        pwTaskView.invalidate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStudyInfoFromExtras();
        setUiElementsToCurrentTask();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnClearField)) {
            clearFields();
        } else if (view.equals(btnStartTask)) {
            clearFields();
            launchActualTask();
        } else if (view.equals(pwTaskView)) {
            // TODO launch explanation dialog
        }
    }

    private void clearFields() {
        et_trainingField.setText("");
        KeyStrokeLogger.getInstance().clearKeyStrokeList();

        ksvView.setKeyStrokeList(null);
        ksvView.invalidate();
    }

    private void launchActualTask() {
        Intent intent = new Intent(this, StudyMainActivity.class);
        intent.putExtra(StudyConstants.INTENT_PID, pid);;
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(StudyConstants.INTENT_CONFIG, studyConfig);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}