package com.github.finacial.datasource;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.github.finacial.domain.User;
import com.github.finacial.utils.HashUtils;

import java.util.ArrayList;
import java.util.List;

public class UserDatasource extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "user";
    private static final String DATABASE = "user.db";
    private static final int VERSION = 1;

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    public UserDatasource(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_FIRST_NAME + " TEXT," +
                COLUMN_LAST_NAME + " TEXT," +
                COLUMN_EMAIL + " TEXT," +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(sqlCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void save(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_LAST_NAME, user.getLastName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, HashUtils.sha256(user.getPassword()));

        long newRowId = db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<User> getAll() {
        List<User> listProduct = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,
                new String[]{"_id", "code", "name", "description", "quantity"},
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            user.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));

            listProduct.add(user);
        }

        cursor.close();
        db.close();

        return listProduct;
    }

    @SuppressLint("Range")
    public User findOneUserByEmailAndPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_ID, COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_EMAIL, COLUMN_PASSWORD};

        String whereClause = "email = ? and password = ?";
        String[] whereArgs = {email, HashUtils.sha256(password)};

        Cursor cursor = db.query(TABLE_NAME, projection, whereClause, whereArgs, null, null, null, "1");

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            user.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME)));
            user.setLastName(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)));

            cursor.close();

            return user;
        }

        return null;
    }

    public boolean delete(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_FIRST_NAME + " = ?", new String[]{code});
        return result > 0;
    }

    public boolean update(String code, String newName, String newDescription, String newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        putValueIfNotEmpty(newName, COLUMN_FIRST_NAME, false, values);
        putValueIfNotEmpty(newDescription, COLUMN_LAST_NAME, false, values);
        putValueIfNotEmpty(newQuantity, COLUMN_EMAIL, true, values);
        putValueIfNotEmpty(newQuantity, COLUMN_PASSWORD, true, values);

        int result = db.update(TABLE_NAME, values, COLUMN_FIRST_NAME + " = ?", new String[]{code});
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
}
