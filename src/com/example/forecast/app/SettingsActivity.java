package com.example.forecast.app;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity 
			implements OnSharedPreferenceChangeListener{
	
	private static final String KEY_EDIT_TEXT_PREFERENCE = "name";
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		// Add general preferences defined in the XML file pref_general.xml
		addPreferencesFromResource(R.xml.pref_general);		
	}
	
	protected void onResume(){
		super.onResume();
		// Set up a listener when a key changes.
		getPreferenceScreen().getSharedPreferences()
			.registerOnSharedPreferenceChangeListener(this);
		updatePreference(KEY_EDIT_TEXT_PREFERENCE);
	}
	
	protected void onPause(){
		super.onPause();
		// Unregister the listener whenever the key changes.
		getPreferenceScreen().getSharedPreferences()
			.unregisterOnSharedPreferenceChangeListener(this);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
		updatePreference(key);
	}
	
	private void updatePreference(String key){
		if(key.equals(KEY_EDIT_TEXT_PREFERENCE)){
			Preference preference = findPreference(key);
			if(preference instanceof EditTextPreference){
				EditTextPreference editTextPreference = (EditTextPreference)preference;
				if(editTextPreference.getText().trim().length() > 0){
					editTextPreference.setSummary(editTextPreference.toString());
				}
			}
		}	
	}

	/*private void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(this);
		
		// Trigger the listener immediately with the preference's
		// current value.
		 
		onPreferenceChange(preference,
				PreferenceManager
					.getDefaultSharedPreferences(preference.getContext())
					.getString(preference.getKey(), ""));
	}*/

	
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		String stringValue = newValue.toString();
		
		if(preference instanceof ListPreference){
			// For list preferences, look up the correct display values
			// in the preference's entries list (since they have seperate
			// label/values.)
			//
			ListPreference listPreference = (ListPreference) preference;
			int prefIndex = listPreference.findIndexOfValue(stringValue);
			if(prefIndex >= 0){
				preference.setSummary(listPreference.getEntries()[prefIndex]);
			}
		}
		else{
			// For other preferences, set the summary to the value's string representation.
			preference.setSummary(stringValue);
		}
		return true;
	}
}
