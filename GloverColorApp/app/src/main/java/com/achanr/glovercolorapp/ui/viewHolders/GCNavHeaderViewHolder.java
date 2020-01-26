package com.achanr.glovercolorapp.ui.viewHolders;

import android.content.Context;
import android.net.Uri;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Andrew Chanrasmi on 11/7/16
 */

public class GCNavHeaderViewHolder {

    @BindView(R.id.please_login_textview)
    TextView pleaseLoginTextview;

    @BindView(R.id.account_information_layout)
    LinearLayout accountInfoLayout;

    @BindView(R.id.navigation_header_sync_status_layout)
    LinearLayout syncStatusLayout;

    @BindView(R.id.navigation_header_tag_layout)
    LinearLayout taglineLayout;

    @BindView(R.id.current_user_textview)
    TextView usernameTextView;

    @BindView(R.id.sync_status_textview)
    TextView syncStatusTextView;

    @BindView(R.id.imageView)
    ImageView profilePictureImageView;

    private Context mContext;

    public GCNavHeaderViewHolder(Context context, View view, View.OnClickListener onClickListener) {
        mContext = context;
        ButterKnife.bind(this, view);
        view.setOnClickListener(onClickListener);
    }

    public void setUserLoginVisibility(boolean isUserLoggedIn) {
        if (isUserLoggedIn) {
            pleaseLoginTextview.setVisibility(View.GONE);
            taglineLayout.setVisibility(View.GONE);
            syncStatusLayout.setVisibility(View.VISIBLE);
            accountInfoLayout.setVisibility(View.VISIBLE);
        } else {
            pleaseLoginTextview.setVisibility(View.VISIBLE);
            taglineLayout.setVisibility(View.VISIBLE);
            syncStatusLayout.setVisibility(View.GONE);
            accountInfoLayout.setVisibility(View.GONE);
            profilePictureImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.glover_color_logo));
        }
    }

    public void setUsernameText(String usernameText) {
        usernameTextView.setText(usernameText);
    }

    public void setSyncStatusText(String syncStatusText) {
        syncStatusTextView.setText(syncStatusText);
    }

    public void setProfilePictureImage(Uri uri) {
        Picasso.with(mContext)
                .load(uri)
                .error(ContextCompat.getDrawable(mContext, R.drawable.glover_color_logo))
                .into(profilePictureImageView);
    }
}
