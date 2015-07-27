package com.jasonmoix.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.jasonmoix.popularmovies.data.MoviesContract;
import com.jasonmoix.popularmovies.sync.MoviesSyncAdapter;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MovieListingFragment.Callback {

    private static final String DETAIL_TAG = "DTAG";
    private String mSortOrder;

    public static boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MoviesSyncAdapter.initializeSyncAdapter(this);

        mSortOrder = Utils.getPreferredSortOrder(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        if(findViewById(R.id.movie_detail_container) != null){

            Log.d("Popular Movies", "Two Pane is True");

            mTwoPane = true;

            if(savedInstanceState == null){

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.movie_detail_container, new StartingFragment())
                        .commit();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_listing, new MovieListingFragment())
                        .commit();
            }

        } else{
            Log.d("Popular Movies", "Two Pane is False");
            mTwoPane = false;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_listing, new MovieListingFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onItemSelected(Uri backdropUri, Uri uri, String title, int position){

        Log.d("Popular Movies", uri.toString());

        if(mTwoPane){
            Bundle bundle = new Bundle();
            bundle.putParcelable(MovieDetailFragment.DETAIL_URI, uri);

            MovieDetailFragment detailFragment = new MovieDetailFragment();
            detailFragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.movie_detail_container, detailFragment)
                    .commit();
        }else {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra(MovieDetailFragment.DETAIL_URI, uri);
            i.putExtra(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropUri);
            i.putExtra(MoviesContract.MovieEntry.COLUMN_TITLE, title);
            startActivityForResult(i, position);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == DetailActivity.DETAIL_RESULT) {
            MovieListingFragment mf = (MovieListingFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_listing);
            if(null != mf && MovieListingFragment.mPosition != GridView.INVALID_POSITION){
                mf.moveToPosition(MovieListingFragment.mPosition);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortby = Utils.getPreferredSortOrder(this);
        Boolean resort = false;
        if(sortby != null & !sortby.equals(mSortOrder)){
            resort = true;
            MovieListingFragment mf = (MovieListingFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_listing);
            if(null != mf){
                mf.onSortOrderChanged();
            }
            mSortOrder = sortby;
        }
        if(mTwoPane) {
            if (MovieListingFragment.mPosition != GridView.INVALID_POSITION && resort == false)
                new getFirstMovieTask().execute(MovieListingFragment.mPosition);
            else {
                resort = false;
                new getFirstMovieTask().execute(0);
            }
        }
    }

    private class getFirstMovieTask extends AsyncTask<Integer, Void, Uri>{

        @Override
        protected Uri doInBackground(Integer... params) {

            Uri movieUri = null;
            Cursor movieCursor = getContentResolver().query(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    new String[]{MoviesContract.MovieEntry._ID},
                    null,
                    null,
                    Utils.getPreferredSortOrder(getBaseContext())
            );

            if(movieCursor.moveToPosition(params[0])){
                String id = movieCursor.getString(movieCursor.getColumnIndex(MoviesContract.MovieEntry._ID));
                Log.d("Movie Cursor", id);
                movieUri = MoviesContract.MovieEntry.buildMovieLocationWithId(id);
            }
            else{
                Log.d("Movie Cursor", "Empty");
            }

            return movieUri;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            super.onPostExecute(uri);

            if(uri != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(MovieDetailFragment.DETAIL_URI, uri);

                MovieDetailFragment detailFragment = new MovieDetailFragment();
                detailFragment.setArguments(bundle);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.movie_detail_container, detailFragment)
                        .commit();
            }
        }
    }

}
