package com.parse.buzzer;
import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
* modelo de dados de uma ocorrência
*/
@ParseClassName("Occurrences")
public class BuzzerOccurrence extends ParseObject {
	public String getText() {
	    return getString("text");
	  }

	  public void setText(String value) {
	    put("text", value);
	  }

	  public ParseUser getUser() {
	    return getParseUser("user");
	  }

	  public void setUser(ParseUser value) {
	    put("user", value);
	  }

	  public ParseGeoPoint getLocation() {
	    return getParseGeoPoint("location");
	  }

	  public void setLocation(ParseGeoPoint value) {
	    put("location", value);
	  }
	  
	  public void setTipo(String value){
		  put("Tipo",value);
	  }
	  
	  public String getTipo(){
		  return getString("Tipo");
	  }
	  
	  public void setData(Date value){
		  put("Data",value);
	  }
	  
	  public void setHora(String value){
		put("Hora",value);  
	  }
	  
	  public static ParseQuery<BuzzerOccurrence> getQuery() {
	    return ParseQuery.getQuery(BuzzerOccurrence.class);
	  }

	public void setAddress(String logradouro) {
		// TODO Auto-generated method stub
		put("address",logradouro);
		
	}

}
