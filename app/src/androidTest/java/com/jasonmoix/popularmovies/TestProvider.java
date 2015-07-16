package com.jasonmoix.popularmovies;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.jasonmoix.popularmovies.data.MovieDbHelper;
import com.jasonmoix.popularmovies.data.MoviesContract;
import com.jasonmoix.popularmovies.data.MoviesProvider;

import junit.framework.Test;

/**
 * Created by jmoix on 7/16/2015.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider(){
        mContext.getContentResolver().delete(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: Records not deleted", 0, cursor.getCount());
        cursor.close();

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    public void testProviderRegistry(){

        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(), MoviesProvider.class.getName());

        try{

            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                " instead of authority: " + MoviesContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MoviesContract.CONTENT_AUTHORITY);

        }catch (PackageManager.NameNotFoundException e){
            assertTrue("Error: MoviesProvider not registered at " + mContext.getPackageName(), false);
        }

    }

    public void testGetType(){

        String type = mContext.getContentResolver().getType(MoviesContract.MovieEntry.CONTENT_URI);
        assertEquals("Error: The MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MoviesContract.MovieEntry.CONTENT_TYPE, type);

        long testId = 135397L;
        type = mContext.getContentResolver().getType(MoviesContract.MovieEntry.buildMovieLocationWithId(testId));
        assertEquals("Error: The MovieEntry CONTENT_URI with Id should return MovieEntry.CONTENT_ITEM_TYPE",
                MoviesContract.MovieEntry.CONTENT_ITEM_TYPE, type);

    }

    public void testBasicMovieQuery(){

        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createMovieValues();

        //Insert row to movies table and get row ID back
        long movieRowId = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, testValues);
        assertTrue(movieRowId != -1);

        db.close();

        Cursor movieCursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testBasicMovieQuery", movieCursor, testValues);
    }

    public void testUpdateLocation(){

        ContentValues values = TestUtilities.createMovieValues();

        Uri locationUri = mContext.getContentResolver()
                .insert(MoviesContract.MovieEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        assertTrue(locationRowId != -1);

        ContentValues updatedValue = new ContentValues(values);
        updatedValue.put(MoviesContract.MovieEntry._ID, locationRowId);
        updatedValue.put(MoviesContract.MovieEntry.COLUMN_TITLE, "Movie Title");

        Cursor locationCursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        locationCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MoviesContract.MovieEntry.CONTENT_URI, updatedValue, MoviesContract.MovieEntry._ID + "= ?",
                    new String[]{ Long.toString(locationRowId)}
        );
        assertEquals(count, 1);

        tco.waitForNotificationOrFail();

        locationCursor.unregisterContentObserver(tco);
        locationCursor.close();

        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                MoviesContract.MovieEntry._ID + " = " + locationRowId,
                null,
                null
        );

        TestUtilities.validateCursor("testUpdateMovie. Error validating Movie entry update.",
                cursor, updatedValue);
        cursor.close();

    }

    public void testInsertReadProvider(){

        ContentValues testValues = TestUtilities.createMovieValues();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(MoviesContract.MovieEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);


        long locationRowId = ContentUris.parseId(movieUri);

        assertTrue(locationRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry",
                cursor, testValues);


    }

    public void testDeleteRecords(){

        testInsertReadProvider();

        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MovieEntry.CONTENT_URI, true, movieObserver);

        deleteAllRecordsFromProvider();

        movieObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);

    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertWeatherValues(){

        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for(int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++){
            ContentValues movieValues = new ContentValues();
            movieValues.put(MoviesContract.MovieEntry._ID, i);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, "/dkMD5qlogeRMiEixC4YNPUvax2T.jpg");
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, "/uXZYawqUsChGSj54wcuBtEdUJbh.jpg");
            movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, "Jurassic World");
            movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, "Twenty-two years after the events " +
                    "of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, " +
                    "Jurassic World, as originally envisioned by John Hammond.");
            movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, 7);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, 68.73452);
            movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, "2015-06-12");
            returnContentValues[i] = movieValues;
        }
        return(returnContentValues);

    }

    public void testBulkInsert(){

        ContentValues[] bulkInsertContentValues = createBulkInsertWeatherValues();

        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MoviesContract.MovieEntry.CONTENT_URI, true, movieObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, bulkInsertContentValues);

        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        Cursor cursor = mContext.getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        cursor.moveToFirst();
        for(int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()){
            TestUtilities.validateCurrentRecord("testBulkInsert. Error validating MovieEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();


    }

}
