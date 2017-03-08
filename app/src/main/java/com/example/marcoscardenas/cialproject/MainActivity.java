package com.example.marcoscardenas.cialproject;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.marcoscardenas.cialproject.DataBase.Conexion;
import com.example.marcoscardenas.cialproject.Model.MesprocesoGetSet;
import com.example.marcoscardenas.cialproject.Sync.SyncAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private TextView emptyView;
    private Button button_ingreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        Conexion c = new Conexion(this);
        MesprocesoGetSet m = new MesprocesoGetSet();


        ArrayList<MesprocesoGetSet> mes = new ArrayList<MesprocesoGetSet>();
        mes = c.getMes_proceso();
        MesprocesoGetSet mes1 = new MesprocesoGetSet();
        mes.size();
        SyncAdapter.inicializarSyncAdapter(this);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

