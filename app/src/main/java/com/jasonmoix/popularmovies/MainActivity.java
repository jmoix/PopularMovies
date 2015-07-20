package com.jasonmoix.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jasonmoix.popularmovies.data.MoviesContract;
import com.jasonmoix.popularmovies.sync.FetchMovieTask;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements MovieListingFragment.Callback {

    private static final String DETAIL_TAG = "DTAG";
    private String mSortOrder;
    private MovieListingFragment movieListingFragment;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new FetchMovieTask(this).execute();

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        mSortOrder = Utils.getPreferredSortOrder(this);

        if(findViewById(R.id.movie_detail_container) != null){

            mTwoPane = true;

            if(savedInstanceState == null){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), DETAIL_TAG)
                        .commit();
            }

        }
        else{
            mTwoPane = false;

        }

        FragmentManager fm = getSupportFragmentManager();
        movieListingFragment =
                ((MovieListingFragment)fm.findFragmentById(R.id.fragment_listing));
        if(movieListingFragment == null){
            movieListingFragment = new MovieListingFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_listing, movieListingFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onItemSelected(ArrayList<String> arguments, int position){

        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(MoviesContract.MovieEntry._ID, MovieListingFragment.COL_ID);
        i.putExtra(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, arguments.get(MovieListingFragment.COL_BACKDROP_URL));
        i.putExtra(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, arguments.get(MovieListingFragment.COL_POSTER_URL));
        i.putExtra(MoviesContract.MovieEntry.COLUMN_TITLE, arguments.get(MovieListingFragment.COL_TITLE));
        i.putExtra(MoviesContract.MovieEntry.COLUMN_OVERVIEW, arguments.get(MovieListingFragment.COL_OVERVIEW));
        i.putExtra(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, arguments.get(MovieListingFragment.COL_VOTE));
        i.putExtra(MoviesContract.MovieEntry.COLUMN_POPULARITY, arguments.get(MovieListingFragment.COL_POPULARITY));
        i.putExtra(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, arguments.get(MovieListingFragment.COL_RELEASE_DATE));
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
            movieListingFragment.moveToPostion(requestCode);
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
