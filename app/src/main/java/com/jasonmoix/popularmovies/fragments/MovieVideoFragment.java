package com.jasonmoix.popularmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasonmoix.popularmovies.R;
import com.jasonmoix.popularmovies.activities.MainActivity;
import com.jasonmoix.popularmovies.adapters.VideoRecycler;
import com.jasonmoix.popularmovies.data.MoviesContract;

import org.solovyev.android.views.llm.DividerItemDecoration;

/**
 * Created by jmoix on 7/30/2015.
 */
public class MovieVideoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static int VIDEO_LIST_LOADER = 3;
    private ShareActionProvider mShareActionProvider;
    private RecyclerView recyclerView;
    private VideoRecycler videoRecycler;
    private TextView emptyView;
    private CardView cardView;
    private Uri mUri;
    private String firstVideoKey;

    public static MovieVideoFragment newInstance(){
        return new MovieVideoFragment();
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

        setHasOptionsMenu(true);
        final LinearLayoutManager layoutManager =
                new org.solovyev.android.views.llm.LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.scrollView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        recyclerView.setAdapter(videoRecycler);

        if(MainActivity.mTwoPane) {
            ((TextView) rootView.findViewById(R.id.header)).setText(getString(R.string.title_detail_videos));
        }

        mUri = MainActivity.mUri;

        emptyView = (TextView)rootView.findViewById(R.id.no_items);
        cardView = (CardView)rootView.findViewById(R.id.no_items_view);

        return(rootView);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(mUri != null) {
            return new CursorLoader(getActivity(),
                    MoviesContract.VideoEntry.CONTENT_URI,
                    null,
                    MoviesContract.VideoEntry.COLUMN_MOVIE_ID + " =?",
                    new String[]{MoviesContract.MovieEntry.getMovieIdFromURI(mUri)},
                    null);
        }
        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        videoRecycler.swapCursor(data);
        if(data == null || data.getCount() == 0){
            emptyView.setText(getResources().getString(R.string.no_videos));
            cardView.setVisibility(View.VISIBLE);
        }else{
            data.moveToFirst();
            firstVideoKey = data.getString(data.getColumnIndex(MoviesContract.VideoEntry.COLUMN_KEY));
            cardView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_videos, menu);

        MenuItem item = menu.findItem(R.id.share);

        mShareActionProvider = (ShareActionProvider)MenuItemCompat.getActionProvider(item);

    }

    public void setShareIntent(String movieKey){

        if(movieKey != null){

            String url = getString(R.string.base_movievideo_url, movieKey);

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, url);
            intent.setType("text/plain");

            mShareActionProvider.setShareIntent(intent);

        }else{

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "Popular Movies");
            intent.setType("text/plain");

            mShareActionProvider.setShareIntent(intent);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.share){
            Log.d("Popular Movies", "share pressed");
            String message;
            if(firstVideoKey != null){
                message = getString(R.string.base_movievideo_url, firstVideoKey);
            }else{
                message = getString(R.string.default_share_message);
            }
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            intent.setType("text/plain");
            startActivity(intent);
            return true;
        }
        return false;

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        videoRecycler.swapCursor(null);
    }
}
