package cyberknight.android.project.DatabaseAndReaders;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Parth on 01-07-2016.
 * CyberKnight apps
 */
public class JsonReader {

    static Context mContext;

    public JsonReader(Context mContext){
        this.mContext = mContext;
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("CategoryAndAccounts.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            Toast.makeText(mContext,"Some Error Occured",Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    public ArrayList<String> getExpenseCategories(){
        ArrayList<String> categories = new ArrayList<>();
        try {
            JSONObject mainObj = new JSONObject(loadJSONFromAsset());
            JSONArray array = mainObj.getJSONArray("CategoryExpense");
            Log.d("JsonReader","Categories array fetched");
            for(int i=0; i<array.length(); i++){
                categories.add(array.getString(i));
            }
        }catch(Exception je){
            Toast.makeText(mContext,"Unable to fetch data",Toast.LENGTH_SHORT).show();
        }
        return categories;
    }

    public ArrayList<String> getIncomeCategories(){
        ArrayList<String> categories = new ArrayList<>();
        try {
            JSONObject mainObj = new JSONObject(loadJSONFromAsset());
            JSONArray array = mainObj.getJSONArray("CategoryIncome");
            Log.d("JsonReader","Categories array fetched");
            for(int i=0; i<array.length(); i++){
                categories.add(array.getString(i));
            }
        }catch(Exception je){
            Toast.makeText(mContext,"Unable to fetch data",Toast.LENGTH_SHORT).show();
        }
        return categories;
    }

    public ArrayList<String> getAccountsNames(){
        ArrayList<String> accounts = new ArrayList<>();
        try {
            JSONObject mainObj = new JSONObject(loadJSONFromAsset());
            JSONArray array = mainObj.getJSONArray("AccountNames");
            for(int i=0; i<array.length(); i++){
                accounts.add(array.getString(i));
            }
        }catch(Exception je){
            Toast.makeText(mContext,"Unable to fetch data",Toast.LENGTH_SHORT).show();
        }
        return accounts;
    }
}
