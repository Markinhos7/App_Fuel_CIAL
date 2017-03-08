package com.example.marcoscardenas.cialproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcoscardenas.cialproject.Adapter.SuggestionAdapter;
import com.example.marcoscardenas.cialproject.Adapter.SuggestionChoferAdapter;
import com.example.marcoscardenas.cialproject.DataBase.Conexion;
import com.example.marcoscardenas.cialproject.Model.ChoferGetSet;
import com.example.marcoscardenas.cialproject.Model.MesprocesoGetSet;
import com.example.marcoscardenas.cialproject.Model.ObraGetSet;
import com.example.marcoscardenas.cialproject.Model.SurtidorGetSet;
import com.example.marcoscardenas.cialproject.Model.VehiculoGetSet;
import com.example.marcoscardenas.cialproject.Provider.ContractParaVale;
import com.example.marcoscardenas.cialproject.Sync.SyncAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Marcos on 03-03-17.
 */

public class InsertValeLocalActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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
    private JsonParse jp = new JsonParse();
    private TextView textView_fecha;
    private ArrayList<ObraGetSet> lista_obra = new ArrayList<ObraGetSet>();
    private ArrayList<MesprocesoGetSet> lista_mes = new ArrayList<MesprocesoGetSet>();
    private ArrayList<SurtidorGetSet> lista_surtidor = new ArrayList<SurtidorGetSet>();
    private Conexion c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vale);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        editText_horometro = (EditText) findViewById(R.id.editText_horometro);
        editText_kilometro = (EditText) findViewById(R.id.editText_kilometro);
        editText_guia_proveedor = (EditText) findViewById(R.id.editText_guia);
        editText_num_sello = (EditText) findViewById(R.id.editText_sello);
        editText_cantidad = (EditText) findViewById(R.id.editText_cantidad);
        editText_num_vale = (EditText) findViewById(R.id.editText_cantidad);
        button_guardar = (Button) findViewById(R.id.button_guardar);
        textView_fecha = (TextView) findViewById(R.id.textView4);

        autoComplete_vehiculos = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_vehiculo);
        autoComplete_chofer = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_chofer);

        spinner_obra = (Spinner) findViewById(R.id.spinner_obra);
        spinner_mes = (Spinner) findViewById(R.id.spinner_mes_proceso);
        spinner_surtidor = (Spinner) findViewById(R.id.spinner_surtidor);


         c = new Conexion(this);
        ArrayList<ChoferGetSet> chofer  = c.getChofer(autoComplete_chofer.getText().toString());
        ArrayList<VehiculoGetSet> vehiculo  = c.getVehiculo(autoComplete_vehiculos.getText().toString());
        List<String> array_chofer = new ArrayList<String>();
        List<String> array_vehiculo = new ArrayList<String>();
        for (int i = 0; i < chofer.size(); i++) {
            array_chofer.add(chofer.get(i).getRut() + chofer.get(i).getRazon_social());
            int a = array_chofer.size();

        }
        for (int i = 0; i < vehiculo.size(); i++) {
            array_vehiculo.add(vehiculo.get(i).getPatente());
            int a = array_chofer.size();

        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, array_chofer);
        autoComplete_chofer.setAdapter(arrayAdapter);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, array_vehiculo);
        autoComplete_vehiculos.setAdapter(arrayAdapter2);

        spinner_obra.setOnItemSelectedListener(this);
        spinner_mes.setOnItemSelectedListener(this);
        spinner_surtidor.setOnItemSelectedListener(this);
        new InsertValeLocalActivity.GetSpinner().execute();

        button_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertValeLocalActivity.this, MainActivity.class);
                Insert_vale();
                startActivity(intent);
            }
        });
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



        ContentValues values_encabezado = new ContentValues();
        ContentValues values_detalle = new ContentValues();

        values_encabezado.put(ContractParaVale.Columnas.OBRA, obra);
        values_encabezado.put(ContractParaVale.Columnas.SURTIDOR, surtidor);
        values_encabezado.put(ContractParaVale.Columnas.FECHA, "");
        values_encabezado.put(ContractParaVale.Columnas.VEHICULO, vehiculo);
        values_encabezado.put(ContractParaVale.Columnas.MES_PROCESO, mes_proceso);
        values_encabezado.put(ContractParaVale.Columnas.USUARIO, "marcos.cardenas");
        values_encabezado.put(ContractParaVale.Columnas.VALE, vale);
        values_encabezado.put(ContractParaVale.Columnas.GUIA, 10);
        values_encabezado.put(ContractParaVale.Columnas.RECIBE, chofer);
        values_encabezado.put(ContractParaVale.Columnas.SELLO, 111);
        values_encabezado.put(ContractParaVale.Columnas.HOROMETRO, horometro);
        values_encabezado.put(ContractParaVale.Columnas.KILOMETRO, kilometro);
        values_encabezado.put(ContractParaVale.Columnas.OBSERVACIONES, "as");

        values_detalle.put(ContractParaVale.Columnas.CANTIDAD, cantidad);
        values_detalle.put(ContractParaVale.Columnas.PRODUCTO, 31);
        values_detalle.put(ContractParaVale.Columnas.VALE_ENC,100000000);

        c.Insertar(values_encabezado,values_detalle);

        }

    private void populateSpinner() {
        Conexion c = new Conexion(this);

        ArrayList<MesprocesoGetSet> mes = new ArrayList<>();
        ArrayList<ObraGetSet> obra = new ArrayList<>();
        ArrayList<SurtidorGetSet> surtidor = new ArrayList<>();
        mes = c.getMes_proceso();
        obra= c.getObra();
        surtidor = c.getSurtidor();
        MesprocesoGetSet mes1 = new MesprocesoGetSet();

        List<String> lables_obra = new ArrayList<String>();
        List<String> lables_mes = new ArrayList<String>();
        List<String> lables_surtidor = new ArrayList<String>();

        for (int i = 0; i < obra.size(); i++) {
            lables_obra.add(obra.get(i).getNombre());
        }
        for (int i = 0; i < mes.size(); i++) {
            lables_mes.add(mes.get(i).getNombre());
        }
        for (int i = 0; i < surtidor.size(); i++) {
            lables_surtidor.add(surtidor.get(i).getDescripcion());
        }

        ArrayAdapter<String> spinnerAdapter_obra = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables_obra);
        ArrayAdapter<String> spinnerAdapter_mes = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables_mes);
        ArrayAdapter<String> spinnerAdapter_surtidor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lables_surtidor);

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

        if (position > 0) {
            // get spinner value
        } else {
            Toast.makeText(
                    getApplicationContext(),
                    parent.getItemAtPosition(position).toString() + " Seleccionado",
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






            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            populateSpinner();
        }


    }
}