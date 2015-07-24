package com.jasonmoix.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.jasonmoix.popularmovies.data.MoviesContract;
import com.jasonmoix.popularmovies.sync.MoviesSyncAdapter;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MovieListingFragment.Callback {

    private static final String DETAIL_TAG = "DTAG";
    private String mSortOrder;
    private MovieListingFragment movieListingFragment;

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

        FragmentManager fm = getSupportFragmentManager();
        movieListingFragment =
                ((MovieListingFragment)fm.findFragmentById(R.id.fragment_listing));
        if(movieListingFragment == null){
            movieListingFragment = new MovieListingFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_listing, movieListingFragment)
                    .commit();
        }

        if(findViewById(R.id.movie_detail_container) != null){

            Log.d("Popular Movies", "Two Pane is True");

            mTwoPane = true;

            if(savedInstanceState == null){
                MovieDetailFragment detailFragment = new MovieDetailFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment())
                        .commit();
            }

        } else{
            Log.d("Popular Movies", "Two Pane is False");
            mTwoPane = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onItemSelected(Uri backdropUri, Uri uri, int position){

        Log.d("Popular Movies", uri.toString());

        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(MovieDetailFragment.DETAIL_URI, uri);
        i.putExtra(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, backdropUri);
        startActivityForResult(i, position);

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
            movieListingFragment.moveToPosition(requestCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sortby = Utils.getPreferredSortOrder(this);
        if(sortby != null & !sortby.equals(mSortOrder)){
            MovieListingFragment mf = (MovieListingFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_listing);
            if(null != mf){
                mf.onSortOrderChanged();
            }
            mSortOrder = sortby;
        }
    }
}
