package com.jasonmoix.popularmovies;

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

import com.jasonmoix.popularmovies.data.MoviesContract;

/**
 * Created by jmoix on 7/30/2015.
 */
public class MovieVideoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static int mPosition;
    private static int VIDEO_LIST_LOADER = 3;
    private RecyclerView recyclerView;
    private VideoRecycler videoRecycler;
    private TextView emptyView;
    private CardView cardView;

    public static MovieVideoFragment newInstance(Bundle bundle){
        MovieVideoFragment fragment = new MovieVideoFragment();
        fragment.setArguments(bundle);
        return(fragment);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(VIDEO_LIST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        videoRecycler = new VideoRecycler(getActivity().getBaseContext(), null);
        View rootView = inflater.inflate(R.layout.fragment_movie_reviews, container, false);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.scrollView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(videoRecycler);

        emptyView = (TextView)rootView.findViewById(R.id.no_items);
        cardView = (CardView)rootView.findViewById(R.id.no_items_view);

        return(rootView);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri mUri = getArguments().getParcelable(MovieDetailFragment.DETAIL_URI);

        return new CursorLoader(getActivity(),
                MoviesContract.VideoEntry.CONTENT_URI,
                null,
                MoviesContract.VideoEntry.COLUMN_MOVIE_ID + " =?",
                new String[]{MoviesContract.MovieEntry.getMovieIdFromURI(mUri)},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        videoRecycler.swapCursor(data);
        if(data == null || data.getCount() == 0){
            emptyView.setText(getResources().getString(R.string.no_videos));
            cardView.setVisibility(View.VISIBLE);
        }else{
            cardView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        videoRecycler.swapCursor(null);
    }
}
