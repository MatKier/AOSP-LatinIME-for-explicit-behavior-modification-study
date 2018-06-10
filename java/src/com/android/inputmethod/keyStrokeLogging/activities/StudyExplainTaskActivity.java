package com.android.inputmethod.keyStrokeLogging.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.inputmethod.keyStrokeLogging.etc.StudyConstants;
import com.android.inputmethod.latin.R;

public class StudyExplainTaskActivity extends Activity implements View.OnClickListener {

    private Button btnStartTask;
    private String pid;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_explain_task);

        btnStartTask = findViewById(R.id.btn_startTask);
        btnStartTask.setOnClickListener(this);
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
    public void onClick(View view) {
        Intent intent = new Intent(this, StudyMainActivity.class);
        intent.putExtra(StudyConstants.INTENT_PID, "");
        startActivity(intent);
        //this.finish();
    }

    @Override
    public void onBackPressed() {
    }
}
