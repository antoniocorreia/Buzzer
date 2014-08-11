package com.parse.buzzer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
* Activity que mostra a tela de login para o usuário.
*/
public class SignUpActivity extends Activity {
  // refs de UI
  private EditText usernameView;
  private EditText passwordView;
  private EditText passwordAgainView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_signup);

    // setup do formulário de signup.
    usernameView = (EditText) findViewById(R.id.username);
    passwordView = (EditText) findViewById(R.id.password);
    passwordAgainView = (EditText) findViewById(R.id.passwordAgain);

    // setup do submit button
    findViewById(R.id.action_button).setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {

        // valida os dados de signup
        boolean validationError = false;
        StringBuilder validationErrorMessage =
            new StringBuilder(getResources().getString(R.string.error_intro));
        if (isEmpty(usernameView)) {
          validationError = true;
          validationErrorMessage.append(getResources().getString(R.string.error_blank_username));
        }
        if (isEmpty(passwordView)) {
          if (validationError) {
            validationErrorMessage.append(getResources().getString(R.string.error_join));
          }
          validationError = true;
          validationErrorMessage.append(getResources().getString(R.string.error_blank_password));
        }
        if (!isMatching(passwordView, passwordAgainView)) {
          if (validationError) {
            validationErrorMessage.append(getResources().getString(R.string.error_join));
          }
          validationError = true;
          validationErrorMessage.append(getResources().getString(
              R.string.error_mismatched_passwords));
        }
        validationErrorMessage.append(getResources().getString(R.string.error_end));

        // se exite erro de validação, mostrar esse erro..
        if (validationError) {
          Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
              .show();
          return;
        }

        // setup do progress dialog
        final ProgressDialog dlg = new ProgressDialog(SignUpActivity.this);
        dlg.setTitle("Por favor, aguarde.");
        dlg.setMessage("Conectando. Por favor, aguarde.");
        dlg.show();

        // setup do novo Parse user
        ParseUser user = new ParseUser();
        user.setUsername(usernameView.getText().toString());
        user.setPassword(passwordView.getText().toString());
        // chama o método do Parse pra signup
        user.signUpInBackground(new SignUpCallback() {

          @Override
          public void done(ParseException e) {
            dlg.dismiss();
            if (e != null) {
              // exibe a mensagem de erro
              Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            } else {
              // inicia uma intent para a dispatch activity
              Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
              intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(intent);
            }
          }
        });
      }
    });
  }

  /*método auxiliar pra verificar se o campo está vazio*/
  private boolean isEmpty(EditText etText) {
    if (etText.getText().toString().trim().length() > 0) {
      return false;
    } else {
      return true;
    }
  }

  /*método auxiliar pra verificar se os passwords são iguais*/
  private boolean isMatching(EditText etText1, EditText etText2) {
    if (etText1.getText().toString().equals(etText2.getText().toString())) {
      return true;
    } else {
      return false;
    }
  }

}

