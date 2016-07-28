package cyberknight.android.project.AccountManagement;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import cyberknight.android.project.R;

public class AccountActivity extends AppCompatActivity implements ReloadFragment{

    String fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        Intent i = getIntent();
        fragment = i.getStringExtra("Fragment");
        setTitle(fragment);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        switch(fragment){
            case "Accounts":
                Fragment fragment = new AccountFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_fragment, fragment).commit();
                break;
            case "Budget":
                fragment = new BudgetFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_fragment, fragment).commit();
                break;
            default:
                Toast.makeText(this,"Error occured",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void reloadFragment() {
        Fragment fragment = new AccountFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_fragment, fragment).commit();
    }
}
