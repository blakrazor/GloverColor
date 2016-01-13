package com.achanr.glovercolorapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.achanr.glovercolorapp.R;
import com.achanr.glovercolorapp.utility.EGCThemeEnum;
import com.achanr.glovercolorapp.utility.GCUtil;

/**
 * Glover Color App Project
 *
 * @author Andrew Chanrasmi
 */

public class GCBaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    protected FrameLayout mFrameLayout;
    protected Toolbar mToolbar;
    protected ActionBarDrawerToggle mToggle;
    protected static int mPosition;
    protected NavigationView mNavigationView;
    private Spinner mThemeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GCUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.navigation_drawer_layout);
        mFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
    }

    protected void setupToolbar(String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(title);
        ((TextView)findViewById(R.id.toolbar_title)).setText(title);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(mPosition);

        setupThemeSpinner();
    }

    protected void setupThemeSpinner(){
        mThemeSpinner = (Spinner) findViewById(R.id.theme_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.themes, R.layout.spinner_theme_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mThemeSpinner.setAdapter(adapter);
        mThemeSpinner.setSelection(getPositionOfStoredThemeEnum(), false);
        mThemeSpinner.setOnItemSelectedListener(this);
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
            super.onBackPressed();
            if(getSupportFragmentManager().getBackStackEntryCount() < 1){
                super.onBackPressed();
            }
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

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home && mPosition != R.id.nav_home) {
            mPosition = R.id.nav_home;
            Intent intent = new Intent(this, GCWelcomeScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_saved_color_sets && mPosition != R.id.nav_saved_color_sets) {
            mPosition = R.id.nav_saved_color_sets;
            Intent intent = new Intent(this, GCSavedSetListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.nav_enter_code && mPosition != R.id.nav_enter_code) {
            mPosition = R.id.nav_enter_code;
            Intent intent = new Intent(this, GCEnterCodeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedTheme = parent.getItemAtPosition(position).toString();
        String newString = selectedTheme.replace(" ", "_").toUpperCase();
        GCUtil.changeToTheme(this, EGCThemeEnum.valueOf(newString));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private int getPositionOfStoredThemeEnum(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String themeString = prefs.getString(GCUtil.THEME_KEY, EGCThemeEnum.BLUE_THEME.toString());
        EGCThemeEnum[] themeValues = EGCThemeEnum.values();
        int position = 0;
        for(EGCThemeEnum themeValue : themeValues){
            if(themeValue == EGCThemeEnum.valueOf(themeString)){
                return position;
            }
            position++;
        }
        return 0;
    }
}
