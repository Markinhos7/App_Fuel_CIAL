package com.example.marcoscardenas.cialproject.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.marcoscardenas.cialproject.R;

public class Splash extends AppCompatActivity {
SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        manager=new SessionManager();





        Thread background = new Thread() {
            public void run() {

                try {

                    sleep(3*1000);


                    String status=manager.getPreferences(Splash.this,"status");
                    Log.d("status",status);
                    if (status.equals("1")){
                        Intent i=new Intent(Splash.this,MainActivity.class);
                        startActivity(i);
                    }else{
                        Intent i=new Intent(Splash.this,LoginActivity.class);
                        startActivity(i);
                    }



                    finish();

                } catch (Exception e) {

                }
            }
        };


        background.start();


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
