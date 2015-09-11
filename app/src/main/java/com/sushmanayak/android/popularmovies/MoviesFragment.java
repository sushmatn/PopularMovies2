package com.sushmanayak.android.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.sushmanayak.android.popularmovies.Adapter.MovieImageAdapter;
import com.sushmanayak.android.popularmovies.Data.FetchMoviesTask;
import com.sushmanayak.android.popularmovies.Data.MovieContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    GridView mMoviesGridView;
    MovieImageAdapter mMoviesAdapter;
    static SearchMethod mCurrentSortMethod = SearchMethod.POPULARITY;
    Toolbar mToolbar;

    static final String MOVIE_LIST = "com.sushmanayak.PopularMovies.CurrentMoviePage";
    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIEID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_POSTERPATH = 2;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri movieUri = MovieContract.MovieEntry.buildMovieUri();
        String sortOrder = MovieContract.MovieEntry._ID + " ASC";
        return new CursorLoader(getActivity(),
                movieUri,
                MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
    }

    enum SearchMethod {
        POPULARITY,
        USER_RATING
    }

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        //mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        //AppCompatActivity activity = (AppCompatActivity) getActivity();
        //activity.setSupportActionBar(mToolbar);

        mMoviesGridView = (GridView) view.findViewById(R.id.movieGridView);

        mMoviesGridView.setOnItemClickListener(movieItemClicked);

        // Set the adapter
        mMoviesAdapter = new MovieImageAdapter(getActivity(), null, 0);
        mMoviesGridView.setAdapter(mMoviesAdapter);

        // Create and set the EmptyView
        ImageView imgView = new ImageView(getActivity());
        imgView.setImageResource(R.drawable.pagenotfound);
        ((ViewGroup) mMoviesGridView.getParent()).addView(imgView);
        mMoviesGridView.setEmptyView(imgView);

        // Fetch the movie list
        if (savedInstanceState == null) {
            //updateMovieList();
        }
        return view;
    }

    AdapterView.OnItemClickListener movieItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
            if(cursor != null) {
                ((MovieImageAdapter.CallBacks) getActivity()).onItemClicked(view, position,cursor.getString(COL_MOVIE_ID));
            }
        }
    };

    void updateMovieList()
    {
        FetchMoviesTask task = new FetchMoviesTask(getActivity());
        task.execute(mCurrentSortMethod.ordinal());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sortbypopularity)
            mCurrentSortMethod = SearchMethod.POPULARITY;
        else if (id == R.id.action_sortbyratings)
            mCurrentSortMethod = SearchMethod.USER_RATING;

        updateMovieList();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
        return super.onOptionsItemSelected(item);
    }
}
