package com.jasonmoix.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.jasonmoix.popularmovies.data.MoviesContract;

import java.util.Map;
import java.util.Set;

/**
 * Created by jmoix on 7/16/2015.
 */
public class TestUtilities extends AndroidTestCase {

    public static ContentValues createMovieValues(){
        ContentValues movieValues = new ContentValues();
        movieValues.put(MoviesContract.MovieEntry._ID, 135397);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, "/dkMD5qlogeRMiEixC4YNPUvax2T.jpg");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/uXZYawqUsChGSj54wcuBtEdUJbh.jpg");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, "Jurassic World");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "Twenty-two years after the events " +
                "of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, " +
                "Jurassic World, as originally envisioned by John Hammond.");
        movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, 7);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, 68.73452);
        movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "2015-06-12");
        return(movieValues);
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues){

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for(Map.Entry<String, Object> entry : valueSet){
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not fouind. " + error, idx == -1);

            if(valueCursor.getType(idx) == 2){
                Double expectedValue = Double.parseDouble(entry.getValue().toString());
                assertEquals("Value '" + expectedValue +
                                "' did not match the expected value '" +
                                valueCursor.getDouble(idx) + "'. " + error,
                        expectedValue, valueCursor.getDouble(idx));
            }else{
                String expectedValue = entry.getValue().toString();
                assertEquals("Value '" + expectedValue +
                                "' did not match the expected value '" +
                                valueCursor.getString(idx) + "'. " + error,
                        expectedValue, valueCursor.getString(idx));
            }

        }

    }
}
