package com.github.davidji80.aarapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.davidji80.guilib.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnLoginClick(View view){
        Intent intent=new Intent();
        intent.setClass(this,LoginActivity.class);
        startActivity(intent);
    }
}
