package com.parse.buzzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class PathJSONParser {
    private int contaOcorrencias = 999999999;
    private int [] contaAux = new int[3];
    public static int rotaEscolhida = 0;
    private int duracaoPercurso = 999999999;
    private int [] duracaoAux = new int[3];
    private boolean flag = false;
    private int indice;
    private AtomicInteger lock = new AtomicInteger(0);

    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        JSONObject jDuration = null;

        try {
            jRoutes = jObject.getJSONArray("routes");
            /** Percorrendo todas as rotas */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
                contaAux[i] = 0;
                duracaoAux[i] = 0;

                /** Percorrendo todos os "legs" */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                    duracaoAux[i] += jDuration.getInt("value");
                    System.out.println(duracaoAux);

                    /** Percorrendo todos os passos (um passo eh uma instrucao do google, ex: "siga 200m na rua X e entao vire na rua Y" */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps
                                .get(k)).get("polyline")).get("points");

                        /** Recebendo todas as coordenadas contidas em um passo **/
                        List<LatLng> list = decodePoly(polyline);

                        /** Pegando a coordenada do ponto inicial e final do passo para sabermos o endereco.
                         * Assim podemos comparar com o endereco da ocorrencia. Isto evita pegarmos ocorrencias de ruas vizinhas.
                         * Pegamos o ponto "1" ao inves do "0" pois em cruzamentos o google retornava o nome da rua errada. **/
                        ParseGeoPoint inicio = new ParseGeoPoint(list.get(1).latitude, list.get(1).longitude);
                        ParseGeoPoint fim = new ParseGeoPoint(list.get(list.size()-1).latitude, list.get(list.size()-1).longitude);

                        /** Descobrindo o endereco deste passo **/
                        List<Address> addresses = null;
                        String temp = "";
                        Geocoder geocoder = new Geocoder(ct, Locale.getDefault());
                        addresses = geocoder.getFromLocation(inicio.getLatitude(), inicio.getLongitude(), 1);
                        Address address = addresses.get(0);
                        temp = address.getAddressLine(0);

                        /** Pegando apenas o nome da rua, removendo o numero e bairro **/
                        String[] parts = temp.split(",");
                        String logradouro = parts[0];

                        /** Aqui filtramos o que queremos que retorne a consulta ao BD **/
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Occurrences");
                        query.whereEqualTo("address",logradouro);

                        /** Essa query funciona assim: query.whereWithinGeoBox("location", (menorLat,menorLong), (maiorLat,maiorLong);
                         * Caso essas restricoes de maior e menor nao sejam atendidas, a consulta nao eh realizada. **/
                        if (inicio.getLatitude() < fim.getLatitude()) {
                            if (inicio.getLongitude() < fim.getLongitude()) {
                                query.whereWithinGeoBox("location", inicio, fim);
                            } else {
                                ParseGeoPoint aux1 = new ParseGeoPoint(inicio.getLatitude(), fim.getLongitude());
                                ParseGeoPoint aux2 = new ParseGeoPoint(fim.getLatitude(), inicio.getLongitude());
                                query.whereWithinGeoBox("location", aux1, aux2);
                            }
                        } else {
                            if (inicio.getLongitude() < fim.getLongitude()) {
                                ParseGeoPoint aux1 = new ParseGeoPoint(fim.getLatitude(), inicio.getLongitude());
                                ParseGeoPoint aux2 = new ParseGeoPoint(inicio.getLatitude(), fim.getLongitude());
                                query.whereWithinGeoBox("location", aux1, aux2);
                            } else {
                                query.whereWithinGeoBox("location", fim, inicio);
                            }

                        }

                        indice = i;
                        
                        /** Apos resolvermos as limitacoes de latitude e longitude do BD, finalmente realizamos a consulta **/
                        query.countInBackground(new CountCallback() {
                            int ind = indice;

                            public void done(int count, ParseException e) {
                                if (e == null) {
                                    contaAux[ind] += count;
                                    System.out.println(contaAux[ind]);
                                    //flag = true;
                                    //contaThreads.getAndIncrement();

                                } else {
                                    System.out.println("ERRO" + " nas ocorrencias: " + e.getMessage());
                                    //flag = true;
                                    //contaThreads.getAndIncrement();
                                }
                            }
                        });

                        /** Convertendo de LatLng pra String e colocando num HashMap, nao sabemos o motivo disso mas achamos melhor nao mexer */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat",
                                    Double.toString(((LatLng) list.get(l)).latitude));
                            hm.put("lng",
                                    Double.toString(((LatLng) list.get(l)).longitude));
                            path.add(hm);
                        }

                        //                        while (flag == false) {
                        //                            System.out.println("Se remover o app para");
                        //                        }
                        //                        flag = false;

                    }
                    routes.add(path);
                }

                /** Escolhendo qual a melhor rota:
                 * Se rota atual tiver menos ocorrencias que rota anterior, escolhe a atual.
                 * Mas se rota atual e a anterior tiverem a mesma quantidade, escolhe a com menor duracao **/
                //                if (contaAux <= contaOcorrencias) {
                //                    if (contaAux < contaOcorrencias) {
                //                        rotaEscolhida = i;
                //                        contaOcorrencias = contaAux;
                //                        duracaoPercurso = duracaoAux;
                //                        System.out.println("Eliminando rota " + (i-1));
                //                    } else {
                //                        if (duracaoAux < duracaoPercurso) {
                //                            rotaEscolhida = i;
                //                            duracaoPercurso = duracaoAux;
                //                            contaOcorrencias = contaAux;
                //                            System.out.println("Escolhi a rota " + i + " ao inves da " + (i-1));
                //                        } else {
                //                            // faz nada, fica com a rota anterior
                //                        }
                //                    }
                //                } else {
                //                    // faz nada, fica com a rota anterior
                //                }
            }

            //            while(lock.get()!=(jRoutes.length()*jLegs.length()*jSteps.length())){
            //                System.out.println(lock.get());
            //                System.out.println(jRoutes.length()*jLegs.length()*jSteps.length());
            //                //Thread.sleep(1);
            //            }
            Thread.sleep(30000);
            for (int i = 0; i < jRoutes.length(); i++){
                System.out.println("teste");
                if (contaAux[i] < contaOcorrencias) {
                    rotaEscolhida = i;
                    contaOcorrencias = contaAux[i];
                    duracaoPercurso = duracaoAux[i];
                    System.out.println("Eliminando rota " + (i-1));
                } else {
                    if (duracaoAux[i] < duracaoPercurso) {
                        rotaEscolhida = i;
                        duracaoPercurso = duracaoAux[i];
                        contaOcorrencias = contaAux[i];
                        System.out.println("Escolhi a rota " + i + " ao inves da " + (i-1));
                    } 
                }
            } 
            System.out.println(rotaEscolhida + " " + contaOcorrencias);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return routes;
    }

    /**
     * Method Courtesy :
     * jeffreysambells.com/2010/05/27
     * /decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    Context ct;
    public PathJSONParser(Context context){
        this.ct = context;
    }
}