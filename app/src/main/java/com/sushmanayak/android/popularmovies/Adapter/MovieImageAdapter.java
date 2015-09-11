package com.sushmanayak.android.popularmovies.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sushmanayak.android.popularmovies.Data.MovieContract;
import com.sushmanayak.android.popularmovies.R;

/**
 * Created by SushmaNayak on 9/7/2015.
 */
public class MovieImageAdapter extends CursorAdapter {

    private Context mContext;

    public MovieImageAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView moviePoster = (ImageView) view.findViewById(R.id.moviePoster);

        Picasso.with(mContext).load(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)))
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.image_unavailable)
                    .into(moviePoster);
    }

    public interface CallBacks {
        public void onItemClicked(View view, int position, String movieId);
    }

}
