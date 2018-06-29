package com.android.inputmethod.keyStrokeLogging.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.inputmethod.keyStrokeLogging.KeyStrokeDataBean;
import com.android.inputmethod.keyStrokeLogging.KeyStrokeLogger;
import com.android.inputmethod.keyStrokeLogging.etc.FlightHoldTimeVisualizerView;
import com.android.inputmethod.keyStrokeLogging.etc.KeyOffsetVisualizerView;
import com.android.inputmethod.keyStrokeLogging.etc.StudyConstants;
import com.android.inputmethod.latin.R;

import java.util.List;

public class StudyExplainTaskActivity extends StudyAbstractActivity implements View.OnClickListener {

    private Button btnStartTask;
    private Button btnClearField;
    private EditText et_trainingField;
    private TextView tv_description;
    private TextView tv_title;
    private TextView tv_lastKeyEventDisplay;

    private KeyOffsetVisualizerView key_rect;
    private FlightHoldTimeVisualizerView fhtView;

    private String pid;
    private int taskId;

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
        tv_lastKeyEventDisplay = findViewById(R.id.tv_lastKeyEventDisplay);

        key_rect = findViewById(R.id.key_rect);
        fhtView = findViewById(R.id.flightHoldTimeDisplay);

        et_trainingField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                List<KeyStrokeDataBean> lastKeyStroke = KeyStrokeLogger.getInstance().getLastKeyStrokes();
                if (lastKeyStroke != null && lastKeyStroke.size() >= 2) {
                    KeyStrokeDataBean lastUp = lastKeyStroke.get(lastKeyStroke.size() - 1);
                    KeyStrokeDataBean lastDown =  lastKeyStroke.get(lastKeyStroke.size() - 2);

                    key_rect.setTouchMarkerCords(lastUp.getOffsetX(), lastUp.getOffsetY());
                    key_rect.invalidate();

                    fhtView.setKeyStrokeList(lastKeyStroke);
                    fhtView.invalidate();

                    String key = lastUp.getKeyValue();
                    String holdTime = lastUp.getHoldTime() + "";
                    String flightTime = (lastDown.getFlightTime()  == -1) ? "0" : lastDown.getFlightTime() + "";
                    String pressure = (lastUp.getPressure() >= 0.2) ? "Fest" : "Normal";
                    //tv_lastKeyEventDisplay.setText("Taste: " + key + "\nHold Time: " + holdTime + " ms\nFlight Time: " + flightTime +" ms\nDruck: " + pressure);#
                    tv_lastKeyEventDisplay.setText("Taste: " + key +"\nDruck: " + pressure);
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
        this.taskId = intent.getIntExtra(StudyConstants.INTENT_TASK_ID, -1);
        if (this.taskId == -1) {
            Toast.makeText(this, "Keine Task-ID gesetzt", Toast.LENGTH_LONG).show();
        }
    }

    private void setUiElementsToCurrentTask() {
        tv_title.setText(getResources().getIdentifier("task_title_" + taskId, "string", this.getPackageName()));
        tv_description.setText(getResources().getIdentifier("task_desc_" + taskId, "string", this.getPackageName()));
        btnStartTask.setText(getResources().getIdentifier("task_button_label_" + taskId, "string", this.getPackageName()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStudyInfoFromExtras();
        setUiElementsToCurrentTask();
    }

    @Override
    public void onClick(View view) {
        et_trainingField.setText("");
        KeyStrokeLogger.getInstance().clearKeyStrokeList();

        tv_lastKeyEventDisplay.setText("Taste: N/A\nDruck: N/A");
        fhtView.setKeyStrokeList(null);
        fhtView.invalidate();
        key_rect.setTouchMarkerCords(Integer.MIN_VALUE, Integer.MIN_VALUE);
        key_rect.invalidate();

        if (view.equals(btnStartTask)) {
            launchActualTask();
        }
    }

    private void launchActualTask() {
        Intent intent = new Intent(this, StudyMainActivity.class);
        intent.putExtra(StudyConstants.INTENT_PID, pid);
        intent.putExtra(StudyConstants.INTENT_TASK_ID, taskId);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}