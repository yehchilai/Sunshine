package com.example.android.sunshine.app;

/**
 * Created by MarkLai on 4/13/15.
 */

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherContract.WeatherEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private String mForecast;
    private ShareActionProvider mShareActionProvider;
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #Sunshine";

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;

    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate menu
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share button
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider
        mShareActionProvider= (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach intent to share provider
        if (mForecast != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
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
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);

        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null || intent.getData() == null) {
            return null;
        }

        return new CursorLoader(getActivity(),
                intent.getData(),
                DETAIL_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        // Weather ID
        int weather_ID = data.getInt(COL_WEATHER_CONDITION_ID);
        mIconView.setImageResource(Utility.getIconResourceForWeatherCondition(weather_ID));

        // Date and Day
        long date = data.getLong(COL_WEATHER_DATE);
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        mFriendlyDateView.setText(friendlyDateText);
        mDateView.setText(dateText);

        // Read description from cursor and update view
        String description = data.getString(COL_WEATHER_DESC);
        mDescriptionView.setText(description);

        // Temperature
        boolean isMetric = Utility.isMetric(getActivity());
        String high = Utility.formatTemperature(getActivity(),data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        mHighTempView.setText(high);
        String low = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
        mLowTempView.setText(low);

        // Read humidity from cursor and update view
        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

        // Read wind speed and direction from cursor and update view
        float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
        float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
        mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

        // Read pressure from cursor and update view
        float pressure = data.getFloat(COL_WEATHER_PRESSURE);
        mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

        // Share intent message
        mForecast = String.format("%s - %s - %s/%s", dateText, description, high, low);

        //TextView detailTextView = (TextView)getView().findViewById(R.id.detailTextView);
        //detailTextView.setText(mForecast);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
