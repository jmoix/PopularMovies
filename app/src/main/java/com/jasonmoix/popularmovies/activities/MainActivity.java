package com.jasonmoix.popularmovies.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import com.jasonmoix.popularmovies.fragments.MovieDetailFragment;
import com.jasonmoix.popularmovies.fragments.MovieListingFragment;
import com.jasonmoix.popularmovies.R;
import com.jasonmoix.popularmovies.fragments.MovieReviewFragment;
import com.jasonmoix.popularmovies.fragments.MovieVideoFragment;
import com.jasonmoix.popularmovies.fragments.StartingFragment;
import com.jasonmoix.popularmovies.tools.Utils;
import com.jasonmoix.popularmovies.data.MoviesContract;
import com.jasonmoix.popularmovies.sync.MoviesSyncAdapter;


public class MainActivity extends AppCompatActivity implements MovieListingFragment.Callback, MovieDetailFragment.ActivityToFragment {

    private static final String DETAIL_TAG = "DTAG";
    private String mSortOrder;

    public static boolean mTwoPane = false;
    public static boolean showFavorites = false;
    public static Uri mUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Popular Movies", "OnCreate");

        setContentView(R.layout.activity_main);

        MoviesSyncAdapter.initializeSyncAdapter(this);

        mSortOrder = Utils.getPreferredSortOrder(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        if(findViewById(R.id.movie_detail) != null){

            Log.d("Popular Movies", "Two Pane is True");

            mTwoPane = true;

            if(savedInstanceState == null){

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_listing, MovieListingFragment.newInstance())
                        .commit();


            }

        } else{
            Log.d("Popular Movies", "Two Pane is False");
            mTwoPane = false;

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_listing, MovieListingFragment.newInstance())
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

            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.DETAIL_URI, uri);





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

        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_favorites:
                if(showFavorites){
                    Toast.makeText(this, getString(R.string.hiding_favorites), Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.ic_favorite_white_24dp);
                }else {
                    Toast.makeText(this, getString(R.string.showing_favorites), Toast.LENGTH_SHORT).show();
                    item.setIcon(R.drawable.favorite_blue);
                }
                showFavorites = !showFavorites;
                ((MovieListingFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_listing))
                        .switchData(showFavorites);
                break;
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
        Log.d("Popular Movies", "OnResume");

        //MoviesSyncAdapter.syncImmediately(this);
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


        }
    }

    public void favorite(View view){

    }

    public void setFab(int drawableId){
        if(mTwoPane){
            ((FloatingActionButton)findViewById(R.id.favorite)).setImageResource(drawableId);
        }
    }

}
