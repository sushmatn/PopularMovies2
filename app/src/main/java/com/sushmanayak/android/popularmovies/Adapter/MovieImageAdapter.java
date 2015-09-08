package com.sushmanayak.android.popularmovies.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.sushmanayak.android.popularmovies.Data.Movie;
import com.sushmanayak.android.popularmovies.Data.MovieList;
import com.sushmanayak.android.popularmovies.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by SushmaNayak on 9/7/2015.
 */
public class MovieImageAdapter extends RecyclerView.Adapter<MovieImageAdapter.ImageViewHolder> {

    private Context mContext;
    private ArrayList<Movie> mMovieList;

    public interface CallBacks {
        public void onItemClicked(View view, int position);
    }

    public MovieImageAdapter(Context context) {
        mContext = context;
        mMovieList = MovieList.getInstance().getMovieArrayList();
    }

    public void setMovieList(ArrayList<Movie> movieList) {
        mMovieList = movieList;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, viewGroup, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder imageViewHolder, int i) {
        Movie movie = mMovieList.get(i);
        imageViewHolder.BindData(movie);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {

        LinearLayout movieHolder;
        ImageView moviePoster;

        public ImageViewHolder(View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.moviePoster);
            movieHolder = (LinearLayout) itemView.findViewById(R.id.movieHolder);
            movieHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CallBacks) mContext).onItemClicked(v, getAdapterPosition());
                }
            });
        }

        public void BindData(Movie currentMovie) {
            Picasso.with(mContext).load(currentMovie.getPosterPath())
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.image_unavailable)
                    .into(moviePoster);
        }
    }
}
