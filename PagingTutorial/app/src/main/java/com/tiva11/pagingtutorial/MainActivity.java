package com.tiva11.pagingtutorial;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        callStackExchange();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void callStackExchange() {
        Log.d("MIKI","Calling Stack Exchange API via Retrofit ...");
        Call<StackExchangeAnswers> call = StackExchangeCient.getStackExchangeAPI().getAnswers(1,5,StackExchangeCient.STACKOVERFLOW);
        call.enqueue(new Callback<StackExchangeAnswers>() {
            @Override
            public void onResponse(@NonNull Call<StackExchangeAnswers> call, @NonNull Response<StackExchangeAnswers> response) {
                StackExchangeAnswers answers = response.body();
                Snackbar.make(findViewById(R.id.fab),"Number of responses is " +
                        (answers != null ? answers.items.size() : -1), Snackbar.LENGTH_LONG)
                        .setAction("OK", null).show();
                Log.d("MIKI","Stack Exchange replied OK");
            }

            @Override
            public void onFailure(@NonNull Call<StackExchangeAnswers> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Error:"+ t.getMessage(),Toast.LENGTH_LONG).show();
                Log.e("MIKI","Oops. Stack Exchange error: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }
}
