package com.example.piumal.getyourdoc;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.ContentValues;
import android.database.Cursor;
import android.widget.Button;
import android.widget.EditText;
import android.database.sqlite.SQLiteDatabase;


public class MainActivity extends Activity implements View.OnClickListener {

    private AppointmentsDate appointments;
    private static String[] FROM = {DETAILS};
    private long date;
    private String data;
    private String title;
    private String time;
    private String details = "";
    private EditText titleET;
    private EditText timeET;
    private EditText detailsET;
    private Controller controller;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View view) {

    }
}
