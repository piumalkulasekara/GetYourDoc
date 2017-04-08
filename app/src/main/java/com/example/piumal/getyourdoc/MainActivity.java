package com.example.piumal.getyourdoc;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.view.Menu;

public class MainActivity extends Activity implements View.OnClickListener {
    private CalendarView dateCv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		/*
         * getting the views
		 */
        Button createAppointmentButton = (Button) findViewById(R.id.create_appointment_button);
        createAppointmentButton.setOnClickListener(this);

        Button deleteAppointmentButton = (Button) findViewById(R.id.delete_appointment_button);
        deleteAppointmentButton.setOnClickListener(this);

        Button viewOrEditAppointmentsButton = (Button) findViewById(R.id.view_edit_appointments_button);
        viewOrEditAppointmentsButton.setOnClickListener(this);

        Button moveAppointmaentsButton = (Button) findViewById(R.id.move_appointment_button);
        moveAppointmaentsButton.setOnClickListener(this);

        Button translateAppointmentsButton = (Button) findViewById(R.id.translate_appointment_button);
        translateAppointmentsButton.setOnClickListener(this);

        Button searchAppointmentsButton = (Button) findViewById(R.id.search_appointment_button);
        searchAppointmentsButton.setOnClickListener(this);

        dateCv = (CalendarView) findViewById(R.id.calendar_view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_appointment_button:
                startActivity(CreateAppointment.class, dateCv.getDate());
                break;
            case R.id.delete_appointment_button:
                startActivity(DeleteAppointment.class, dateCv.getDate());
                break;
            case R.id.view_edit_appointments_button:
                startActivity(ViewOrEditAppointment.class, dateCv.getDate());
                break;
            case R.id.move_appointment_button:
                startActivity(MoveAppointment.class, dateCv.getDate());
                break;
            case R.id.translate_appointment_button:
                startActivity(TranslateAppointment.class, dateCv.getDate());
                break;
            case R.id.search_appointment_button:
                long currentDate = System.currentTimeMillis();
                startActivity(SearchAppointment.class, currentDate);
                break;
        }
    }

    /*
     * starts new activity
     */
    private void startActivity(Class c, long i) {
        Intent intent = new Intent(MainActivity.this, c);
        Bundle bundle = new Bundle();
        bundle.putLong("SELECTED_DATE", i);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
