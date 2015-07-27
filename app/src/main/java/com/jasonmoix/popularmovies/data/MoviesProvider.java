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
    public static final int REVIEWS = 102;
    public static final int REVIEWS_WITH_MOVIE = 103;
    public static final int VIDEOS = 104;
    public static final int VIDEOS_WITH_MOVIE = 105;

    public static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES + "/*", MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS + "/*", REVIEWS_WITH_MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_VIDEOS, VIDEOS);
        matcher.addURI(authority, MoviesContract.PATH_VIDEOS + "/*", VIDEOS_WITH_MOVIE);

        return(matcher);

    }
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;

        switch (match){
            case MOVIES:
                db.beginTransaction();

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

            case REVIEWS:
                db.beginTransaction();

                try {

                    for(ContentValues value : values){
                        long _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, value);
                        if(_id != -1) returnCount++;
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return(returnCount);

            case VIDEOS:
                db.beginTransaction();

                try {

                    for(ContentValues value : values){
                        long _id = db.insert(MoviesContract.VideoEntry.TABLE_NAME, null, value);
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
                        MoviesContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(
                        MoviesContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEOS:
                rowsDeleted = db.delete(
                        MoviesContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
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
            case REVIEWS:
                return MoviesContract.ReviewEntry.CONTENT_TYPE;
            case REVIEWS_WITH_MOVIE:
                return MoviesContract.ReviewEntry.CONTENT_TYPE;
            case VIDEOS:
                return MoviesContract.VideoEntry.CONTENT_TYPE;
            case VIDEOS_WITH_MOVIE:
                return MoviesContract.VideoEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = movieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id = 0;

        switch (match){
            case MOVIES:
                _id = db.insert(MoviesContract.MovieEntry.TABLE_NAME, null, values);
                if(_id > 0) returnUri = MoviesContract.MovieEntry.buildMovieUri(_id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case REVIEWS:
                _id = db.insert(MoviesContract.ReviewEntry.TABLE_NAME, null, values);
                if(_id > 0) returnUri = MoviesContract.ReviewEntry.buildReviewsUri(_id);
                else throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case VIDEOS:
                _id = db.insert(MoviesContract.VideoEntry.TABLE_NAME, null, values);
                if(_id > 0) returnUri = MoviesContract.VideoEntry.buildVideosUri(_id);
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
            case REVIEWS:
                rowsUpdated = db.update(MoviesContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case VIDEOS:
                rowsUpdated = db.update(MoviesContract.VideoEntry.TABLE_NAME, values, selection, selectionArgs);
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
