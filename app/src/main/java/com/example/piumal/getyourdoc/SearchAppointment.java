package com.example.piumal.getyourdoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.piumal.getyourdoc.Constants.DATE;
import static com.example.piumal.getyourdoc.Constants.DETAILS;
import static com.example.piumal.getyourdoc.Constants.TIME;
import static com.example.piumal.getyourdoc.Constants.TITLE;

/**
 * Created by piumal on 4/5/17.
 */
public class SearchAppointment extends Activity implements View.OnClickListener {
    private static String[] FROM = { DATE, TITLE, TIME, DETAILS };
    private AppointmentsData appointments;
    private long date;
    private long newDate;
    private String search;
    private String title;
    private String details;
    private String time;
    private EditText searchEt;
    private ListView listView;
    private Button searchButton;
    private Controller controller;
    private ArrayList<String> appointmentsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_appointments);

        appointments = new AppointmentsData(this);
        controller = new Controller();

        Bundle bundle = getIntent().getExtras();
		/*
		 * get the selected data from the previous activity
		 */
        date = bundle.getLong("SELECTED_DATE");

		/*
		 * getting the views
		 */
        searchEt = (EditText) findViewById(R.id.search_text);
        listView = (ListView) findViewById(R.id.list_view);
        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_appointments, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_button:
                searchAppointment();
                break;
        }
    }

    /*
     * search appointment
     */
    private void searchAppointment() {
        listView.setAdapter(null);
        search = searchEt.getText().toString();

		/*
		 * the searched appointment should have a future date
		 */
        Cursor cursor = controller.getAppointment(appointments, FROM, "DATE>'"
                + date + "'", null);

        appointmentsArray = new ArrayList<String>();

        while (cursor.moveToNext()) {
            newDate = Long.parseLong(cursor.getString(0));
            title = cursor.getString(1);
            time = cursor.getString(2);
            details = cursor.getString(3);

			/*
			 * search from both title and the details whether such appointment
			 * is available in the date to come in future
			 */
            if (title.compareToIgnoreCase(search) == 0
                    || details.compareToIgnoreCase(search) == 0) {
                appointmentsArray.add(appointmentsArray.size() + 1 + "." + time
                        + " " + title);

                final StableArrayAdapter adapter = new StableArrayAdapter(this,
                        android.R.layout.simple_list_item_1, appointmentsArray);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> parent,
                                            final View view, final int position, long id) {

                        final String string = parent
                                .getItemAtPosition(position).toString();
                        String titleSt = string.substring(string.indexOf(':') + 4);

                        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                                SearchAppointment.this);
                        myAlertDialog.setTitle("Confirm view appointment");

                        myAlertDialog
                                .setMessage("Would you like to view appointment: \""
                                        + titleSt + "\"? ");
                        myAlertDialog.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0,
                                                        int arg1) {

                                        Intent intent = new Intent(
                                                SearchAppointment.this,
                                                ViewAppointment.class);
										/*
										 * taking the values form current
										 * activity to the next activity
										 */
                                        Bundle bundle = new Bundle();
                                        bundle.putLong("DATE", newDate);
                                        bundle.putString("TITLE", title);
                                        bundle.putString("TIME", time);
                                        bundle.putString("DETAILS", details);
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


