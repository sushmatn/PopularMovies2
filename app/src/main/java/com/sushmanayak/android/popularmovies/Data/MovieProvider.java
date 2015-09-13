package com.sushmanayak.android.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.net.Uri;

/**
 * Created by SushmaNayak on 9/9/2015.
 */
public class MovieProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int REVIEWS = 200;
    static final int MOVIE_REVIEWS = 201;
    static final int TRAILERS = 300;
    static final int MOVIE_TRAILERS = 301;
    static final int MOVIE_DETAILS = 101;

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        int match = sUriMatcher.match(uri);
        SQLiteDatabase database = mOpenHelper.getReadableDatabase();

        switch (match) {

            case MOVIE_DETAILS:
                String movieId = uri.getPathSegments().get(1);
                retCursor = database.query(MovieContract.MovieEntry.TABLE_NAME, projection, MovieContract.MovieEntry.COLUMN_MOVIEID + "= ?",
                        new String[]{movieId}, null, null, sortOrder);
                break;

            case MOVIE_REVIEWS:
                movieId = uri.getPathSegments().get(1);
                retCursor = database.query(MovieContract.ReviewEntry.TABLE_NAME, projection, MovieContract.ReviewEntry.COLUMN_MOVIEID + "= ?",
                        new String[]{movieId}, null, null, sortOrder);
                break;

            case MOVIE_TRAILERS:
                movieId = uri.getPathSegments().get(1);
                retCursor = database.query(MovieContract.TrailerEntry.TABLE_NAME, projection, MovieContract.TrailerEntry.COLUMN_MOVIEID + "= ?",
                        new String[]{movieId}, null, null, sortOrder);
                break;

            case MOVIES:
                retCursor = database.query(MovieContract.MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case REVIEWS:
                retCursor = database.query(MovieContract.ReviewEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case TRAILERS:
                retCursor = database.query(MovieContract.TrailerEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Undefined uri " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_DETAILS:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case MOVIE_REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case TRAILERS:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case MOVIE_TRAILERS:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);
        SQLiteDatabase writableDatabase = mOpenHelper.getWritableDatabase();
        long rowId;
        Uri returnUri;

        switch (match) {
            case MOVIES:
                rowId = writableDatabase.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (rowId > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case REVIEWS:
                rowId = writableDatabase.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (rowId > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(rowId);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case TRAILERS:
                rowId = writableDatabase.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (rowId > 0)
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(rowId);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            default:
                throw new UnsupportedOperationException("Unsupported Uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERS:
                rowsDeleted = db.delete(
                        MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            case MOVIE_DETAILS:
                String movieId = uri.getPathSegments().get(1);
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, MovieContract.MovieEntry.COLUMN_MOVIEID + "= ?",
                        new String[]{movieId});
                break;

            case REVIEWS:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRAILERS:
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEWS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TRAILERS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILERS, TRAILERS);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEWS + "/*", MOVIE_REVIEWS);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILERS + "/*", MOVIE_TRAILERS);
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_DETAILS);
        return uriMatcher;
    }
}
