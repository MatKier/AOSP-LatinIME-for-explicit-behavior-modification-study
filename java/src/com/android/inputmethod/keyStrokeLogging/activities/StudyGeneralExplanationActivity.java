package com.android.inputmethod.keyStrokeLogging.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.inputmethod.keyStrokeLogging.etc.StudyConstants;
import com.android.inputmethod.latin.R;

public class StudyGeneralExplanationActivity extends StudyAbstractActivity implements View.OnClickListener{

    private String pid;
    private Button btn_startTaskExplanation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_general_explanation);

        btn_startTaskExplanation = findViewById(R.id.btnStart);
        btn_startTaskExplanation.setOnClickListener(this);

        getPidFromIntent();
    }

    private void getPidFromIntent() {
        Intent intent = getIntent();
        this.pid = intent.getStringExtra(StudyConstants.INTENT_PID);
        if (pid == null || pid.equals("")) {
            Toast.makeText(this, "Keine ID gesetzt", Toast.LENGTH_LONG).show();
            pid = "noPid";
        }
    }

    @Override
    public void onClick(View view) {
        launchExplainTaskActivity();
    }

    private void launchExplainTaskActivity() {
        // TODO revert to StudyExplainTaskActivity after PreStudy
        //Intent intent = new Intent(this, StudyExplainTaskActivity.class);
        Intent intent = new Intent(this, StudyPreStudyActivity.class);
        intent.putExtra(StudyConstants.INTENT_PID, pid);
        intent.putExtra(StudyConstants.INTENT_TASK_ID, StudyConstants.TASK_ID_INITIAL);
        startActivity(intent);
        this.finish();
    }
}