package com.parse.buzzer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

/**
 * activity que inicia uma intent tanto pra logged (MainActivity) ou logged out (SignUpOrLoginActivity).
 */
public class DispatchActivity extends Activity {

	public DispatchActivity() {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// checa se existe info do current user
		if (ParseUser.getCurrentUser() != null) {
			// inicia uma intent para MainActivity
			startActivity(new Intent(this, MainActivity.class));
		} else {
			// inicia uma intent para SignUpOrLogInActivity
			startActivity(new Intent(this, SignUpOrLogInActivity.class));
		}
	}

}