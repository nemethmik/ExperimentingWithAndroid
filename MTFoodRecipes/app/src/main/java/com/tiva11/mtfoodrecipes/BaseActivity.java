package com.tiva11.mtfoodrecipes;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

@Deprecated
public abstract class BaseActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    @Override
    public void setContentView(int layoutResID) {
        ConstraintLayout cl = (ConstraintLayout)getLayoutInflater().inflate(R.layout.activity_base,null);
        getLayoutInflater().inflate(layoutResID,(FrameLayout)cl.findViewById(R.id.activityContent),true);
        this.progressBar = (ProgressBar)cl.findViewById(R.id.progressBar);
        super.setContentView(cl);
        //super.setContentView(layoutResID);
    }
    public void showProgressBar(boolean show) {
        this.progressBar.setVisibility(show ? ProgressBar.VISIBLE : ProgressBar.INVISIBLE);
    }
}
