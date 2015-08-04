package com.jasonmoix.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Movie;

/**
 * Created by jmoix on 7/16/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

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
                MoviesContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MoviesContract.MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL" +
                " );";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MoviesContract.ReviewEntry.TABLE_NAME + " (" +
                MoviesContract.ReviewEntry._ID + " TEXT PRIMARY KEY," +
                MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MoviesContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL," +
                MoviesContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL," +
                MoviesContract.ReviewEntry.COLUMN_URL + " TEXT NOT NULL," +
                " FOREIGN KEY (" + MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MovieEntry.TABLE_NAME + " (" + MoviesContract.MovieEntry._ID + ") " +
                " );";

        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + MoviesContract.VideoEntry.TABLE_NAME + " (" +
                MoviesContract.VideoEntry._ID + " TEXT PRIMARY KEY," +
                MoviesContract.VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                MoviesContract.VideoEntry.COLUMN_ISO + " TEXT NOT NULL," +
                MoviesContract.VideoEntry.COLUMN_KEY + " TEXT NOT NULL," +
                MoviesContract.VideoEntry.COLUMN_NAME + " TEXT NOT NULL," +
                MoviesContract.VideoEntry.COLUMN_SITE + " TEXT NOT NULL," +
                MoviesContract.VideoEntry.COLUMN_SIZE + " INTEGER NOT NULL," +
                MoviesContract.VideoEntry.COLUMN_TYPE + " TEXT NOT NULL," +
                " FOREIGN KEY (" + MoviesContract.VideoEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MoviesContract.MovieEntry.TABLE_NAME + " (" + MoviesContract.MovieEntry._ID + ") " +
                " );";


        db.execSQL(SQL_CREATE_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_VIDEOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.VideoEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MovieEntry.TABLE_NAME);
        onCreate(db);

    }

}
