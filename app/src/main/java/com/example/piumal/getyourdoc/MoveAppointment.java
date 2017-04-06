package com.example.piumal.getyourdoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.piumal.getyourdoc.Constants.*;

/**
 * Created by piumal on 4/5/17.
 */
public class MoveAppointment extends Activity implements View.OnClickListener {
    private static String[] FROM = {DATE, TITLE, TIME, DETAILS};
    private Controller controller;
    private AppointmentsData appointments;
    private ArrayList<String> appointmentsArray;
    private ListView listView;
    private CalendarView newCalendarView;
    private long date;
    private long newDate;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_appointments);

        controller = new Controller();
        appointments = new AppointmentsData(this);

        Bundle bundle = getIntent().getExtras();

        /*get the selected data from previous activity*/
        date = bundle.getLong("SELECTED_DATE");

        /*Getting the vies*/
        Button btnMove = (Button) findViewById(R.id.btnMove_Appointment);
        btnMove.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.list_view);

        newCalendarView = (CalendarView) findViewById(R.id.new_calendar_view);

        setAppointmentsToListView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu: this adds items to the action bar if it's available
        getMenuInflater().inflate(R.menu.move_appointments, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.move_selected_button:
                confirmMoveAppointment();
                break;
        }
    }


    private void setAppointmentsToListView() {
        listView.setAdapter(null);

        Cursor cursor = controller.getAppointment(appointments, FROM, "DATE='"
                + date + "'", TIME);

        appointmentsArray = new ArrayList<String>();

        /*until there are cursors*/
        while (cursor.moveToNext()) {
            final String title = cursor.getString(1);
            String time = cursor.getString(2);

            /*Adding the values to appointments array*/
            appointmentsArray.add(appointmentsArray.size() + 1 + "." + time
                    + " " + title);

            final StableArrayAdapter adapter = new StableArrayAdapter(this,
                    android.R.layout.simple_list_item_1, appointmentsArray);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                    String string = parent.getItemAtPosition(position).toString();

                    String newTitle = string.substring(string.indexOf(':') + 4);
                    setTitle(newTitle);
                    newCalendarView.setVisibility(0);


                }
            });
        }
    }

    private void setTitle(String newTitle) {
        title = newTitle;
    }

    /*Confirm moving the appointment*/
    private void confirmMoveAppointment() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(MoveAppointment.this);
        myAlertDialog.setTitle("Confirm Move Appointment");
        myAlertDialog.setMessage("Would you like to move selected appointment? ");
        myAlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveAppointment();
            }
        });
        myAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        myAlertDialog.show();
    }

    private void moveAppointment() {
        /*get selected appointment which want to move*/
        Cursor cursor = controller.getAppointment(appointments, FROM, "DATE='" + date + "' AND TITLE='" + title + "'", null);

        if (cursor.moveToFirst()) {
            newDate = newCalendarView.getDate();
            title = cursor.getString(1);
        }
        SQLiteDatabase db = appointments.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DATE, newDate + "");


        /*get the appointment to update*/
        Cursor cursor1 = controller.getAppointment(appointments, FROM, "DATE='"
                + newDate + "' AND TITLE='" + title + "'", null);
        if (cursor1.getCount() == 0) {
            try {
                /*Update the database*/
                db.update(TABLE_NAME, values, "DATE='" + date + "' AND TITLE='"
                        + title + "'", null);
                Toast.makeText(this, "Appointment moved sucessfully.",
                        Toast.LENGTH_LONG).show();
                setAppointmentsToListView();
                newCalendarView.setVisibility(-1);
            } catch (Exception e) {
                Toast.makeText(this, "Exception: " + e.toString(),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(
                    this,
                    "There cannot be 2 appointments for a day with the same name. "
                            + "So that this appointment cannot be moved to the given date.",
                    Toast.LENGTH_LONG).show();
        }
    }

    /*Inner class to add data to the list view*/
    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
