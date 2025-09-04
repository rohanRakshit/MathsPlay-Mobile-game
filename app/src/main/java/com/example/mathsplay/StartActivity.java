package com.example.mathsplay;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {

    private EditText nameInput, numQuestionsInput;
    private Button proceedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_page);

        nameInput = findViewById(R.id.nameInput);
        numQuestionsInput = findViewById(R.id.questionInput);
        proceedButton = findViewById(R.id.button);

        proceedButton.setOnClickListener(v -> {
            String playerName = nameInput.getText().toString().trim();
            String numQStr = numQuestionsInput.getText().toString().trim();

            if (playerName.isEmpty() || numQStr.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int numQuestions = Integer.parseInt(numQStr);

            // ✅ only 10–30, must be multiple of 5
            if (numQuestions < 10 || numQuestions > 30) {
                Toast.makeText(this, "Questions must be between 10 to 30", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.putExtra("playerName", playerName);
            intent.putExtra("totalQuestions", numQuestions); // ✅ renamed key
            startActivity(intent);
            finish();
        });
    }
}
