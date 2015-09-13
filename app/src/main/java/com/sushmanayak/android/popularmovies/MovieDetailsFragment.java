package com.sushmanayak.android.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.sushmanayak.android.popularmovies.Data.MovieContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static String CURR_MOVIE = "com.sushmanayak.popularmovies.CurrentMovie";

    private static final int REVIEW_LOADER = 1;
    private static final int TRAILER_LOADER = 2;
    private static final int DETAIL_LOADER = 3;

    private static final String[] MOVIE_DETAILS_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_THUMBNAIL_PATH,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_DURATION,
            MovieContract.MovieEntry.COLUMN_FAVORITE,
    };
    static int TITLE_IDX = 0;
    static int OVERVIEW_IDX = 1;
    static int POSTER_PATH_IDX = 2;
    static int RELEASE_DATE_IDX = 3;
    static int THUMBNAIL_PATH_IDX = 4;
    static int RATING_IDX = 5;
    static int DURATION_IDX = 6;
    static int FAV_IDX = 7;

    CollapsingToolbarLayout mCollapsingToolbarLayout;
    String currentMovie;
    TextView overviewTextView;
    TextView releaseDateTextView;
    TextView userRating;
    TextView movieDuration;
    RatingBar userRatingBar;
    ImageView moviePosterView;
    ImageView thumbnailView;
    LinearLayout reviewContainer;
    LinearLayout trailerContainer;
    FloatingActionButton btnFav;
    private Cursor movieDetailCursor = null;
    Boolean movieIsFav = false;

    final static String MOVIEID = "DETAIL_MOVIE_ID";
    final static String YOUTUBE_LINK = "http://www.youtube.com/watch?v=";
    private ShareActionProvider mShareActionProvider;
    private String mTrailerLink = null;

    public MovieDetailsFragment() {
        setHasOptionsMenu(true);
    }

    public static MovieDetailsFragment newInstance(String movieId) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putString(MOVIEID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);

        moviePosterView = (ImageView) view.findViewById(R.id.moviePosterView);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar_layout);
        overviewTextView = (TextView) view.findViewById(R.id.overview_textview);
        releaseDateTextView = (TextView) view.findViewById(R.id.releasedate_textview);
        userRatingBar = (RatingBar) view.findViewById(R.id.userRatingBar);
        userRating = (TextView) view.findViewById(R.id.userRating);
        thumbnailView = (ImageView) view.findViewById(R.id.thumbnailView);
        movieDuration = (TextView) view.findViewById(R.id.movieDuration);
        reviewContainer = (LinearLayout) view.findViewById(R.id.reviewContainer);
        trailerContainer = (LinearLayout) view.findViewById(R.id.trailerContainer);
        btnFav = (FloatingActionButton) view.findViewById(R.id.btnFav);

        btnFav.setOnClickListener(favClickListener);

        currentMovie = this.getArguments().getString(MOVIEID);
        if (savedInstanceState != null)
            currentMovie = savedInstanceState.getString(CURR_MOVIE);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURR_MOVIE, currentMovie);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_movie_details, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mShareActionProvider.setShareIntent(null);

        if (mTrailerLink != null)
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mTrailerLink);
        return shareIntent;
    }

    View.OnClickListener favClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (movieDetailCursor != null)
            {
                movieIsFav = !movieIsFav;
                ContentValues movieValues = new ContentValues();
                DatabaseUtils.cursorRowToContentValues(movieDetailCursor, movieValues);

                // Update the favorite movie status in the database
                movieValues.remove(MovieContract.MovieEntry.COLUMN_FAVORITE);
                movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, movieIsFav == true ? 1 : 0);
                getContext().getContentResolver().update(MovieContract.MovieEntry.buildMovieDetails(currentMovie), movieValues, null, null);

                // Update the icon
                if (movieIsFav) {
                    MoviesActivity.FavoriteMovies.put(currentMovie, currentMovie);
                    btnFav.setImageResource(R.drawable.ic_fav_checked);
                } else {
                    MoviesActivity.FavoriteMovies.remove(currentMovie);
                    btnFav.setImageResource(R.drawable.ic_fav);
                }
            }
        }
    };


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DETAIL_LOADER)
            return new CursorLoader(getActivity(), MovieContract.MovieEntry.buildMovieDetails(currentMovie), MOVIE_DETAILS_COLUMNS, null, null, null);
        else if (id == REVIEW_LOADER)
            return new CursorLoader(getActivity(), MovieContract.ReviewEntry.buildReviewsForMovieUri(currentMovie), null, null, null, null);
        else if (id == TRAILER_LOADER)
            return new CursorLoader(getActivity(), MovieContract.TrailerEntry.buildTrailerForMovieUri(currentMovie), null, null, null, null);
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (!data.moveToFirst()) {
            return;
        }

        if (loader.getId() == DETAIL_LOADER)
            updateMovieDetails(data);
        else if (loader.getId() == REVIEW_LOADER)
            updateReviews(data);
        else if (loader.getId() == TRAILER_LOADER) {
            updateTrailers(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void updateMovieDetails(Cursor data) {
        movieDetailCursor = data;
        overviewTextView.setText(data.getString(OVERVIEW_IDX));
        releaseDateTextView.setText(data.getString(RELEASE_DATE_IDX));
        userRatingBar.setRating(Float.valueOf(data.getString(RATING_IDX)));
        userRating.setText(data.getString(RATING_IDX));
        movieDuration.setText(data.getString(DURATION_IDX) + " mins");
        Picasso.with(getActivity()).load(data.getString(POSTER_PATH_IDX)).into(thumbnailView);
        Picasso.with(getActivity()).load(data.getString(THUMBNAIL_PATH_IDX)).into(moviePosterView);
        mCollapsingToolbarLayout.setTitle(data.getString(TITLE_IDX));
        getActivity().setTitle(data.getString(TITLE_IDX));
        movieIsFav = MoviesActivity.FavoriteMovies.containsKey(currentMovie);

        if (movieIsFav)
            btnFav.setImageResource(R.drawable.ic_fav_checked);
        else
            btnFav.setImageResource(R.drawable.ic_fav);

    }

    private void updateReviews(Cursor data) {
        while (data.moveToNext()) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.review_list_item, reviewContainer, false);
            TextView content = (TextView) view.findViewById(R.id.reviewContent);
            TextView author = (TextView) view.findViewById(R.id.reviewAuthor);
            content.setText(data.getString(data.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW)));
            author.setText(data.getString(data.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR)));
            reviewContainer.addView(view);
        }
    }

    private void updateTrailers(Cursor data){
        int i = 1;

        while (data.moveToNext())
        {
            Button trailerButton = new Button(getActivity());
            trailerButton.setText("Trailer " + i);
            String trailerLink = data.getString(data.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER));
            trailerButton.setTag(trailerLink);
            trailerButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_next, 0, 0, 0);

            trailerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_LINK + v.getTag().toString())));
                }
            });

            if (i == 1)
                mTrailerLink = YOUTUBE_LINK + trailerLink;

            trailerContainer.addView(trailerButton);
            i++;
        }
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent());
        }

    }
}
