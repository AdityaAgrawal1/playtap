package com.example.playtap;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView scoreTextView, highScoreTextView;
    private int score = 0,highScore = 0;;
    private final Handler handler = new Handler();
    private Runnable runnable;
    private AppCompatButton[] buttons;
    private int currentGreyBox = -1;
    private boolean isGameActive = false;

    private final int[] originalColors = new int[]{R.drawable.button_red, R.drawable.button_blue, R.drawable.button_yellow, R.drawable.button_green};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scoreTextView = findViewById(R.id.scoreTextView);
        highScoreTextView = findViewById(R.id.highScoreTextView);
        AppCompatButton buttonRed = findViewById(R.id.buttonRed);
        AppCompatButton buttonBlue = findViewById(R.id.buttonBlue);
        AppCompatButton buttonYellow = findViewById(R.id.buttonYellow);
        AppCompatButton buttonGreen = findViewById(R.id.buttonGreen);
        buttons = new AppCompatButton[]{buttonRed, buttonBlue, buttonYellow, buttonGreen};

        for (Button button : buttons) {
            button.setOnClickListener(view -> checkTap(button));
        }
        startGame();
        highScoreTextView.setText(String.valueOf(highScore));
    }

    private void startGame() {
        score = 0;
        isGameActive = true;
        updateScoreDisplay();
        scheduleNextColorChange();
    }

    private void scheduleNextColorChange() {
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(this);

                if (currentGreyBox != -1) {
                    gameOver();
                    return;
                }
                int newBox = new Random().nextInt(4);
                buttons[newBox].setBackgroundResource(R.drawable.button_gray);
                currentGreyBox = newBox;
                handler.postDelayed(() -> {
                    if (currentGreyBox == newBox) {
                        gameOver();
                    }
                }, 1000);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);

    }

    private void checkTap(Button tappedButton) {
        if (!isGameActive) return;
        if (currentGreyBox != -1 && tappedButton == buttons[currentGreyBox]) {
            score++;
            updateScoreDisplay();
            updateHighScore();
            buttons[currentGreyBox].setBackgroundResource(originalColors[currentGreyBox]);
            currentGreyBox = -1;
        } else {
            gameOver();
        }
    }

    private void updateScoreDisplay() {
        scoreTextView.setText(String.valueOf(score));
    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
            highScoreTextView.setText(String.valueOf(highScore));
        }
    }

    private void gameOver() {
        isGameActive = false;
        handler.removeCallbacks(runnable);
        if (score > highScore) {
            highScore = score;
            highScoreTextView.setText(String.valueOf(highScore));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Game Over. Your score is: " + score)
                .setCancelable(false)
                .setPositiveButton("RESTART",
                        (dialog, which) -> {
                            startGame();
                            handler.postDelayed(runnable, 2000);
                        });
        AlertDialog gameOverDialog = builder.create();
        gameOverDialog.show();
    }
}