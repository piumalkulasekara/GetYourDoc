package com.example.piumal.getyourdoc;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.piumal.getyourdoc.Constants.*;

/**
 * Created by piumal on 4/4/17.
 */
public class AppointmentsData extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "appointmentsDb.db";
    private static final int DATABSE_VERSION = 1;

    public AppointmentsData(Context ctx) {

        super(ctx, DATABASE_NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*
		 * query for create the table
		 */
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + DATE
                + " TEXT NOT NULL, " + TITLE + " TEXT NOT NULL, " + TIME
                + " TEXT NOT NULL, " + DETAILS + " TEXT, PRIMARY KEY (" + DATE
                + "," + TITLE + "));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);
        onCreate(db);
    }


    public void addDates(Context ctx) {
        ContentValues values = new ContentValues();

        values.put("SELECTED_DATE", DATE);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }


}
