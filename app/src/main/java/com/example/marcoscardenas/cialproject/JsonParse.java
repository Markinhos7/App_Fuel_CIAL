package com.example.marcoscardenas.cialproject;

import com.example.marcoscardenas.cialproject.Model.ChoferGetSet;
import com.example.marcoscardenas.cialproject.Model.VehiculoGetSet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonParse {
	double current_latitude, current_longitude;

	public JsonParse() {
	}

	public JsonParse(double current_latitude, double current_longitude) {
		this.current_latitude = current_latitude;
		this.current_longitude = current_longitude;
	}

	// IP de mi Url
	String IP = "http://192.168.0.213:80/serviciosweb";
	// Rutas de los Web Services
	String GET_BY_PATENTE = IP + "/search_vehiculo.php?";
	String GET_BY_CHOFER = IP + "/search_usuario.php?";
	String GET_BY_OBRA = IP + "/search_obra.php";
	String GET_BY_MES = IP + "/search_mes.php";
	String GET_BY_SURTIDOR = IP + "/search_surtidor.php";
	String POST_VALE = IP + "/insert_Vale.php";
	List<String> respList = new ArrayList<String>();

	public List<VehiculoGetSet> getParseJsonWCF(String sName) {
		List<VehiculoGetSet> ListData = new ArrayList<VehiculoGetSet>();
		try {
			String temp = sName;
			URL js = new URL(GET_BY_PATENTE + "parametro=" + temp);
			URLConnection jc = js.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
			String line = reader.readLine();
			JSONObject jsonResponse = new JSONObject(line);
			JSONArray jsonArray = jsonResponse.getJSONArray("vehiculos");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject r = jsonArray.getJSONObject(i);
				ListData.add(new VehiculoGetSet(r.getString("patente"), r.getInt("codigo"),r.getString("nombre_vehiculo")));
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return ListData;

	}

	public List<ChoferGetSet> getParseJsonChofer(String sName) {
		List<ChoferGetSet> ListDataChofer = new ArrayList<ChoferGetSet>();
		try {
			String temp = sName;
			URL js = new URL(GET_BY_CHOFER + "parametro=" + temp);
			URLConnection jc = js.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
			String line = reader.readLine();
			JSONObject jsonResponse = new JSONObject(line);
			JSONArray jsonArray = jsonResponse.getJSONArray("chofer");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject r = jsonArray.getJSONObject(i);
				ListDataChofer.add(new ChoferGetSet(r.getString("rut"), r.getString("razon_social")));
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return ListDataChofer;

	}

/*	public JSONObject registerVale()

	{
		List<GetVale> vale = new ArrayList<GetVale>();
		try {
			String temp = "";
			URL js = new URL(POST_VALE + "parametro=" + temp);
			URLConnection jc = js.openConnection();
			JSONObject jsonResponse = new JSONObject();

			jsonResponse.put("mes_proceso", mes_proceso);
			jsonResponse.put("surtidor", surtidor);
			jsonResponse.put("patente", vehiculo);
			jsonResponse.put("obra", obra);
			jsonResponse.put("recibe", chofer);
			jsonResponse.put("usuario", idMeta);
			jsonResponse.put("fecha", idMeta);
			jsonResponse.put("vale", vale);
			jsonResponse.put("guia_proveedor", guia_despacho);
			jsonResponse.put("horometro", horometro);
			jsonResponse.put("kilometro", kilometro);
			jsonResponse.put("numsello", num_sello);
			jsonResponse.put("cantidad", cantidad);
			jsonResponse.put("horometro", );
			jsonResponse.put("horometro", idMeta);
			OutputStream os = jc.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os ,"UFT-8"));
			writer.write(jsonResponse.toString());
			writer.flush();
			writer.close();
			String line = reader.readLine();
			JSONArray jsonArray = jsonResponse.getJSONArray("obra");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject r = jsonArray.getJSONObject(i);
				vale.add(new ObraGetSet(r.getString("nombre")));
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return ListDataChofer1;
	}*/
	public Boolean logiUser(String user, String password){
		try {
			String temp = "";
			URL js = new URL(GET_BY_CHOFER + "parametro=" + temp);
			URLConnection jc = js.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(jc.getInputStream()));
			String line = reader.readLine();
			JSONObject jsonResponse = new JSONObject(line);
			JSONArray jsonArray = jsonResponse.getJSONArray("Chofer");
			for (int i = 0; i < jsonArray.length(); i++) {

			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


	return  true;
	}
}