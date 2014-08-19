package com.parse.buzzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
	LatLng PONTO1;
	LatLng PONTO2;
	GoogleMap googleMap = MainActivity.map.getMap();
	private static Polyline polyLine = null;
	private static Marker marc1 = null;
	private static Marker marc2 = null;
	private ProgressDialog progressDialog;

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
		PONTO1 = new LatLng(address.getLatitude(),address.getLongitude());
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

			
			//setContentView(R.layout.activity_routes);
			
			//EditText endereco = (EditText) findViewById(R.id.editTextDestino);

			for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
				if (address.getAddressLine(i) != null) {
					temp = temp + address.getAddressLine(i);
					if (i % 3 == 0 && i != 0)
						temp = temp + "\n";
					else if (i != address.getMaxAddressLineIndex() - 1)
						temp = temp + ", ";
				}
			}
			//endereco.setText(temp);
			PONTO2 = new LatLng(address.getLatitude(),address.getLongitude());
			gerarRota(view);
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
	
	public void teste(View view){
		try {
			procuraLugar();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void gerarRota(View view) {
		
		
		/** Removendo a rota e os pontos iniciais e finais da rota anterior */
		if (polyLine != null) {
			polyLine.remove();
		}
		if (marc1 != null) {
			marc1.remove();
		}
		if (marc2 != null) {
			marc2.remove();
		}
		
		PathJSONParser.rotaEscolhida = 0;
		
		
		MarkerOptions options = new MarkerOptions();
		options.position(PONTO1);
		options.position(PONTO2);
		//options.icon(R.drawable.markerraio);
		//googleMap.addMarker(options);
		
		
		String url = getMapsApiDirectionsUrl();
		ReadTask downloadTask = new ReadTask(RouteActivity.this);
		RouteActivity.this.progressDialog = ProgressDialog.show(
				RouteActivity.this, "", "Buscando a rota mais segura...", true);
		downloadTask.execute(url);
		addMarkers();
		
		//super.finish();
	}
	
	private String getMapsApiDirectionsUrl() {
		String output = "json?";
		String mode = "driving"; // vai vir do botao
		String params = "sensor=false" + "&" + "alternatives=true" + "&" + "mode=" + mode;
		String url = "https://maps.googleapis.com/maps/api/directions/"	+ output + "origin=" + PONTO1.latitude + "," + PONTO1.longitude + "&" 
		+ "destination=" + PONTO2.latitude + "," + PONTO2.longitude + "&" +  params;
		return url;
	}

	private void addMarkers() {
		if (googleMap != null) {
			marc1 = googleMap.addMarker(new MarkerOptions().position(PONTO1)
					.title("Origem"));
			marc2 = googleMap.addMarker(new MarkerOptions().position(PONTO2)
					.title("Destino"));
		}
	}

	private class ReadTask extends AsyncTask<String, Void, String> {
		private RouteActivity mCallBack;
		
		public ReadTask(RouteActivity callback) {
			mCallBack = callback;
		}
		
		@Override
		protected String doInBackground(String... url) {
			String data = "";
			try {
				HttpConnection http = new HttpConnection();
				data = http.readUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			new ParserTask(mCallBack).execute(result);
		}
	}

	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
		
		private RouteActivity mCallBack;

		public ParserTask(RouteActivity callback) {
			mCallBack = callback;
		}
		
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;
			try {
				jObject = new JSONObject(jsonData[0]);
				PathJSONParser parser = new PathJSONParser(getBaseContext());
				routes = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return routes;
		}

		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
			ArrayList<LatLng> points = null;

				points = new ArrayList<LatLng>();
				PolylineOptions polyLineOptions = new PolylineOptions();
				List<HashMap<String, String>> path = routes.get(PathJSONParser.rotaEscolhida);

				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);
					points.add(position);
				}
					polyLineOptions.addAll(points);
					polyLineOptions.width(10);
					polyLineOptions.color(Color.BLUE);
					polyLine = googleMap.addPolyline(polyLineOptions);
					RouteActivity.this.progressDialog.dismiss();
					mCallBack.finish();
		}
	}


}
