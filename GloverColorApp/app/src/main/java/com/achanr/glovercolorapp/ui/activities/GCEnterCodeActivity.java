package com.achanr.glovercolorapp.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.models.GCChip;
import com.achanr.glovercolorapp.models.GCMode;
import com.achanr.glovercolorapp.models.GCPoweredColor;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.common.GCChipUtil;
import com.achanr.glovercolorapp.common.GCModeUtil;
import com.achanr.glovercolorapp.common.GCUtil;

import java.util.ArrayList;

public class GCEnterCodeActivity extends GCBaseActivity {

    private TextInputLayout mEnterCodeEditText;
    private Button mSubmitCodeButton;
    private Context mContext;

    private boolean isTextEntered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_enter_code, mFrameLayout);
        setupToolbar(getString(R.string.title_enter_code));
        mContext = this;

        mEnterCodeEditText = (TextInputLayout) findViewById(R.id.edit_text_enter_code);
        mSubmitCodeButton = (Button) findViewById(R.id.submit_code_button);

        updateButton();

        mEnterCodeEditText.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && isTextEntered) {
                    submitAction();
                    return true;
                }
                return false;
            }
        });

        mEnterCodeEditText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    isTextEntered = true;
                } else {
                    isTextEntered = false;
                }
                updateButton();
            }
        });

        mSubmitCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTextEntered) {
                    submitAction();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setPosition(R.id.nav_enter_code);
    }

    private void submitAction() {
        String setString = mEnterCodeEditText.getEditText().getText().toString().trim();
        GCSavedSet newSet = convertStringToSavedSet(setString);
        if (newSet != null) {
            mEnterCodeEditText.getEditText().setText("");
            Intent intent = new Intent(mContext, GCSavedSetListActivity.class);
            intent.putExtra(GCSavedSetListActivity.FROM_NAVIGATION, GCEnterCodeActivity.class.getName());
            intent.putExtra(GCSavedSetListActivity.NEW_SET_KEY, newSet);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else {
            showErrorDialog(getString(R.string.error_invalid_code));
        }
    }

    private GCSavedSet convertStringToSavedSet(String setString) {
        GCSavedSet newSet = new GCSavedSet();

        if (setString.trim().isEmpty()) {
            return null;
        }

        String[] splitString = setString.split(GCUtil.BREAK_CHARACTER_FOR_SHARING);
        if (splitString.length < 3) {
            return null;
        }

        String title = splitString[0];
        String chipsetString = splitString[1];
        String shortenedColorString = splitString[2];
        String mode = splitString[3];

        ArrayList<GCPoweredColor> newColorList = GCUtil.convertShortenedColorStringToColorList(shortenedColorString);
        GCChip chipSet = GCChipUtil.getChipUsingTitle(chipsetString);
        GCMode newMode = GCModeUtil.getModeUsingTitle(mode);

        newSet.setTitle(title);
        newSet.setColors(newColorList);
        newSet.setChipSet(chipSet);
        newSet.setMode(newMode);

        return newSet;
    }

    private void updateButton() {
        if (isTextEntered) {
            mSubmitCodeButton.setEnabled(true);
            mSubmitCodeButton.setClickable(true);
        } else {
            mSubmitCodeButton.setEnabled(false);
            mSubmitCodeButton.setClickable(false);
        }
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.error))
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
