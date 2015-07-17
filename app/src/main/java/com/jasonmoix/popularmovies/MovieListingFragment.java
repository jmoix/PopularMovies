package com.jasonmoix.popularmovies;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.jasonmoix.popularmovies.data.MoviesContract;

/**
 * Created by jmoix on 7/15/2015.
 */
public class MovieListingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MovieListingAdapter movieListingAdapter;
    private GridView mGridView;
    private int mPosition = GridView.INVALID_POSITION;

    private static final int MOVIE_LOADER = 0;
    private static final String SELECTED_KEY = "selected_position";

    static final int COL_POSTER_URL = 2;

    public interface Callback {
        void onItemSelected();
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

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
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

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != GridView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MoviesContract.MovieEntry.COLUMN_POPULARITY + "ASC";
        Uri movieLocationUri = MoviesContract.MovieEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                movieLocationUri,
                null,
                null,
                null,
                null);

    }
}
