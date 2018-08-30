package com.android.inputmethod.ebmStudy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.inputmethod.ebmStudy.etc.StudyConfigBean;
import com.android.inputmethod.ebmStudy.etc.StudyConstants;
import com.android.inputmethod.ebmStudy.ui.dialogs.ExplainNotationDialog;
import com.android.inputmethod.latin.R;

import java.util.ArrayList;

public class StudyGeneralExplanationActivity extends StudyAbstractActivity implements View.OnClickListener{

    private String pid;
    private ArrayList<StudyConfigBean> studyConfig;
    private Button btn_startTaskExplanation;
    private TextView tv_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_general_explanation);

        btn_startTaskExplanation = findViewById(R.id.btnStart);
        btn_startTaskExplanation.setOnClickListener(this);

        tv_description = findViewById(R.id.textViewGeneralDescription);
        tv_description.setOnClickListener(this);

        getExtras();
    }

    private void getExtras() {
        Intent intent = getIntent();
        this.pid = intent.getStringExtra(StudyConstants.INTENT_PID);
        if (pid == null || pid.equals("")) {
            Toast.makeText(this, "Keine ID gesetzt", Toast.LENGTH_LONG).show();
            pid = "noPid";
        }

        Bundle bundle = getIntent().getExtras();
        this.studyConfig = bundle.getParcelableArrayList(StudyConstants.INTENT_CONFIG);
        if (this.studyConfig == null || this.studyConfig.size() == 0) {
            Toast.makeText(this, "Keine oder leere Config", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        if(view == btn_startTaskExplanation) {
            launchExplainTaskActivity();
        } else if (view == tv_description) {
            ExplainNotationDialog end = new ExplainNotationDialog(this, studyConfig.get(studyConfig.size() - 4).getPwTask());
            end.show();
        }
    }

    private void launchExplainTaskActivity() {
        Intent intent;
        if (StudyConstants.IS_PRE_STUDY_ACTIVE) {
            intent = new Intent(this, StudyPreStudyActivity.class);
        } else {
            intent = new Intent(this, StudyExplainTaskActivity.class);
        }
        intent.putExtra(StudyConstants.INTENT_PID, pid);
        intent.putExtra(StudyConstants.INTENT_TASK_ID, StudyConstants.TASK_ID_INITIAL);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(StudyConstants.INTENT_CONFIG, studyConfig);
        intent.putExtras(bundle);

        startActivity(intent);
        this.finish();
    }
}