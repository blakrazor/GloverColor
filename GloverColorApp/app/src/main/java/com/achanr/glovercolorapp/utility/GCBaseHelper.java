package com.achanr.glovercolorapp.utility;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.activities.GCEnterCodeActivity;
import com.achanr.glovercolorapp.activities.GCSavedSetListActivity;
import com.achanr.glovercolorapp.activities.GCSettingsActivity;
import com.achanr.glovercolorapp.activities.GCWelcomeScreenActivity;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 * @created 2/16/16 4:38 PM
 */
public class GCBaseHelper implements NavigationView.OnNavigationItemSelectedListener {

    private Activity mContext;
    private FrameLayout mFrameLayout;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mToggle;
    private static int mPosition;
    private NavigationView mNavigationView;

    public GCBaseHelper(Activity parent) {
        mContext = parent;

        GCUtil.onActivityCreateSetTheme(mContext);
        mContext.setContentView(R.layout.navigation_drawer_layout);
        mFrameLayout = (FrameLayout) mContext.findViewById(R.id.content_frame);
    }

    public FrameLayout getFrameLayout() {
        return mFrameLayout;
    }

    public Toolbar setupToolbar(String title) {
        mToolbar = (Toolbar) mContext.findViewById(R.id.toolbar);
        ((AppCompatActivity) mContext).setSupportActionBar(mToolbar);
        ((AppCompatActivity) mContext).getSupportActionBar().setTitle(title);
        setCustomTitle(title);

        DrawerLayout drawer = (DrawerLayout) mContext.findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                mContext, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = (NavigationView) mContext.findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(mPosition);
        return mToolbar;
    }

    public void setCustomTitle(String title) {
        ((TextView) mContext.findViewById(R.id.toolbar_title)).setText(title);
    }

    public void setPosition(int position) {
        mPosition = position;
        mNavigationView.setCheckedItem(mPosition);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home && mPosition != R.id.nav_home) {
            mPosition = R.id.nav_home;
            Intent intent = new Intent(mContext, GCWelcomeScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        } else if (id == R.id.nav_saved_color_sets && mPosition != R.id.nav_saved_color_sets) {
            mPosition = R.id.nav_saved_color_sets;
            Intent intent = new Intent(mContext, GCSavedSetListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        } else if (id == R.id.nav_enter_code && mPosition != R.id.nav_enter_code) {
            mPosition = R.id.nav_enter_code;
            Intent intent = new Intent(mContext, GCEnterCodeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        } else if (id == R.id.nav_settings && mPosition != R.id.nav_settings) {
            mPosition = R.id.nav_settings;
            Intent intent = new Intent(mContext, GCSettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) mContext.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
