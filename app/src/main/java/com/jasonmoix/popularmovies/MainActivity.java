package com.jasonmoix.popularmovies;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends AppCompatActivity {

    private static final String DETAIL_TAG = "DTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
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
        MovieListingFragment movieListingFragment =
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
