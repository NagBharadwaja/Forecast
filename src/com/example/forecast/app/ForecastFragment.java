package com.example.forecast.app;

import com.example.forecast.app.data.WeatherContract;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
	
	private ForecastAdapter mForecastAdapter;
	private ListView listView;
	private SharedPreferences preferences;
	private String location;
	
	private static final int FORECAST_LOADER = 0;
	private static final String[] FORECAST_COLUMNS = {
		WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
		WeatherContract.WeatherEntry.COLUMN_DATE,
		WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
		WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
		WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
		WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
		WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
		WeatherContract.LocationEntry.COLUMN_LAT,
		WeatherContract.LocationEntry.COLUMN_LONG
	};
	
	static final int COL_WEATHER_ID = 0;
	static final int COL_WEATHER_DATE = 1;
	static final int COL_WEATHER_DESC = 2;
	static final int COL_WEATHER_MAX_TEMP = 3;
	static final int COL_WEATHER_MIN_TEMP = 4;
	static final int COL_LOCATION_SETTING = 5;
	static final int COL_WEATHER_CONDITION_ID = 6;
	static final int COL_COORD_LAT = 7;
	static final int COL_COORD_LONG = 8;

    public ForecastFragment() {
    }
    
    public void onCreate(Bundle savedInstanceState){
    	super.onCreate(savedInstanceState);
    	// To handle all the menu events
    	setHasOptionsMenu(true);
    }
    
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
    	inflater.inflate(R.menu.forecastfragment, menu);    	
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
    	// Handles action bar clicks here
    	int id = item.getItemId();
    	
    	// Checks if the item clicked is Settings
    	if(id == R.id.action_settings){
    		Intent intent = new Intent(getActivity(), SettingsActivity.class);
    		startActivity(intent);
    		return true;
    	}
    	
    	if(id == R.id.action_refresh){
    		updateWeather();
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);// Initializing the adapter.
        
        // Attach the adapter to the list view.
        listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        
        // Adding OnItemClickListener to get the detail view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
        	@Override
			public void onItemClick(AdapterView adapterView, View view,
					int position, long id) {
				// TODO Auto-generated method stub
        		// CursorAdapter returns a cursor at the correct position for getItem()
        		// or null if it cannot seek the position.
        		Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        		if(cursor != null){
        			String locationSetting = Utility.getPreferredLocation(getActivity());
        			Intent intent = new Intent(getActivity(), DetailActivity.class)
        								.setData(WeatherContract.WeatherEntry
        										.buildWeatherLocationWithDate(locationSetting, 
        												cursor.getLong(COL_WEATHER_DATE)));
        			startActivity(intent);
        		}
				
			}
        });
        return rootView;
    }
    
    public void onActivityCreated(Bundle savedInstanceState){
    	getLoaderManager().initLoader(FORECAST_LOADER, null, this);
    	super.onActivityCreated(savedInstanceState);
    }
    
    public void updateWeather(){
    	FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
    	preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
    	location = preferences.getString(
    			getString(R.string.pref_location_key), getString(R.string.pref_location_default));
		weatherTask.execute(location);
    }
    
    /*public void onStart(){
    	super.onStart();
    	updateWeather();
    }*/

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle arg1) {
		// TODO Auto-generated method stub
		
		// Get location setting
        String locationSetting = Utility.getPreferredLocation(getActivity());
        
        // Set sort order based on date in ascending order.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        
        // Set the URI to query the database using URI.
        Uri weatherForLocationUri = WeatherContract.WeatherEntry
        		.buildWeatherLocationWithStartDate(
        				locationSetting, System.currentTimeMillis());

        // Query the database using the above URI and get the cursor.
        Cursor cursor = getActivity().getContentResolver().query(
        				weatherForLocationUri, null, null, null, sortOrder);

		return new CursorLoader(getActivity(), 
				weatherForLocationUri, 
                FORECAST_COLUMNS, 
                null, 
                null, 
                sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		// TODO Auto-generated method stub
		mForecastAdapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
		// TODO Auto-generated method stub
		mForecastAdapter.swapCursor(null);
	}
	
	public void onLocationChanged(){
		updateWeather();
		getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
	}
    
}
