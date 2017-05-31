package com.example.piumal.getyourdoc.thesaurus;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.piumal.getyourdoc.R;


public class ThesaurusAdapter extends ArrayAdapter<Synonym> {

    TextView categoryTV , synonymsTV;


    public ThesaurusAdapter(Context context, int textViewResourceId, List<Synonym> synonyms) {
        super(context, textViewResourceId, synonyms);
    }

    /**
     *
     * This method will create the rows for the list view
     */
    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        RelativeLayout row = (RelativeLayout)convertView;
        if(null == row){
            //No recycled View, we have to inflate one.
            LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = (RelativeLayout)inflater.inflate(R.layout.list_row_thesaurus, null);
        }

        //initializing the two text views
        categoryTV = (TextView)row.findViewById(R.id.categoryTextView);
        synonymsTV = (TextView)row.findViewById(R.id.synonymsTextView);


        //Set the resulting synonym category and synonyms in the TextViews
        categoryTV.setText(getItem(pos).getCategory());
        synonymsTV.setText(getItem(pos).getSynonyms());

        return row;


    }

}

