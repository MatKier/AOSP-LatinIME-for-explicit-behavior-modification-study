package com.android.inputmethod.ebmStudy.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

import com.android.inputmethod.ebmStudy.etc.keyStrokeVisualizer.KeyStrokeVisualizerView;
import com.android.inputmethod.ebmStudy.keyStrokeLogging.SimpleKeyStrokeDataBean;
import com.android.inputmethod.latin.R;

import java.util.List;

public class ExplainNotationDialog extends Dialog {

    private List<? extends SimpleKeyStrokeDataBean> keyStrokeList;

    public ExplainNotationDialog(Activity activity, List<? extends SimpleKeyStrokeDataBean> keyStrokeList) {
        super(activity);
        this.keyStrokeList = keyStrokeList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_explain_notation);
        KeyStrokeVisualizerView pwTaskView = findViewById(R.id.pwTaskDisplay);
        pwTaskView.setKeyStrokeList(keyStrokeList);
        pwTaskView.invalidate();
    }
}
