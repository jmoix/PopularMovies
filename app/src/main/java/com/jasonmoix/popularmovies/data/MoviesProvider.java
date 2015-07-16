package com.jasonmoix.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by jmoix on 7/16/2015.
 */
public class MoviesProvider extends ContentProvider {

    private MovieDbHelper movieDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final int MOVIES = 100;
    public static final int MOVIE = 101;

    static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/*", MOVIE);

        return(matcher);

    }
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;

                try{

                    for(ContentValues value : values){
                        long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, value);
                        if(_id != -1) returnCount++;
                    }
                    db.setTransactionSuccessful();

                }finally {
                    db.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);
                return(returnCount);

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted = 0;

        if(null == selection) selection = "1";
        switch (match){
            case MOVIES:
                rowsDeleted = db.delete(
                        MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsDeleted != 0) getContext().getContentResolver().notifyChange(uri, null);
        return(rowsDeleted);
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match){

            case MOVIES:
                return MoviesContract.MovieEntry.CONTENT_TYPE;
            case MOVIE:
                return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIES:
                long _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if(_id > 0) returnUri = MoviesContract.MovieEntry.buildMovieUri(_id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public boolean onCreate() {
        movieDbHelper = new MovieDbHelper(getContext());
        return(true);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;

        switch (sUriMatcher.match(uri)){
            case MOVIES:
                retCursor = movieDbHelper.getReadableDatabase().query(
                        MoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                retCursor.setNotificationUri(getContext().getContentResolver(), uri);
                return(retCursor);
            case MOVIE:
                return(null);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }


    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        movieDbHelper.close();
        super.shutdown();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;

        switch (match){
            case MOVIES:
                rowsUpdated = db.update(MoviesContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return(rowsUpdated);
    }

}
