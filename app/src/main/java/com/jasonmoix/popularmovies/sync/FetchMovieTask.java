package com.jasonmoix.popularmovies.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.jasonmoix.popularmovies.R;
import com.jasonmoix.popularmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by jmoix on 7/17/2015.
 */
public class FetchMovieTask extends AsyncTask<Void, Void, Void> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTask(Context context){
        mContext = context;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.d("Popular Movies", "Task Begin");
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try{

            Uri movieDbUri = Uri.parse(mContext.getString(R.string.base_moviedb_url)).buildUpon()
                    .appendQueryParameter(mContext.getString(R.string.url_sortBy_key), mContext.getString(R.string.url_sortBy_value))
                    .appendQueryParameter(mContext.getString(R.string.url_api_key_key), mContext.getString(R.string.url_api_key_value))
                    .build();

            //initialize url for call
            URL callLogLocation = new URL(movieDbUri.toString());
            //open http connection
            urlConnection = (HttpURLConnection)callLogLocation.openConnection();
            //get input stream from request
            InputStream in = urlConnection.getInputStream();
            //call readstream() to get information to initialize result string
            String result = readStream(in);

            getMovieDataFromJSON(result);


        }catch (IOException e){
            e.printStackTrace();
        } finally {

            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader != null){
                try {
                    reader.close();
                }catch (final IOException e){
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public void getMovieDataFromJSON(String moviesJsonStr){

        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "original_title";
        final String RELEASE_DATE = "release_date";
        final String OVERVIEW = "overview";
        final String VOTE = "vote_average";
        final String POPULARITY = "popularity";
        final String BACKDROP = "backdrop_path";
        final String POSTER = "poster_path";

        try{

            JSONObject moviesJSON = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJSON.getJSONArray("results");

            Vector<ContentValues> cVVector = new Vector<ContentValues>(moviesArray.length());

            for(int i = 0; i < moviesArray.length(); i++){

                int id = moviesArray.getJSONObject(i).getInt(MOVIE_ID);
                String title = moviesArray.getJSONObject(i).getString(MOVIE_TITLE);
                String release_date = moviesArray.getJSONObject(i).getString(RELEASE_DATE);
                String overview = moviesArray.getJSONObject(i).getString(OVERVIEW);
                double vote = moviesArray.getJSONObject(i).getDouble(VOTE);
                double popularity = moviesArray.getJSONObject(i).getDouble(POPULARITY);
                String backdrop = moviesArray.getJSONObject(i).getString(BACKDROP);
                String poster = moviesArray.getJSONObject(i).getString(POSTER);

                ContentValues movieValues = new ContentValues();
                movieValues.put(MoviesContract.MovieEntry._ID, id);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_POPULARITY, popularity);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH, backdrop);
                movieValues.put(MoviesContract.MovieEntry.COLUMN_POSTER_PATH, poster);

                cVVector.add(movieValues);
            }

            int inserted = 0;

            if( cVVector.size() > 0){
                //delete old data
                mContext.getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI,null,null);

                //add new data
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cvArray);

            }

            Log.d("Popular Movies", "Task Complete. " + inserted + " inserted.");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String readStream(InputStream in){
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();
        String buffer;

        try {
            while((buffer = reader.readLine()) != null){
                response.append(buffer);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return(response.toString());
    }
}
