package com.achanr.glovercolorapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.utility.EGCColorEnum;
import com.achanr.glovercolorapp.utility.EGCModeEnum;
import com.achanr.glovercolorapp.utility.GCUtil;

import java.util.ArrayList;

public class GCEnterCodeActivity extends GCBaseActivity {

    private EditText mEnterCodeEditText;
    private Button mSubmitCodeButton;
    private Context mContext;

    private boolean isTextEntered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.activity_enter_code, mFrameLayout);
        setupToolbar(getString(R.string.title_enter_code));
        mContext = this;

        mEnterCodeEditText = (EditText) findViewById(R.id.edit_text_enter_code);
        mSubmitCodeButton = (Button) findViewById(R.id.submit_code_button);

        updateButton();

        mEnterCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && isTextEntered) {
                    submitAction();
                    return true;
                }
                return false;
            }
        });

        mEnterCodeEditText.addTextChangedListener(new TextWatcher() {
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
        String setString = mEnterCodeEditText.getText().toString().trim();
        GCSavedSet newSet = convertStringToSavedSet(setString);
        if (newSet != null) {
            Intent intent = new Intent(mContext, GCSavedSetListActivity.class);
            intent.putExtra(GCSavedSetListActivity.FROM_NAVIGATION, GCEnterCodeActivity.class.getName());
            intent.putExtra(GCSavedSetListActivity.NEW_SET_KEY, newSet);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
        String shortenedColorString = splitString[1];
        String mode = splitString[2];

        ArrayList<EGCColorEnum> newColorList = GCUtil.convertShortenedColorStringToColorList(shortenedColorString);
        EGCModeEnum newMode = EGCModeEnum.valueOf(mode);

        newSet.setTitle(title);
        newSet.setColors(newColorList);
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
