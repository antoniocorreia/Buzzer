package com.parse.buzzer;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.parse.ParseUser;

/**
* Activity que mostra a tela de configurações.
*/
public class SettingsActivity extends Activity {

  private RadioGroup searchDistanceGroup;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    // as opções de distância (isso vai ser útil para definir o raio que quer ser alertado, tipo quero ser alertado em um raio de 500m)
    searchDistanceGroup = (RadioGroup) findViewById(R.id.searchDistanceGroup);
    float searchDistance = Application.getSearchDistance();
    if (searchDistance > 1000f) {
      searchDistanceGroup.check(R.id.feet4000Button);
    } else if (searchDistance > 250f) {
      searchDistanceGroup.check(R.id.feet1000Button);
    } else {
      searchDistanceGroup.check(R.id.feet250Button);
    }
    // seta o 'selection handler' pra salvar o que foi selecionado
    searchDistanceGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
        case R.id.feet4000Button:
          Application.setSearchDistance(4000);
          break;
        case R.id.feet1000Button:
          Application.setSearchDistance(1000);
          break;
        case R.id.feet250Button:
          Application.setSearchDistance(250);
          break;
        }
      }
    });

    // setup de logout
    ((Button) findViewById(R.id.voltarButton)).setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // chama o método do Parse de log out
        //ParseUser.logOut();
        // inicia a intent pra dispatch activity
        
        // limpa a lista que guarda as ocorrencias j� alertadas
        //MainActivity.alreadyAlerted.clear();
        
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
      }
    });
  }
}
