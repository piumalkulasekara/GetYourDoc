package com.example.piumal.getyourdoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.piumal.getyourdoc.Constants.*;

/**
 * Created by piumal on 4/5/17.
 */
public class CreateAppointment extends Activity implements View.OnClickListener {
    private static String[] FROM = {DATE, TITLE};
    private EditText titleEt;
    private EditText timeEt;
    private EditText detailsEt;
    private Button saveButton;
    public AppointmentsData appointments;
    private long date;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        controller = new Controller();

        Bundle bundle = getIntent().getExtras();
        /*
		 * get the selected date from the previous activity
		 */
        date = bundle.getLong("SELECTED_DATE");

		/*
         * getting the views
		 */
        titleEt = (EditText) findViewById(R.id.appointment_title_text);
        timeEt = (EditText) findViewById(R.id.appointment_time_text);
        detailsEt = (EditText) findViewById(R.id.appointment_details_text);
        saveButton = (Button) findViewById(R.id.save_button);

        saveButton.setOnClickListener(this);

        appointments = new AppointmentsData(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_appointment, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_button:
                addAppointent();
                break;
        }
    }

    private void addAppointent() {
        String title = titleEt.getText().toString();
        String time = timeEt.getText().toString();
        String details = detailsEt.getText().toString();

        // checks whether data entered in the text fields are not null
        if (!title.equals("") && !time.equals("") && !details.equals("")) {
            // getting the writable database
            SQLiteDatabase db = appointments.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DATE, date + "");
            values.put(TITLE, title);
            values.put(TIME, time);
            values.put(DETAILS, details);

            Cursor cursor = controller.getAppointment(appointments, FROM,
                    "DATE='" + date + "' AND TITLE='" + title + "'", null);

            try {
                /*
				 * checks whether there is an another appointment with the same
				 * data and title
				 */
                if (cursor.getCount() == 0) {
                    db.insertOrThrow(TABLE_NAME, null, values);
					/*
					 * displays if the data saved successfully
					 */
                    Toast.makeText(this, "Data saved successfully",
                            Toast.LENGTH_LONG).show();
                    finish();

                } else {
                    /*
					 * if there is an appointment with the same date and the
					 * title an error is prompt
					 */
                    AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                            CreateAppointment.this);
                    myAlertDialog.setTitle(R.string.error_label);

                    myAlertDialog
                            .setMessage("There cannot be 2 appointments for a day with the same name");
                    myAlertDialog.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0,
                                                    int arg1) {

                                }
                            });

                    myAlertDialog.show();
                    titleEt.setText(null);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Exception: " + e.toString(),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            /*
			 * displays if the no data are entered before saving
			 */
            Toast.makeText(
                    this,
                    "Enter details for the given fields before you save the data",
                    Toast.LENGTH_LONG).show();
        }
    }
}
