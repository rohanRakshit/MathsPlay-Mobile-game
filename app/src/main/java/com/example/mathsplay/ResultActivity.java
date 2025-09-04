package com.example.mathsplay;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private TextView resultText;
    private Button restartButton, exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ending_page); // make sure your XML is activity_result.xml

        resultText = findViewById(R.id.resultText);
        restartButton = findViewById(R.id.restartButton);
        exitButton = findViewById(R.id.exitButton);

        // Get data from MainActivity
        Intent intent = getIntent();
        int highestStreak = intent.getIntExtra("highestStreak", 0);
        String playerName = intent.getStringExtra("playerName");

        resultText.setText(playerName + ", your highest streak is " + highestStreak);

        restartButton.setOnClickListener(v -> {
            Intent restartIntent = new Intent(ResultActivity.this, StartActivity.class);
            startActivity(restartIntent);
            finish();
        });

        exitButton.setOnClickListener(v -> {
            finishAffinity(); // closes the whole app
        });
    }
}
