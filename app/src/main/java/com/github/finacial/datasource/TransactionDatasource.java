package com.github.finacial.datasource;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.finacial.domain.Transaction;
import com.github.finacial.domain.TransactionType;

import java.util.ArrayList;
import java.util.List;

public class TransactionDatasource extends SQLiteOpenHelper  {

    private static final String TABLE_NAME = "transactions";
    private static final String DATABASE = "transaction.db";
    private static final int VERSION = 1;

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER_ID = "_user_id";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_AMOUNT = "amount";


    public TransactionDatasource(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USER_ID + " INTERGER," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_TYPE + " TEXT," +
                COLUMN_AMOUNT + " REAL)";
        db.execSQL(sqlCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void save(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, transaction.getUserId());
        values.put(COLUMN_DESCRIPTION, transaction.getDescription());
        values.put(COLUMN_DATE, transaction.getDate());
        values.put(COLUMN_TYPE, transaction.getType().name());
        values.put(COLUMN_AMOUNT, transaction.getAmount());

        long newRowId = db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Transaction> getAll() {
        List<Transaction> transactions = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_USER_ID, COLUMN_DESCRIPTION, COLUMN_DATE, COLUMN_TYPE, COLUMN_AMOUNT},
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            Transaction transaction = new Transaction();
            transaction.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            transaction.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)));
            transaction.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            transaction.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)));
            transaction.setType(TransactionType.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))));
            transaction.setAmount(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)));

            transactions.add(transaction);
        }

        cursor.close();
        db.close();

        return transactions;
    }

    public boolean delete(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{code});
        return result > 0;
    }

    public boolean update(String description, String date, String type, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        putValueIfNotEmpty(description, COLUMN_DESCRIPTION, false, values);
        putValueIfNotEmpty(date, COLUMN_DATE, false, values);
        putValueIfNotEmpty(type, COLUMN_TYPE, true, values);

        int result = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{description});
        return result > 0;
    }

    private static void putValueIfNotEmpty(String newValue, String columnName, boolean isInt, ContentValues values) {
        if(newValue != null && !newValue.isEmpty()) {
            if(isInt) {
                values.put(columnName, Integer.parseInt(newValue));
            } else {
                values.put(columnName, newValue);
            }

        }
    }

    @SuppressLint("Range")
    public double getTotalAmount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "SUM(CASE WHEN " + COLUMN_TYPE + " = 'CREDIT' THEN " + COLUMN_AMOUNT + " ELSE 0 END) " +
                "+ SUM(CASE WHEN " + COLUMN_TYPE + " = 'ANOTHER' THEN " + COLUMN_AMOUNT + " ELSE 0 END) " +
                "- SUM(CASE WHEN " + COLUMN_TYPE + " = 'DEBIT' THEN " + COLUMN_AMOUNT + " ELSE 0 END) " +
                "AS totalAmount " +
                "FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);
        double totalAmount = 0;

        try {
            if (cursor.moveToFirst()) {
                totalAmount = cursor.getDouble(cursor.getColumnIndex("totalAmount"));
            }
        } finally {
            cursor.close();
        }

        return totalAmount;
    }

}
