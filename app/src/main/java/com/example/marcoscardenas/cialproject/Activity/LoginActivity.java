package com.example.marcoscardenas.cialproject.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcoscardenas.cialproject.R;
import com.example.marcoscardenas.cialproject.DataBase.Conexion;

import org.json.JSONException;


public class LoginActivity extends Activity {

    Button btnLogin;
    Button btnLinkToRegister;
    EditText inputUsuario;
    EditText inputPassword;
    TextView loginErrorMsg;

    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Importing all assets like buttons, text fields
        inputUsuario = (EditText) findViewById(R.id.input_email);
        inputPassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);


        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String usuario = inputUsuario.getText().toString();
                String password = inputPassword.getText().toString();
                Conexion c = new Conexion(LoginActivity.this);
                Boolean login = c.Login(usuario,password);

                    if (login == true) {

                        Intent dashboard = new Intent(getApplicationContext(), MainActivity.class);

                        // Close all views before launching Dashboard
                        dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(dashboard);
                        Toast toast1 =
                                Toast.makeText(getApplicationContext(),
                                        "Bienvenido" +" "+usuario, Toast.LENGTH_SHORT);

                        toast1.show();
                        // Close Login Screen
                        finish();
                    } else {
                        // Error in login
                        Toast toast1 =
                                Toast.makeText(getApplicationContext(),
                                        "Incorrecto Usuario o password", Toast.LENGTH_SHORT);

                        toast1.show();

                    }
                }
        });
    }
}
