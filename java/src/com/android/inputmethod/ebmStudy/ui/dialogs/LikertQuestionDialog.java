package com.android.inputmethod.ebmStudy.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.inputmethod.ebmStudy.etc.StudyConfigBean;
import com.android.inputmethod.ebmStudy.keyStrokeLogging.KeyStrokeLogger;
import com.android.inputmethod.latin.R;

public class LikertQuestionDialog extends Dialog implements View.OnClickListener {

    private StudyConfigBean currentTask;
    private String pid;
    private TextView tv_question1;
    private TextView tv_question2;
    private SeekBar sb_anserwer1;
    private SeekBar sb_anserwer2;
    private Button btn_enter;


    public LikertQuestionDialog(@NonNull Context context, StudyConfigBean currentTask, String pid) {
        super(context, false, null);
        this.currentTask = currentTask;
        this.pid = pid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_likert_questions);

        tv_question1 = findViewById(R.id.tv_question1);
        tv_question2 = findViewById(R.id.tv_question2);
        sb_anserwer1 = findViewById(R.id.sb_answer1);
        sb_anserwer2 = findViewById(R.id.sb_answer2);
        btn_enter = findViewById(R.id.btn_enter);
        btn_enter.setOnClickListener(this);

        currentTask.getGroupName();
    }

    @Override
    public void onClick(View v) {
        int answer1 = sb_anserwer1.getProgress() + 1;
        int answer2 = sb_anserwer2.getProgress() + 1;

        String filePath = KeyStrokeLogger.getTaskGroupPath(currentTask, pid);

        this.dismiss();
    }
}
