package com.example.piumal.getyourdoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
    private EditText titleEditText;
    private EditText timeEditText;
    private EditText detailsEditText;
    private Button btnSave;
    public AppointmentsData appointmentsData;
    private long date;
    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        controller= new Controller();

        Bundle bundle = getIntent().getExtras();

        /*
        * get the selected date from the previous activity
        * */
        date =bundle.getLong("SELECTED_DATE");

        /*
        * getting the view
        * */
        timeEditText = (EditText) findViewById(R.id.appointment_title_text);
        timeEditText = (EditText) findViewById(R.id.appointment_time_text);
        detailsEditText = (EditText) findViewById(R.id.appointment_details_text);
        btnSave = (Button) findViewById(R.id.save_button);
        btnSave.setOnClickListener(this);

        appointmentsData = new AppointmentsData(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_button:
                addAppointment();
                break;
        }

    }

    private void addAppointment() {
        String title = titleEditText.getText().toString();
        String time = timeEditText.getText().toString();
        String details = detailsEditText.getText().toString();


        //Check whether data entered in the text fields are not null
        if(!title.equals("") && !time.equals("") && !details.equals("")){
            //Getting the writable database.
            SQLiteDatabase db = appointmentsData.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(DATE, date + "");
            values.put(TITLE, title);
            values.put(TIME, time);
            values.put(DETAILS, details);

            Cursor cursor = controller.getAppointment(appointmentsData, FROM, "DATE="+ date+ " AND TITLE="+title+"", null);

            try {
                /*
                * Check whethe there is another appointment
                * with the same date and title
                * */
                if(cursor.getCount()==0){
                    db.insertOrThrow(TABLE_NAME, null,values);

                    /*Displays if the data saved successfully*/
                    Toast.makeText(this,"Data Saved Successfully", Toast.LENGTH_LONG).show();
                    finish();

                } else {
                    /*
                    * if there is an appointment with the same date and the
                    * title and error will occure
                    * */
                    AlertDialog.Builder errorAlerDialog = new AlertDialog.Builder(CreateAppointment.this);
                    errorAlerDialog.setTitle(R.string.error_label);
                    errorAlerDialog.setMessage("Error: 2 Appointments for a day with Same Name");
                    errorAlerDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    errorAlerDialog.show();
                    titleEditText.setText(null);
                }

            }catch (Exception e){
                Toast.makeText(this, "Exception: " + e.toString(), Toast.LENGTH_LONG).show();

            }


        } else {
            /*
            * displays if the no data are entered before saving
            * */
            Toast.makeText(this, "Enter Details fo the given fileds befor you save.", Toast.LENGTH_LONG).show();

        }
    }
}
