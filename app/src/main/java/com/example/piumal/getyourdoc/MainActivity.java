package com.example.piumal.getyourdoc;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.view.Menu;

public class MainActivity extends Activity implements View.OnClickListener {

    private CalendarView dateCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

     /*
      * Getting the Views
      */
        Button btnCreateAppointment = (Button) findViewById(R.id.btnCreate_Appoinment);
        btnCreateAppointment.setOnClickListener(this);


        Button btnDeleteAppointment = (Button) findViewById(R.id.btnDelete_Appointment);
        btnDeleteAppointment.setOnClickListener(this);


        Button btnViewOrEditAppointment = (Button) findViewById(R.id.btnView_Edit_Appointment);
        btnViewOrEditAppointment.setOnClickListener(this);


        Button btnMoveAppointment = (Button) findViewById(R.id.btnMove_Appointment);
        btnMoveAppointment.setOnClickListener(this);

        Button btnTranslateAppointment = (Button) findViewById(R.id.btnTranslate_Appointment);
        btnTranslateAppointment.setOnClickListener(this);


        Button btnSearchAppointment = (Button) findViewById(R.id.btnSearch_Appointment);
        btnSearchAppointment.setOnClickListener(this);

        dateCV = (CalendarView) findViewById(R.id.calander_view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreate_Appoinment:
                startActivity(CreateAppointment.class, dateCV.getDate());
                break;
            case R.id.btnDelete_Appointment:
                startActivity(DeleteAppointment.class, dateCV.getDate());
                break;
            case R.id.btnMove_Appointment:
                startActivity(MoveAppointment.class, dateCV.getDate());
                break;
            case R.id.btnView_Edit_Appointment:
                startActivity(ViewOrEditAppointment.class, dateCV.getDate());
                break;
            case R.id.btnTranslate_Appointment:
                startActivity(TranslateAppointment.class, dateCV.getDate());
                break;
            case R.id.btnSearch_Appointment:
                startActivity(SearchAppointment.class, dateCV.getDate());
                break;
        }

    }

    /*
    * Starts new Activities
    * */
    private void startActivity(Class c, long i) {
        Intent intent = new Intent(MainActivity.this, c);
        Bundle bundle = new Bundle();
        bundle.putLong("SELECTED_DATE", i);
        intent.putExtras(bundle);
        startActivity(intent);

    }

}
