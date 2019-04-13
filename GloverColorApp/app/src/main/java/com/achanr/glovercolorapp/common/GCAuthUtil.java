package com.achanr.glovercolorapp.common;

import android.app.Activity;

import com.achanr.glovercolorapp.BuildConfig;
import com.achanr.glovercolorapp.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

/**
 * @author Andrew Chanrasmi on 10/21/16
 */

public class GCAuthUtil {

    // Choose an arbitrary request code value
    public static final int RC_SIGN_IN = 123;

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static boolean isCurrentUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static void startLoginActivity(Activity activity) {
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName("com.achanr.glovercolorapp", /*installIfNotAvailable*/true, /*minimumVersion*/null)
                .setHandleCodeInApp(true)
                .setUrl("glovercolorfirebase.firebaseapp.com") // This URL needs to be whitelisted
                .build();

        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                new AuthUI.IdpConfig.EmailBuilder()
                                        .enableEmailLinkSignIn()
                                        .setActionCodeSettings(actionCodeSettings)
                                        .build()))
                        .setTheme(R.style.DefaultTheme)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setTosAndPrivacyPolicyUrls("https://sites.google.com/view/glovercolor/terms-and-conditions",
                                "https://sites.google.com/view/glovercolor/privacy-policy")
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