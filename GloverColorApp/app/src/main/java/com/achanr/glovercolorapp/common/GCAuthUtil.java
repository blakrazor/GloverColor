package com.achanr.glovercolorapp.common;

import android.app.Activity;

import com.achanr.glovercolorapp.BuildConfig;
import com.achanr.glovercolorapp.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

/**
 * @author Andrew Chanrasmi on 10/21/16
 */

public class GCAuthUtil {

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static boolean isCurrentUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static void startLoginActivity(Activity activity) {
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setProviders(
                                AuthUI.EMAIL_PROVIDER,
                                AuthUI.GOOGLE_PROVIDER,
                                AuthUI.FACEBOOK_PROVIDER)
                        .setTheme(R.style.DefaultTheme)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .build(),
                RC_SIGN_IN
        );
    }

    public static void logOut(final Activity activity, OnCompleteListener<Void> onCompleteListener) {
        AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener(onCompleteListener);
    }
}