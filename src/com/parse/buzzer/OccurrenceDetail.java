package com.parse.buzzer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class OccurrenceDetail extends Activity {

	Button botaoData, botaoHora;
	Calendar c;
	CheckBox localizacaoAtual;
	TextView tipo;
	List<Address> addresses = null; // guarda os enderecos possiveis pra uma
	// localizacao
	int mYear, mMonth, mDay = -1; // ajudar no ajuste da data e da hora
	private int hora = -1;
	private int min;
	double longi, lat; // ajudar na localizacao
	private final BuzzerOccurrence occurrence = new BuzzerOccurrence(); // responsavel
	private Address address = null;
	// pela
	// ocorrencia
	// a ser
	// cadastrada.

	/**
	 * This integer will uniquely define the dialog to be used for displaying
	 * time picker.
	 */
	static final int TIME_DIALOG_ID = 0;

	/** Callback received when the user "picks" a time in the dialog */
	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			hora = hourOfDay;
			min = minute;
			updateDisplay();
		}
	};

	/** Updates the time in the TextView */
	private void updateDisplay() {
		botaoHora.setText(new StringBuilder().append(pad(hora)).append(":")
				.append(pad(min)));
	}

	// funcao que transforma a hora/data em uma string para uma melhor
	// visualizacao
	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ocorrencia_detalhes); // tela para detalhar a ocorrencia
		
		tipo = (TextView) findViewById(R.id.textViewTipo);
		//tipo passado da OccurrenceTypeActivity
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String tipoOcorrencia = extras.getString("Tipo");
			tipo.setText(tipoOcorrencia);
		}
		
		botaoData = (Button) findViewById(R.id.data);
		botaoHora = (Button) findViewById(R.id.hora);

		//localização deve vir carregada
		localizacaoAtual = (CheckBox) findViewById(R.id.checkLocalizacaoAtual);
		localizacaoAtual.setChecked(true);


		if (localizacaoAtual.isChecked()){
			//já carrega localização atual
			try {
				setAtual();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}

	}

	// a funcao cadastra a ocorrencia no parse, e retorna para a tela principal
	public void returnToMain(View view) throws java.text.ParseException {
		if (occurrence.getLocation() != null) { // checa se a posicao foi colocada
			String comentario = "";
			EditText comentarioTextBox = (EditText) findViewById(R.id.comentario);
			comentario = comentario + comentarioTextBox.getText().toString();
			occurrence.setText(comentario);
			occurrence.setTipo(tipo.getText().toString().trim());
			
			//data
			botaoData = (Button) findViewById(R.id.data);
			
			DateFormat formatter = new SimpleDateFormat("dd/mm/yyyy");
			Date date = (Date)formatter.parse(botaoData.getText().toString());

			occurrence.setData(date);
			
			//hora
			botaoHora = (Button) findViewById(R.id.hora);
			occurrence.setHora(botaoHora.getText().toString());
			
			//address
			String t = address.getAddressLine(0);
			String[] parts = t.split(",");
			String logradouro = parts[0];
			occurrence.setAddress(logradouro);
			
			ParseACL acl = new ParseACL();
			// permite public read access
			acl.setPublicReadAccess(true);
			occurrence.setACL(acl);
			// Save the post
			occurrence.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {

				}
			});
			
			
			
			Intent intent = new Intent(OccurrenceDetail.this,DispatchActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK	| Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			
			Toast.makeText(OccurrenceDetail.this,"Ocorrência cadastrada com sucesso!", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(OccurrenceDetail.this,"Digite o local da ocorrência", Toast.LENGTH_LONG).show();
		}
	}

	public void clickLocalizacaoAtual(View v){
		CheckBox check = (CheckBox) findViewById(R.id.checkLocalizacaoAtual);
		//
		if(check.isChecked()){
			try {
				setAtual();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			EditText local = (EditText) findViewById(R.id.local);
			local.setText("");
		}
	}

	// seta a hora,data, e localizacao para a atual.
	public void setAtual() throws IOException {
		String temp = "";
		address = null;
		c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		hora = c.get(Calendar.HOUR_OF_DAY);
		min = c.get(Calendar.MINUTE);
		atualizaHoraData();
		EditText endereco = (EditText) findViewById(R.id.local);
		Geocoder geocoder = new Geocoder(OccurrenceDetail.this,
				Locale.getDefault());

		// seta o local para o local do usuÃ¡rio
		occurrence.setLocation(MainActivity.myPointLoc);
		occurrence.setUser(ParseUser.getCurrentUser());

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
		endereco.setText(temp);
	}

	private void atualizaHoraData(){
		if (mDay != (-1)) {
			botaoData = (Button) findViewById(R.id.data);
			botaoData.setText(pad(mDay) + "/" + pad(mMonth + 1) + "/"
					+ pad(mYear));
		}
		if (hora != (-1)) {
			botaoHora = (Button) findViewById(R.id.hora);
			botaoHora.setText(new StringBuilder().append(pad(hora))
					.append(":").append(pad(min)));
		}
	}
	
	private void atualizaTipo(){
		tipo = (TextView) findViewById(R.id.textViewTipo);
		//tipo passado da OccurrenceTypeActivity
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String tipoOcorrencia = extras.getString("Tipo");
			tipo.setText(tipoOcorrencia);
		}
	}

	// pega o endereco escolhido da lista de enderecos possiveis, e seta
	// novamente a tela de detalhes.
	public void getEnd(View view) {
		if (!addresses.isEmpty()) {
			String temp = "";
			address = null;
			if (view.getId() == R.id.a0)
				address = addresses.get(0);
			else if (view.getId() == R.id.a1)
				address = addresses.get(1);
			else if (view.getId() == R.id.a2)
				address = addresses.get(2);
			else if (view.getId() == R.id.a3)
				address = addresses.get(0);

			setContentView(R.layout.activity_ocorrencia_detalhes);
			atualizaHoraData();
			atualizaTipo();
			EditText endereco = (EditText) findViewById(R.id.local);

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
			occurrence.setLocation(new ParseGeoPoint(address.getLatitude(),
					address.getLongitude()));
			occurrence.setUser(ParseUser.getCurrentUser());
		} else {
			setContentView(R.layout.activity_ocorrencia_detalhes);
			atualizaHoraData();
		}
	}

	// procura os possiveis lugares de acordo com a localizacao dada, e lista os
	// 4 mais provaveis (se houver)
	public void procuraLugar(View view) throws IOException {

		Geocoder geocoder = new Geocoder(OccurrenceDetail.this,Locale.getDefault());
		EditText endereco = (EditText) findViewById(R.id.local);
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

	@Override
	// qd clica na hora ou data.
	protected Dialog onCreateDialog(int id) {
		if (R.id.data == id) {
			return new DatePickerDialog(this, listener, mYear, mMonth, mDay);
		} else if (id == R.id.hora)
			return new TimePickerDialog(this, mTimeSetListener, hora, min,
					false);
		else
			return null;
	}

	// para setar a data manualmente
	private OnDateSetListener listener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			botaoData.setText(pad(mDay) + "/" + pad(mMonth + 1) + "/"
					+ pad(mYear));
		}
	};

	public void setHora(View view) {
		showDialog(view.getId());
	}

	@SuppressWarnings("deprecation")
	public void selecionarData(View view) {
		showDialog(view.getId());
	}

}
