package com.jasonmoix.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jasonmoix.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by jmoix on 7/15/2015.
 */
public class DetailActivity extends AppCompatActivity {

    private final int NUM_PAGES = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        CoordinatorLayout rootLayout = (CoordinatorLayout)findViewById(R.id.main_content);

        Log.d("PopularMovies", getIntent().getStringExtra(MoviesContract.MovieEntry.COLUMN_TITLE));

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(getIntent().getStringExtra(MoviesContract.MovieEntry.COLUMN_TITLE));


        ImageView backdrop = (ImageView)findViewById(R.id.backdrop);
        Uri imagePath = Uri.parse(getString(R.string.base_moviebackdrop_url, getIntent().getStringExtra(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH)));
        Glide.with(this).load(imagePath).centerCrop().into(backdrop);

        Log.d("PopularMovies", getString(R.string.base_moviebackdrop_url, getIntent().getStringExtra(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH)));

        ViewPager viewPager = (ViewPager)findViewById(R.id.detailPager);
        viewPager.setAdapter(new FragmentPager(getSupportFragmentManager()));

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
        }
        return super.onOptionsItemSelected(item);

    }

    public class FragmentPager extends FragmentPagerAdapter {

        public FragmentPager(FragmentManager fm){ super(fm); }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public Fragment getItem(int position) {
            return MovieDetailFragment.newInstance(getIntent().getExtras());
        }

    }
}
