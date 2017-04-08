package com.example.piumal.getyourdoc;

/**
 * Created by piumal on 4/4/17.
 */

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.piumal.getyourdoc.Constants.*;

public class TranslationOfAppointment extends Activity implements OnClickListener {
    private static final String TAG = "TranslateTask";
    private AppointmentsData appointments;
    private static String[] FROM = {DETAILS, TIME};
    private String data;
    private long date;
    private String title;
    private String time;
    private String details;
    private TextView fromLabel;
    private TextView toLabel;
    private Controller controller;
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private Button translateButton;
    private Button saveTranslationButton;
    private Button manageLanguageButton;

    private String fromLang;
    private String toLang;

    private TextWatcher textWatcher;
    private OnItemSelectedListener itemListener;
    private OnClickListener buttonListener;

    private String accessToken;
    private ArrayAdapter<CharSequence> adapter;

    private SharedPreferences preference;
    private Set<String> launguages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation_of_appointment);

        appointments = new AppointmentsData(this);
        controller = new Controller();

        manageLanguageButton = (Button) findViewById(R.id.manage_languages_button);
        manageLanguageButton.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        data = bundle.getString("DATA");
        date = bundle.getLong("SELECTED_DATE");

        title = data.substring(data.indexOf(':') + 4);

        findViews();
        saveTranslationButton.setOnClickListener(this);

        Cursor cursor = controller.getAppointment(appointments, FROM, "DATE='"
                + date + "' AND TITLE='" + title + "'", null);

        if (cursor.moveToFirst()) {
            details = cursor.getString(0);
            time = cursor.getString(1);
        }

        fromLabel.setText(details);

        setAdapters();
        setListeners();

        getLanguageSet();

        new GetAccessTokenTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.translation_of_appointment, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_translation_button:
                saveTranslation();
                break;
            case R.id.manage_languages_button:
                Intent intent = new Intent(TranslationOfAppointment.this,
                        SelectLanguages.class);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        getLanguageSet();
    }

    private void getLanguageSet() {
        preference = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        launguages = preference.getStringSet("LANGUAGE", new HashSet<String>());

        adapter.clear();

        for (String s : launguages) {
            adapter.add(s);
        }
    }

    private void findViews() {
        fromSpinner = (Spinner) findViewById(R.id.from_language);
        toSpinner = (Spinner) findViewById(R.id.to_language);
        fromLabel = (TextView) findViewById(R.id.from_label);
        toLabel = (TextView) findViewById(R.id.translated_label);
        translateButton = (Button) findViewById(R.id.translation_button);
        saveTranslationButton = (Button) findViewById(R.id.save_translation_button);
        manageLanguageButton = (Button) findViewById(R.id.manage_languages_button);
    }

    /* Define data source for the spinners */
    private void setAdapters() {
        // Spinner list comes from a resource,
        // Spinner user interface uses standard layouts
        adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
        // Automatically select two spinner items
        fromSpinner.setSelection(8); // English (en)
        toSpinner.setSelection(25); // Spanish (es)
    }

    private void setListeners() {
        textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                /*
				 * doTranslate2(origText.getText().toString().trim(), fromLang,
				 * toLang);
				 */
            }

            public void afterTextChanged(Editable s) {
            }
        };
        fromLabel.addTextChangedListener(textWatcher);

        itemListener = new OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View v,
                                       int position, long id) {
                fromLang = getLang(fromSpinner);
                toLang = getLang(toSpinner);
                if (accessToken != null)
                    doTranslate2(fromLabel.getText().toString().trim(),
                            fromLang, toLang);
            }

            public void onNothingSelected(AdapterView parent) {
                /* Do nothing */
            }
        };
        fromSpinner.setOnItemSelectedListener(itemListener);
        toSpinner.setOnItemSelectedListener(itemListener);

        buttonListener = new OnClickListener() {
            public void onClick(View v) {
                if (accessToken != null)
                    doTranslate2(fromLabel.getText().toString().trim(),
                            fromLang, toLang);
            }
        };
        translateButton.setOnClickListener(buttonListener);
    }

    /* Extract the language code from the current spinner item */
    private String getLang(Spinner spinner) {
        String result = spinner.getSelectedItem().toString();
        int lparen = result.indexOf('(');
        int rparen = result.indexOf(')');
        result = result.substring(lparen + 1, rparen);
        return result;
    }

    private void doTranslate2(String original, String from, String to) {
        if (accessToken != null)
            new TranslationTask().execute(original, from, to);
    }

    private class TranslationTask extends AsyncTask<String, Void, String> {
        protected void onPostExecute(String translation) {
            toLabel.setText(translation);
        }

        protected String doInBackground(String... s) {
            HttpURLConnection con2 = null;
            String result = getResources()
                    .getString(R.string.translation_error);
            String original = s[0];
            String from = s[1];
            String to = s[2];

            try {
                // Read results from the query
                BufferedReader reader;
                String uri = "http://api.microsofttranslator.com"
                        + "/v2/Http.svc/Translate?text="
                        + URLEncoder.encode(original) + "&from=" + from
                        + "&to=" + to;
                URL url_translate = new URL(uri);
                String authToken = "Bearer" + " " + accessToken;
                con2 = (HttpURLConnection) url_translate.openConnection();
                con2.setRequestProperty("Authorization", authToken);
                con2.setDoInput(true);
                con2.setReadTimeout(10000 /* milliseconds */);
                con2.setConnectTimeout(15000 /* milliseconds */);

                reader = new BufferedReader(new InputStreamReader(
                        con2.getInputStream(), "UTF-8"));
                String translated_xml = reader.readLine();
                reader.close();
                // parse the XML returned
                DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(
                        translated_xml)));
                NodeList node_list = doc.getElementsByTagName("string");
                NodeList l = node_list.item(0).getChildNodes();

                Node node;
                String translated = null;
                if (l != null && l.getLength() > 0) {
                    node = l.item(0);
                    translated = node.getNodeValue();
                }
                if (translated != null)
                    result = translated;
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (con2 != null) {
                    con2.disconnect();
                }
            }
            return result;
        }
    }

    private class GetAccessTokenTask extends AsyncTask<Void, Void, String> {
        protected void onPostExecute(String access_token) {
            accessToken = access_token;
        }

        protected String doInBackground(Void... v) {
            String result = null;
            HttpURLConnection con = null;
            String clientID = "925153320v";
            String clientSecret = "oR0rfE6G+IBAF2pauAhRxaQZF3lOcRr1RxAbe06pDaY=";
            String strTranslatorAccessURI = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13";
            String strRequestDetails = "grant_type="
                    + "client_credentials&client_id="
                    + URLEncoder.encode(clientID) + "&client_secret="
                    + URLEncoder.encode(clientSecret)
                    + "&scope=http://api.microsofttranslator.com";

            try {
                URL url = new URL(strTranslatorAccessURI);
                con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(10000 /* milliseconds */);
                con.setConnectTimeout(15000 /* milliseconds */);
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setChunkedStreamingMode(0);
                // Start the query
                con.connect();
                OutputStream out = new BufferedOutputStream(
                        con.getOutputStream());
                out.write(strRequestDetails.getBytes());
                out.flush();

                // Read results from the query
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "UTF-8"));
                String payload = reader.readLine();
                reader.close();
                out.close();
                // Parse to get translated text
                JSONObject jsonObject = new JSONObject(payload);
                result = jsonObject.getString("access_token");
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);

            } catch (JSONException e) {
                Log.e(TAG, "JSONException", e);
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            return result;
        }
    }

    private void saveTranslation() {
        String newDetail = fromLabel.getText().toString();

        ContentValues values = new ContentValues();

        values.put(DATE, date + "");
        values.put(TITLE, title);
        values.put(TIME, time);
        values.put(DETAILS, newDetail);

        SQLiteDatabase db = appointments.getWritableDatabase();
        try {
            db.update(TABLE_NAME, values, "DATE='" + date + "' AND TITLE='"
                    + title + "'", null);
            Toast.makeText(this, "Data updated successfully", Toast.LENGTH_LONG)
                    .show();
        } catch (Exception e) {
            Toast.makeText(this, "Exception: " + e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
