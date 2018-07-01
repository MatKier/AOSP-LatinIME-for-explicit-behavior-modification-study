package com.android.inputmethod.keyStrokeLogging.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.inputmethod.keyStrokeLogging.KeyStrokeDataBean;
import com.android.inputmethod.keyStrokeLogging.KeyStrokeLogger;
import com.android.inputmethod.keyStrokeLogging.etc.KeyOffsetVisualizerView;
import com.android.inputmethod.keyStrokeLogging.etc.KeyStrokeVisualizerView;
import com.android.inputmethod.keyStrokeLogging.etc.StudyConstants;
import com.android.inputmethod.latin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Only to be used for PreStudy
 */
public class StudyPreStudyActivity extends StudyAbstractActivity implements View.OnClickListener {

    private Button btnClearField;
    private EditText et_trainingField;
    private TextView tv_lastKeyEventDisplay;

    private Spinner sp_selectPreStudyPw;
    private ImageView iv_preStudyPw;

    private KeyOffsetVisualizerView key_rect;
    private KeyStrokeVisualizerView ksvView;

    private String pid;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_pre_study);

        btnClearField = findViewById(R.id.btn_clearField);
        btnClearField.setOnClickListener(this);
        et_trainingField = findViewById(R.id.et_trainingField);
        tv_lastKeyEventDisplay = findViewById(R.id.tv_lastKeyEventDisplay);

        iv_preStudyPw = findViewById(R.id.iv_preStudyPw);

        sp_selectPreStudyPw = findViewById(R.id.sp_SelectPWPic);
        List<String> selectPwSpinnerArray = new ArrayList<String>();
        for (int i = 0; i <= 10; i ++) {
            selectPwSpinnerArray.add("pre_stud_pw_" + i);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, selectPwSpinnerArray);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_selectPreStudyPw.setAdapter(arrayAdapter);
        sp_selectPreStudyPw.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               iv_preStudyPw.setImageResource(getResources().getIdentifier(sp_selectPreStudyPw.getSelectedItem().toString(),
                       "drawable", getApplicationContext().getPackageName()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });

        key_rect = findViewById(R.id.key_rect);
        //fhtView = findViewById(R.id.keyStrokeBiometricsDisplay);

        ksvView = findViewById(R.id.keyStrokeBiometricsDisplay);

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

                    key_rect.setTouchMarkerCords(lastUp.getOffsetX(), lastUp.getOffsetY());
                    key_rect.invalidate();

                    ksvView.setKeyStrokeList(lastKeyStroke);
                    ksvView.invalidate();

                    String key = lastUp.getKeyValue();
                    String pressure = (lastUp.getPressure() >= 0.2) ? "Fest" : "Normal";
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

    @Override
    protected void onResume() {
        super.onResume();
        getStudyInfoFromExtras();
    }

    @Override
    public void onClick(View view) {
        String path = "/" + "PreStudy" + "/" + "ID_" + pid + "/" + sp_selectPreStudyPw.getSelectedItem().toString();
        KeyStrokeLogger.getInstance().writeToCSVFile(this, path);
        et_trainingField.setText("");

        tv_lastKeyEventDisplay.setText("Taste: N/A\nDruck: N/A");
        ksvView.setKeyStrokeList(null);
        ksvView.invalidate();
        key_rect.setTouchMarkerCords(Integer.MIN_VALUE, Integer.MIN_VALUE);
        key_rect.invalidate();
    }
}