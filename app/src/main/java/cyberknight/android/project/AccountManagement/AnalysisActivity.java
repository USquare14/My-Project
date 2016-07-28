package cyberknight.android.project.AccountManagement;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import cyberknight.android.project.HomeScreen.RecordScreenUpdater;
import cyberknight.android.project.R;

public class AnalysisActivity extends AppCompatActivity implements RecordScreenUpdater {

    private DrawerAnalysisFragment drawerAnalysisFragment;
    private PieChartFragment chartReport;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_analysis);

        drawerAnalysisFragment = new DrawerAnalysisFragment();
        chartReport = new PieChartFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.analysis_fragment,drawerAnalysisFragment).commit();

        date = drawerAnalysisFragment.getDate();
    }

    @Override
    public void updateScreenRecords() {
        if(date.length()==10){

            chartReport.setCurrent(date);
            chartReport.setReportOf("Date");
            Log.d("Date: ",date);
            FragmentManager fragmentManager = getSupportFragmentManager() ;
            fragmentManager.beginTransaction().replace(R.id.analysis_fragment,chartReport).commit();
        }
        else{

            chartReport.setCurrent(date);
            chartReport.setReportOf("Month");
            Log.d("Date: ",date);
            FragmentManager fragmentManager = getSupportFragmentManager() ;
            fragmentManager.beginTransaction().replace(R.id.analysis_fragment,chartReport).commit();

        }
    }

    @Override
    public void setDateTo(String date) {
        this.date = date;
    }
}
