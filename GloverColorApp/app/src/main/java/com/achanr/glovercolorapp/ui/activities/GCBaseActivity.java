package com.achanr.glovercolorapp.ui.activities;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.common.GCAuthUtil;
import com.achanr.glovercolorapp.common.GCOnlineDatabaseUtil;
import com.achanr.glovercolorapp.common.GCUtil;
import com.achanr.glovercolorapp.ui.viewHolders.GCNavHeaderViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */

public abstract class GCBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected abstract void setupContentLayout();

    @BindView(R.id.content_frame)
    FrameLayout mFrameLayout;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    Toolbar mToolbar;
    private GCNavHeaderViewHolder mNavHeaderViewHolder;
    private static int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GCUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.navigation_drawer_layout);
        ButterKnife.bind(this);
        setupNavDrawerHeader();
        updateLoginView();
        checkForDrawerSwipe();
        setupContentLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfThemeCorrect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // user is signed in!
                GCOnlineDatabaseUtil.checkSyncStatus(this, new GCOnlineDatabaseUtil.OnCompletionHandler() {
                    @Override
                    public void onComplete() {
                        updateLoginView();
                        Toast.makeText(GCBaseActivity.this, R.string.login_successful, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // user is not signed in. Maybe just wait for the user to press
                // "sign in" again, or show a message
                Toast.makeText(GCBaseActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GCOnlineDatabaseUtil.SYNC_CONFLICT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                updateUIAfterSync();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(GCBaseActivity.this, R.string.sync_cancelled, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkIfThemeCorrect() {
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.themeName, outValue, true);
        String themeString = GCUtil.getCurrentTheme(this);
        if (!themeString.equals(outValue.string)) {
            GCUtil.refreshActivity(this);
        }
    }

    void setupToolbar(String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(title);
        setCustomTitle(title);

        ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                GCOnlineDatabaseUtil.checkSyncStatus(GCBaseActivity.this, null);
                updateSyncStatus();
            }
        };
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(mPosition);
    }

    void setCustomTitle(String title) {
        ((TextView) findViewById(R.id.toolbar_title)).setText(title);
    }

    void setPosition(int position) {
        mPosition = position;
        mNavigationView.setCheckedItem(mPosition);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login_logout) {
            loginOrLogout();
            return true;
        }
        if (id == R.id.nav_sync) {
            GCOnlineDatabaseUtil.syncToOnline(GCBaseActivity.this, new GCOnlineDatabaseUtil.OnCompletionHandler() {
                @Override
                public void onComplete() {
                    updateUIAfterSync();
                    updateSyncStatus();
                }
            });
            return true;
        }

        Intent intent;
        if (id == R.id.nav_home && mPosition != R.id.nav_home) {
            mPosition = R.id.nav_home;
            intent = new Intent(this, GCHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityTransition(intent);
        } else if (id == R.id.nav_saved_color_sets && mPosition != R.id.nav_saved_color_sets) {
            mPosition = R.id.nav_saved_color_sets;
            intent = new Intent(this, GCSavedSetListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityTransition(intent);
        } else if (id == R.id.nav_collections && mPosition != R.id.nav_collections) {
            mPosition = R.id.nav_collections;
            intent = new Intent(this, GCCollectionsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityTransition(intent);
        } else if (id == R.id.nav_enter_code && mPosition != R.id.nav_enter_code) {
            mPosition = R.id.nav_enter_code;
            intent = new Intent(this, GCEnterCodeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityTransition(intent);
        } else if (id == R.id.nav_settings && mPosition != R.id.nav_settings) {
            mPosition = R.id.nav_settings;
            intent = new Intent(this, GCSettingsActivity.class);
            startActivityTransition(intent);
            //overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /*public void showLeavingDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle("Leaving GloverColor")
                .setMessage("Are you sure you want to exit the application?")
                .setPositiveButton(mContext.getString(R.string.exit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.ic_warning_black_48dp)
                .show();
    }*/

    void startActivityTransition(final Intent intent) {
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (this instanceof GCSavedSetListActivity) {
                ((GCSavedSetListActivity) this).animateFab(false, new GCSavedSetListActivity.AnimationCompleteListener() {
                    @Override
                    public void onComplete() {
                        ((GCSavedSetListActivity) GCBaseActivity.this).animateListView(false, new GCSavedSetListActivity.AnimationCompleteListener() {

                            @Override
                            public void onComplete() {
                                startActivityMaterialDesignTransition(intent);
                            }
                        });
                    }
                });
            } else if (this instanceof GCCollectionsActivity) {
                ((GCCollectionsActivity) this).animateFab(false, new GCCollectionsActivity.AnimationCompleteListener() {
                    @Override
                    public void onComplete() {
                        ((GCCollectionsActivity) GCBaseActivity.this).animateListView(false, new GCCollectionsActivity.AnimationCompleteListener() {

                            @Override
                            public void onComplete() {
                                startActivityMaterialDesignTransition(intent);
                            }
                        });
                    }
                });
            } else {
                startActivityMaterialDesignTransition(intent);
            }
        } else {
            // Implement this feature without material design
            startActivity(intent);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startActivityMaterialDesignTransition(Intent intent) {
        if (mPosition == R.id.nav_settings) {
            getWindow().setExitTransition(new Slide());
        } else {
            getWindow().setExitTransition(new Explode());
        }
        @SuppressWarnings("unchecked") ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivity(intent, options.toBundle());
    }

    private void loginOrLogout() {
        if (GCAuthUtil.isCurrentUserLoggedIn()) {
            //Currently logged in, so log out
            GCAuthUtil.logOut(GCBaseActivity.this, new OnCompleteListener<Void>() {
                public void onComplete(@NonNull Task<Void> task) {
                    // user is now signed out
                    updateLoginView();
                    Toast.makeText(GCBaseActivity.this, R.string.logout_successful, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //Currently not logged in, so log in
            GCAuthUtil.startLoginActivity(GCBaseActivity.this);
        }

    }

    private void updateLoginView() {
        Menu navMenu = mNavigationView.getMenu();
        if (GCAuthUtil.isCurrentUserLoggedIn()) {
            //User currently logged in
            mNavHeaderViewHolder.setUserLoginVisibility(true);
            navMenu.findItem(R.id.nav_login_logout).setTitle(R.string.logout);
            navMenu.findItem(R.id.nav_sync).setVisible(true);

            FirebaseUser currentUser = GCAuthUtil.getCurrentUser();
            if (currentUser.getPhotoUrl() != null) {
                mNavHeaderViewHolder.setProfilePictureImage(currentUser.getPhotoUrl());
            }
            String username = "N/A";
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                username = currentUser.getDisplayName();
            } else if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
                username = currentUser.getEmail();
            }
            mNavHeaderViewHolder.setUsernameText(username);
            updateSyncStatus();
        } else {
            //User is not logged in
            mNavHeaderViewHolder.setUserLoginVisibility(false);
            navMenu.findItem(R.id.nav_login_logout).setTitle(getString(R.string.login));
            navMenu.findItem(R.id.nav_sync).setVisible(false);
        }
    }

    private void updateSyncStatus() {
        switch (GCOnlineDatabaseUtil.CurrentSyncStatus) {
            case Unavailable:
                mNavHeaderViewHolder.setSyncStatusText(getString(R.string.sync_status_unavailable));
                break;
            case OutOfSync:
                mNavHeaderViewHolder.setSyncStatusText(getString(R.string.sync_status_out_of_sync));
                break;
            case Synced:
                mNavHeaderViewHolder.setSyncStatusText(getString(R.string.sync_status_synced));
                break;
        }
    }

    private void updateUIAfterSync() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateSyncStatus();
                if (GCBaseActivity.this instanceof GCSavedSetListActivity) {
                    ((GCSavedSetListActivity) GCBaseActivity.this).refreshList();
                } else if (GCBaseActivity.this instanceof GCCollectionsActivity) {
                    ((GCCollectionsActivity) GCBaseActivity.this).refreshList();
                }
            }
        });
    }

    private void checkForDrawerSwipe() {
        if (this instanceof GCHomeActivity
                || this instanceof GCSavedSetListActivity
                || this instanceof GCCollectionsActivity
                || this instanceof GCEnterCodeActivity) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void setupNavDrawerHeader() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GCAuthUtil.isCurrentUserLoggedIn()) {
                    //Currently not logged in, so log in
                    GCAuthUtil.startLoginActivity(GCBaseActivity.this);
                } else {
                    //User is logged in
                    if (GCOnlineDatabaseUtil.CurrentSyncStatus == GCOnlineDatabaseUtil.SyncStatus.OutOfSync) {
                        //Out of sync
                        GCOnlineDatabaseUtil.syncToOnline(GCBaseActivity.this, new GCOnlineDatabaseUtil.OnCompletionHandler() {
                            @Override
                            public void onComplete() {
                                updateUIAfterSync();
                                updateSyncStatus();
                            }
                        });
                    }
                }
            }
        };

        mNavHeaderViewHolder = new GCNavHeaderViewHolder(this, mNavigationView.getHeaderView(0), onClickListener);
    }
}
