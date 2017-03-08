package com.example.marcoscardenas.cialproject;

/**
 * Created by Marcos on 21-02-17.
 */
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcoscardenas.cialproject.Adapter.SuggestionAdapter;
import com.example.marcoscardenas.cialproject.Adapter.SuggestionChoferAdapter;
import com.example.marcoscardenas.cialproject.Model.MesprocesoGetSet;
import com.example.marcoscardenas.cialproject.Model.ObraGetSet;
import com.example.marcoscardenas.cialproject.Model.SurtidorGetSet;
import com.example.marcoscardenas.cialproject.Provider.ContractParaVale;
import com.example.marcoscardenas.cialproject.Sync.SyncAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class InsertValeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener , DatePickerDialog.OnDateSetListener {


    private EditText editText;
    private AutoCompleteTextView autoComplete_vehiculos;
    private AutoCompleteTextView autoComplete_chofer;
    private Spinner spinner_obra;
    private Spinner spinner_mes;
    private Spinner spinner_surtidor;
    private EditText editText_horometro;
    private EditText editText_kilometro;
    private EditText editText_num_sello;
    private EditText editText_cantidad;
    private EditText editText_guia_proveedor;
    private EditText editText_num_vale;
    private Button button_guardar;
    private JsonParse jp =new JsonParse();
    private TextView textView_fecha;
    private ArrayList<ObraGetSet> lista_obra         = new ArrayList<ObraGetSet>();
    private ArrayList<MesprocesoGetSet> lista_mes    = new ArrayList<MesprocesoGetSet>();
    private ArrayList<SurtidorGetSet> lista_surtidor = new ArrayList<SurtidorGetSet>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vale);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        editText_horometro      = (EditText) findViewById(R.id.editText_horometro);
        editText_kilometro      = (EditText) findViewById(R.id.editText_kilometro);
        editText_guia_proveedor = (EditText) findViewById(R.id.editText_guia);
        editText_num_sello      = (EditText) findViewById(R.id.editText_sello);
        editText_cantidad       = (EditText) findViewById(R.id.editText_cantidad);
        editText_num_vale       = (EditText) findViewById(R.id.editText_cantidad);
        button_guardar          = (Button) findViewById(R.id.button_guardar);
        textView_fecha          = (TextView) findViewById(R.id.textView4);

        autoComplete_vehiculos = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_vehiculo);
        autoComplete_chofer    = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_chofer);

        spinner_obra     = (Spinner) findViewById(R.id.spinner_obra);
        spinner_mes      = (Spinner) findViewById(R.id.spinner_mes_proceso);
        spinner_surtidor = (Spinner) findViewById(R.id.spinner_surtidor);

        autoComplete_vehiculos.setAdapter(new SuggestionAdapter(this, autoComplete_vehiculos.getText().toString()));
        autoComplete_chofer.setAdapter(new SuggestionChoferAdapter(this, autoComplete_chofer.getText().toString()));

        spinner_obra.setOnItemSelectedListener(this);
        spinner_mes.setOnItemSelectedListener(this);
        spinner_surtidor.setOnItemSelectedListener(this);
        new GetSpinner().execute();



        textView_fecha.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DateDialog().show(getSupportFragmentManager(), "DatePicker");
                    }
                }
        );
        button_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertValeActivity.this, MainActivity.class);
                Insert_vale();
                startActivity(intent);
            }
        });

    }
    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(year + "-" + monthOfYear + "-" + dayOfMonth);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outDate = dateFormat.format(date);

        textView_fecha.setText(outDate);
    }

    private void Insert_vale(){

        final String vehiculo      = autoComplete_vehiculos.getText().toString();
        final String chofer        = autoComplete_chofer.getText().toString();
        final String mes_proceso   = spinner_mes.getSelectedItem().toString();
        final String obra          = spinner_obra.getSelectedItem().toString();
        final String surtidor      = spinner_surtidor.getSelectedItem().toString();
        final String horometro     = spinner_mes.getSelectedItem().toString();
        final String cantidad      = editText_cantidad.getText().toString();
        final String kilometro     = editText_kilometro.getText().toString();
        final String vale          = editText_num_vale.getText().toString();
        //final String guia_despacho = editText_guia_proveedor.getText().toString();
        final String num_sello     = editText_num_sello.getText().toString();

        String url = jp.POST_VALE;

        try {


            DataOutputStream printout;
            DataInputStream input;
            URL js = new URL(url);
            HttpURLConnection urlConn = (HttpURLConnection) js.openConnection();
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Accept", "application/json");
            urlConn.connect();

            JSONObject jsonResponse = new JSONObject();
            HashMap<String, String> map = new HashMap<>();
            map.put("mes_proceso", mes_proceso);
            map.put("surtidor", surtidor);
            map.put("patente", vehiculo);
            map.put("obra", obra);
            map.put("recibe", chofer);
            map.put("usuario", "");
            map.put("fecha", "");
            map.put("vale", "");
            map.put("guia_proveedor", "");
            map.put("horometro", horometro);
            map.put("kilometro", kilometro);
            map.put("numsello", num_sello);
            map.put("cantidad", cantidad);
            OutputStream os = urlConn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os ,"UTF-8"));
            writer.write(jsonResponse.toString());
            writer.flush();
            writer.close();




            int respuesta = urlConn.getResponseCode();

            StringBuilder result = new StringBuilder();

            if (respuesta == HttpURLConnection.HTTP_OK) {


                    ContentValues values = new ContentValues();
                    values.put(ContractParaVale.Columnas.OBRA, obra);
                    values.put(ContractParaVale.Columnas.SURTIDOR, surtidor);
                    values.put(ContractParaVale.Columnas.FECHA, "");
                    values.put(ContractParaVale.Columnas.VEHICULO, vehiculo);
                    values.put(ContractParaVale.Columnas.MES_PROCESO, mes_proceso);
                    values.put(ContractParaVale.Columnas.USUARIO, "");
                    values.put(ContractParaVale.Columnas.VALE, vale);
                    values.put(ContractParaVale.Columnas.GUIA, "");
                    values.put(ContractParaVale.Columnas.RECIBE, chofer);
                    values.put(ContractParaVale.Columnas.SELLO, "");
                    values.put(ContractParaVale.Columnas.HOROMETRO, horometro);
                    values.put(ContractParaVale.Columnas.KILOMETRO, kilometro);
                    values.put(ContractParaVale.Columnas.OBSERVACIONES, "");
                    values.put(ContractParaVale.Columnas.PENDIENTE_INSERCION, 1);
                    getContentResolver().insert(ContractParaVale.CONTENT_URI, values);
                    SyncAdapter.sincronizarAhora(this, true);
                    if (Utilidades.materialDesign())
                        finishAfterTransition();
                    else finish();
                }



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateSpinner() {

        List<String> lables_obra     = new ArrayList<String>();
        List<String> lables_mes      = new ArrayList<String>();
        List<String> lables_surtidor = new ArrayList<String>();

        for (int i = 0; i < lista_obra.size(); i++) {
            lables_obra.add(lista_obra.get(i).getNombre());
        }
        for (int i = 0; i < lista_mes.size(); i++) {
            lables_mes.add(lista_mes.get(i).getNombre());
        }
        for (int i = 0; i < lista_surtidor.size(); i++) {
            lables_surtidor.add(lista_surtidor.get(i).getDescripcion());
        }

        ArrayAdapter<String> spinnerAdapter_obra      = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables_obra);
        ArrayAdapter<String> spinnerAdapter_mes       = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables_mes);
        ArrayAdapter<String> spinnerAdapter_surtidor  = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables_surtidor);

        spinnerAdapter_obra.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter_mes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter_surtidor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner_obra.setAdapter(spinnerAdapter_obra);
        spinner_mes.setAdapter(spinnerAdapter_mes);
        spinner_surtidor.setAdapter(spinnerAdapter_surtidor);
        spinner_mes.setPrompt("Seleccionar Mes proceso");
        spinner_obra.setPrompt("Seleccionar Obra");
        spinner_surtidor.setPrompt("Seleccionar Surtidor");
        int pos = 0;
        spinner_mes.setSelection(pos);

    }
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {

        if(position > 0){
            // get spinner value
        }else{
            Toast.makeText(
                    getApplicationContext(),
                    parent.getItemAtPosition(position).toString() + " Seleccionado" ,
                    Toast.LENGTH_LONG).show();
        }


    }


    public void onNothingSelected(AdapterView<?> arg0) {
    }

    private class GetSpinner extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            ServiceHandler jsonParser = new ServiceHandler();
            String url_Obra       = jp.GET_BY_OBRA;
            String url_mesproceso = jp.GET_BY_MES;
            String url_surtidor   = jp.GET_BY_SURTIDOR;

            String json_obra        = jsonParser.makeServiceCall(url_Obra, ServiceHandler.GET);
            String json_mes_proceso = jsonParser.makeServiceCall(url_mesproceso,ServiceHandler.GET);
            String json_surtidor    = jsonParser.makeServiceCall(url_surtidor,ServiceHandler.GET);

            Log.e("Response: ", "> " + json_obra);
            Log.e("Mes de proceso", json_mes_proceso);


            if (json_obra != null && json_mes_proceso != null) {
                try {
                    JSONObject jsonObj_obra = new JSONObject(json_obra);
                    JSONObject jsonObj_mes  = new JSONObject(json_mes_proceso);
                    JSONObject jsonObj_surtidor  = new JSONObject(json_surtidor);
                    if (jsonObj_obra != null && json_mes_proceso != null && json_surtidor != null) {
                        JSONArray obra = jsonObj_obra.getJSONArray("obra");
                        JSONArray mes = jsonObj_mes.getJSONArray("mes");
                        JSONArray surtidor = jsonObj_surtidor.getJSONArray("surtidor");


                        for (int i = 0; i < obra.length(); i++) {

                            JSONObject r = obra.getJSONObject(i);
                            ObraGetSet cat = new ObraGetSet(r.getString("nombre"),r.getInt("cod_obra"));
                            lista_obra.add(cat);
                        }for (int i = 0; i < mes.length(); i++) {

                            JSONObject r = mes.getJSONObject(i);
                            MesprocesoGetSet cat = new MesprocesoGetSet(r.getString("proceso"));
                            lista_mes.add(cat);
                        }
                        for (int i = 0; i < surtidor.length(); i++) {

                            JSONObject r = surtidor.getJSONObject(i);
                            SurtidorGetSet cat = new SurtidorGetSet(r.getString("descripcion"),r.getInt("codigo"));
                            lista_surtidor.add(cat);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "¿No ha recibido ningún dato desde el servidor!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            populateSpinner();
        }

    }


}