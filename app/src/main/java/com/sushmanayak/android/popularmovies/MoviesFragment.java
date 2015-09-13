package com.sushmanayak.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.sushmanayak.android.popularmovies.Adapter.MovieImageAdapter;
import com.sushmanayak.android.popularmovies.Data.FetchMoviesTask;
import com.sushmanayak.android.popularmovies.Data.MovieContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    GridView mMoviesGridView;
    MovieImageAdapter mMoviesAdapter;
    static MoviesActivity.SearchMethod mCurrentSortMethod = MoviesActivity.SearchMethod.POPULARITY;
    final String SORT_METHOD = "SortMethod";
    static boolean mDataFetched = false;

    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIEID,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_POSTERPATH = 2;

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri movieUri = MovieContract.MovieEntry.buildMovieUri();
        String sortOrder = MovieContract.MovieEntry._ID + " ASC";
        if (mCurrentSortMethod == MoviesActivity.SearchMethod.FAVORITES)
            return new CursorLoader(getActivity(),
                    movieUri,
                    MOVIE_COLUMNS,
                    MovieContract.MovieEntry.COLUMN_FAVORITE + " = ?",
                    new String[]{"1"},
                    sortOrder);
        else
            return new CursorLoader(getActivity(),
                    movieUri,
                    MOVIE_COLUMNS,
                    MovieContract.MovieEntry.COLUMN_SORTBY + " = ?",
                    new String[]{Integer.toString(mCurrentSortMethod.ordinal())},
                    sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data.moveToFirst() == false && !mDataFetched) {
            mDataFetched = true;
            updateMovieList();
        }
        mMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesAdapter.swapCursor(null);
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

        mMoviesGridView = (GridView) view.findViewById(R.id.movieGridView);
        mMoviesGridView.setOnItemClickListener(movieItemClicked);

        // Set the adapter
        mMoviesAdapter = new MovieImageAdapter(getActivity(), null, 0);
        mMoviesGridView.setAdapter(mMoviesAdapter);

        SharedPreferences preferences = getActivity().getSharedPreferences(SORT_METHOD, 0);
        int SortMethod = Integer.parseInt(preferences.getString(SORT_METHOD, "0"));
        if (SortMethod == 0)
            mCurrentSortMethod = MoviesActivity.SearchMethod.POPULARITY;
        else if (SortMethod == 1)
            mCurrentSortMethod = MoviesActivity.SearchMethod.USER_RATING;
        else
            mCurrentSortMethod = MoviesActivity.SearchMethod.FAVORITES;
        return view;
    }

    AdapterView.OnItemClickListener movieItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
            if (cursor != null) {
                ((MovieImageAdapter.CallBacks) getActivity()).onItemClicked(view, position, cursor.getString(COL_MOVIE_ID));
            }
        }
    };

    void updateMovieList() {
        FetchMoviesTask task = new FetchMoviesTask(getActivity());
        task.execute(mCurrentSortMethod.ordinal());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        MoviesActivity.SearchMethod newSearch = mCurrentSortMethod;

        if (id == R.id.action_sortbypopularity)
            newSearch = MoviesActivity.SearchMethod.POPULARITY;
        else if (id == R.id.action_sortbyratings)
            newSearch = MoviesActivity.SearchMethod.USER_RATING;
        else if (id == R.id.action_sortbyfavorites)
            newSearch = MoviesActivity.SearchMethod.FAVORITES;

        if (newSearch != mCurrentSortMethod) {
            mCurrentSortMethod = newSearch;
            Toast toast = Toast.makeText(getContext(), getString(R.string.loading), Toast.LENGTH_SHORT);
            toast.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
            toast.show();

            updateMovieList();
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);

            SharedPreferences.Editor editor = getActivity().getSharedPreferences(SORT_METHOD, 0).edit();
            editor.putString(SORT_METHOD, Integer.toString(mCurrentSortMethod.ordinal()));
            editor.commit();
        }
        return super.onOptionsItemSelected(item);
    }
}
