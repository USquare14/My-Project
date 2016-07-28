package cyberknight.android.project.AccountManagement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import cyberknight.android.project.DatabaseAndReaders.DbHelper;
import cyberknight.android.project.HomeScreen.MainActivity;
import cyberknight.android.project.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String SETTINGS_FILE = "Settings";
    private TextView deletrecord, backUp, restore;
    private Switch passwordSwitch, notificationSwitch;
    private LinearLayout setPassword, setNotifications;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        deletrecord = (TextView) findViewById(R.id.deleteDatabase);
        backUp = (TextView) findViewById(R.id.backup);
        restore = (TextView) findViewById(R.id.restore);
        passwordSwitch = (Switch) findViewById(R.id.swich_password);
        notificationSwitch = (Switch) findViewById(R.id.swich_notification);
        setPassword = (LinearLayout) findViewById(R.id.password);
        setNotifications = (LinearLayout) findViewById(R.id.notification);

        deletrecord.setOnClickListener(this);
        backUp.setOnClickListener(this);
        restore.setOnClickListener(this);
        setNotifications.setOnClickListener(this);
        setPassword.setOnClickListener(this);

        sharedPreferences = getSharedPreferences(SETTINGS_FILE,Context.MODE_PRIVATE);
        passwordSwitch.setChecked(sharedPreferences.getBoolean("passwordSet",false));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.deleteDatabase:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        (new DbHelper(MainActivity.applicationContext)).deleteAllRecords();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.setTitle("Delete All Record!");
                alertDialogBuilder.setIcon(getResources().getDrawable(R.drawable.ic_delete_dark));
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setMessage("Are you sure you want to delete all database?\nThis action is not undoable.");
                alertDialog.show();
                break;
            case R.id.password:
                sharedPreferences = getSharedPreferences(SETTINGS_FILE,Context.MODE_PRIVATE);
                if(sharedPreferences.getBoolean("passwordSet",false)){
                    passwordSwitch.setChecked(false);
                    sharedPreferences.edit().putBoolean("passwordSet",false).apply();
                }
                else {
                    passwordSwitch.setChecked(true);
                    sharedPreferences.edit().putBoolean("passwordSet",true).apply();

                    View promptsView = LayoutInflater.from(this).inflate(R.layout.dialog_password, null);
                    alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setView(promptsView);
                    final EditText userInput = (EditText) promptsView.findViewById(R.id.editPassword);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            sharedPreferences.edit().putString("password",userInput.getText().toString()).apply();
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            passwordSwitch.setChecked(false);
                                            sharedPreferences.edit().putBoolean("passwordSet",false).apply();
                                        }
                                    });

                    alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
                break;
            case R.id.notification:
                alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialogBuilder.setTitle("Work is under progress!");
                //alertDialogBuilder.setIcon(getResources().getDrawable(R.drawable.));
                alertDialog = alertDialogBuilder.create();
                alertDialog.setMessage("Sorry, This feature is unavailable for now.\nThe feature will be included in next update.");
                alertDialog.show();
                break;
            case R.id.backup:alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialogBuilder.setTitle("Work is under progress!");
                //alertDialogBuilder.setIcon(getResources().getDrawable(R.drawable.));
                alertDialog = alertDialogBuilder.create();
                alertDialog.setMessage("Sorry, This feature is unavailable for now.\nThe feature will be included in next update.");
                alertDialog.show();
                break;
            case R.id.restore:alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialogBuilder.setTitle("Work is under progress!");
                //alertDialogBuilder.setIcon(getResources().getDrawable(R.drawable.));
                alertDialog = alertDialogBuilder.create();
                alertDialog.setMessage("Sorry, This feature is unavailable for now.\nThe feature will be included in next update.");
                alertDialog.show();
                break;
            default:
                break;
        }
    }
}
