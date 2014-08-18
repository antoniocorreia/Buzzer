package com.parse.buzzer;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class RouteActivity extends Activity{
	ImageButton carro;
	ImageButton ape;
	ImageButton bicicleta;
	String meioTransporte = "driving";
	CheckBox localizacaoAtual;
	List<Address> addresses = null;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routes);

		//localização deve vir carregada
		//		localizacaoAtual = (CheckBox) findViewById(R.id.localizacaoAtual);
		//		localizacaoAtual.setChecked(true);


		//já carrega localização atual
		try {
			setAtual();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		EditText destino = (EditText) findViewById(R.id.editTextDestino);
		destino.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		    @Override
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		            try {
						procuraLugar();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            return true;
		        }
		        return false;
		    }
		});
	}



	public void setAtual() throws IOException {
		String temp = "";
		Address address = null;

		//TextView endereco = (TextView) findViewById(R.id.textViewOrigem);
		Geocoder geocoder = new Geocoder(RouteActivity.this,Locale.getDefault());

		// seta o local para o local do usuÃ¡rio
		//		occurrence.setLocation(MainActivity.myPointLoc);
		//		occurrence.setUser(ParseUser.getCurrentUser());

		addresses = geocoder.getFromLocation(
				MainActivity.myPointLoc.getLatitude(),
				MainActivity.myPointLoc.getLongitude(), 1);
		address = addresses.get(0);
		// constroi a string do endereco
		for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
			if (address.getAddressLine(i) != null) {
				temp = temp + address.getAddressLine(i);
				if (i % 3 == 0 && i != 0)
					temp = temp + "\n";
				else if (i != address.getMaxAddressLineIndex() - 1)
					temp = temp + ", ";
			}
		}
		//endereco.setText("Você está em: "+temp);
	}

	public void selecionarOpcao(View v){
		carro = (ImageButton) findViewById(R.id.carroButton);
		ape = (ImageButton) findViewById(R.id.apeButton);
		//bicicleta = (ImageButton) findViewById(R.id.bicicletaButton);

		switch(v.getId()){
		case R.id.carroButton:
			meioTransporte = "driving";//para parametro do geraRota
			carro.setBackgroundResource(R.drawable.ic_rotas_carro_pressed);
			ape.setBackgroundResource(R.drawable.ic_rota_ape);
			//bicicleta.setBackgroundResource(R.drawable.ic_rota_bicicleta);
			break;
		case R.id.apeButton:
			meioTransporte = "walking";
			carro.setBackgroundResource(R.drawable.ic_rotas_carro);
			ape.setBackgroundResource(R.drawable.ic_rota_ape_pressed);
			//bicicleta.setBackgroundResource(R.drawable.ic_rota_bicicleta);
			break;
			//		case R.id.bicicletaButton:
			//			meioTransporte = "bicycling";
			//			carro.setBackgroundResource(R.drawable.ic_rotas_carro);
			//			ape.setBackgroundResource(R.drawable.ic_rota_ape);
			//			bicicleta.setBackgroundResource(R.drawable.ic_rota_bicicleta_pressed);
			//			break;
		}

	}
	
	public void getEnd(View view) {
		if (!addresses.isEmpty()) {
			String temp = "";
			Address address = null;
			if (view.getId() == R.id.a0)
				address = addresses.get(0);
			else if (view.getId() == R.id.a1)
				address = addresses.get(1);
			else if (view.getId() == R.id.a2)
				address = addresses.get(2);
			else if (view.getId() == R.id.a3)
				address = addresses.get(3);

			setContentView(R.layout.activity_routes);
			
			EditText endereco = (EditText) findViewById(R.id.editTextDestino);

			for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
				if (address.getAddressLine(i) != null) {
					temp = temp + address.getAddressLine(i);
					if (i % 3 == 0 && i != 0)
						temp = temp + "\n";
					else if (i != address.getMaxAddressLineIndex() - 1)
						temp = temp + ", ";
				}
			}
			endereco.setText(temp);
			// seta o local para o local do usuÃ¡rio
//			occurrence.setLocation(new ParseGeoPoint(address.getLatitude(),
//					address.getLongitude()));
//			occurrence.setUser(ParseUser.getCurrentUser());
		} else {
			setContentView(R.layout.activity_routes);
			
		}
	}
	public void procuraLugar() throws IOException {

		Geocoder geocoder = new Geocoder(RouteActivity.this,Locale.getDefault());
		EditText endereco = (EditText) findViewById(R.id.editTextDestino);
		String aux = endereco.getText().toString();
		setContentView(R.layout.activity_lista_lugares);
		TextView[] temp = new TextView[4];
		temp[0] = (TextView) findViewById(R.id.a0);
		temp[1] = (TextView) findViewById(R.id.a1);
		temp[2] = (TextView) findViewById(R.id.a2);
		temp[3] = (TextView) findViewById(R.id.a3);

		addresses = geocoder.getFromLocationName(aux, 4);
		String endTotal = "";
		for (int i = 0; i < temp.length && i < addresses.size(); i++) {
			endTotal = "";
			for (int j = 0; j < addresses.get(i).getMaxAddressLineIndex(); j++) {
				if (addresses.get(i).getAddressLine(j) != null) {
					endTotal = endTotal + addresses.get(i).getAddressLine(j);
					if (j % 3 == 0 && j != 0)
						endTotal = endTotal + "\n";
					else if (j != addresses.get(i).getMaxAddressLineIndex() - 1)
						endTotal = endTotal + ", ";
				}
			}
			temp[i].setText(endTotal);
		}
	}


}
