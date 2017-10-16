package com.achanr.glovercolorapp.models;

import android.net.Uri;

import com.achanr.glovercolorapp.common.GCUtil;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

/**
 * @author Andrew Chanrasmi on 11/15/16
 */

public class GCUser implements Serializable {

    private String mDisplayName;
    private String mEmail;
    private String mPhotoUrl;
    private String mProviderId;
    private String mUid;
    private boolean mIsEmailVerified;

    public GCUser() {
        // Default constructor required for Firebase Database
    }

    private GCUser(String displayName, String email, Uri photoUrl, String providerId, String uid, boolean isEmailVerified) {
        mDisplayName = displayName;
        mEmail = email;
        mPhotoUrl = photoUrl != null ? photoUrl.toString() : "";
        mProviderId = providerId;
        mUid = uid;
        mIsEmailVerified = isEmailVerified;
    }

    public static GCUser convertFromFirebaseUser(FirebaseUser firebaseUser) {
        return new GCUser(
                GCUtil.hashStringUsingMD5(firebaseUser.getDisplayName()),
                GCUtil.hashStringUsingMD5(firebaseUser.getEmail()),
                firebaseUser.getPhotoUrl(),
                firebaseUser.getProviderId(),
                firebaseUser.getUid(),
                firebaseUser.isEmailVerified());
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getPhotoUrl() {
        return mPhotoUrl;
    }

    public String getProviderId() {
        return mProviderId;
    }

    public String getUid() {
        return mUid;
    }

    public boolean isEmailVerified() {
        return mIsEmailVerified;
    }
}
