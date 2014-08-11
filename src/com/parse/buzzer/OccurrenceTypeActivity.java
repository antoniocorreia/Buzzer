package com.parse.buzzer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class OccurrenceTypeActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_occurrence_type);
	}

	public void selecionarOpcao(View view) {
		/*
		com base na view que foi clicada
		iremos tomar a ação correta para passar o tipo da ocorrência para próxima activity
		 */
		switch(view.getId()){
		case R.id.novo_assalto:
			Intent assalto = new Intent(this,OccurrenceDetail.class);
			assalto.putExtra("Tipo", "Assalto");
			startActivity(assalto);
			break;
		case R.id.novo_furto:
			Intent furto = new Intent(this,OccurrenceDetail.class);
			furto.putExtra("Tipo", "Furto");
			startActivity(furto);
			break;
		case R.id.novo_tumulto:
			Intent tumulto = new Intent(this,OccurrenceDetail.class);
			tumulto.putExtra("Tipo", "Tumulto");
			startActivity(tumulto);
			break;
		case R.id.novo_outros:
			Intent outros = new Intent(this,OccurrenceDetail.class);
			outros.putExtra("Tipo", "Outros");
			startActivity(outros);
			break;
		}

	}
}
