package com.example.forecast.app;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.preference.PreferenceManager;



public class MainActivity extends ActionBarActivity {
	
	private static final String LOG_TAG = MainActivity.class.getSimpleName();
	private static final String FORECASTFRAGMENT_TAG = "FFTAG";
	private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(LOG_TAG,"CREATEDDDDDD");
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mLocation = prefs.getString(getString(R.string.pref_location_key), 
        		getString(R.string.pref_location_default));
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_map) {
        	openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void openPreferredLocationInMap(){
    	
    	// Get location
    	String location = Utility.getPreferredLocation(this);
    	// Build Uri
    	Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
    						.appendQueryParameter("q", location)
    						.build();
    	
    	// Displaying the location in intent
    	Intent intent = new Intent(Intent.ACTION_VIEW);
    	intent.setData(geoLocation);
    	
    	// Start the activity if it resolves successfully
    	if(intent.resolveActivity(getPackageManager()) !=  null){
    		startActivity(intent);
    	}
    	else{
    		Log.v(LOG_TAG, "Couldn't start activity");
    	}
    }
    
    public void onStart(){
    	super.onStart();
    	Log.v(LOG_TAG,"STARTTTTT");
    }
    public void onPause(){
    	super.onPause();
    	Log.v(LOG_TAG,"PAAAAUUUUSE");
    }
    public void onResume(){
    	super.onResume();
    	Log.v(LOG_TAG,"RESUME");
    	
    	String loc = Utility.getPreferredLocation(this);
    	if((loc != null) && (!mLocation.equals(loc))){
    		ForecastFragment ff = (ForecastFragment)getSupportFragmentManager()
    								.findFragmentByTag(FORECASTFRAGMENT_TAG);
    		if(ff != null){
    			ff.onLocationChanged();
    		}
    		mLocation = loc;
    	}
    }
    public void onStop(){
    	super.onStop();
    	Log.v(LOG_TAG,"STOP!!!");
    }
}
