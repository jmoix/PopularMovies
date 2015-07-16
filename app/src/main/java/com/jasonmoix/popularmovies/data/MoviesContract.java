package com.jasonmoix.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jmoix on 7/16/2015.
 */
public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.jasonmoix.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String TABLE_NAME = "MovieDB";

        public static final String _ID = "_ID";
        public static final String COLUMN_BACKDROP_PATH = "Backdrop_Path";
        public static final String COLUMN_POSTER_PATH = "Poster_Path";
        public static final String COLUMN_TITLE = "Title";
        public static final String COLUMN_OVERVIEW = "Overview";
        public static final String COLUMN_VOTE_AVERAGE = "Vote_Average";
        public static final String COLUMN_POPULARITY = "Popularity";
        public static final String COLUMN_RELEASE_DATE = "Release_Date";

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
