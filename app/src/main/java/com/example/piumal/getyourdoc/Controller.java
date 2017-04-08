package com.example.piumal.getyourdoc;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import static com.example.piumal.getyourdoc.Constants.TABLE_NAME;

/**
 * Created by piumal on 4/4/17.
 */

public class Controller {
    /*
     * search from the database for the given values and returns a Cursor object
     */
    public Cursor getAppointment(AppointmentsData appointments, String[] from,
                                 String where, String orderby) {
        SQLiteDatabase db = appointments.getReadableDatabase();
        Cursor cursor = null;
        try {
            // condition to get the values
            cursor = db.query(TABLE_NAME, from, null, null, "DATE, TITLE",
                    where, orderby);
        } catch (Exception e) {
        }
        return cursor;
    }
}

