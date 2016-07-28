package cyberknight.android.project.HomeScreen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cyberknight.android.project.AccountManagement.AboutUsActivity;
import cyberknight.android.project.AccountManagement.AnalysisActivity;
import cyberknight.android.project.AccountManagement.SettingsActivity;
import cyberknight.android.project.DatabaseAndReaders.DbHelper;
import cyberknight.android.project.AccountManagement.AccountActivity;
import cyberknight.android.project.DatabaseAndReaders.JsonReader;
import cyberknight.android.project.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecordScreenUpdater, SimpleGestureFilter.SimpleGestureListener{

    public static Context applicationContext;
    private LinearLayout drawerPane, navButtons[] = new LinearLayout[6], bottomBarHome, bottomBarSummary, bottomBarAnalysis;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private HomeFragment home;
    private String currentDate;
    private Fragment currentLoadedFragment;
    private SimpleGestureFilter detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.navDrawer);
        drawerPane = (LinearLayout) findViewById(R.id.drawerPane);

        navButtons[0] = (LinearLayout) findViewById(R.id.navHome);
        navButtons[1] = (LinearLayout) findViewById(R.id.navAccoout);
        navButtons[2] = (LinearLayout) findViewById(R.id.navAnalysis);
        navButtons[3] = (LinearLayout) findViewById(R.id.navBudget);
        navButtons[4] = (LinearLayout) findViewById(R.id.navSettings);
        navButtons[5] = (LinearLayout) findViewById(R.id.navAboutUs);

        bottomBarHome = (LinearLayout) findViewById(R.id.bottom_bar_home);
        bottomBarSummary = (LinearLayout) findViewById(R.id.bottom_bar_summary);
        bottomBarAnalysis = (LinearLayout) findViewById(R.id.bottom_bar_analysis);
        FrameLayout contentFrame = (FrameLayout) findViewById(R.id.content_frame);

        if(getSharedPreferences(SettingsActivity.SETTINGS_FILE,Context.MODE_PRIVATE).getBoolean("firstCheck",false) && getSharedPreferences(SettingsActivity.SETTINGS_FILE,Context.MODE_PRIVATE).getBoolean("passwordSet",false)) {
            View promptsView = LayoutInflater.from(this).inflate(R.layout.dialog_password, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setView(promptsView);
            final EditText userInput = (EditText) promptsView.findViewById(R.id.editPassword);
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    SharedPreferences sharedPreferences = getSharedPreferences(SettingsActivity.SETTINGS_FILE,Context.MODE_PRIVATE);
                                    if(sharedPreferences.getString("password","").equals(userInput.getText().toString())){
                                        dialog.dismiss();
                                        getSharedPreferences(SettingsActivity.SETTINGS_FILE,Context.MODE_PRIVATE).edit().putBoolean("firstCheck",false).commit();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"Wrong Password!",Toast.LENGTH_SHORT).show();
                                        userInput.setText("");
                                        finish();
                                    }
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
        applicationContext = getApplicationContext();
        DbHelper database = new DbHelper(getApplicationContext());

        try {
            ArrayList<String> accountNames;
            JsonReader tmp = new JsonReader(applicationContext);
            accountNames = tmp.getAccountsNames();

            for (int i = 0; i < accountNames.size(); i++) {
                database.addInitialAmount(i, accountNames.get(i), 0);
            }
        }catch (Error e){
            Log.d("MainActivity","Records Not Added");
        }

        detector = new SimpleGestureFilter(MainActivity.this, this);
        detector.setSwipeMaxDistance(500);
        detector.setSwipeMinVelocity(0);
        currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        home = new HomeFragment();

        home.setDateTo(currentDate);
        currentLoadedFragment = home;
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, home).commit();

        for(int i=0; i<6; i++) navButtons[i].setOnClickListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close){
            @Override
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
            @Override
            public void onDrawerClosed(View drawerView){
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        bottomBarHome.setOnClickListener(this);
        bottomBarSummary.setOnClickListener(this);
        bottomBarAnalysis.setOnClickListener(this);

        drawerPane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        try {
            getSupportActionBar().setElevation(2);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }catch(NullPointerException npe){
            Log.d("MainActivity","support action bar failed");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_calendar) {

            DialogFragment calendarFragment = new CalendarFragment();
            calendarFragment.setTargetFragment(currentLoadedFragment,0);
            calendarFragment.show(fragmentManager,"Calendar Fragment");

            return true;
        }else if (id == R.id.action_aboutUs) {
            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
            return true;
        }
        else if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    public String changeDate(String date, int numToAdd){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, numToAdd);
        date = sdf.format(c.getTime());
        return date;
    }

    @Override
    public void onClick(View v) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch(v.getId()){
            case R.id.bottom_bar_home:
                bottomBarHome.setAlpha(1f);
                bottomBarSummary.setAlpha(0.5f);
                bottomBarAnalysis.setAlpha(0.5f);
                home.setDateTo(currentDate);
                currentLoadedFragment = home;
                fragmentManager.beginTransaction().replace(R.id.content_frame, home).commit();
                break;
            case R.id.bottom_bar_summary:
                bottomBarHome.setAlpha(0.5f);
                bottomBarSummary.setAlpha(1f);
                bottomBarAnalysis.setAlpha(0.5f);
                SummaryFragment summaryFragment = new SummaryFragment();
                summaryFragment.setDateTo(currentDate);
                currentLoadedFragment = summaryFragment;
                fragmentManager.beginTransaction().replace(R.id.content_frame, summaryFragment).commit();
                break;
            case R.id.bottom_bar_analysis:
                bottomBarHome.setAlpha(0.5f);
                bottomBarSummary.setAlpha(0.5f);
                bottomBarAnalysis.setAlpha(1f);
                AnalysisFragment analysisFragment = new AnalysisFragment();
                analysisFragment.setDateTo(currentDate);
                currentLoadedFragment = analysisFragment;
                fragmentManager.beginTransaction().replace(R.id.content_frame, analysisFragment).commit();
                break;
            case R.id.navHome:
                home.setDateTo(currentDate);
                currentLoadedFragment = home;
                fragmentManager.beginTransaction().replace(R.id.content_frame, home).commit();
                setTitle("Home");
                break;
            case R.id.navAccoout:
                Intent i = new Intent(MainActivity.this,AccountActivity.class);
                i.putExtra("Fragment","Accounts");
                startActivity(i);
                break;
            case R.id.navAnalysis:
                i = new Intent(MainActivity.this,AnalysisActivity.class);
                startActivity(i);
                break;
            case R.id.navBudget:
                i = new Intent(MainActivity.this,AccountActivity.class);
                i.putExtra("Fragment","Budget");
                startActivity(i);
                break;
            case R.id.navSettings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            case R.id.navAboutUs:
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                break;
            default:
        }
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void updateScreenRecords() {

    }

    @Override
    public void setDateTo(String date) {
        currentDate = date;
    }

    @Override
    public void finish() {
        getSharedPreferences(SettingsActivity.SETTINGS_FILE,Context.MODE_PRIVATE).edit().putBoolean("firstCheck",true).commit();
        super.finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        Log.d("HomeFragment","\n\n\n\n\n-1");
        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                currentDate = changeDate(currentDate,-1);
                ((RecordScreenUpdater)currentLoadedFragment).setDateTo(currentDate);
                ((RecordScreenUpdater)currentLoadedFragment).updateScreenRecords();
                Log.d("HomeFragment","\n\n\n\n\n-1");
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                currentDate = changeDate(currentDate,1);
                ((RecordScreenUpdater)currentLoadedFragment).setDateTo(currentDate);
                ((RecordScreenUpdater)currentLoadedFragment).updateScreenRecords();
                Log.d("HomeFragment","\n\n\n\n\n-1");
                break;
        }
    }

    @Override
    public void onDoubleTap() {

    }
}