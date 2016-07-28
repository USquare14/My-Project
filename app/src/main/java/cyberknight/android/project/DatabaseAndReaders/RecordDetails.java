package cyberknight.android.project.DatabaseAndReaders;

/**
 * Created by umang on 30/6/16.
 */
public class RecordDetails {

    private int id;
    private String transaction;
    private String category;
    private String date;
    private String accountType;
    private double amount;
    private String note;

    public RecordDetails() {
        this.category = "";
        this.accountType = "";
        this.note = "";
    }

    public RecordDetails(String transaction, String category, String date, String accountType, double amount, String note) {
        this.transaction = transaction;
        this.category = category;
        this.date = date;
        this.accountType = accountType;
        this.amount = amount;
        this.note = note;
    }

    public String getTransaction() {
        return this.transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public double getAmount() {
        return (double) Math.round(amount*100d)/100d;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
