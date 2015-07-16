package com.jasonmoix.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.jasonmoix.popularmovies.data.MovieDbHelper;
import com.jasonmoix.popularmovies.data.MoviesContract;

import java.util.HashSet;

/**
 * Created by jmoix on 7/16/2015.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteDatabase(){
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    public void setUp(){
        deleteDatabase();
    }

    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MoviesContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        //have we created the tables?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain all the tables
        assertTrue("Error: Your database was created without all of the tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MoviesContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: Unable to query for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MoviesContract.MovieEntry._ID);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_POPULARITY);
        movieColumnHashSet.add(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required movie entry columns.",
                movieColumnHashSet.isEmpty());
        db.close();

    }

    public void testMovieTable(){

        //get a writable database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //create movie values
        ContentValues movieValues = TestUtilities.createMovieValues();

        //Insert row to movies table and get row ID back
        long movieRowId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, movieValues);
        assertTrue(movieRowId != -1);

        //Query the database and get cursor back
        Cursor movieCursor = db.query(
                MoviesContract.MovieEntry.TABLE_NAME, //table to query
                null, //which columns to return
                null, //cols for where clause
                null, //vals for where clause
                null, //cols for group by clause
                null, //cols to filter by row groups
                null //sort order
        );
        assertTrue("Error: No records returned", movieCursor.moveToFirst());

        //Validate Entry
        TestUtilities.validateCurrentRecord("testInsertReadDb movieEntry failed to validate",
                movieCursor, movieValues);

        //Move cursor to ensure existence of single record
        assertFalse("Error: more than one record returned", movieCursor.moveToNext());

        //close cursor and database
        movieCursor.close();
        dbHelper.close();


    }

}
