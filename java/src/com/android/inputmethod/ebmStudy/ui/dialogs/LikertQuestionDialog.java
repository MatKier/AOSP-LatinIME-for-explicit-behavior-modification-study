package com.android.inputmethod.ebmStudy.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.inputmethod.ebmStudy.etc.StudyConfigBean;
import com.android.inputmethod.ebmStudy.keyStrokeLogging.KeyStrokeLogger;
import com.android.inputmethod.latin.R;

import java.util.Arrays;

public class LikertQuestionDialog extends Dialog implements View.OnClickListener, View.OnTouchListener{

    private StudyConfigBean currentTask;
    private String pid;
    private TextView tv_question1;
    private TextView tv_question2;
    private TextView tv_question3;
    private SeekBar sb_anserwer1;
    private SeekBar sb_anserwer2;
    private SeekBar sb_anserwer3;
    private Button btn_enter;
    private TextView tv_desc;

    private boolean[] isQuestionAnswered = {false, false, false};
    private static final boolean[] ALLTRUE = {true, true, true};


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
        tv_question3 = findViewById(R.id.tv_question3);

        sb_anserwer1 = findViewById(R.id.sb_answer1);
        sb_anserwer2 = findViewById(R.id.sb_answer2);
        sb_anserwer3 = findViewById(R.id.sb_answer3);

        btn_enter = findViewById(R.id.btn_enter);
        btn_enter.setOnClickListener(this);

        tv_desc = findViewById(R.id.tv_description);
        tv_desc.setText("Aufgabengruppe: " + currentTask.getGroupName());

        sb_anserwer1.getThumb().mutate().setAlpha(0);
        sb_anserwer1.setOnTouchListener(this);
        sb_anserwer2.getThumb().mutate().setAlpha(0);
        sb_anserwer2.setOnTouchListener(this);
        sb_anserwer3.getThumb().mutate().setAlpha(0);
        sb_anserwer3.setOnTouchListener(this);

        currentTask.getGroupName();
    }

    @Override
    public void onClick(View v) {
        if(v == btn_enter) {
            saveAnswer();
        }
    }

    private void saveAnswer() {
        if (Arrays.equals(isQuestionAnswered, ALLTRUE)) {
            int answer1 = sb_anserwer1.getProgress() + 1;
            int answer2 = sb_anserwer2.getProgress() + 1;
            int answer3 = sb_anserwer3.getProgress() + 1;

            String filePath = KeyStrokeLogger.getTaskPath(currentTask, pid);

            String csvHeader = tv_question1.getText() + "; " + tv_question2.getText() + "; " + tv_question3.getText();
            String csvAnswer = answer1 + "; " + answer2 + "; " + answer3;

            String csvString = csvHeader + "\n" + csvAnswer;

            KeyStrokeLogger.writeLikertAnswersToCSVFile(filePath, "G" + currentTask.getSortingGroupId() + "_T" + currentTask.getTaskId(), csvString, getContext());

            this.dismiss();
        } else {
            Toast.makeText(getContext(), "Bitte alle Aussagen bewerten", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v == sb_anserwer1) {
            sb_anserwer1.getThumb().mutate().setAlpha(255);
            isQuestionAnswered[0] = true;
        } else if (v == sb_anserwer2) {
            sb_anserwer2.getThumb().mutate().setAlpha(255);
            isQuestionAnswered[1] = true;
        } else if (v == sb_anserwer3) {
            sb_anserwer3.getThumb().mutate().setAlpha(255);
            isQuestionAnswered[2] = true;
        }
        return false;
    }
}