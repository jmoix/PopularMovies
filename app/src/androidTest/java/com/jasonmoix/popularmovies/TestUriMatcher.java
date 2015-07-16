package com.jasonmoix.popularmovies;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.jasonmoix.popularmovies.data.MoviesContract;
import com.jasonmoix.popularmovies.data.MoviesProvider;

/**
 * Created by jmoix on 7/16/2015.
 */
public class TestUriMatcher extends AndroidTestCase {

    private static final long MOVIE_ID = 135397L;

    private static final Uri TEST_MOVIE_DIR = MoviesContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_ID = MoviesContract.MovieEntry.buildMovieLocationWithId(MOVIE_ID);

    public void testUriMatcher(){

        UriMatcher testMatcher = MoviesProvider.buildUriMatcher();

        assertEquals("Error: Movies URI matched Incorrectly",
                testMatcher.match(TEST_MOVIE_DIR), MoviesProvider.MOVIES);

        assertEquals("Error: Movies URI matched Incorrectly",
                testMatcher.match(TEST_MOVIE_WITH_ID), MoviesProvider.MOVIE);

    }
}
