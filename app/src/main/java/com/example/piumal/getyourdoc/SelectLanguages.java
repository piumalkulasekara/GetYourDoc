package com.example.piumal.getyourdoc;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by piumal on 4/8/17.
 */

public class SelectLanguages extends Activity implements View.OnClickListener {
    private ListView listView;
    private Spinner languagesSpinner;
    private Set<String> selectedLanguages;
    private ArrayAdapter<String> arrayAdapter;
    private int position = -1;
    private SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_languages);

        Button addLanguageButton = (Button) findViewById(R.id.add_language_button);
        addLanguageButton.setOnClickListener(this);

        Button removeLanguageButton = (Button) findViewById(R.id.remove_language_button);
        removeLanguageButton.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.languages_list_view);
        languagesSpinner = (Spinner) findViewById(R.id.select_language);
        preference = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        setLanguagesToSpinner();

        // selectedLanguages = new HashSet<String>();

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                                    long arg3) {
                SelectLanguages.this.position = position;
            }
        });

        selectedLanguages = preference.getStringSet("LANGUAGE",
                new HashSet<String>());
        loadLanguages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_languages, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_language_button:
                String language = languagesSpinner.getSelectedItem().toString();
                if (!selectedLanguages.contains(language)) {
                    selectedLanguages.add(language);
                    arrayAdapter.add(language);
                }
                break;
            case R.id.remove_language_button:
                if (position != -1) {
                    selectedLanguages.remove(arrayAdapter.getItem(position));
                    arrayAdapter.remove(arrayAdapter.getItem(position));
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = preference.edit();
        editor.putStringSet("LANGUAGE", selectedLanguages);
        editor.commit();

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    /*
     * set languages to spinner
     */
    private void setLanguagesToSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.languages, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languagesSpinner.setAdapter(adapter);
    }

    private void loadLanguages() {
        arrayAdapter.clear();
        for (String language : selectedLanguages) {
            arrayAdapter.add(language);
        }
    }
}
