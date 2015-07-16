package com.jasonmoix.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private static final String DETAIL_TAG = "DTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri movieDbUri = Uri.parse(getString(R.string.base_moviedb_url)).buildUpon()
                .appendQueryParameter(getString(R.string.url_sortBy_key), getString(R.string.url_sortBy_value))
                .appendQueryParameter(getString(R.string.url_api_key_key), getString(R.string.url_api_key_value))
                .build();

        Log.d("Popular Movies", getString(R.string.base_movieposter_url, "stuff"));
        Log.d("Popular Movies", movieDbUri.toString());

        new GetMoviesTask().execute();

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

    private class GetMoviesTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {

            String returnValue = new String();

            try{

                Uri movieDbUri = Uri.parse(getString(R.string.base_moviedb_url)).buildUpon()
                        .appendQueryParameter(getString(R.string.url_sortBy_key), getString(R.string.url_sortBy_value))
                        .appendQueryParameter(getString(R.string.url_api_key_key), getString(R.string.url_api_key_value))
                        .build();

                URL url = new URL(movieDbUri.toString());

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                InputStream in = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String buffer;

                try{
                    while((buffer = br.readLine()) != null)
                        returnValue = returnValue + buffer;
                    br.close();
                }catch (IOException e){
                    e.printStackTrace();
                }

                connection.disconnect();

            }catch(Exception e){
                e.printStackTrace();
            }

            return returnValue;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                JSONObject object = new JSONObject(s);
                JSONArray array = object.getJSONArray("results");

                for(int i = 0; i < array.length(); i++){
                    Log.d("id", "     " + array.getJSONObject(i).getString("id"));
                    Log.d("title", "     " + array.getJSONObject(i).getString("original_title"));
                    Log.d("release date", "     " + array.getJSONObject(i).getString("release_date"));
                    Log.d("overview", "     " + array.getJSONObject(i).getString("overview"));
                    Log.d("average vote", "     " + array.getJSONObject(i).getString("vote_average"));
                    Log.d("popularity", "     " + array.getJSONObject(i).getString("popularity"));
                    Log.d("backdrop", "     " + array.getJSONObject(i).getString("backdrop_path"));
                    Log.d("poster", "     " + array.getJSONObject(i).getString("poster_path"));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
