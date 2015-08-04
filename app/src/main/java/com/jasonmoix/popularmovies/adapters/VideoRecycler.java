package com.jasonmoix.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jasonmoix.popularmovies.R;
import com.jasonmoix.popularmovies.data.MoviesContract;

/**
 * Created by jmoix on 7/30/2015.
 */
public class VideoRecycler extends CursorRecyclerAdapter<VideoRecycler.ViewHolder> {

    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView title;
        public final TextView url;

        public ViewHolder(View view){
            super(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri address = Uri.parse(v.getContext().getString(R.string.base_youtube_url, url.getText().toString()));
                    Intent intent = new Intent(Intent.ACTION_VIEW, address);
                    if(intent.resolveActivity(v.getContext().getPackageManager()) != null) {
                        v.getContext().startActivity(intent);
                    }
                }
            });
            title = (TextView)view.findViewById(R.id.title);
            url = (TextView)view.findViewById(R.id.url);
        }

    }

    public VideoRecycler(Context context, Cursor cursor){
        super(cursor);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(MoviesContract.VideoEntry.COLUMN_NAME));
        String url = cursor.getString(cursor.getColumnIndex(MoviesContract.VideoEntry.COLUMN_KEY));
        holder.title.setText(title);
        holder.url.setText(url);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(holder.getLayoutPosition());
        holder.title.setText(cursor.getString(cursor.getColumnIndex(MoviesContract.VideoEntry.COLUMN_NAME)));
        holder.url.setText(cursor.getString(cursor.getColumnIndex(MoviesContract.VideoEntry.COLUMN_KEY)));
        super.onViewAttachedToWindow(holder);
    }

}
