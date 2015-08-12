package com.jasonmoix.popularmovies.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasonmoix.popularmovies.R;
import com.jasonmoix.popularmovies.activities.MainActivity;
import com.jasonmoix.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by jmoix on 7/15/2015.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String DETAIL_URI = "URI";

    private static final int DETAIL_LOADER = 1;

    private static final String[] DETAIL_COLUMNS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_BACKDROP_PATH,
            MoviesContract.MovieEntry.COLUMN_POSTER_PATH,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_OVERVIEW,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MoviesContract.MovieEntry.COLUMN_POPULARITY,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_FAVORITE
    };

    private static final String[] REVIEW_COLUMNS = {
            MoviesContract.ReviewEntry.TABLE_NAME + "." + MoviesContract.ReviewEntry._ID,
            MoviesContract.ReviewEntry.COLUMN_MOVIE_ID,
            MoviesContract.ReviewEntry.COLUMN_AUTHOR,
            MoviesContract.ReviewEntry.COLUMN_CONTENT,
            MoviesContract.ReviewEntry.COLUMN_URL
    };

    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_BACKDROP = 1;
    public static final int COL_MOVIE_POSTER = 2;
    public static final int COL_MOVIE_TITLE = 3;
    public static final int COL_MOVIE_OVERVIEW = 4;
    public static final int COL_MOVIE_VOTE = 5;
    public static final int COL_MOVIE_POPULARITY = 6;
    public static final int COL_MOVIE_RELEASE = 7;
    public static final int COL_FAVORITE = 8;

    public static final int COL_REVIEW_ID = 0;
    public static final int COL_REVIEW_MOVIE_ID = 1;
    public static final int COL_REVIEW_AUTHOR = 2;
    public static final int COL_REVIEW_CONTENT = 3;
    public static final int COL_REVIEW_URL = 4;

    private ImageView poster;
    private ImageView backdrop;
    private TextView title;
    private TextView releaseDate;
    private TextView rating;
    private TextView overview;
    private Uri mUri;

    public interface ActivityToFragment{
        void setFab(int drawableId);
    }

    public static MovieDetailFragment newInstance(){
        return new MovieDetailFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        mUri = MainActivity.mUri;
        Log.d("URI", mUri.toString());
        poster = (ImageView)rootView.findViewById(R.id.movie_poster);
        title = (TextView) rootView.findViewById(R.id.movie_title);
        releaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        rating = (TextView) rootView.findViewById(R.id.movie_rating);
        overview = (TextView) rootView.findViewById(R.id.movie_overview);

        return(rootView);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }


    public String formatDate(String date){

        Calendar newDate = new GregorianCalendar(Calendar.getInstance().getTimeZone());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");

        try{
            Date oldDate = formatter.parse(date);
            newDate.setTime(oldDate);
        }catch (ParseException e){
            e.printStackTrace();
        }

        return  (newDate.get(Calendar.MONTH)+1) + "/" +
                newDate.get(Calendar.DAY_OF_MONTH) + "/" +
                newDate.get(Calendar.YEAR);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(null != mUri){
            Log.d("Popular Movies", "Detail Loader Created");
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    MoviesContract.MovieEntry._ID + " =?",
                    new String[]{MoviesContract.MovieEntry.getMovieIdFromURI(mUri)},
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {

            Uri posterUri = Uri.parse(data.getString(COL_MOVIE_POSTER));
            String titleInfo = data.getString(COL_MOVIE_TITLE);
            String overviewInfo = data.getString(COL_MOVIE_OVERVIEW);
            String ratingInfo = data.getString(COL_MOVIE_VOTE);
            String releaseInfo = data.getString(COL_MOVIE_RELEASE);
            Picasso.with(getActivity().getBaseContext())
                    .load(getString(R.string.base_movieposter_url, posterUri))
                    .into(poster);
            title.setText(titleInfo);
            releaseDate.setText(getString(R.string.release_date,
                    formatDate(releaseInfo)));
            rating.setText(getString(R.string.movie_rating, ratingInfo));
            overview.setText(overviewInfo);

            int favorite = data.getInt(data.getColumnIndex(MoviesContract.MovieEntry.COLUMN_FAVORITE));
            if(favorite == 0) ((ActivityToFragment)getActivity()).setFab(R.drawable.ic_favorite_border_white_24dp);
            else ((ActivityToFragment)getActivity()).setFab(R.drawable.ic_favorite_white_24dp);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
