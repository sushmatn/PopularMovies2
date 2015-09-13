package com.sushmanayak.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.sushmanayak.android.popularmovies.Data.FetchMoviesTask;
import com.sushmanayak.android.popularmovies.Data.MovieContract;

import org.json.JSONObject;

/**
 * Created by SushmaNayak on 9/12/2015.
 */
public class Utility {

    public static final String THEMOVIEDB_BASE_URL =
            "http://api.themoviedb.org/3/discover/movie?";
    public static final String MOVIEDB_ID = "id";
    public static final String MOVIEDB_OVERVIEW = "overview";
    public static final String MOVIEDB_RELEASEDATE = "release_date";
    public static final String MOVIEDB_POSTERPATH = "poster_path";
    public static final String MOVIEDB_THUMBNAILPATH = "backdrop_path";
    public static final String MOVIEDB_USERRATING = "vote_average";
    public static final String MOVIEDB_RUNTIME = "runtime";
    public static final String MOVIEDB_TITLE = "original_title";
    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w300/";

    public static ContentValues GetCVObject(JSONObject movieObject, int SortBy) {
        ContentValues movieValues = new ContentValues();

        try {
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIEID, movieObject.getString(MOVIEDB_ID));
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieObject.getString(MOVIEDB_TITLE));
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieObject.getString(MOVIEDB_OVERVIEW));
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, BASE_IMAGE_URL + movieObject.getString(MOVIEDB_POSTERPATH));
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieObject.getString(MOVIEDB_RELEASEDATE));
            movieValues.put(MovieContract.MovieEntry.COLUMN_THUMBNAIL_PATH, BASE_IMAGE_URL + movieObject.getString(MOVIEDB_THUMBNAILPATH));
            movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, movieObject.getString(MOVIEDB_USERRATING));
            movieValues.put(MovieContract.MovieEntry.COLUMN_DURATION, movieObject.getString(MOVIEDB_RUNTIME));
            movieValues.put(MovieContract.MovieEntry.COLUMN_SORTBY, SortBy);
        }
        catch (Exception e) {
            Log.e(FetchMoviesTask.LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
        return movieValues;
    }

    public static boolean IsDeviceOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}