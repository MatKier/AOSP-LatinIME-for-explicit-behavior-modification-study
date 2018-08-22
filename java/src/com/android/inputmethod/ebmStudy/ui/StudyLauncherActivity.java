package com.android.inputmethod.ebmStudy.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.inputmethod.ebmStudy.etc.StudyConfigBean;
import com.android.inputmethod.ebmStudy.keyStrokeLogging.KeyStrokeLogger;
import com.android.inputmethod.ebmStudy.etc.StudyConstants;
import com.android.inputmethod.ebmStudy.etc.StudyXMLParser;
import com.android.inputmethod.latin.R;

import java.util.ArrayList;

public class StudyLauncherActivity extends StudyAbstractActivity implements View.OnClickListener {

    private EditText et_ParticipantId;
    private Button btn_saveParticipantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_launcher);

        et_ParticipantId = findViewById(R.id.et_participantId);
        btn_saveParticipantId = findViewById(R.id.btn_startStudy);
        btn_saveParticipantId.setOnClickListener(this);

        showInputChooserDialog();
    }

    private void showXmlExceptionDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("Error while parsing config XML")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Close App", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAndRemoveTask();
                    }
                })
                .create()
                .show();
    }

    private void showInputChooserDialog() {
        InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (im != null) {
            im.showInputMethodPicker();
        }
    }

    @Override
    public void onClick(View view) {
        String participantIdText = et_ParticipantId.getText().toString();
        if (!participantIdText.equals("")) {
            // Remove the pid from the Keystroke list
            KeyStrokeLogger.getInstance().clearKeyStrokeList();
            launchExplainStudyActivity(participantIdText);
        } else {
            Toast.makeText(this, "Bitte (gültige) TeilnehmerId angeben", Toast.LENGTH_LONG).show();
        }
    }

    private void launchExplainStudyActivity(String participantIdText) {
        Intent intent = new Intent(this, StudyGeneralExplanationActivity.class);
        intent.putExtra(StudyConstants.INTENT_PID, participantIdText);

        int xmlNo = Integer.parseInt(participantIdText) % 12;
        try {
            ArrayList<StudyConfigBean> studyConfig = StudyXMLParser.parseStudyConfig(getApplicationContext(), xmlNo);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(StudyConstants.INTENT_CONFIG, studyConfig);
            intent.putExtras(bundle);

            Toast.makeText(this, "Using config " + xmlNo, Toast.LENGTH_LONG).show();

            startActivity(intent);
            this.finish();
        } catch (Exception ex) {
            Log.e("XML Error", ex.toString());
            showXmlExceptionDialog(ex.toString());
        }
    }
}