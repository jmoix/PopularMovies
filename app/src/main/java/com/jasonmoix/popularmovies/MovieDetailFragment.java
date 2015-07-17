package com.jasonmoix.popularmovies;

import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class MovieDetailFragment extends Fragment {

    View rootView;

    public static MovieDetailFragment newInstance(Bundle arguments){
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(arguments);
        return(fragment);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        return(rootView);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Picasso.with(getActivity().getBaseContext())
                .load(getString(R.string.base_movieposter_url, getArguments().getString(MoviesContract.MovieEntry.COLUMN_POSTER_PATH)))
                .into(((ImageView) rootView.findViewById(R.id.movie_poster)));

        ((TextView)rootView.findViewById(R.id.movie_title)).setText(getArguments().getString(MoviesContract.MovieEntry.COLUMN_TITLE));
        ((TextView)rootView.findViewById(R.id.movie_release_date)).setText(getString(R.string.release_date,
                formatDate(getArguments().getString(MoviesContract.MovieEntry.COLUMN_RELEASE_DATE))));
        ((TextView)rootView.findViewById(R.id.movie_rating)).setText(getString(R.string.movie_rating, getArguments().getString(MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
        ((TextView)rootView.findViewById(R.id.movie_overview)).setText(getArguments().getString(MoviesContract.MovieEntry.COLUMN_OVERVIEW));

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
}
