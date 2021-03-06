package com.android.inputmethod.ebmStudy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.inputmethod.ebmStudy.ui.dialogs.ExplainNotationDialog;
import com.android.inputmethod.ebmStudy.etc.StudyConfigBean;
import com.android.inputmethod.ebmStudy.keyStrokeLogging.KeyStrokeDataBean;
import com.android.inputmethod.ebmStudy.keyStrokeLogging.KeyStrokeLogger;
import com.android.inputmethod.ebmStudy.etc.keyStrokeVisualizer.KeyStrokeVisualizerView;
import com.android.inputmethod.ebmStudy.etc.StudyConstants;
import com.android.inputmethod.latin.R;

import java.util.ArrayList;
import java.util.List;

public class StudyExplainTaskActivity extends StudyAbstractActivity implements View.OnClickListener {

    private Button btnStartTask;
    private Button btnClearField;
    private EditText etTrainingField;
    private TextView tvDescription;
    private TextView tvTitle;
    private TextView tvBiometricsTitle;
    private TextView tvPwTaskTitle;
    private HorizontalScrollView horScrollView;
    private TextView tv_taskGroupDescription;
    private TextView tv_description2;

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
        etTrainingField = findViewById(R.id.et_trainingField);
        tvDescription = findViewById(R.id.tv_description);
        tv_description2 = findViewById(R.id.tv_description2);
        tvTitle = findViewById(R.id.tv_title);
        tvBiometricsTitle = findViewById(R.id.tv_biometrics_title);
        tvPwTaskTitle = findViewById(R.id.tvPwTaskTitle);
        horScrollView = findViewById(R.id.horScrollView);
        pwTaskView = findViewById(R.id.pwTaskDisplay);
        pwTaskView.setOnClickListener(this);
        ksvView = findViewById(R.id.keyStrokeBiometricsDisplay);
        tv_taskGroupDescription = findViewById(R.id.tv_taskGroupDescription);

        etTrainingField.addTextChangedListener(new TextWatcher() {
            int charCount = 0;
            boolean isFieldResettable = true;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                charCount = etTrainingField.getText().length();
                if (charCount == 0) {
                    KeyStrokeLogger.getInstance().clearKeyStrokeListExceptForLastEventPair();
                    ksvView.setKeyStrokeList(null);
                    ksvView.invalidate();
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (etTrainingField.getText().length() > charCount) {
                    List<KeyStrokeDataBean> lastKeyStrokes = KeyStrokeLogger.getInstance().getLastKeyStrokes(8);
                    ksvView.setKeyStrokeList(lastKeyStrokes);
                    ksvView.invalidate();
                    isFieldResettable = true;
                } else {
                    if (isFieldResettable) {
                        isFieldResettable = false;
                        //etTrainingField.setText("");
                        clearFields();
                    }
                    charCount = 0;

                    KeyStrokeLogger.getInstance().clearKeyStrokeList();
                    ksvView.setKeyStrokeList(null);
                    ksvView.invalidate();
                }
            }
        });

        etTrainingField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    clearFields();
                }
                return true;
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
        StudyConfigBean currentTask = studyConfig.get(0);

        tvTitle.setText("Aufgabe: " + currentTask.getTaskId());
        btnStartTask.setText("Aufgabe " + currentTask.getTaskId() + " starten");
        tv_taskGroupDescription.setText("Aufgabengruppe: " + currentTask.getGroupName());

        if (currentTask.isIntroductionGroup() && currentTask.getPwTask().size() != 0) {
            tvDescription.setText("Geben Sie das untenstehende Passwort " + currentTask.getNumberOfReps() + "-mal ein.");
            pwTaskView.setVisibility(View.GONE);
            ksvView.setVisibility(View.GONE);
            horScrollView.setVisibility(View.GONE);
            tvBiometricsTitle.setVisibility(View.GONE);
            tvPwTaskTitle.setText("Aufgaben Passwort: \n" + currentTask.getTaskPWString() + "\n");
        } else if (!currentTask.isIntroductionGroup() && currentTask.getPwTask().size() != 0) {
            tvDescription.setText("Geben Sie das untenstehende Passwort " + currentTask.getNumberOfReps() + "-mal entsprechend seiner Notation ein.");
            pwTaskView.setVisibility(View.VISIBLE);
            ksvView.setVisibility(View.VISIBLE);
            horScrollView.setVisibility(View.VISIBLE);
            tvBiometricsTitle.setVisibility(View.VISIBLE);
            tvPwTaskTitle.setText("Aufgaben Passwort: (Berühren für Erläuterung)");
            pwTaskView.setKeyStrokeList(currentTask.getPwTask());
            pwTaskView.invalidate();
        } else if (currentTask.getPwTask().size() == 0) {
            tvDescription.setText("Denken Sie sich ein eigenes 6-8 stelliges Verhaltensbiometrie-Passwort aus (nur Kleinbuchstaben).\n" +
                    "Passen Sie dabei mind. eine Eigenschaft an (offset, holdtime, flighttime, area).\n\n" +
                    "Sie können das Übungsfeld Nutzen, um die Eingabe Ihres Passwortes zu üben.\n\n" +
                    "Notieren Sie ihr Passwort auf einem Stück Papier.\n\n"+
                    "Wenn Sie bereit sind, drücken sie den Button 'Aufgabe Starten' und geben Sie dann Ihr selbst ausgedachtes Passwort " + currentTask.getNumberOfReps() +
                    "-mal auf die gleiche Art und Weise ein.");
            ksvView.setVisibility(View.VISIBLE);
            horScrollView.setVisibility(View.VISIBLE);
            tvBiometricsTitle.setVisibility(View.VISIBLE);
            tv_description2.setVisibility(View.GONE);
            pwTaskView.setVisibility(View.GONE);
            tvPwTaskTitle.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStudyInfoFromExtras();
        setUiElementsToCurrentTask();
    }

    @Override
    public void onClick(View view) {
        if (view == btnClearField) {
            clearFields();
        } else if (view == btnStartTask) {
            clearFields();
            launchActualTask();
        } else if (view == pwTaskView) {
            ExplainNotationDialog end = new ExplainNotationDialog(this, studyConfig.get(0).getPwTask());
            end.show();
        }
    }

    private void clearFields() {
        final StudyConfigBean currentStudyTask = studyConfig.get(0);
        String path = KeyStrokeLogger.getTaskPath(currentStudyTask, pid) + "/training";

        KeyStrokeLogger.getInstance().writeToCSVFile(this, path, "training");
        etTrainingField.setText("");

        ksvView.setKeyStrokeList(null);
        ksvView.invalidate();
    }

    private void launchActualTask() {
        Intent intent = new Intent(this, StudyTaskActivity.class);
        intent.putExtra(StudyConstants.INTENT_PID, pid);
        ;
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(StudyConstants.INTENT_CONFIG, studyConfig);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}