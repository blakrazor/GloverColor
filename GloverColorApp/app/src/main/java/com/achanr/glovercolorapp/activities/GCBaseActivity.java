package com.achanr.glovercolorapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.utility.GCUtil;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */

public class GCBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected FrameLayout mFrameLayout;
    protected Toolbar mToolbar;
    protected ActionBarDrawerToggle mToggle;
    protected static int mPosition;
    protected NavigationView mNavigationView;
    private Context mContext;

    protected static final int CHANGE_SETTINGS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        GCUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.navigation_drawer_layout);
        mFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void setupToolbar(String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(title);
        setCustomTitle(title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, drawer, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(mPosition);
    }

    protected void setCustomTitle(String title) {
        ((TextView) findViewById(R.id.toolbar_title)).setText(title);
    }

    protected void setPosition(int position) {
        mPosition = position;
        mNavigationView.setCheckedItem(mPosition);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mPosition != R.id.nav_home
                    && mPosition != R.id.nav_settings) {
                Intent intent = new Intent(mContext, GCWelcomeScreenActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else if (isTaskRoot()) {
                showLeavingDialog();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_SETTINGS_REQUEST_CODE) {
            finish();
            startActivityTransition(new Intent(this, this.getClass()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = null;
        if (id == R.id.nav_home && mPosition != R.id.nav_home) {
            mPosition = R.id.nav_home;
            intent = new Intent(mContext, GCWelcomeScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityTransition(intent);
        } else if (id == R.id.nav_saved_color_sets && mPosition != R.id.nav_saved_color_sets) {
            mPosition = R.id.nav_saved_color_sets;
            intent = new Intent(mContext, GCSavedSetListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityTransition(intent);
        } else if (id == R.id.nav_enter_code && mPosition != R.id.nav_enter_code) {
            mPosition = R.id.nav_enter_code;
            intent = new Intent(mContext, GCEnterCodeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityTransition(intent);
        } else if (id == R.id.nav_settings && mPosition != R.id.nav_settings) {
            mPosition = R.id.nav_settings;
            intent = new Intent(mContext, GCSettingsActivity.class);
            startActivityForResult(intent, CHANGE_SETTINGS_REQUEST_CODE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showLeavingDialog() {
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
    }

    private void startActivityTransition(Intent intent) {
        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            /*supportFinishAfterTransition();
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, options.toBundle());*/
            finish();
            startActivity(intent);
        } else {
            // Implement this feature without material design
            finish();
            startActivity(intent);
        }
    }
}
