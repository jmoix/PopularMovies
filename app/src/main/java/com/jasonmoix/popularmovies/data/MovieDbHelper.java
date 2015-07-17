package com.jasonmoix.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jmoix on 7/16/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.TABLE_NAME + " (" +
                MoviesContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL" +
                " );";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(db);

    }

}
