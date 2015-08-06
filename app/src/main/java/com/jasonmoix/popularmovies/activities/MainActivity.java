package com.jasonmoix.popularmovies.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

    private MovieDetailFragment movieDetailFragment;
    private MovieReviewFragment movieReviewFragment;
    private MovieVideoFragment movieVideoFragment;
    private PageChangeListener pageChangeListener;

    private static Boolean detailRefresh = false;
    private static Boolean videoRefresh = false;
    private static String currentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MoviesSyncAdapter.initializeSyncAdapter(this);

        mSortOrder = Utils.getPreferredSortOrder(this);
        detailRefresh = false;
        videoRefresh  = false;

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        if(findViewById(R.id.movie_detail_container) != null){

            Log.d("Popular Movies", "Two Pane is True");

            mTwoPane = true;

            if(savedInstanceState == null){


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

            int pos = ((ViewPager)findViewById(R.id.movie_detail_container)).getCurrentItem();
            Log.d("Popular Movies", "Position = " + pos);
            switch (pos){
                case 0:
                    movieDetailFragment.reloadData(uri);
                    movieReviewFragment.reloadData(uri);
                    videoRefresh = true;
                    break;
                case 1:
                    movieDetailFragment.reloadData(uri);
                    movieReviewFragment.reloadData(uri);
                    movieVideoFragment.reloadData(uri);
                    break;
                case 2:
                    detailRefresh = true;
                    movieReviewFragment.reloadData(uri);
                    movieVideoFragment.reloadData(uri);
                    break;
            }
            pageChangeListener.setUri(uri);
            currentId = MoviesContract.MovieEntry.getMovieIdFromURI(uri);
            new GetFavoriteTask(this).execute(uri.toString());
            Log.d("Popular Movies", "Item clicked in two pane");

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
            //Detail Frag
            if (MovieListingFragment.mPosition != GridView.INVALID_POSITION && resort == false)
                new getFirstMovieTask(this).execute(MovieListingFragment.mPosition);
            else {
                resort = false;
                new getFirstMovieTask(this).execute(0);
            }
        }
    }

    public void favorite(View view){
        new AlterFavoriteTask(this).execute(currentId);
    }

    public void setFab(int drawableId){
        if(mTwoPane){
            ((FloatingActionButton)findViewById(R.id.favorite)).setImageResource(drawableId);
        }
    }

    private class getFirstMovieTask extends AsyncTask<Integer, Void, Uri>{

        private Context context;

        public getFirstMovieTask(Context context){
            this.context = context;
        }

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

                movieDetailFragment = MovieDetailFragment.newInstance(bundle);
                movieVideoFragment = MovieVideoFragment.newInstance(bundle);
                movieReviewFragment = MovieReviewFragment.newInstance(bundle);

                ViewPager viewPager = (ViewPager)findViewById(R.id.movie_detail_container);
                viewPager.setAdapter(new FragmentPager(((AppCompatActivity)context).getSupportFragmentManager()));
                pageChangeListener = new PageChangeListener();
                viewPager.addOnPageChangeListener(pageChangeListener);

                ((TabLayout)findViewById(R.id.detailTabs)).setupWithViewPager(viewPager);

                currentId = MoviesContract.MovieEntry.getMovieIdFromURI(uri);
                new GetFavoriteTask(context).execute(uri.toString());
            }
        }
    }

    public class FragmentPager extends FragmentPagerAdapter {


        public FragmentPager(FragmentManager fm){
            super(fm);
        }

        @Override
        public int getCount() {
            return DetailActivity.NUM_PAGES;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return movieDetailFragment;
                case 1:
                    return movieReviewFragment;
                case 2:
                    return movieVideoFragment;
                default:
                    return null;
            }

        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return getString(R.string.title_detail_information);
                case 1:
                    return getString(R.string.title_detail_reviews);
                case 2:
                    return getString(R.string.title_detail_videos);
                default:
                    return null;
            }
        }

    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        private Uri uri;

        @Override
        public void onPageScrollStateChanged(int state) {}

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {

            switch (position){
                case 0:
                    if(detailRefresh){
                        movieDetailFragment.reloadData(uri);
                        detailRefresh = false;
                    }
                    break;
                case 2:
                    if(videoRefresh){
                        movieVideoFragment.reloadData(uri);
                        videoRefresh = false;
                    }
                    break;
            }

        }

        public void setUri(Uri uri){
            this.uri = uri;
        }

    }

    private class GetFavoriteTask extends AsyncTask<String, Void, Integer>{

        private Context context;

        public GetFavoriteTask(Context context){
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... params) {

            Uri uri = Uri.parse(params[0]);
            int favorite = 0;

            Cursor cursor = context.getContentResolver().query(
                    uri,
                    null,
                    MoviesContract.MovieEntry._ID + " =?",
                    new String[]{MoviesContract.MovieEntry.getMovieIdFromURI(uri)},
                    null
            );

            if(cursor != null){
                cursor.moveToFirst();
                favorite = cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_FAVORITE));
                Log.d("Popular Movies", "Favorite = " + favorite);
                cursor.close();
            }

            return favorite;

        }

        @Override
        protected void onPostExecute(Integer favorite) {
            super.onPostExecute(favorite);
            if(favorite == 0) setFab(R.drawable.ic_favorite_border_white_24dp);
            else setFab(R.drawable.ic_favorite_white_24dp);
            if(findViewById(R.id.favorite).getVisibility() == View.GONE){
                findViewById(R.id.favorite).setVisibility(View.VISIBLE);
            }
        }

    }

    public class AlterFavoriteTask extends AsyncTask<String, Void, Boolean> {

        private Context context;

        public AlterFavoriteTask(Context context){
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            boolean favorite = false;
            String movieId = params[0];
            Log.d("Popular Movies", "ID = " + movieId);

            Cursor cursor = context.getContentResolver().query(
                    MoviesContract.MovieEntry.CONTENT_URI,
                    null,
                    MoviesContract.MovieEntry._ID + " =?",
                    new String[]{movieId},
                    null
            );

            Log.d("Popular Movies", "Cursor Count = " + cursor.getCount());

            cursor.moveToFirst();
            int isFavorite = cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_FAVORITE));

            cursor.close();

            ContentValues values;

            switch (isFavorite){
                case 0:
                    values = new ContentValues();
                    values.put(MoviesContract.MovieEntry.COLUMN_FAVORITE, 1);
                    context.getContentResolver().update(
                            MoviesContract.MovieEntry.CONTENT_URI,
                            values,
                            MoviesContract.MovieEntry._ID + " =?",
                            new String[]{movieId}
                    );
                    break;
                case 1:
                    values = new ContentValues();
                    values.put(MoviesContract.MovieEntry.COLUMN_FAVORITE, 0);
                    context.getContentResolver().update(
                            MoviesContract.MovieEntry.CONTENT_URI,
                            values,
                            MoviesContract.MovieEntry._ID + " =?",
                            new String[]{movieId}
                    );
                    favorite = true;
                    break;
                default:
                    break;
            }

            return favorite;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            if(aBoolean){
                setFab(R.drawable.ic_favorite_white_24dp);
            }
            else{
                setFab(R.drawable.ic_favorite_border_white_24dp);
            }

        }
    }

}
