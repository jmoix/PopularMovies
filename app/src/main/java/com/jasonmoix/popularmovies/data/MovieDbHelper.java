package com.jasonmoix.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jmoix on 7/16/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL," +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL," +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                " );";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);

    }

    public  static class MovieEntry {

        static final String TABLE_NAME = "MovieDB";

        static final String _ID = "_ID";
        static final String COLUMN_BACKDROP_PATH = "Backdrop_Path";
        static final String COLUMN_POSTER_PATH = "Poster_Path";
        static final String COLUMN_TITLE = "Title";
        static final String COLUMN_OVERVIEW = "Overview";
        static final String COLUMN_VOTE_AVERAGE = "Vote_Average";
        static final String COLUMN_POPULARITY = "Popularity";
        static final String COLUMN_RELEASE_DATE = "Release_Date";

    }

}
