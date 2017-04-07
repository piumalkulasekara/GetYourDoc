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
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.piumal.getyourdoc.Constants.*;

/**
 * Created by piumal on 4/5/17.
 */
public class TranslateAppointment extends Activity {
    private AppointmentsData appointments;
    private static String[] FROM = {DATE, TITLE, TIME, DETAILS};
    private long date;
    private ListView listView;
    private ArrayList<String> appointmentsArray;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate_appointment);

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
        getMenuInflater().inflate(R.menu.translate_appointment, menu);
        return true;
    }


    private void setAppointmentsToListView() {
        // setting the appointments to the list view
        listView.setAdapter(null);
        Cursor cursor = controller.getAppointment(appointments, FROM, "DATE='"
                + date + "'", TIME);
        appointmentsArray = new ArrayList<String>();


        /*Until there are cursors*/
        while (cursor.moveToNext()) {
            String title = cursor.getString(1);
            String time = cursor.getString(2);

            //Adding the values to appointments array
            appointmentsArray.add(appointmentsArray.size() + 1 + "." + time + " " + title);
            final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, appointmentsArray);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view, final int position, long id) {
                    final String string = parent.getItemAtPosition(position).toString();

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(TranslateAppointment.this);
                    alertBuilder.setTitle("Confirm to translate appointpointme");

                    alertBuilder.setMessage("Would you prefer to chance the appointment?");
                    alertBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(TranslateAppointment.this, TranslationOfAppointment.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("DATA", string);
                            bundle.putLong("SELECTED_DATE", date);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

                    alertBuilder.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                }
                            });
                    alertBuilder.show();

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

