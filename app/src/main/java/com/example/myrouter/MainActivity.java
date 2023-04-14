package com.example.myrouter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myrouter_api.core.MyRouter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyRouter.getInstance()
                .action("login/action")
                .context(this)
                .param("key", "val")
                .invokeAction(new ActionCallback() {

                });
    }
}