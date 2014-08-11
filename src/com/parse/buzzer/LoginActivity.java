package com.parse.buzzer;

import java.util.Arrays;
import java.util.List;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends Activity{
	private EditText usuario;
	private EditText senha;
	private Dialog progressDialog;
	private Button loginButton;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});

	}

	public void login(View v) { 
		LoginActivity.this.progressDialog = ProgressDialog.show(
				LoginActivity.this, "", "Entrando...", true);

		usuario = (EditText) findViewById(R.id.usuario);
		senha = (EditText) findViewById(R.id.senha);

		String usuarioInformado = usuario.getText().toString().trim();
		String senhaInformada = senha.getText().toString().trim();

		ParseUser.logInInBackground(usuarioInformado,senhaInformada, new LogInCallback() {

			public void done(ParseUser user, ParseException e) {

				LoginActivity.this.progressDialog.dismiss();
				if (user != null) {
					//					Intent intent = new Intent(SignUpOrLogInActivity.this,MainActivity.class);
					//					startActivity(intent);
					// inicia uma intent para a dispatch activity
					Intent intent = new Intent(LoginActivity.this, DispatchActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} else {
					// Signup failed. Look at the ParseException to see what happened.
					AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
					builder.setMessage(e.getMessage()).setTitle(R.string.login_erro)
					.setPositiveButton(android.R.string.ok,	null);
					AlertDialog dialog = builder.create();
					dialog.show();
				}
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {
		LoginActivity.this.progressDialog = ProgressDialog.show(
				LoginActivity.this, "", "Entrando...", true);
		
		List<String> permissions = Arrays.asList("basic_info", "user_about_me",
				"user_birthday", "user_location");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				LoginActivity.this.progressDialog.dismiss();
				if (user == null) {
					//Log.d("buzzer",	"Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					//Log.d("buzzer","User signed up and logged in through Facebook!");
					// inicia uma intent para a dispatch activity
					Intent intent = new Intent(LoginActivity.this, DispatchActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);

				} else {
					//Log.d("buzzer",	"User logged in through Facebook!");
					// inicia uma intent para a dispatch activity
					Intent intent = new Intent(LoginActivity.this, DispatchActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			}
		});
	}
	public void cadastro(View v){
		startActivity(new Intent(this,SignUpOrLogInActivity.class));
	}

	public void recuperarSenha(View v){

		startActivity(new Intent(this,ResetPasswordActivity.class));
	}
}
