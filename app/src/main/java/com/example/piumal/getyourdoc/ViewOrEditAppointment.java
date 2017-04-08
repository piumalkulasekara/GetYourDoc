package com.example.piumal.getyourdoc;


/**
 * Created by piumal on 4/5/17.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import static com.example.piumal.getyourdoc.Constants.*;

public class ViewOrEditAppointment extends Activity {
    private AppointmentsData appointments;
    private static String[] FROM = {DATE, TITLE, TIME, DETAILS};
    private ListView listView;
    private ArrayList<String> appointmentsArray;
    private long date;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_or_edit_appointment);

        controller = new Controller();
        appointments = new AppointmentsData(this);

        Bundle bundle = getIntent().getExtras();
        date = bundle.getLong("SELECTED_DATE");

        listView = (ListView) findViewById(R.id.list_view);

        setAppointmentsToListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_or_edit_appointment, menu);
        return true;
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
            final String title = cursor.getString(1);
            String time = cursor.getString(2);

			/*
             * adding the values to appointments array
			 */
            appointmentsArray.add(appointmentsArray.size() + 1 + "." + time
                    + " " + title);

            final StableArrayAdapter adapter = new StableArrayAdapter(this,
                    android.R.layout.simple_list_item_1, appointmentsArray);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent,
                                        final View view, final int position, long id) {

                    final String string = parent.getItemAtPosition(position)
                            .toString();
                    String titleSt = string.substring(string.indexOf(':') + 4);

                    AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                            ViewOrEditAppointment.this);
                    myAlertDialog.setTitle("Confirm edit appointment");

                    myAlertDialog.setMessage("Would you like to edit event: \""
                            + titleSt + "\"? ");
                    myAlertDialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {

                                    Intent intent = new Intent(
                                            ViewOrEditAppointment.this,
                                            EditAppointment.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("DATA", string);
                                    bundle.putLong("SELECTED_DATE", date);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
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
        }
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
