package com.jasonmoix.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.jasonmoix.popularmovies.R;
import com.jasonmoix.popularmovies.data.MoviesContract;
import com.jasonmoix.popularmovies.fragments.MovieListingFragment;
import com.squareup.picasso.Picasso;

/**
 * Created by jmoix on 7/17/2015.
 */
public class MovieListingAdapter extends CursorAdapter {

    public MovieListingAdapter(Context context, Cursor c, int flags){ super(context,c,flags);}

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String fav = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_FAVORITE));

        ViewHolder viewHolder = (ViewHolder)view.getTag();
        Picasso.with(context).load(
                context.getString(R.string.base_movieposter_url,
                        cursor.getString(MovieListingFragment.COL_POSTER_URL)))
                .fit()
                .into(viewHolder.poster);

        if(fav.equals("1")) viewHolder.favorite.setVisibility(View.VISIBLE);
        else viewHolder.favorite.setVisibility(View.INVISIBLE);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return(view);

    }

    public static class ViewHolder{

        public final ImageView poster;
        public final ImageView favorite;

        public ViewHolder(View view){
            poster = (ImageView)view.findViewById(R.id.movie_poster);
            favorite = (ImageView)view.findViewById(R.id.fav_indicator);
        }
    }
}
