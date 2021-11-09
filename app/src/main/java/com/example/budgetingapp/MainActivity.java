package com.example.budgetingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private CardView budgetCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        budgetCardView = findViewById(R.id.budgetCardview);

        budgetCardView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,BudgetActivity.class);
            startActivity(intent);
         });

    }
}