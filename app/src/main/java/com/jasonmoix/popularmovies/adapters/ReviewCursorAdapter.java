package com.jasonmoix.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jasonmoix.popularmovies.R;
import com.jasonmoix.popularmovies.data.MoviesContract;
import com.squareup.picasso.Picasso;

/**
 * Created by jmoix on 7/29/2015.
 */
public class ReviewCursorAdapter extends CursorAdapter {

    public ReviewCursorAdapter(Context context, Cursor c, int flags){ super(context,c,flags);}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {


        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return(view);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        viewHolder.author.setText(cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_AUTHOR)));
        viewHolder.content.setText(cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_CONTENT)));
    }

    public static class ViewHolder{

        public final TextView author;
        public final TextView content;

        public ViewHolder(View view){

            author = (TextView)view.findViewById(R.id.author);
            content = (TextView)view.findViewById(R.id.content);

        }
    }

}
