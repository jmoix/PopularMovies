package com.jasonmoix.popularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jasonmoix.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by jmoix on 7/15/2015.
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

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

        FragmentManager fm = getSupportFragmentManager();
        MovieDetailFragment detailFragment = (MovieDetailFragment)fm.findFragmentById(R.id.movie_detail_container);
        if(detailFragment == null){
            detailFragment = new MovieDetailFragment();
            detailFragment.setArguments(getIntent().getExtras());
            fm.beginTransaction()
                    .add(R.id.movie_detail_container, detailFragment)
                    .commit();
        }

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
}
