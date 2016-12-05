package com.achanr.glovercolorapp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.GCChipUtil;
import com.achanr.glovercolorapp.common.GCModeUtil;
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.models.GCChip;
import com.achanr.glovercolorapp.models.GCMode;
import com.achanr.glovercolorapp.models.GCPoweredColor;
import com.achanr.glovercolorapp.models.GCSavedSet;
import com.achanr.glovercolorapp.ui.viewHolders.GCEnterCodeViewHolder;

import java.util.ArrayList;

public class GCEnterCodeActivity extends GCBaseActivity {

    private TextInputLayout mEnterCodeEditText;
    private Button mSubmitCodeButton;
    private Button mClearTextButton;

    private boolean isTextEntered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar(getString(R.string.title_enter_code));

        updateButton();
        setListeners();

        if (getIntent() != null) {
            Intent intent = getIntent();
            Uri data = intent.getData();
            if (data != null) {
                String matchString = "entercode/";
                String setString = data.toString().substring(data.toString().indexOf(matchString) + matchString.length());
                if (!setString.trim().isEmpty()) {
                    mEnterCodeEditText.getEditText().setText(setString);
                    Toast.makeText(this, R.string.prefill_share_code_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.prefill_share_code_failure, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void setupContentLayout() {
        View view = getLayoutInflater().inflate(R.layout.activity_enter_code, mFrameLayout);
        GCEnterCodeViewHolder viewHolder = new GCEnterCodeViewHolder(view);
        mEnterCodeEditText = viewHolder.getEnterCodeEditText();
        mSubmitCodeButton = viewHolder.getSubmitCodeButton();
        mClearTextButton = viewHolder.getClearTextButton();
    }

    private void setListeners() {
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
                isTextEntered = s.length() > 0;
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

        mClearTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEnterCodeEditText.getEditText().setText("");
                isTextEntered = false;
                updateButton();
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
            Intent intent = new Intent(this, GCSavedSetListActivity.class);
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
        if (splitString.length < 4) {
            return null;
        }

        String title = splitString[0];
        String chipsetString = splitString[1];
        String shortenedColorString = splitString[2];
        String mode = splitString[3];

        ArrayList<GCPoweredColor> newColorList = GCUtil.convertShortenedColorStringToColorList(shortenedColorString);
        GCChip chipSet = GCChipUtil.getChipUsingTitle(chipsetString.replace("_", " "));
        GCMode newMode = GCModeUtil.getModeUsingTitle(this, mode.replace("_", " "));
        if (newColorList == null || chipSet == null || newMode == null) {
            return null;
        }

        String customColorString = "";
        if (splitString.length > 4) {
            customColorString += splitString[4];
            ArrayList<int[]> customColorArray = GCUtil.parseCustomColorShareString(customColorString);
            if (customColorArray == null) {
                return null;
            }
            newSet.setCustomColors(customColorArray);
        }

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
        new AlertDialog.Builder(this)
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
