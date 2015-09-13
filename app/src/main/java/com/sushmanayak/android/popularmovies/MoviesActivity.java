package com.sushmanayak.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.ActivityOptionsCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sushmanayak.android.popularmovies.Adapter.MovieImageAdapter;

import java.util.HashMap;
import java.util.Map;

public class MoviesActivity extends AppCompatActivity implements MovieImageAdapter.CallBacks {

    private boolean mTwoPane = false;
    final String DETAIL_FRG_TAG = "DFTAG";
    public static HashMap<String,String> FavoriteMovies = new HashMap<>();
    final static String FAV_MOVIES = "Favorite_Movies";

    public static enum SearchMethod {
        POPULARITY,
        USER_RATING,
        FAVORITES
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        // Get the favorite movie Ids in SharedPreferences
        SharedPreferences prefs = getSharedPreferences(FAV_MOVIES, 0);
        for( Map.Entry entry : prefs.getAll().entrySet() )
            FavoriteMovies.put(entry.getKey().toString(), entry.getValue().toString());

        if (findViewById(R.id.movieDetailsContainer) != null) {
            mTwoPane = true;
        } else
            mTwoPane = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Save the favorite movie Ids in SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences(FAV_MOVIES, 0).edit();
        for( Map.Entry entry : FavoriteMovies.entrySet() )
            editor.putString(entry.getKey().toString(), entry.getValue().toString());
        editor.commit();
    }

    @SuppressWarnings("NewApi")
    public void onItemClicked(View v, int position, String movieId) {
        if (mTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movieDetailsContainer, MovieDetailsFragment.newInstance(movieId), DETAIL_FRG_TAG)
                    .commit();
        } else {
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, v, getString(R.string.imageTransition));
            Intent intent = new Intent(this, MovieInfoActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, movieId);
            startActivity(intent, optionsCompat.toBundle());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movies, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}
