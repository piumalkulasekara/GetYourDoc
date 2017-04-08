package com.example.piumal.getyourdoc;

import android.provider.BaseColumns;

/**
 * Created by piumal on 4/5/17.
 */
public interface Constants extends BaseColumns {
    public static final String TABLE_NAME = "appointments";
    // Columns in the Appointments database
    public static final String DATE = "date";
    public static final String TITLE = "title";
    public static final String TIME = "time";
    public static final String DETAILS = "details";
}

