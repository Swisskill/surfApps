package com.example.expenses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager;

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container, new expend()).commit();

    }
}
