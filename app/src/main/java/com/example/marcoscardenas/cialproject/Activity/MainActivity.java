package com.example.marcoscardenas.cialproject.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.marcoscardenas.cialproject.DataBase.Conexion;
import com.example.marcoscardenas.cialproject.Model.MesprocesoGetSet;
import com.example.marcoscardenas.cialproject.R;
import com.example.marcoscardenas.cialproject.Sync.SyncAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button button_ingreso;
    SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        Conexion c = new Conexion(this);

        ArrayList<MesprocesoGetSet> mes = new ArrayList<MesprocesoGetSet>();
        mes = c.getMes_proceso();
        mes.size();
        SyncAdapter.inicializarSyncAdapter(this);

        manager = new SessionManager();

        Button button = (Button) findViewById(R.id.btn_logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.setPreferences(MainActivity.this, "status", "0");
                //finish();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);

                startActivity(intent);
            }
        });
        button_ingreso = (Button) findViewById(R.id.fab);
        button_ingreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if (isOnlineNet() == false){
                Intent intent = new Intent(MainActivity.this, InsertValeLocalActivity.class);

                startActivity(intent);

                Log.d("SIN NET:", "ERROR NO HAY INTERNET");

            }else {
                Intent intent = new Intent(MainActivity.this, InsertValeActivity.class);

                startActivity(intent);
            }
            }
        });
    }
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sync) {
            SyncAdapter.sincronizarAhora(this, false);
            Toast toast1 =
                    Toast.makeText(getApplicationContext(),
                            "Sincronizando...", Toast.LENGTH_SHORT);

            toast1.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }
}

