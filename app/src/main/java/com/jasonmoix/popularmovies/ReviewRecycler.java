package com.jasonmoix.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasonmoix.popularmovies.data.MoviesContract;

/**
 * Created by jmoix on 7/30/2015.
 */
public class ReviewRecycler extends CursorRecyclerAdapter<ReviewRecycler.ViewHolder>{

    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView author;
        public final TextView content;
        public final TextView url;

        public ViewHolder(View view){
            super(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri address = Uri.parse(url.getText().toString());
                    Intent intent = new Intent(Intent.ACTION_VIEW, address);
                    if(intent.resolveActivity(v.getContext().getPackageManager()) != null){
                        v.getContext().startActivity(intent);
                    }
                }
            });
            Log.d("Popular Movies", "Construct View Holder");
            author = (TextView)view.findViewById(R.id.author);
            content = (TextView)view.findViewById(R.id.content);
            url = (TextView)view.findViewById(R.id.url);
        }

    }

    public ReviewRecycler(Context context, Cursor cursor){
        super(cursor);
        Log.d("Popular Movies", "Create Recycler");
        this.context = context;
    }

    @Override
    public ReviewRecycler.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("Popular Movies", "Create View Holder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewRecycler.ViewHolder holder, Cursor cursor) {
        Log.d("Popular Movies", "Bind View Holder");
        String author = cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_AUTHOR));
        String content = cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_CONTENT));
        String url = cursor.getString(cursor.getColumnIndex(MoviesContract.ReviewEntry.COLUMN_URL));

        holder.author.setText(author);
        holder.url.setText(url);

        if(content.length() > 100){
            holder.content.setText(context.getString(R.string.content_abridged, content.substring(0, 99)));
        }
        else{
            holder.content.setText(content);
        }

    }


}
