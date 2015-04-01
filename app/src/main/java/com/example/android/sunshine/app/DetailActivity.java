package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DetailActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {

        String message;
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final String FORECAST_SHARE_HASHTAG = " #Sunshine";

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
                message = intent.getStringExtra(Intent.EXTRA_TEXT);
//                TextView textView = (TextView)rootView.findViewById(R.id.detailTextView);
//                textView.setText(message);
                ((TextView)rootView.findViewById(R.id.detailTextView))
                        .setText(message);
            }
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

            // Inflate menu
            inflater.inflate(R.menu.detailfragment, menu);

            // Retrieve the share button
            MenuItem menuItem = menu.findItem(R.id.action_share);

            // Get the provider
            ShareActionProvider shareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            // Attach intent to share provider
            if(shareActionProvider != null){
                shareActionProvider.setShareIntent(createShareForecastIntent());
            }else{
                Log.d(LOG_TAG, " Share action is null !!!");
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            return super.onOptionsItemSelected(item);

        }

        private Intent createShareForecastIntent(){
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message + FORECAST_SHARE_HASHTAG);

            return shareIntent;
        }

    }
}
