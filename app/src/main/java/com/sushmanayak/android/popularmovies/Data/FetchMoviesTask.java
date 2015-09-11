package com.sushmanayak.android.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sushmanayak.android.popularmovies.MoviesActivity;
import com.sushmanayak.android.popularmovies.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by SushmaNayak on 9/9/2015.
 */
public class FetchMoviesTask extends AsyncTask<Integer, Void, Boolean> {

    public static final String LOG_TAG = MoviesActivity.class.getSimpleName();
    private Context mContext;

    static final String THEMOVIEDB_BASE_URL =
            "http://api.themoviedb.org/3/discover/movie?";
    static final String SORTBY_PARAM = "sort_by";
    static final String API_KEY_PARAM = "api_key";
    static final String SORTBY_POPULARITY = "popularity.desc";
    static final String SORTBY_RATING = "vote_count.desc";
    static final String THEMOVIEDB_MOVIE_URL =
            "http://api.themoviedb.org/3/movie/";
    static final String APPEND_PARAM = "append_to_response";
    static final String APPEND_PARAM_VALUES = "trailers,reviews,credits";

    static final String MOVIEDB_TITLE = "original_title";
    static final String MOVIEDB_ID = "id";
    static final String MOVIEDB_OVERVIEW = "overview";
    static final String MOVIEDB_RELEASEDATE = "release_date";
    static final String MOVIEDB_POSTERPATH = "poster_path";
    static final String MOVIEDB_THUMBNAILPATH = "backdrop_path";
    static final String MOVIEDB_USERRATING = "vote_average";
    static final String MOVIEDB_HOMEPAGE = "homepage";
    static final String MOVIEDB_RUNTIME = "runtime";
    static final String MOVIEDB_TRAILERS = "trailers";
    static final String MOVIEDB_YOUTUBE = "youtube";
    static final String MOVIEDB_VIDEOSOURCE = "source";
    static final String MOVIEDB_REVIEWS = "reviews";
    static final String MOVIEDB_RESULTS = "results";
    static final String MOVIEDB_REVIEWAUTHOR = "author";
    static final String MOVIEDB_REVIEW = "content";
    static final String MOVIEDB_CREDITS = "credits";
    static final String MOVIEDB_CAST = "cast";
    static final String MOVIEDB_ACTOR = "name";
    static final String MOVIEDB_PROFILE_PIC = "profile_path";
    static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w300/";


    public FetchMoviesTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPostExecute(Boolean bRet) {
        super.onPostExecute(bRet);
        //mMoviesAdapter.setMovieList(MovieList.getInstance().getMovieArrayList());
        //mMoviesAdapter.notifyDataSetChanged();
    }

    @Override
    protected Boolean doInBackground(Integer... params) {


        String sortBy = SORTBY_POPULARITY;
        if (params[0] == 1)
            sortBy = SORTBY_RATING;

        String uriString = Uri.parse(THEMOVIEDB_BASE_URL)
                .buildUpon()
                .appendQueryParameter(SORTBY_PARAM, sortBy)
                .appendQueryParameter(API_KEY_PARAM, mContext.getString(R.string.api_key))
                .build().toString();

        String moviesJsonStr = GetJsonDataFromUrl(uriString);

        try {

            ArrayList<String> movieIDs = GetMovieListFromJSon(moviesJsonStr);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieIDs.size());
            Vector<ContentValues> cVReviewVector = new Vector<>();
            Vector<ContentValues> cVTrailerVector = new Vector<>();

            for (String id : movieIDs) {
                uriString = Uri.parse(THEMOVIEDB_MOVIE_URL)
                        .buildUpon()
                        .appendPath(id + "?")
                        .appendQueryParameter(API_KEY_PARAM, mContext.getString(R.string.api_key))
                        .appendQueryParameter(APPEND_PARAM, APPEND_PARAM_VALUES)
                        .build().toString();
                moviesJsonStr = GetJsonDataFromUrl(uriString);
                JSONObject movieObject = new JSONObject(moviesJsonStr);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIEID, movieObject.getString(MOVIEDB_ID));
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movieObject.getString(MOVIEDB_TITLE));
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieObject.getString(MOVIEDB_OVERVIEW));
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, BASE_IMAGE_URL + movieObject.getString(MOVIEDB_POSTERPATH));
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieObject.getString(MOVIEDB_RELEASEDATE));
                movieValues.put(MovieContract.MovieEntry.COLUMN_THUMBNAIL_PATH, BASE_IMAGE_URL + movieObject.getString(MOVIEDB_THUMBNAILPATH));
                movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, movieObject.getString(MOVIEDB_USERRATING));
                movieValues.put(MovieContract.MovieEntry.COLUMN_DURATION, movieObject.getString(MOVIEDB_RUNTIME));
                movieValues.put(MovieContract.MovieEntry.COLUMN_SORT_TYPE, params[0]);

                cVVector.add(movieValues);

                JSONArray trailerList = movieObject.getJSONObject(MOVIEDB_TRAILERS).getJSONArray(MOVIEDB_YOUTUBE);
                for (int i = 0; i < trailerList.length(); i++) {
                    JSONObject trailer = (JSONObject) trailerList.get(i);
                    ContentValues trailerValues = new ContentValues();

                    trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIEID, movieObject.getString(MOVIEDB_ID));
                    trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER, trailer.getString(MOVIEDB_VIDEOSOURCE));

                    cVTrailerVector.add(trailerValues);
                }

                JSONArray reviewList = movieObject.getJSONObject(MOVIEDB_REVIEWS).getJSONArray(MOVIEDB_RESULTS);
                for (int i = 0; i < reviewList.length(); i++) {
                    JSONObject review = (JSONObject) reviewList.get(i);
                    ContentValues reviewValues = new ContentValues();

                    reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIEID, movieObject.getString(MOVIEDB_ID));
                    reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getString(MOVIEDB_REVIEWAUTHOR));
                    reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW, review.getString(MOVIEDB_REVIEW));

                    cVReviewVector.add(reviewValues);
                }
            }
            // Delete the entries from the database whenever a new fetchtask is created
            mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
            mContext.getContentResolver().delete(MovieContract.ReviewEntry.CONTENT_URI, null, null);
            mContext.getContentResolver().delete(MovieContract.TrailerEntry.CONTENT_URI, null, null);
            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);

                Log.d(LOG_TAG, inserted + " Movies Inserted");
            }
            if (cVReviewVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVReviewVector.size()];
                cVReviewVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);

                Log.d(LOG_TAG, inserted + " Reviews Inserted");
            }
            if (cVTrailerVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVTrailerVector.size()];
                cVTrailerVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);

                Log.d(LOG_TAG, inserted + " Trailers Inserted");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    private ArrayList<String> GetMovieListFromJSon(String moviesJsonStr) throws JSONException {
        final String MOVIEDB_RESULTS = "results";
        final String MOVIEDB_ID = "id";

        JSONObject jsonObject = new JSONObject(moviesJsonStr);
        JSONArray movieList = jsonObject.getJSONArray(MOVIEDB_RESULTS);

        ArrayList<String> movieIDs = new ArrayList<>();

        for (int i = 0; i < movieList.length(); i++) {
            String id = ((JSONObject) movieList.get(i)).getString(MOVIEDB_ID);
            movieIDs.add(id);
        }
        return movieIDs;
    }

    private String GetJsonDataFromUrl(String uriString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJsonStr = null;
        try {
            URL url = new URL(uriString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return moviesJsonStr;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return moviesJsonStr;
            }
            moviesJsonStr = buffer.toString();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("MoviesFragment", "Error closing stream", e);
                }
            }
        }
        return moviesJsonStr;
    }
}
