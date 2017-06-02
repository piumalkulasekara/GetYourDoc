package com.example.piumal.getyourdoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.piumal.getyourdoc.Constants.*;

/**
 * Created by piumal on 4/5/17.
 */
public class DeleteAppointment extends Activity implements View.OnClickListener {
    private AppointmentsData appointments;
    private static String[] FROM = {DATE, TITLE, TIME, DETAILS};
    private long date;
    private ListView listView;
    private ArrayList<String> appointmentsArray;
    private SQLiteDatabase db;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_appointment);

        controller = new Controller();
        appointments = new AppointmentsData(this);

        Bundle bundle = getIntent().getExtras();
        /*
		 * get the selected date from the previous activity
		 */
        date = bundle.getLong("SELECTED_DATE");

		/*
         * getting the views
		 */
        Button deleteAllAppointmentsButton = (Button) findViewById(R.id.delete_all_appointments_button);
        deleteAllAppointmentsButton.setOnClickListener(this);

        Button selectAppointmentToDeleteButton = (Button) findViewById(R.id.select_appointment_to_delete_button);
        selectAppointmentToDeleteButton.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.list_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.delete_appointments, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_all_appointments_button:
                deleteAppointment("DATE='" + date + "'");
                break;
            case R.id.select_appointment_to_delete_button:
                setAppointmentsToListView();
                break;
        }
    }

    private void setAppointmentsToListView() {
        listView.setAdapter(null);
        Cursor cursor = controller.getAppointment(appointments, FROM, "DATE='"
                + date + "'", TIME);
        appointmentsArray = new ArrayList<String>();

		/*
         * until there are cursors
		 */
        while (cursor.moveToNext()) {
            String title = cursor.getString(1);
            String time = cursor.getString(2);

			/*
             * adding the values to appointments array
			 */
            appointmentsArray.add(appointmentsArray.size() + 1 + "." + time
                    + " " + title);

            final StableArrayAdapter adapter = new StableArrayAdapter(this,
                    android.R.layout.simple_list_item_1, appointmentsArray);
            listView.setAdapter(adapter);

			/*
             * clicking on the list make a prompt to confirm deletion
			 */
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(final AdapterView<?> parent,
                                        final View view, final int position, long id) {

                    final String string = parent.getItemAtPosition(position)
                            .toString();
                    String titleSt = string.substring(string.indexOf(':') + 4);

                    AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                            DeleteAppointment.this);
                    myAlertDialog.setTitle("Confirm delete appointment");

                    myAlertDialog.setMessage("Would you like to delete event: \""+ titleSt + "\"? ");
                    myAlertDialog.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    final String string = parent
                                            .getItemAtPosition(position)
                                            .toString();
                                    /*
									 * getting the title of the selected
									 * appointment
									 */
                                    String titleSt = string.substring(string
                                            .indexOf(':') + 4);
									/*
									 * call method to delete the appointment
									 */
                                    deleteAppointment("TITLE='" + titleSt
                                            + "' AND DATE='" + date + "'");

                                }
                            });

                    myAlertDialog.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                }
                            });
                    myAlertDialog.show();
                }
            });
            adapter.notifyDataSetChanged();
        }
    }

    /*
     * delete the appointment from the database
     */
    private int deleteAppointment(String where) {
        db = appointments.getWritableDatabase();
        int i = 0;
        try {
            /*
			 * delete appointment query
			 */
            i = db.delete(TABLE_NAME, where, null);
            if (where.equals("DATE='" + date + "'")) {
                /*
				 * delete every appointment for the selected date
				 */
                Toast.makeText(this,
                        "All the apoinments for the selected date are deleted",
                        Toast.LENGTH_LONG).show();
                Log.d("Results-Appointment","Selected-Deleted");
            } else {
                Toast.makeText(this,
                        "Selected appointment deleted successfully",
                        Toast.LENGTH_LONG).show();

                Log.d("Results-Appointment","All-Deleted");
            }
        } catch (Exception e) {
            Toast.makeText(this, "Exception: " + e.toString(),
                    Toast.LENGTH_LONG).show();

            Log.d("Results-Appointment","Selected-Deleted");
        }
        /*
		 * adding the appointments to the list view
		 */
        setAppointmentsToListView();
        return i;
    }

    /*
     * creates inner class to add data to the list view
     */
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

