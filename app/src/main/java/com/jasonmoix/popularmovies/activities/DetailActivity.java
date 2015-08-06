package com.jasonmoix.popularmovies.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
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
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jasonmoix.popularmovies.fragments.MovieDetailFragment;
import com.jasonmoix.popularmovies.fragments.MovieReviewFragment;
import com.jasonmoix.popularmovies.fragments.MovieVideoFragment;
import com.jasonmoix.popularmovies.R;
import com.jasonmoix.popularmovies.data.MoviesContract;

/**
 * Created by jmoix on 7/15/2015.
 */
public class DetailActivity extends AppCompatActivity implements MovieDetailFragment.ActivityToFragment {

    public static final int NUM_PAGES = 3;
    public static final int DETAIL_RESULT = 100;

    private FloatingActionButton favoriteFab;

    public void favorite(View view){

        Uri movieUri = getIntent().getParcelableExtra(MovieDetailFragment.DETAIL_URI);
        String[] movieId = new String[]{MoviesContract.MovieEntry.getMovieIdFromURI(movieUri)};

        new AlterFavoriteTask(this).execute(movieId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        setResult(DETAIL_RESULT, new Intent());

        CoordinatorLayout rootLayout = (CoordinatorLayout)findViewById(R.id.main_content);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_chevron_left_white_24dp));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getIntent().getStringExtra(MoviesContract.MovieEntry.COLUMN_TITLE));


        ImageView backdrop = (ImageView)findViewById(R.id.backdrop);
        Uri backdropUri = getIntent().getParcelableExtra(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH);
        Glide.with(this).load(backdropUri).centerCrop().into(backdrop);

        ViewPager viewPager = (ViewPager)findViewById(R.id.detailPager);
        viewPager.setAdapter(new FragmentPager(getSupportFragmentManager()));

        ((TabLayout)findViewById(R.id.detailTabs)).setupWithViewPager(viewPager);

        favoriteFab = (FloatingActionButton)findViewById(R.id.favorite);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return(true);
        }
        return super.onOptionsItemSelected(item);

    }

    public void setFab(int drawableId){
        favoriteFab.setImageResource(drawableId);
    }

    public class FragmentPager extends FragmentPagerAdapter {

        public FragmentPager(FragmentManager fm){ super(fm); }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return MovieDetailFragment.newInstance(getIntent().getExtras());
                case 1:
                    return MovieReviewFragment.newInstance(getIntent().getExtras());
                case 2:
                    return MovieVideoFragment.newInstance(getIntent().getExtras());
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
                favoriteFab.setImageResource(R.drawable.ic_favorite_white_24dp);
            }
            else{
                favoriteFab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            }

        }
    }
}
