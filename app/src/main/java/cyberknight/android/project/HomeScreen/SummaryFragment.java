package cyberknight.android.project.HomeScreen;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cyberknight.android.project.AccountManagement.BudgetFragment;
import cyberknight.android.project.DatabaseAndReaders.RecordDetails;
import cyberknight.android.project.DatabaseAndReaders.DbHelper;
import cyberknight.android.project.R;

/**
 * Created by Parth on 30-06-2016.
 * CyberKnight apps
 */
public class SummaryFragment extends Fragment implements View.OnClickListener,RecordScreenUpdater{

    private DbHelper database;
    private ArrayList<RecordDetails> allRecords;
    private String date;
    private TextView pageDate, budget, expenseB, income, expenseC, balance1, balance2;
    private LinearLayout bal1, bal2;
    private Activity parentActivity;

    public SummaryFragment(){}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        pageDate = (TextView) view.findViewById(R.id.summary_pageDate);
        budget = (TextView) view.findViewById(R.id.summary_budget);
        expenseB = (TextView) view.findViewById(R.id.summary_expense_b);
        income = (TextView) view.findViewById(R.id.summary_income);
        expenseC = (TextView) view.findViewById(R.id.summary_expense_c);
        balance1 = (TextView) view.findViewById(R.id.summary_balance_1);
        balance2 = (TextView) view.findViewById(R.id.summary_balance_2);
        bal1 = (LinearLayout) view.findViewById(R.id.summary_balance_b);
        bal2 = (LinearLayout) view.findViewById(R.id.summary_balance_c);

        ImageView prev = (ImageView) view.findViewById(R.id.summary_previousDate);
        ImageView next = (ImageView) view.findViewById(R.id.summary_nextDate);

        prev.setOnClickListener(this);
        next.setOnClickListener(this);

        updateScreenRecords();

        return view;
    }

    @Override
    public void updateScreenRecords() {
        SharedPreferences sharedPreferences = MainActivity.applicationContext.getSharedPreferences(BudgetFragment.BUDGET_PREFRENCES_FILE, Context.MODE_PRIVATE);
        budget.setText(String.valueOf((double)Math.round((sharedPreferences.getFloat("budget",0f)/30)*100d)/100d));

        pageDate.setText(date);
        database = new DbHelper(MainActivity.applicationContext);
        allRecords = database.getAllAccountDetailsByDate(date);
        double totalExpense = 0;
        double totalIncome = 0;
        for(int i=0; i<allRecords.size(); i++){
            if(allRecords.get(i).getTransaction().equals("Expense")) totalExpense += allRecords.get(i).getAmount();
            else totalIncome += allRecords.get(i).getAmount();
        }
        totalExpense = (double) Math.round(totalExpense*100d)/100d;
        totalIncome = (double) Math.round(totalIncome*100d)/100d;
        expenseB.setText(totalExpense+"");
        expenseC.setText(totalExpense+"");
        income.setText(totalIncome+"");

        double balance_1 = (double)Math.round((sharedPreferences.getFloat("budget",0f)/30)*100d)/100d - totalExpense;
        double balance_2 = totalIncome - totalExpense;

        if(balance_1>0) bal1.setBackgroundColor(getResources().getColor(R.color.green_400));
        else if(balance_1<0) bal1.setBackgroundColor(getResources().getColor(R.color.red_400));
        else bal1.setBackgroundColor(getResources().getColor(R.color.orange_400));

        if(balance_2>0) bal2.setBackgroundColor(getResources().getColor(R.color.green_400));
        else if(balance_2<0) bal2.setBackgroundColor(getResources().getColor(R.color.red_400));
        else bal2.setBackgroundColor(getResources().getColor(R.color.orange_400));

        balance1.setText((double) Math.round(balance_1*100d)/100d+"");
        balance2.setText((double) Math.round(balance_2*100d)/100d+"");
    }

    @Override
    public void setDateTo(String date) {
        this.date = date;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.summary_previousDate)    date = changeDate(date,-1);
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