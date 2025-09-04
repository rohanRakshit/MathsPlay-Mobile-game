package com.example.mathsplay;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.view.ViewGroup;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("mathsplay");
    }
    public native String stringFromJNI();

    private static final String TAG = "MainActivity";

    private TextView questionText, timerText, feedbackText, streakText;
    private GridLayout optionsLayout;
    private Button submitButton;
    private String correctAnswer;
    private int currentStreak = 0, maxStreak = 0;
    private CountDownTimer countDownTimer;
    private String selectedOption = null;

    private int currentQuestion = 0;
    private int totalQuestions = 0;   // ✅ added
    private String playerName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (intent != null) {
            playerName = intent.getStringExtra("playerName");
            totalQuestions = intent.getIntExtra("totalQuestions", 10); // ✅ default 10
        }
        if (currentQuestion >= totalQuestions) {
            goToResult();
            return;
        }

        questionText = findViewById(R.id.questionText);
        timerText = findViewById(R.id.timerText);
        feedbackText = findViewById(R.id.feedbackText);
        optionsLayout = findViewById(R.id.optionsLayout);
        submitButton = findViewById(R.id.submitButton);
        streakText = findViewById(R.id.streakText);

        streakText.setText("Current Streak: 0 | Max Streak: 0");

        loadNextQuestion();

        submitButton.setOnClickListener(v -> checkAnswer());
    }

    private void loadNextQuestion() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        // ✅ stop if reached totalQuestions
        if (currentQuestion >= totalQuestions) {
            goToResult();
            return;
        }

        String qWithOptions = null;
        try {
            qWithOptions = stringFromJNI();
        } catch (Exception e) {
            Log.e(TAG, "stringFromJNI() threw", e);
        }

        if (qWithOptions == null || qWithOptions.trim().isEmpty()) {
            Log.e(TAG, "No more questions. Ending quiz.");
            goToResult();
            return;
        }

        currentQuestion++;
        selectedOption = null;
        feedbackText.setText("");
        submitButton.setEnabled(true);

        String[] parts = qWithOptions.split("\n", 2);
        String questionPart = parts.length > 0 ? parts[0].trim() : "";
        String optionsPart = parts.length > 1 ? parts[1].trim() : "";

        questionText.setText("Q" + currentQuestion + ": " + questionPart);

        String[] optionTokens = optionsPart.isEmpty() ? new String[0] : optionsPart.split("\t");

        correctAnswer = null;
        for (String s : optionTokens) {
            if (s.contains("✔")) {
                correctAnswer = s.replace("✔", "").trim();
                break;
            }
        }

        populateOptions(optionTokens);
        startTimer();
    }

    private void populateOptions(String[] optionTokens) {
        optionsLayout.removeAllViews();

        // Always 5 options
        String[] finalOptions = new String[5];
        for (int i = 0; i < 4; i++) {
            finalOptions[i] = (optionTokens != null && i < optionTokens.length)
                    ? optionTokens[i]
                    : "(" + (char) ('a' + i) + ") --";
        }
        finalOptions[4] = "(e) None of these";

        if (correctAnswer == null) {
            correctAnswer = finalOptions[4].trim();
        }

        for (int i = 0; i < finalOptions.length; i++) {
            final String displayText = finalOptions[i].replace("✔", "").trim();

            Button btn = new Button(this);
            btn.setText(displayText);
            btn.setTextSize(20f);
            btn.setPadding(12, 12, 12, 12);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

            if (i == 4) {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.columnSpec = GridLayout.spec(0, 2);
            } else {
                layoutParams.width = 0;
                layoutParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            }

            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.setMargins(12, 12, 12, 12);
            btn.setLayoutParams(layoutParams);

            btn.setOnClickListener(v -> {
                selectedOption = displayText;
                for (int j = 0; j < optionsLayout.getChildCount(); j++) {
                    optionsLayout.getChildAt(j).setBackground(null);
                }
                btn.setBackgroundColor(Color.LTGRAY);
            });

            optionsLayout.addView(btn);
        }
    }

    private void checkAnswer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        if (selectedOption == null) {
            feedbackText.setText("Please select an option!");
            return;
        }

        if (selectedOption.equals(correctAnswer)) {
            feedbackText.setText("Hurray! Correct ✅");
            currentStreak++;
            maxStreak = Math.max(currentStreak, maxStreak);
        } else {
            feedbackText.setText("Aww! Wrong ❌\nCorrect: " + correctAnswer);
            currentStreak = 0;
        }

        streakText.setText("Current Streak: " + currentStreak + " | Max Streak: " + maxStreak);
        submitButton.setEnabled(false);

        optionsLayout.postDelayed(this::loadNextQuestion, 2000);
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText((millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                timerText.setText("0s");
                feedbackText.setText("Time's up! Correct: " + correctAnswer);
                currentStreak = 0;
                streakText.setText("Current Streak: " + currentStreak + " | Max Streak: " + maxStreak);
                submitButton.setEnabled(false);

                optionsLayout.postDelayed(MainActivity.this::loadNextQuestion, 2000);
            }
        }.start();
    }

    private void goToResult() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        Intent resultIntent = new Intent(MainActivity.this, ResultActivity.class);
        resultIntent.putExtra("highestStreak", maxStreak);
        resultIntent.putExtra("playerName", playerName == null ? "" : playerName);
        resultIntent.putExtra("totalQuestions", totalQuestions); // ✅ send back also
        startActivity(resultIntent);
        finish();
    }
}
