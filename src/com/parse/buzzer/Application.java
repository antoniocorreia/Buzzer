package com.parse.buzzer;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.PushService;

public class Application extends android.app.Application {
	// Debugging switch
	public static final boolean APPDEBUG = false;

	// Debugging tag for the application
	public static final String APPTAG = "Buzzer";

	// Key for saving the search distance preference
	private static final String KEY_SEARCH_DISTANCE = "searchDistance";
	
	private static final String SONORE_ALERT = "sonore_alert";
	
	private static final String VIBRATORY_ALERT = "vibratory_alert";

	private static SharedPreferences preferences;

	public Application() {
	}

	@Override
	public void onCreate() {
		super.onCreate();

		ParseObject.registerSubclass(BuzzerOccurrence.class);
		Parse.initialize(this, "A0oJwpPbn9ggEnXwxBHqz4ffwBPPLPJriD8aqI6T", "cEmzblmV7KraUobCjQ5MiFzkFqlRISVy9Du3dqKo");
		ParseFacebookUtils.initialize(getString(R.string.app_id));
		// Specify an Activity to handle all pushes by default.
		PushService.setDefaultPushCallback(this, MainActivity.class);
		preferences = getSharedPreferences("com.parse.buzzer", Context.MODE_PRIVATE);
	}

	public static float getSearchDistance() {
		return preferences.getFloat(KEY_SEARCH_DISTANCE, 250);
	}

	public static void setSearchDistance(float value) {
		preferences.edit().putFloat(KEY_SEARCH_DISTANCE, value).commit();
	}
	
	public static void setSonoreAlert(boolean value){
		preferences.edit().putBoolean(SONORE_ALERT, value).commit();
	}
	
	public static void setVibratoryAlert(boolean value){
		preferences.edit().putBoolean(VIBRATORY_ALERT, value).commit();
	}
	
	public static boolean getSonoreAlert(){
		return preferences.getBoolean(SONORE_ALERT, false);
	}
	
	public static boolean getVibratoryAlert(){
		return preferences.getBoolean(VIBRATORY_ALERT, true);
	}
}
