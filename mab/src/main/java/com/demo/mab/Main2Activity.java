package com.demo.mab;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String key = intent.getStringExtra("key");
        if ("1".equals(key)) {
            Toast.makeText(getApplicationContext(), "key=1", Toast.LENGTH_SHORT).show();

        }
    }

}
