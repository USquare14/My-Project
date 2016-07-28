package cyberknight.android.project.DatabaseAndReaders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


/**
 * Created by umang on 30/6/16.
 */
public class DbHelper extends SQLiteOpenHelper {


    private static final String TAG = DbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "Account";

    public static final String TABLE_NAME = "DailyAccount";
    public static final String KEY_ID = "auto_id";
    public static final String KEY_CATEGORY = "category_type";
    public static final String KEY_DATE = "date";
    public static final String KEY_AMOUNT = "amount";
    public static final String KEY_ACCOUNT_TYPE = "account_type";
    public static final String KEY_NOTE = "note";
    public static final String KEY_BALANCE_TYPE = "balance_type";

    public static final String CREATE_TABLE_DAILY_INFO = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + KEY_CATEGORY + " VARCHAR (20) , "
            + KEY_DATE + " VARCHAR (10) , "
            + KEY_ACCOUNT_TYPE + " VARCHAR (10) , "
            + KEY_AMOUNT + " REAL , "
            + KEY_NOTE + " VARCHAR (100) , "
            + KEY_BALANCE_TYPE + " VARCHAR (10) "
            + ")";


    public static final String ACCOUNTS_TABLE = "Accounts";
    public static final String KEY_ACCOUNT_NAME = "account";
    public static final String KEY_ACCOUNT_AMOUNT = "amount";

    public static final String CREATE_TABLE_ACCOUNTS_TABLE = "CREATE TABLE IF NOT EXISTS " + ACCOUNTS_TABLE
            + "("
            + KEY_ID + " INTEGER PRIMARY KEY , "
            + KEY_ACCOUNT_NAME + " VARCHAR , "
            + KEY_ACCOUNT_AMOUNT + " REAL "
            + ")";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DAILY_INFO);
        db.execSQL(CREATE_TABLE_ACCOUNTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + ACCOUNTS_TABLE);
        onCreate(db);
    }

    public void addInitialAmount(int id, String accountName, double initialAmount){

        Log.d(TAG,"Adding Amount...");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID,id);
        values.put(KEY_ACCOUNT_NAME,accountName);
        values.put(KEY_ACCOUNT_AMOUNT,initialAmount);


        Log.d(TAG,"RECORD ADDED++++++++++++++++++++++++++++++++++++++++ "+values);
        db.insert(ACCOUNTS_TABLE,null,values);
    }

    public void addRecord(String balanceType, double amount, String date,String category,String accountType,String note){

        Log.d(TAG,"Adding Record...");

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AMOUNT,amount);
        values.put(KEY_DATE,date);
        values.put(KEY_CATEGORY,category);
        values.put(KEY_ACCOUNT_TYPE,accountType);
        values.put(KEY_NOTE,note);
        values.put(KEY_BALANCE_TYPE,balanceType);

        db.insert(TABLE_NAME,null,values);

        Log.d(TAG,"RECORD ADDED++++++++++++++++++++++++++++++++++++++++ "+TABLE_NAME);

    }

    public void modifyAccount(String account, double amount, boolean add){
        double initialAmount=0;

        if(add) {
            initialAmount = getAmountByName(account);
        }

        int id;
        if(account.equals("Cash")) id = 0;
        else if(account.equals("Card")) id = 1;
        else id = 2;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        if(add) values.put(KEY_ACCOUNT_AMOUNT, initialAmount+amount);
        else values.put(KEY_ACCOUNT_AMOUNT, amount);

        db.update(ACCOUNTS_TABLE, values, KEY_ID + " = " + id, null);
    }

    public void changeRecord(int id,String balanceType, double amount, String date,String category,String accountType,String note){

        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG,"Updated message status");

        ContentValues values = new ContentValues();
        values.put(KEY_AMOUNT,amount);
        values.put(KEY_DATE,date);
        values.put(KEY_CATEGORY,category);
        values.put(KEY_ACCOUNT_TYPE,accountType);
        values.put(KEY_NOTE,note);
        values.put(KEY_BALANCE_TYPE,balanceType);

        db.update(TABLE_NAME, values, KEY_ID + " = " + id,null);

    }

    public double getAmountByName(String amountType){

        double amount = 0;

        String selectQuery = "SELECT  * FROM " + ACCOUNTS_TABLE + " tm WHERE tm." + KEY_ACCOUNT_NAME + " = '" + amountType +"' ORDER BY tm." +KEY_ID + " DESC";

        Log.d(TAG, "Get All Date Query "+selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        Log.d(TAG, "Date Cursor "+c.toString());

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                amount+=c.getDouble(c.getColumnIndex(KEY_ACCOUNT_AMOUNT));
                Log.d(TAG, "\n\n\n\n\n\n\nGet All Date Query=================== "+amount);

            } while (c.moveToNext());
        }
        return amount;
    }

    public ArrayList<RecordDetails> getAllAccountDetails() {

        ArrayList<RecordDetails> accountModels = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " tq ";

        Log.e(TAG, "Get All Details " + selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Log.d(TAG, "Account Cursor "+c.toString());

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                RecordDetails account = new RecordDetails();
                account.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                account.setTransaction(c.getString(c.getColumnIndex(KEY_BALANCE_TYPE)));
                account.setAmount(c.getDouble(c.getColumnIndex(KEY_AMOUNT)));
                account.setNote(c.getString(c.getColumnIndex(KEY_NOTE)));
                account.setCategory(c.getString(c.getColumnIndex(KEY_CATEGORY)));
                account.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
                account.setAccountType(c.getString(c.getColumnIndex(KEY_ACCOUNT_TYPE)));
                accountModels.add(account);

            } while (c.moveToNext());
        }
        c.close();
        return accountModels;
    }

    public ArrayList<RecordDetails> getAllAccountDetailsByDate(String date) {

        ArrayList<RecordDetails> accountModels = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " tm WHERE tm." + KEY_DATE + " = '" + date +"' ORDER BY tm." +KEY_ID + " DESC";

        Log.d(TAG, "Get All Date Query "+selectQuery);


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        Log.d(TAG, "Date Cursor "+c.toString());

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                RecordDetails account = new RecordDetails();
                account.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                account.setTransaction(c.getString(c.getColumnIndex(KEY_BALANCE_TYPE)));
                account.setAmount(c.getDouble(c.getColumnIndex(KEY_AMOUNT)));
                account.setNote(c.getString(c.getColumnIndex(KEY_NOTE)));
                account.setCategory(c.getString(c.getColumnIndex(KEY_CATEGORY)));
                account.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
                account.setAccountType(c.getString(c.getColumnIndex(KEY_ACCOUNT_TYPE)));
                accountModels.add(account);

            } while (c.moveToNext());
        }
        return accountModels;
    }

    public ArrayList<RecordDetails> getAllAccountDetailsByMonth(String year, String month) {

        ArrayList<RecordDetails> accountModels = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " tm ";

        Log.d(TAG, "Get All Month Query "+selectQuery);

        String search = year+"-"+month;
        Log.d("SearchString", search);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        Log.d(TAG, "Date Cursor "+c.toString());

        int count=0;
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                if(c.getString(c.getColumnIndex(KEY_DATE)).contains(search)){
                    RecordDetails account = new RecordDetails();
                    account.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                    account.setTransaction(c.getString(c.getColumnIndex(KEY_BALANCE_TYPE)));
                    account.setAmount(c.getDouble(c.getColumnIndex(KEY_AMOUNT)));
                    account.setNote(c.getString(c.getColumnIndex(KEY_NOTE)));
                    account.setCategory(c.getString(c.getColumnIndex(KEY_CATEGORY)));
                    account.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
                    account.setAccountType(c.getString(c.getColumnIndex(KEY_ACCOUNT_TYPE)));
                    accountModels.add(account);
                    count++;
                }

            } while (c.moveToNext());
        }
        Log.d("Count",count+"");
        return accountModels;
    }

    public ArrayList<RecordDetails> getAllAccountDetailsByCategory(String category) {

        ArrayList<RecordDetails> accountModels = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " tm WHERE tm." + KEY_CATEGORY + " = '" + category +"' ORDER BY tm." +KEY_DATE + " DESC";

        Log.d(TAG, "Get All Date Query "+selectQuery);


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        Log.d(TAG, "Date Cursor "+c.toString());

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {

                RecordDetails account = new RecordDetails();
                account.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                account.setTransaction(c.getString(c.getColumnIndex(KEY_BALANCE_TYPE)));
                account.setAmount(c.getDouble(c.getColumnIndex(KEY_AMOUNT)));
                account.setNote(c.getString(c.getColumnIndex(KEY_NOTE)));
                account.setCategory(c.getString(c.getColumnIndex(KEY_CATEGORY)));
                account.setDate(c.getString(c.getColumnIndex(KEY_DATE)));
                account.setAccountType(c.getString(c.getColumnIndex(KEY_ACCOUNT_TYPE)));
                accountModels.add(account);

            } while (c.moveToNext());
        }
        return accountModels;
    }

    public boolean deleteRecord(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, KEY_ID + "=" + id, null) > 0;
    }

    public void deleteAllRecords(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
        db.execSQL("delete from "+ ACCOUNTS_TABLE);

    }
}
