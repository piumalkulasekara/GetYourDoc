package com.example.piumal.getyourdoc;

import android.app.Activity;
import android.content.ContentValues;
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
 * Created by piumal on 4/6/17.
 */
public class EditAppointment extends Activity implements View.OnClickListener {
    private AppointmentsData appointments;
    private static String[] FROM = {DETAILS};
    private long date;
    private String data;
    private String title;
    private String time;
    private String details = "";
    private EditText titleEt;
    private EditText timeEt;
    private EditText detailsEt;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appointment);

        appointments = new AppointmentsData(this);
        controller = new Controller();

        Bundle bundle = getIntent().getExtras();

        /*Get the selected data from the previuos activity*/
        data = bundle.getString("DATA");

        /*Get the selected DATE from the previous activity*/
        date = bundle.getLong("SELECTED_DATE");

        /*Getting the views*/
        titleEt = (EditText) findViewById(R.id.appointment_title_text);
        timeEt = (EditText) findViewById(R.id.appointment_time_text);
        detailsEt = (EditText) findViewById(R.id.appointment_details_text);

        /*Getting the values*/
        title = data.substring(data.indexOf(':') + 4);
        time = data.substring(data.indexOf('.') + 1, data.indexOf(':') + 3);

        /*Getting the appointment to edit*/
        Cursor cursor = controller.getAppointment(appointments, FROM, "DATE='"
                + date + "' AND TITLE='" + title + "'", null);

        if (cursor.moveToFirst()) {
            details = cursor.getString(0);

        }

        titleEt.setText(title);
        timeEt.setText(time);
        detailsEt.setText(details);

        Button btnEdit = (Button) findViewById(R.id.edit_button);
        btnEdit.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu: this adds items to the action bar is it's available default.
        getMenuInflater().inflate(R.menu.edit_appointment, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_button:
                editAppointment();
                break;
        }
    }

    /*Edit selected appointments*/
    private void editAppointment() {
        /*get the values for edit*/
        String title = titleEt.getText().toString();
        String time = timeEt.getText().toString();
        String details = detailsEt.getText().toString();

        /*if the values are not null*/
        if (!title.equals("") && !time.equals("") && !details.equals("")) {
            SQLiteDatabase db = appointments.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(DATE, date + "");
            values.put(TITLE, title);
            values.put(TIME, time);
            values.put(DETAILS, details);

            try {
                /*update selectes appointment int to the database*/
                db.update(TABLE_NAME, values, "DATE='" + date + "' AND TITLE='"
                        + title + "'", null);
                Toast.makeText(this, "Data edited successfully", Toast.LENGTH_LONG).show();


            } catch (Exception e) {
                Toast.makeText(this, "Exception: " + e.toString(), Toast.LENGTH_LONG).show();

            }
            finish();
        } else {
            Toast.makeText(this, "Complete the fields beforr you edit the data", Toast.LENGTH_LONG).show();

        }

    }
}
