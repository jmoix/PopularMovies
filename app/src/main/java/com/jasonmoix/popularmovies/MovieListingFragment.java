package com.jasonmoix.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.jasonmoix.popularmovies.data.MoviesContract;
import com.jasonmoix.popularmovies.sync.MoviesSyncAdapter;

import java.util.ArrayList;

/**
 * Created by jmoix on 7/15/2015.
 */
public class MovieListingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieListingAdapter movieListingAdapter;
    private GridView mGridView;
    public static int mPosition;

    private static final int MOVIE_LOADER = 0;
    private static final String SELECTED_KEY = "selected_position";
    private static final String POPULARITY_CASE = "Popularity DESC";
    private static final String RATING_CASE = "Vote_Average DESC";

    static final int COL_ID = 0;
    static final int COL_BACKDROP_URL = 1;
    static final int COL_POSTER_URL = 2;
    static final int COL_TITLE = 3;
    static final int COL_OVERVIEW = 4;
    static final int COL_VOTE = 5;
    static final int COL_POPULARITY = 6;
    static final int COL_RELEASE_DATE = 7;


    public Uri getFirstMovieInfo(){

        Cursor movieCursor = movieListingAdapter.getCursor();
        movieCursor.moveToFirst();
        Uri movieUri = MoviesContract.MovieEntry.buildMovieLocationWithId(
                movieCursor.getString(COL_ID));
        return(movieUri);

    }

    public interface Callback {
        void onItemSelected(Uri backdropPath, Uri uri, String title, int position);
    }

    public void moveToPosition(int position){
        mGridView.smoothScrollToPosition(position);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        movieListingAdapter = new MovieListingAdapter(getActivity(), null, 0);

        View view = inflater.inflate(R.layout.fragment_movie_listing, container, false);
        mGridView = (GridView)view.findViewById(R.id.gridview_movies);
        mGridView.setAdapter(movieListingAdapter);
        mGridView.setClickable(true);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {

                    cursor.moveToPosition(position);

                    String movieId = cursor.getString(COL_ID);
                    Uri movieUri = MoviesContract.MovieEntry.buildMovieLocationWithId(movieId);

                    Uri backdropPath = Uri.parse(getString(R.string.base_moviebackdrop_url, cursor.getString(COL_BACKDROP_URL)));

                    String movieTitle = cursor.getString(COL_TITLE);

                    mPosition = position;
                    ((Callback) getActivity()).onItemSelected(backdropPath, movieUri, movieTitle, mPosition);

                }
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);

        }else{

            mPosition = GridView.INVALID_POSITION;

        }

        return view;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieListingAdapter.swapCursor(data);
        if(mPosition != GridView.INVALID_POSITION){
            mGridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieListingAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != GridView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    public void onSortOrderChanged(){

        String sortOrder = Utils.getPreferredSortOrder(getActivity().getBaseContext());
        switch (sortOrder){
            case POPULARITY_CASE:
                Toast.makeText(getActivity().getBaseContext(),
                        getString(R.string.settings_changed_popularity), Toast.LENGTH_SHORT).show();
                break;
            case RATING_CASE:
                Toast.makeText(getActivity().getBaseContext(),
                        getString(R.string.settings_changed_rating), Toast.LENGTH_SHORT).show();
                break;
        }
        Cursor cursor = getActivity().getContentResolver().query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                Utils.getPreferredSortOrder(getActivity().getBaseContext()),
                null
        );
        movieListingAdapter.swapCursor(cursor);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = Utils.getPreferredSortOrder(getActivity().getBaseContext());
        Uri movieLocationUri = MoviesContract.MovieEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                movieLocationUri,
                null,
                null,
                null,
                sortOrder);

    }

}
