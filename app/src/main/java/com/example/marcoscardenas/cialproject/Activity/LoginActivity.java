package com.example.marcoscardenas.cialproject.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcoscardenas.cialproject.R;
import com.example.marcoscardenas.cialproject.DataBase.Conexion;

import org.json.JSONException;


public class LoginActivity extends Activity {

    SessionManager manager;
    Button btnLogin;
    EditText inputUsuario;
    EditText inputPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        manager = new SessionManager();

        inputUsuario = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String usuario = inputUsuario.getText().toString();
                String password = inputPassword.getText().toString();
                Conexion c = new Conexion(LoginActivity.this);
                Boolean login = c.Login(usuario,password);

                    if (login == true) {
                        manager.setPreferences(LoginActivity.this, "status", "1");
                        String status=manager.getPreferences(LoginActivity.this,"status");
                        Log.d("status", status);
                        Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);

                        dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(dashboard);
                        Toast toast1 =
                                Toast.makeText(getApplicationContext(),
                                        "Bienvenido" +" "+usuario, Toast.LENGTH_SHORT);

                        toast1.show();
                        // Cerrar login
                        finish();
                    } else {
                        // Error en login

                            Toast toast1 =
                                    Toast.makeText(getApplicationContext(),
                                            "Incorrecto Usuario o password", Toast.LENGTH_SHORT);

                            toast1.show();

                    }
                }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }
}
