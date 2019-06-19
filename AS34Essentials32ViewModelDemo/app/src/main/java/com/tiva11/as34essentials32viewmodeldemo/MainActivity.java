package com.tiva11.as34essentials32viewmodeldemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tiva11.as34essentials32viewmodeldemo.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }
}
