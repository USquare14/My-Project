package cyberknight.android.project.AccountManagement;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import cyberknight.android.project.DatabaseAndReaders.DbHelper;
import cyberknight.android.project.DatabaseAndReaders.JsonReader;
import cyberknight.android.project.HomeScreen.MainActivity;
import cyberknight.android.project.R;

/**
 * Created by umang on 1/7/16.
 */
public class AccountFragment extends Fragment {

    DbHelper dbHelper;
    private double credits;
    private double debt;
    private String amount;
    private Activity parentActivity;

    public AccountFragment(){}

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.parentActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        TextView textCredits = (TextView) view.findViewById(R.id.tvCredits);
        TextView textDebt = (TextView) view.findViewById(R.id.tvDebt);
        TextView textBalance = (TextView) view.findViewById(R.id.tvBalance);
        final TextView textBank = (TextView) view.findViewById(R.id.tvBankAccount);
        final TextView textCard = (TextView) view.findViewById(R.id.tvCard);
        final TextView textCash = (TextView) view.findViewById(R.id.tvCash);
        LinearLayout balanceBack = (LinearLayout) view.findViewById(R.id.balanceBack);
        LinearLayout btnBank = (LinearLayout) view.findViewById(R.id.accounts_bank);
        LinearLayout btnCards = (LinearLayout) view.findViewById(R.id.accounts_cards);
        LinearLayout btnCash = (LinearLayout) view.findViewById(R.id.accounts_cash);

        dbHelper = new DbHelper(MainActivity.applicationContext);

        double cash = dbHelper.getAmountByName("Cash");
        double card = dbHelper.getAmountByName("Card");
        double bank = dbHelper.getAmountByName("Bank");

        textCash.setText(cash +"");
        textCard.setText(card +"");
        textBank.setText(bank +"");

        if(cash >0)
            credits+= cash;
        else
            debt+= cash;

        if(card >0)
            credits+= card;
        else
            debt+= card;

        if(bank >0)
            credits+= bank;
        else
            debt+= bank;

        double balance = credits + debt;

        textCredits.setText(credits+"");
        textDebt.setText(debt+"");
        textBalance.setText(balance +"");

        if(balance>0)   balanceBack.setBackgroundColor(getResources().getColor(R.color.green_400));
        else if(balance<0)  balanceBack.setBackgroundColor(getResources().getColor(R.color.red_400));

        btnBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Bank");
            }
        });

        btnCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Card");
            }
        });

        btnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog("Cash");
            }
        });

        return view;
    }

    private void showDialog(final String account){
        View promptsView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_accounts, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.editAccount);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(!userInput.getText().toString().equals("")){
                                    dbHelper.modifyAccount(account,Double.parseDouble(userInput.getText().toString()),false);
                                    ((ReloadFragment)parentActivity).reloadFragment();
                                }
                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
