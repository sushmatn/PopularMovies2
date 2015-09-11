package com.sushmanayak.android.popularmovies;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sushmanayak.android.popularmovies.Adapter.MovieImageAdapter;
import com.sushmanayak.android.popularmovies.Data.MovieContract;

public class MoviesActivity extends AppCompatActivity implements MovieImageAdapter.CallBacks {

    private boolean mTwoPane = false;
    final String DETAIL_FRG_TAG = "DFTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        if (findViewById(R.id.movieDetailsContainer) != null) {
            mTwoPane = true;
        } else
            mTwoPane = false;
    }

    @SuppressWarnings("NewApi")
    public void onItemClicked(View v, int position, String movieId) {
        if (mTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movieDetailsContainer, MovieDetailsFragment.newInstance(movieId), DETAIL_FRG_TAG)
                    .commit();
        } else {
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, v, "posterTransition");
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
