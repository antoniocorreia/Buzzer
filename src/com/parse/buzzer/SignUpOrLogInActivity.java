package com.parse.buzzer;

import java.util.Arrays;
import java.util.List;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpOrLogInActivity extends Activity{

	private EditText editTextUsuario;
	private EditText editTextEmail;
	private EditText editTextSenha;
	private EditText editTextConfirmaSenha;
	private Dialog progressDialog;
	private Button loginButton;
	private Button cadastrarButton;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up_or_log_in);

		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLoginButtonClicked();
			}
		});

		cadastrarButton = (Button) findViewById(R.id.buttonCadastrar);
		cadastrarButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				cadastrar();
			}
		});
	}

	public void loginNormal(View v){
		startActivity(new Intent(this,LoginActivity.class));
	}

	public void cadastrar(){
		editTextSenha = (EditText) findViewById(R.id.editTextSenha);
		editTextConfirmaSenha = (EditText) findViewById(R.id.editTextConfirmarSenha);
		editTextUsuario = (EditText) findViewById(R.id.editTextUsuario);
		editTextEmail = (EditText) findViewById(R.id.editTextEmail);

		//checar se usuário e senhas e email estão preenchidos
		if(editTextUsuario.getText().toString().trim().matches("") || editTextSenha.getText().toString().trim().matches("") || editTextEmail.getText().toString().trim().matches("")){
			Toast.makeText(this, R.string.mensagem_erro_campos_cadastro,Toast.LENGTH_SHORT).show();
		}else{
			//checa se confirmacao de senha esta correta
			if (editTextSenha.getText().toString().trim().equals(editTextConfirmaSenha.getText().toString().trim())){
				SignUpOrLogInActivity.this.progressDialog = ProgressDialog.show(
						SignUpOrLogInActivity.this, "", "Cadastrando...", true);

				ParseUser user = new ParseUser();
				user.setUsername(editTextUsuario.getText().toString().trim());
				user.setPassword(editTextSenha.getText().toString().trim());
				user.setEmail(editTextEmail.getText().toString().trim());

				// other fields can be set just like with ParseObject
				//user.put("phone", "650-253-0000");

				user.signUpInBackground(new SignUpCallback() {

					@Override
					public void done(ParseException e) {
						SignUpOrLogInActivity.this.progressDialog.dismiss();
						if (e == null) {

							// Hooray! Let them use the app now.

							// inicia uma intent para a dispatch activity
							Intent intent = new Intent(SignUpOrLogInActivity.this, DispatchActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);

						} else {
							// Sign up didn't succeed. Look at the ParseException
							// to figure out what went wrong
							AlertDialog.Builder builder = new AlertDialog.Builder(
									SignUpOrLogInActivity.this);
							builder.setMessage(e.getMessage())
							.setTitle(R.string.cadastro_erro)
							.setPositiveButton(android.R.string.ok,
									null);
							AlertDialog dialog = builder.create();
							dialog.show();
						}
					}
				});


			}else{
				Toast toast1 = Toast.makeText(this, R.string.mensagem_erro_conf_senha,Toast.LENGTH_SHORT);
				toast1.show();
				editTextConfirmaSenha.requestFocus();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void onLoginButtonClicked() {
		SignUpOrLogInActivity.this.progressDialog = ProgressDialog.show(
				SignUpOrLogInActivity.this, "", "Entrando...", true);
		List<String> permissions = Arrays.asList("basic_info","user_birthday", "user_location");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				SignUpOrLogInActivity.this.progressDialog.dismiss();
				if (user == null) {
					Log.d("buzzer",	"Uh oh. The user cancelled the Facebook login." + err.getMessage());
				} else if (user.isNew()) {
					//Log.d("buzzer","User signed up and logged in through Facebook!");
					Intent intent = new Intent(SignUpOrLogInActivity.this,MainActivity.class);
					startActivity(intent);

				} else {
					//Log.d("buzzer",	"User logged in through Facebook!");
					Intent intent = new Intent(SignUpOrLogInActivity.this, MainActivity.class);
					startActivity(intent);
				}
			}
		});
	}

}
