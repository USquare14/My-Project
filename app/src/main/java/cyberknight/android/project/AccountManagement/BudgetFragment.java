package cyberknight.android.project.AccountManagement;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import cyberknight.android.project.HomeScreen.MainActivity;
import cyberknight.android.project.R;

/**
 * Created by umang on 1/7/16.
 */
public class BudgetFragment extends Fragment {

    TextView textdaily,textweekly,textyearly;
    EditText edittextmonthly;
    ImageButton okbutton;
    public static final String BUDGET_PREFRENCES_FILE = "BudgetFile";
    SharedPreferences budget;

    public BudgetFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        edittextmonthly = (EditText) view.findViewById(R.id.edit_monthlybudget);
        textweekly = (TextView) view.findViewById(R.id.tvWeeklyBudget);
        textdaily = (TextView) view.findViewById(R.id.tvDailyBudget);
        textyearly = (TextView) view.findViewById(R.id.tvYearlyBudget);
        okbutton = (ImageButton) view.findViewById(R.id.budone);

        budget = MainActivity.applicationContext.getSharedPreferences(BUDGET_PREFRENCES_FILE, Context.MODE_PRIVATE);
        double budgetMonthly = budget.getFloat("budget",0);
        if(budgetMonthly!=0)    edittextmonthly.setText(String.valueOf(budgetMonthly));
        textweekly.setText(Math.round(budgetMonthly * 100d / 4)/100d+ "");
        textdaily.setText(Math.round(budgetMonthly * 100d / 30)/100d + "");
        textyearly.setText(Math.round(budgetMonthly * 100d / 4)/100d + "");

        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double dbudget = Double.parseDouble(edittextmonthly.getText().toString());
                    SharedPreferences.Editor editor = budget.edit();
                    editor.putFloat("budget",(float) dbudget);
                    textweekly.setText(Math.round(dbudget * 100d / 4)/100d + "");
                    textdaily.setText(Math.round(dbudget * 100d / 30)/100d + "");
                    textyearly.setText(Math.round(dbudget * 100d / 4)/100d + "");
                    editor.apply();
                }catch(Exception e){
                    Toast.makeText(getContext(),"Enter some values",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
