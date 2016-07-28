package cyberknight.android.project.HomeScreen;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


import cyberknight.android.project.DatabaseAndReaders.MyColorTemplate;
import cyberknight.android.project.DatabaseAndReaders.RecordDetails;
import cyberknight.android.project.DatabaseAndReaders.DbHelper;
import cyberknight.android.project.DatabaseAndReaders.JsonReader;
import cyberknight.android.project.R;

/**
 * Created by Parth on 02-07-2016.
 * CyberKnight apps
 */
public class AnalysisFragment extends Fragment implements View.OnClickListener,RecordScreenUpdater{

    private ArrayList<String> categories = new ArrayList<>();
    private JsonReader jsonReader = new JsonReader(MainActivity.applicationContext);
    private View view;
    private PieChart pieChart;
    private ArrayList<RecordDetails> foodRecords=new ArrayList<>();
    private TextView pageDate;
    private double income = 0;
    private double expense = 0;
    private Activity parentActivity;

    DbHelper dbHelper;
    private String date;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = activity;
    }

    public AnalysisFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_analysis, container, false);

        categories = jsonReader.getExpenseCategories();
        pageDate = (TextView) view.findViewById(R.id.analysis_pageDate);
        pieChart = (PieChart) view.findViewById(R.id.chart);
        ImageView prev = (ImageView) view.findViewById(R.id.analysis_previousDate);
        ImageView next = (ImageView) view.findViewById(R.id.analysis_nextDate);

        prev.setOnClickListener(this);
        next.setOnClickListener(this);

        updateScreenRecords();

        return view;
    }


    public String getDate() {
        return date;
    }

    public double getAmountOfCategory(String category){
        double amount = 0;

        for(int i=0;i<foodRecords.size();i++){
            if(foodRecords.get(i).getCategory().equals(category) && foodRecords.get(i).getTransaction().equals("Expense")){
                amount+=foodRecords.get(i).getAmount();
            }
        }
        Log.d("Amount",amount+"\n");
        return  amount;
    }

    @Override
    public void updateScreenRecords() {

        ArrayList<Entry> entries = new ArrayList<>();

        pieChart = (PieChart) view.findViewById(R.id.chart);
        pageDate.setText(date);
        dbHelper = new DbHelper(MainActivity.applicationContext);
        categories = jsonReader.getExpenseCategories();
        foodRecords = dbHelper.getAllAccountDetailsByDate(date);

        expense = 0;
        for (int i=0;i<categories.size();i++){
            double temp = getAmountOfCategory(categories.get(i));
            if(temp==0){
                categories.remove(i);
                i--;
            }
            else {
                entries.add(new Entry((float)temp,i));
            }
            expense+=temp;
        }

        income = 0;
        for(int i=0; i<foodRecords.size(); i++){
            if(foodRecords.get(i).getTransaction().equals("Income"))   income += foodRecords.get(i).getAmount();
        }

        Log.d("Records","\n\n\n\n"+foodRecords);
        Log.d("Records","\n\n\n\n"+entries);

        PieDataSet dataset = new PieDataSet(entries, "Categories");
        // creating labels

        Legend l=pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        l.setXEntrySpace(12f);
        l.setYEntrySpace(12f);
        l.setYOffset(0f);
        PieData data = new PieData(categories, dataset); // initialize Piedata
        pieChart.setData(data); //set data into chart
        pieChart.setDescription("");  // set the description

        dataset.setColors(MyColorTemplate.COLORFUL_COLORS); // set the color
        pieChart.animateY(600);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("Income: "+income+"\n"+"Expense: "+expense);

    }

    @Override
    public void setDateTo(String date) {
        this.date = date;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.analysis_previousDate)    date = changeDate(date,-1);
        else    date = changeDate(date,1);
        ((RecordScreenUpdater)parentActivity).setDateTo(date);
        pageDate.setText(date);
        updateScreenRecords();
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
}
