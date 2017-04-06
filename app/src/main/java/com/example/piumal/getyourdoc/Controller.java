package com.example.piumal.getyourdoc;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import static com.example.piumal.getyourdoc.Constants.TABLE_NAME;

/**
 * Created by piumal on 4/4/17.
 */
public class Controller {

    public Cursor getAppointment(AppointmentsData appointmentsData, String[] from, String where, String orderby) {
        SQLiteDatabase db = appointmentsData.getReadableDatabase();
        Cursor cursor = null;

        try {
            //Condition to get the values.
            cursor = db.query(TABLE_NAME, from, null, null, "DATE, TITLE", where, orderby);

        } catch (Exception e) {
            Toast.makeText(null, "SQL database is unreadable", Toast.LENGTH_SHORT).show();;
        }
        return cursor;
    }
}
