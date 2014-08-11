package com.parse.buzzer;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ResetPasswordActivity extends Activity{
	private EditText email;
	private Dialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		
		email = (EditText) findViewById(R.id.email);
	}

	public void recuperarSenha(View v){
		ResetPasswordActivity.this.progressDialog = ProgressDialog.show(
				ResetPasswordActivity.this, "", "Enviando e-mail...", true);
		
		ParseUser.requestPasswordResetInBackground(email.getText().toString(),
				new RequestPasswordResetCallback() {
			public void done(ParseException e) {
				ResetPasswordActivity.this.progressDialog.dismiss();
				if (e == null) {
					// An email was successfully sent with reset instructions.
					Toast toast = Toast.makeText(ResetPasswordActivity.this,R.string.email_enviado,Toast.LENGTH_SHORT);
					toast.show();
					Intent intent = new Intent(ResetPasswordActivity.this,LoginActivity.class);
					startActivity(intent);
					
				} else {
					// Something went wrong. Look at the ParseException to see what's up.
					AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
					builder.setMessage(e.getMessage()).setTitle(R.string.login_erro)
					.setPositiveButton(android.R.string.ok,	null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}
		});
	}
}
