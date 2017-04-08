package com.example.piumal.getyourdoc;


/**
 * Created by piumal on 4/7/17.
 */

import java.sql.Date;
import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class ViewAppointment extends Activity {
    private long date;
    private String title;
    private String time;
    private String details = "";
    private TextView titleTv;
    private TextView timeTv;
    private TextView detailsTv;
    private TextView dateTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment);

        Bundle bundle = getIntent().getExtras();
        date = bundle.getLong("DATE");
        title = bundle.getString("TITLE");
        time = bundle.getString("TIME");
        details = bundle.getString("DETAILS");

        titleTv = (TextView) findViewById(R.id.appointment_title_label);
        timeTv = (TextView) findViewById(R.id.appointment_time_label);
        detailsTv = (TextView) findViewById(R.id.appointment_details_label);
        dateTv = (TextView) findViewById(R.id.appointment_date_label);

        titleTv.setText(title);
        timeTv.setText(time);
        detailsTv.setText(details);

        Date date1 = new Date(date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String dateText = dateFormat.format(date1);
        dateTv.setText(dateText);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_appointment, menu);
        return true;
    }
}

