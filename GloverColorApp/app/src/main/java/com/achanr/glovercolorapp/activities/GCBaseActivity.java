package com.achanr.glovercolorapp.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.utility.GCBaseHelper;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */

public class GCBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected FrameLayout mFrameLayout;
    protected Toolbar mToolbar;

    private GCBaseHelper mBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseHelper = new GCBaseHelper(this);
        mFrameLayout = mBaseHelper.getFrameLayout();
    }

    protected void setupToolbar(String title) {
        mToolbar = mBaseHelper.setupToolbar(title);

    }

    protected void setCustomTitle(String title){
        mBaseHelper.setCustomTitle(title);
    }

    protected void setPosition(int position) {
        mBaseHelper.setPosition(position);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        mBaseHelper.onNavigationItemSelected(item);
        return true;
    }
}
