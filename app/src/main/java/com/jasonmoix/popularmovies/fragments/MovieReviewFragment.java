package com.jasonmoix.popularmovies.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasonmoix.popularmovies.R;
import com.jasonmoix.popularmovies.activities.DetailActivity;
import com.jasonmoix.popularmovies.adapters.ReviewRecycler;
import com.jasonmoix.popularmovies.data.MoviesContract;

/**
 * Created by jmoix on 7/30/2015.
 */
public class MovieReviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static int mPosition;
    private static int REVIEW_LIST_LOADER = 2;
    private ReviewRecycler reviewRecycler;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private CardView cardView;
    private Uri mUri;

    public static MovieReviewFragment newInstance(Bundle bundle){

        MovieReviewFragment fragment = new MovieReviewFragment();
        fragment.setArguments(bundle);
        return(fragment);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(REVIEW_LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        reviewRecycler = new ReviewRecycler(getActivity().getBaseContext(), null);
        View rootView = inflater.inflate(R.layout.fragment_movie_reviews, container, false);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.scrollView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(reviewRecycler);

        mUri = getArguments().getParcelable(MovieDetailFragment.DETAIL_URI);
        emptyView = (TextView)rootView.findViewById(R.id.no_items);
        cardView = (CardView)rootView.findViewById(R.id.no_items_view);

        return(rootView);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(mUri != null) {

            return new CursorLoader(getActivity(),
                    MoviesContract.ReviewEntry.CONTENT_URI,
                    null,
                    MoviesContract.ReviewEntry.COLUMN_MOVIE_ID + " =?",
                    new String[]{MoviesContract.MovieEntry.getMovieIdFromURI(mUri)},
                    null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        reviewRecycler.swapCursor(data);
        if(data == null || data.getCount() == 0){
            emptyView.setText(getResources().getString(R.string.no_reviews));
            cardView.setVisibility(View.VISIBLE);
        }else{
            cardView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        reviewRecycler.swapCursor(null);
    }

    public void reloadData(Uri uri){
        mUri = uri;
        getLoaderManager().restartLoader(REVIEW_LIST_LOADER, null, this);
    }

}
