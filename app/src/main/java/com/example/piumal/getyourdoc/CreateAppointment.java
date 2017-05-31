package com.example.piumal.getyourdoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.piumal.getyourdoc.thesaurus.ThesaurusAdapter;
import com.example.piumal.getyourdoc.thesaurus.ThesaurusXMLPullParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.example.piumal.getyourdoc.Constants.DATE;
import static com.example.piumal.getyourdoc.Constants.DETAILS;
import static com.example.piumal.getyourdoc.Constants.TABLE_NAME;
import static com.example.piumal.getyourdoc.Constants.TIME;
import static com.example.piumal.getyourdoc.Constants.TITLE;

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

    //Thesaurus
    private ThesaurusAdapter thesaurusAdapter;
    private ListView synonymlist; //list view to store the synonyms
    EditText input;
    private Button checkThesaurus;
    PopupWindow popupWindow;

    //variables to store the input from the text box
    private String inputWord;
    //constant for the thesaurus service key
    public static final String THESAURUS_KEY = "VZzyoquJHsPcVb6TbHv9"; //obtain one from here http://thesaurus.altervista.org/mykey
    //variable to store the language
    private String lang = "en_US";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_appointment);

        controller = new Controller();

        Bundle bundle = getIntent().getExtras();

        /*get the selected date from the previous activity*/
        date = bundle.getLong("SELECTED_DATE");

		/*getting the views*/
        titleEt = (EditText) findViewById(R.id.appointment_title_text);
        timeEt = (EditText) findViewById(R.id.appointment_time_text);
        detailsEt = (EditText) findViewById(R.id.appointment_details_text);
        saveButton = (Button) findViewById(R.id.save_button);
        checkThesaurus = (Button) findViewById(R.id.thesaurus_check_button);
        input = (EditText)findViewById(R.id.thesaurus_text);


        checkThesaurus.setOnClickListener(this);
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
            case R.id.thesaurus_check_button:
                inputWord = input.getText().toString();
                resultPopUp(v);
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
                db.close();
                cursor.close();
            } catch (Exception e) {
                Toast.makeText(this, "Exception: " + e.toString(),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            /*
			 * displays if the no data are entered before saving
			 */
            Toast.makeText(this, "Enter details for the given fields before you save the data",
                    Toast.LENGTH_LONG).show();
        }
    }

    //**Thesaurus Functions**//
    //Helper method to determine if Internet connection is available.
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     *
     * This function creates a popup window that will display all the results of the
     * returned XML
     *
     * @param v The current view instance is passed
     */
    private void resultPopUp (View v) {

        try {
            //get an instance of layout inflater
            LayoutInflater inflater = (LayoutInflater) CreateAppointment.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //initiate the view
            View layout = inflater.inflate(R.layout.list_view_thesaurus,
                    (ViewGroup) findViewById(R.id.popUpList));

            //initialize a size for the popup
            popupWindow = new PopupWindow(layout, 1000, 800 ,  true);
            // display the popup in the center
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            //Get reference to our ListView
            synonymlist = (ListView) layout.findViewById(R.id.synonymList);

		    /*
		     * If network is available download the xml from the Internet.
		     * If not toast internet error and close the popup
		    */
            if(isNetworkAvailable()){

                CreateAppointment.SitesDownloadTask download = new CreateAppointment.SitesDownloadTask();
                download.execute();
            }else{

                Toast.makeText(getBaseContext() , "No internet Connection. Please connect " +
                        "your device to the internet and try again" , Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(),"Error",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * AsyncTask that will download the xml file for us and store it locally.
     * After the download is done we'll parse the local file.
     */
    private class SitesDownloadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            //Download the file
            try {
                DownloadFromUrl("http://thesaurus.altervista.org/thesaurus/v1?word=" + inputWord +
                                "&language="+ lang +"&%20key="+ THESAURUS_KEY +"&output=xml",
                        openFileOutput("synonyms.xml", Context.MODE_PRIVATE));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){

            //setup our Adapter and set it to the ListView.
            thesaurusAdapter = new ThesaurusAdapter(CreateAppointment.this, -1,
                    ThesaurusXMLPullParser.getSynonymsFromFile(CreateAppointment.this));
            synonymlist.setAdapter(thesaurusAdapter);

        }
    }

    /**
     * This method will try to download the xml form the internet
     * @param URL URL to make the request
     * @param fos The name to store the XML file
     */
    public static void DownloadFromUrl(String URL, FileOutputStream fos) {
        try {

            java.net.URL url = new URL(URL); //URL of the file

			/* Open a connection to that URL. */
            URLConnection connection = url.openConnection();


            //input stream that'll read from the connection
            InputStream is = connection.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            //buffer output stream that'll write to the xml file
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            //write to the file while reading
            byte data[] = new byte[1024];
            int count;
            //loop and read the current chunk
            while ((count = bis.read(data)) != -1) {
                //write this chunk
                bos.write(data, 0, count);
            }

            bos.flush();
            bos.close();

        } catch (IOException e) {
        }
    }
}
