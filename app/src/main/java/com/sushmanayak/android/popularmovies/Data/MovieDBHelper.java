package com.sushmanayak.android.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SushmaNayak on 9/9/2015.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "Movie.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + "( " +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                MovieContract.MovieEntry.COLUMN_MOVIEID + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_THUMBNAIL_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_DURATION + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_SORTBY + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_FAVORITE + " INTEGER DEFAULT 0, " +

                " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIEID + ", " + MovieContract.MovieEntry.COLUMN_SORTBY + " ) ON CONFLICT IGNORE);";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + "( " +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                MovieContract.ReviewEntry.COLUMN_MOVIEID + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL," +
                MovieContract.ReviewEntry.COLUMN_REVIEW + " TEXT NOT NULL, " +
				"FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIEID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + "(" + MovieContract.MovieEntry.COLUMN_MOVIEID + ")" +
				
                " UNIQUE (" + MovieContract.ReviewEntry.COLUMN_MOVIEID + ", " +
                MovieContract.ReviewEntry.COLUMN_AUTHOR + ") ON CONFLICT IGNORE)";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + "( " +
                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                MovieContract.TrailerEntry.COLUMN_MOVIEID + " TEXT NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER + " TEXT NOT NULL, " +

				"FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIEID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + "(" + MovieContract.MovieEntry.COLUMN_MOVIEID + ")" +

                " UNIQUE (" + MovieContract.TrailerEntry.COLUMN_MOVIEID + ", " +
                MovieContract.TrailerEntry.COLUMN_TRAILER + ") ON CONFLICT IGNORE)";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        final String SQL_DELETE_MOVIE_TABLE = "DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME;
        final String SQL_DELETE_REVIEW_TABLE = "DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME;
        final String SQL_DELETE_TRAILER_TABLE = "DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_MOVIE_TABLE);
        db.execSQL(SQL_DELETE_REVIEW_TABLE);
        db.execSQL(SQL_DELETE_TRAILER_TABLE);
        onCreate(db);
    }
}
