package com.jasonmoix.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by jmoix on 7/20/2015.
 */
public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = MoviesSyncAdapter.class.getSimpleName();
    private Context mContext;

    public static final int SYNC_INTERVAL = 60*180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public MoviesSyncAdapter(Context context, boolean autoInitialize){
        super(context, autoInitialize);
        mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d("Popular Movies", "onPerformSync Called.");
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
    }

    public static void syncImmediately(Context context){
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context){

        AccountManager accountManager =
                (AccountManager)context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if(null == accountManager.getPassword(newAccount)){

            if(!accountManager.addAccountExplicitly(newAccount, "", null)){
                return null;
            }

            onAccountCreated(newAccount, context);

        }

        return(newAccount);
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

        ArrayList<Integer> ids_inserted = new ArrayList<>();

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

                ids_inserted.add(id);
                cVVector.add(movieValues);
            }

            int inserted = 0;

            if( cVVector.size() > 0){
                //delete old data
                mContext.getContentResolver().delete(MoviesContract.MovieEntry.CONTENT_URI,null,null);
                mContext.getContentResolver().delete(MoviesContract.ReviewEntry.CONTENT_URI,null,null);
                mContext.getContentResolver().delete(MoviesContract.VideoEntry.CONTENT_URI,null,null);

                //add new data
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MoviesContract.MovieEntry.CONTENT_URI, cvArray);

            }

            for(int i = 0; i < ids_inserted.size(); i++){
                getReviews(ids_inserted.get(i));
                getVideos(ids_inserted.get(i));
            }

            Log.d("Popular Movies", "Sync Finished - " + Integer.toString(inserted) + " records inserted!");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getReviewDataFromJSON(String jsonString, int movieId){

        final String ID = "id";
        final String AUTHOR = "author";
        final String CONTENT = "content";
        final String URL = "url";

        Log.d("Popular Movies", "Review JSON =" + jsonString);

        try{

            JSONObject object = new JSONObject(jsonString);
            JSONArray array = object.getJSONArray("results");

            Vector<ContentValues> cVVector = new Vector<ContentValues>(array.length());

            for(int i = 0; i < array.length(); i++){
                String id = array.getJSONObject(i).getString(ID);
                String author = array.getJSONObject(i).getString(AUTHOR);
                String content = array.getJSONObject(i).getString(CONTENT);
                String url = array.getJSONObject(i).getString(URL);

                ContentValues value = new ContentValues();
                value.put(MoviesContract.ReviewEntry._ID, id);
                value.put(MoviesContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                value.put(MoviesContract.ReviewEntry.COLUMN_AUTHOR, author);
                value.put(MoviesContract.ReviewEntry.COLUMN_CONTENT, content);
                value.put(MoviesContract.ReviewEntry.COLUMN_URL, url);

                cVVector.add(value);
            }

            int inserted = 0;

            if( cVVector.size() > 0){

                //add new data
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MoviesContract.ReviewEntry.CONTENT_URI, cvArray);

            }

            Log.d("Popular Movies", "Sync Finished - " + Integer.toString(inserted) + " records inserted!");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getReviews(int movieId){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try{
            Uri movieDbUri = Uri.parse(mContext.getString(R.string.base_moviereview_url, movieId)).buildUpon()
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

            getReviewDataFromJSON(result, movieId);

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
    }

    public void getVideoDataFromJSON(String jsonString, int movieId){

        final String ID = "id";
        final String ISO = "iso_639_1";
        final String KEY = "key";
        final String NAME = "name";
        final String SITE = "site";
        final String SIZE = "size";
        final String TYPE = "type";

        Log.d("Popular Movies", "Video JSON =" + jsonString);

        try{

            JSONObject object = new JSONObject(jsonString);
            JSONArray array = object.getJSONArray("results");

            Vector<ContentValues> cVVector = new Vector<ContentValues>(array.length());

            for(int i = 0; i < array.length(); i++){
                String id = array.getJSONObject(i).getString(ID);
                String iso = array.getJSONObject(i).getString(ISO);
                String key = array.getJSONObject(i).getString(KEY);
                String name = array.getJSONObject(i).getString(NAME);
                String site = array.getJSONObject(i).getString(SITE);
                int size = array.getJSONObject(i).getInt(SIZE);
                String type = array.getJSONObject(i).getString(TYPE);

                ContentValues value = new ContentValues();
                value.put(MoviesContract.VideoEntry._ID, id);
                value.put(MoviesContract.VideoEntry.COLUMN_MOVIE_ID, movieId);
                value.put(MoviesContract.VideoEntry.COLUMN_ISO, iso);
                value.put(MoviesContract.VideoEntry.COLUMN_KEY, key);
                value.put(MoviesContract.VideoEntry.COLUMN_NAME, name);
                value.put(MoviesContract.VideoEntry.COLUMN_SITE, site);
                value.put(MoviesContract.VideoEntry.COLUMN_SIZE, size);
                value.put(MoviesContract.VideoEntry.COLUMN_TYPE, type);

                cVVector.add(value);
            }

            int inserted = 0;

            if( cVVector.size() > 0){

                //add new data
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MoviesContract.VideoEntry.CONTENT_URI, cvArray);

            }

            Log.d("Popular Movies", "Sync Finished - " + Integer.toString(inserted) + " records inserted!");

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void getVideos(int movieId){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try{
            Uri movieDbUri = Uri.parse(mContext.getString(R.string.base_movievideo_url, movieId)).buildUpon()
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

            getVideoDataFromJSON(result, movieId);

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


    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime){

        Account account = getSyncAccount(context);

        String authority = context.getString(R.string.content_authority);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){

            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);

        }else{
            ContentResolver.addPeriodicSync(
                    account,
                    authority,
                    new Bundle(),
                    syncInterval);
        }
    }

    public static void initializeSyncAdapter(Context context){
        getSyncAccount(context);
    }

    public static void onAccountCreated(Account newAccount, Context context){
        MoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }

}
