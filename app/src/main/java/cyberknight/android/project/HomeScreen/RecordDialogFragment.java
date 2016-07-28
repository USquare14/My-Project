package cyberknight.android.project.HomeScreen;

import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cyberknight.android.project.DatabaseAndReaders.RecordDetails;
import cyberknight.android.project.DatabaseAndReaders.DbHelper;
import cyberknight.android.project.R;

/**
 * Created by Parth on 03-07-2016.
 * CyberKnight apps
 */
public class RecordDialogFragment extends DialogFragment{

    private ArrayList<RecordDetails> records;
    private int position;

    static RecordDialogFragment newInstance(int num) {
        RecordDialogFragment f = new RecordDialogFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(android.support.v4.app.DialogFragment.STYLE_NO_TITLE,0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_record_dialog, container, false);
        final LinearLayout titleBack = (LinearLayout) v.findViewById(R.id.recordTitleBack);
        final TextView title = (TextView) v.findViewById(R.id.recordTitle);
        final TextView amount = (TextView) v.findViewById(R.id.recordAmount);
        final TextView category = (TextView) v.findViewById(R.id.recordCategory);
        final TextView paymentType = (TextView) v.findViewById(R.id.recordPaymentType);
        final TextView note = (TextView) v.findViewById(R.id.recordNote);
        final TextView date = (TextView) v.findViewById(R.id.recordDate);
        final ImageView imageIcon = (ImageView) v.findViewById(R.id.recordImgCategory);
        Button cancel = (Button) v.findViewById(R.id.recordCancel);
        Button modify = (Button) v.findViewById(R.id.recordModify);
        ImageView delete = (ImageView) v.findViewById(R.id.recordDelete);

        DbHelper database = new DbHelper(MainActivity.applicationContext);
        records = database.getAllAccountDetailsByDate(getArguments().getString("date"));
        position = getArguments().getInt("position");
        if(records.get(position).getTransaction().equals("Income")) titleBack.setBackgroundColor(getResources().getColor(R.color.green_400));
        else    titleBack.setBackgroundColor(getResources().getColor(R.color.red_400));
        title.setText(records.get(position).getTransaction());
        amount.setText("Rs. "+records.get(position).getAmount());
        category.setText(records.get(position).getCategory());
        imageIcon.setImageResource(HomeFragment.getIconFor(category.getText().toString()));
        paymentType.setText(records.get(position).getAccountType());
        note.setText(records.get(position).getNote());
        date.setText(records.get(position).getDate());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DbHelper database = new DbHelper(MainActivity.applicationContext);
                        if(database.deleteRecord(records.get(position).getId())) {
                            dialog.dismiss();
                            dismissDialog();
                            database.modifyAccount(records.get(position).getAccountType(),
                                    records.get(position).getTransaction().equals("Income")?
                                            records.get(position).getAmount()*-1:records.get(position).getAmount(),
                                    true);
                            RecordScreenUpdater updater = (RecordScreenUpdater) getTargetFragment();
                            updater.updateScreenRecords();
                        }
                        else{
                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Unable to delete record!");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.create().show();
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.setTitle("Delete Record!");
                alertDialogBuilder.setIcon(getResources().getDrawable(R.drawable.ic_delete_dark));
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setMessage("Are you sure you want to delete this record?");
                alertDialog.show();
            }
        });

        modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewRecord newRecord = NewRecord.newInstance(0);
                Bundle temp = new Bundle();
                temp.putString("Mode","edit");
                temp.putInt("Id",records.get(position).getId());
                temp.putDouble("Amount",records.get(position).getAmount());
                temp.putString("PaymentType",records.get(position).getAccountType());
                temp.putString("Note",records.get(position).getNote());
                temp.putString("Date",records.get(position).getDate());
                temp.putString("Transaction",records.get(position).getTransaction());
                newRecord.setArguments(temp);
                newRecord.setCancelable(false);
                newRecord.setTargetFragment(getTargetFragment(),0);
                dismissDialog();
                newRecord.show(getFragmentManager(),"NewRecordFromRecord");
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode== KeyEvent.KEYCODE_BACK){
                    dismissDialog();
                }
                return false;
            }
        });
        return v;
    }

    public void dismissDialog(){
        getDialog().dismiss();
    }

    private int getIconFor(String s){
        int icon;
        switch (s){
            case "Food":
                icon = R.drawable.item_food;
                break;
            case "Entertainment":
                icon = R.drawable.item_entertainment;
                break;
            case "Travel":
                icon = R.drawable.item_travel;
                break;
            case "Education":
                icon = R.drawable.item_education;
                break;
            case "Clothing/Beauty":
                icon = R.drawable.item_clothing;
                break;
            case "Social":
                icon = R.drawable.item_social;
                break;
            case "Medical":
                icon = R.drawable.item_medical;
                break;
            default:
                icon = 0;
        }
        return icon;
    }
}
