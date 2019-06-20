package com.tiva11.as34essentials32viewmodeldemo;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tiva11.as34essentials32viewmodeldemo.databinding.MainActivityBinding;
import com.tiva11.as34essentials32viewmodeldemo.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // When data binding is enabled and the layout XML is sandwitched with the layout tag,
        // The generated binding classes could be used instead of setContentView
//        setContentView(R.layout.main_activity);
        //It's interesting that the MainActivityBinding class has no setContentView method.
        MainActivityBinding binding =
                DataBindingUtil.setContentView(this, R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }
}
