package com.parse.buzzer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity implements Runnable{
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		Handler handler = new Handler();
		handler.postDelayed(this, 2000);
	}
	
	@Override
	public void run() {
		startActivity(new Intent(this, DispatchActivity.class));
		finish();		
	}
	
}
