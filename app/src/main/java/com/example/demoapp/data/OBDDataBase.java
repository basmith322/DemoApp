package com.example.demoapp.data;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class OBDDataBase extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "obd-trouble-codes.db";
    private static final int DATABASE_VERSION = 1;

    public OBDDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public Cursor getCodes() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"0 _id", "desc"};
        String sqlTables = "codes";

        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, null,null,
                null,null,null);
        c.moveToFirst();
        return c;
    }
}