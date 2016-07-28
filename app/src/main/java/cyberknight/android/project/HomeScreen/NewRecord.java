package cyberknight.android.project.HomeScreen;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cyberknight.android.project.DatabaseAndReaders.DbHelper;
import cyberknight.android.project.DatabaseAndReaders.JsonReader;
import cyberknight.android.project.R;

/**
 * Created by Parth on 30-06-2016.
 * CyberKnight apps
 */
public class NewRecord extends DialogFragment{

    private DbHelper database;
    private JsonReader jsonReader;
    private String transaction;
    private int Id;
    private Spinner category, paymentType;
    private LinearLayout transactionBack;
    private ImageView incomeBar,expenseBar,imageIcon;

    static NewRecord newInstance(int num) {
        NewRecord f = new NewRecord();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE,0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_record,container,false);

        category = (Spinner)v.findViewById(R.id.spinnerCategory);
        paymentType = (Spinner)v.findViewById(R.id.spinnerPaymentType);
        final EditText amount = (EditText) v.findViewById(R.id.textAmount);
        final EditText note = (EditText) v.findViewById(R.id.textNote);
        final DatePicker date = (DatePicker) v.findViewById(R.id.datePicker);
        transactionBack = (LinearLayout) v.findViewById(R.id.transactionBackground);
        final LinearLayout expense = (LinearLayout) v.findViewById(R.id.expenseListener);
        final LinearLayout income = (LinearLayout) v.findViewById(R.id.incomeListener);
        expenseBar = (ImageView) v.findViewById(R.id.expenseBar);
        incomeBar = (ImageView) v.findViewById(R.id.incomeBar);
        imageIcon = (ImageView) v.findViewById(R.id.imgCategory);

        jsonReader = new JsonReader(v.getContext());
        database = new DbHelper(MainActivity.applicationContext);
        transaction = "Expense";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(v.getContext(), R.layout.spinner_item , jsonReader.getExpenseCategories());
        category.setAdapter(adapter);
        adapter = new ArrayAdapter<>(v.getContext(), R.layout.spinner_item , jsonReader.getAccountsNames());
        paymentType.setAdapter(adapter);
        imageIcon.setImageResource(HomeFragment.getIconFor(category.getSelectedItem().toString()));

        String currDate;
        if(getArguments().getString("Mode").equals("edit")){
            Id = getArguments().getInt("Id");
            currDate = getArguments().getString("Date");
            amount.setText(String.valueOf(getArguments().getDouble("Amount")));
            double previousAmount = getArguments().getDouble("Amount");
            if(getArguments().getString("Transaction").equals("Expense")) {
                setTransactionTypeExpense();
                ArrayList<String> expenseCategories = jsonReader.getExpenseCategories();
                for(int i=0; i<expenseCategories.size(); i++){
                    if(expenseCategories.get(i).equals(getArguments().getString("Category"))){
                        category.setSelection(i);
                        break;
                    }
                }
                database.modifyAccount(getArguments().getString("PaymentType"), previousAmount,true);
            }
            else{
                setTransactionTypeIncome();
                ArrayList<String> expenseCategories = jsonReader.getIncomeCategories();
                for(int i=0; i<expenseCategories.size(); i++){
                    if(expenseCategories.get(i).equals(getArguments().getString("Category"))){
                        category.setSelection(i);
                        break;
                    }
                }
                database.modifyAccount(getArguments().getString("PaymentType"), previousAmount *-1,true);
            }
            ArrayList<String> payment = jsonReader.getAccountsNames();
            for(int i=0; i<payment.size(); i++){
                if(payment.get(i).equals(getArguments().getString("PaymentType"))){
                    paymentType.setSelection(i);
                    break;
                }
            }
            note.setText(getArguments().getString("Note"));
        }
        else  currDate = getArguments().getString("CurrentDate");
        try {
            Date tmp = new SimpleDateFormat("yyyy-MM-dd").parse(currDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(tmp);
            date.updateDate(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageIcon.setImageResource(HomeFragment.getIconFor(category.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button save = (Button)v.findViewById(R.id.btnSave);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getDialog().setCancelable(true);
                if(amount.getText().toString().equals("")) amount.setText("0");

                if(getArguments().getString("Mode").equals("edit")) database.changeRecord(Id,transaction,Double.parseDouble(amount.getText().toString()), getSringFormatForDate(date.getYear(),date.getMonth()+1,date.getDayOfMonth()),category.getSelectedItem().toString(),paymentType.getSelectedItem().toString(),note.getText().toString());
                else    database.addRecord(transaction,Double.parseDouble(amount.getText().toString()), getSringFormatForDate(date.getYear(),date.getMonth()+1,date.getDayOfMonth()),category.getSelectedItem().toString(),paymentType.getSelectedItem().toString(),note.getText().toString());

                if(transaction.equals("Income")) database.modifyAccount(paymentType.getSelectedItem().toString(),Double.parseDouble(amount.getText().toString()),true);
                else database.modifyAccount(paymentType.getSelectedItem().toString(),Double.parseDouble(amount.getText().toString())*-1,true);

                RecordScreenUpdater mUpdater = (RecordScreenUpdater) getTargetFragment();
                mUpdater.updateScreenRecords();
                getDialog().dismiss();
            }
        });

        Button addNext = (Button)v.findViewById(R.id.btnAddNext);
        addNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(amount.getText().toString().equals("")) amount.setText("0");

                if(getArguments().getString("Mode").equals("edit")) database.changeRecord(Id,transaction,Double.parseDouble(amount.getText().toString()), getSringFormatForDate(date.getYear(),date.getMonth()+1,date.getDayOfMonth()),category.getSelectedItem().toString(),paymentType.getSelectedItem().toString(),note.getText().toString());
                else    database.addRecord(transaction,Double.parseDouble(amount.getText().toString()), getSringFormatForDate(date.getYear(),date.getMonth()+1,date.getDayOfMonth()),category.getSelectedItem().toString(),paymentType.getSelectedItem().toString(),note.getText().toString());

                if(transaction.equals("Income")) database.modifyAccount(paymentType.getSelectedItem().toString(),Double.parseDouble(amount.getText().toString()),true);
                else database.modifyAccount(paymentType.getSelectedItem().toString(),Double.parseDouble(amount.getText().toString())*-1,true);

                RecordScreenUpdater mUpdater = (RecordScreenUpdater) getTargetFragment();
                mUpdater.updateScreenRecords();
                amount.setText("");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(v.getContext(), R.layout.spinner_item , transaction.equals("Income")? jsonReader.getIncomeCategories(): jsonReader.getExpenseCategories());
                category.setAdapter(adapter);
                adapter = new ArrayAdapter<>(v.getContext(), R.layout.spinner_item , jsonReader.getAccountsNames());
                paymentType.setAdapter(adapter);
                note.setText("");
            }
        });

        expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTransactionTypeExpense();
            }
        });

        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTransactionTypeIncome();
            }
        });

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode== KeyEvent.KEYCODE_BACK){
                    getDialog().dismiss();
                }
                return false;
            }
        });

        return v;
    }

    public void setTransactionTypeExpense(){
        transactionBack.setBackgroundColor(getResources().getColor(R.color.red_400));
        expenseBar.setBackgroundColor(getResources().getColor(R.color.grey_50));
        incomeBar.setBackgroundColor(getResources().getColor(R.color.red_400));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item , jsonReader.getExpenseCategories());
        category.setAdapter(adapter);
        transaction = "Expense";
    }

    private void setTransactionTypeIncome(){
        transactionBack.setBackgroundColor(getResources().getColor(R.color.green_400));
        expenseBar.setBackgroundColor(getResources().getColor(R.color.green_400));
        incomeBar.setBackgroundColor(getResources().getColor(R.color.grey_50));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item , jsonReader.getIncomeCategories());
        category.setAdapter(adapter);
        transaction = "Income";
    }

    public static String getSringFormatForDate(int year, int month, int day){
        String date = year + "-";

        if(month<10) date += "0" + month;
        else date += month;

        date += "-";

        if(day<10) date += "0" + day;
        else date += day;

        return date;
    }
}
