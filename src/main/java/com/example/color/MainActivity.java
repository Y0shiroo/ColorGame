package com.example.color;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private List<ImageView> colorImageViews = new ArrayList<>();
    private ImageView diceImageView1;
    private int selectedBoxIndex = -1; // User's selected gray box index
    private int lastWinningColorIndex = -1; // Index of the last winning color

    private int[] grayDrawables = {
            R.drawable.gray1,
            R.drawable.gray2,
            R.drawable.gray3,
            R.drawable.gray4,
            R.drawable.gray5,
            R.drawable.gray6
    };

    private int[] colorDrawables = {
            R.drawable.red,
            R.drawable.orange,
            R.drawable.yellow,
            R.drawable.green,
            R.drawable.blue,
            R.drawable.pink
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeImageViews();

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            if (selectedBoxIndex != -1) {
                startColorReveal();
            } else {
                Toast.makeText(MainActivity.this, "Please select a box first", Toast.LENGTH_SHORT).show();
            }
        });

        diceImageView1 = findViewById(R.id.diceImageView1);
    }

    private void initializeImageViews() {
        for (int i = 1; i <= 6; i++) {
            int resID = getResources().getIdentifier("color" + i, "id", getPackageName());
            ImageView imageView = findViewById(resID);
            colorImageViews.add(imageView); // Add the ImageView to the list
            final int index = i - 1;
            imageView.setOnClickListener(v -> selectBox(index));
        }
    }

    private void selectBox(int index) {
        // Reset previous selection if any
        if (selectedBoxIndex != -1) {
            colorImageViews.get(selectedBoxIndex).setImageResource(grayDrawables[selectedBoxIndex]);
        }
        selectedBoxIndex = index;
        showToast("Selected box: " + (index + 1));
        animateBox(colorImageViews.get(index));
    }

    private void startColorReveal() {
        // Generate a random number between 1 and 6 (inclusive) to simulate rolling a dice
        Random random = new Random();
        int diceResult = random.nextInt(6) + 1;

        // Display the rolled dice result with rolling animation
        diceImageView1.setVisibility(View.VISIBLE);
        diceImageView1.setImageResource(getDiceImageResource(diceResult));
        animateDice();

        // Shuffle the color drawables array
        List<Integer> shuffledColorDrawables = new ArrayList<>();
        for (int color : colorDrawables) {
            shuffledColorDrawables.add(color);
        }
        Collections.shuffle(shuffledColorDrawables);

        // Choose the winning color index, giving less weight to the last winning color
        int winningColorIndex = chooseWinningColorIndex(random);

        // Set the colors for the image views using the shuffled array
        for (int i = 0; i < colorImageViews.size(); i++) {
            int colorIndex = (winningColorIndex + i) % colorDrawables.length;
            colorImageViews.get(i).setImageResource(shuffledColorDrawables.get(colorIndex));
        }

        // Update the last winning color index
        lastWinningColorIndex = winningColorIndex;

        // Add a delay before resetting to simulate the game's outcome phase
        colorImageViews.get(0).postDelayed(this::resetGame, 2000); // 2 seconds delay
    }

    private int chooseWinningColorIndex(Random random) {
        // Set probabilities for each color
        double[] probabilities = {0.15, 0.15, 0.15, 0.15, 0.15, 0.25};

        // If a color won last time, reduce its probability
        if (lastWinningColorIndex != -1) {
            probabilities[lastWinningColorIndex] *= 0.5;
            normalizeProbabilities(probabilities);
        }

        // Choose a color index based on the probabilities
        double randomValue = random.nextDouble();
        double cumulativeProbability = 0.0;

        for (int i = 0; i < probabilities.length; i++) {
            cumulativeProbability += probabilities[i];
            if (randomValue <= cumulativeProbability) {
                return i;
            }
        }

        // Default to a random color if something goes wrong
        return random.nextInt(colorDrawables.length);
    }

    private void normalizeProbabilities(double[] probabilities) {
        double sum = 0.0;

        for (double probability : probabilities) {
            sum += probability;
        }

        for (int i = 0; i < probabilities.length; i++) {
            probabilities[i] /= sum;
        }
    }

    private void resetGame() {
        for (ImageView imageView : colorImageViews) {
            imageView.setImageResource(grayDrawables[colorImageViews.indexOf(imageView)]);
        }
        selectedBoxIndex = -1; // Reset selected box index
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void animateBox(ImageView box) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 1.2f, // Start and end X scale
                1.0f, 1.2f, // Start and end Y scale
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, // Pivot X type and value
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f); // Pivot Y type and value
        scaleAnimation.setDuration(500); // Animation duration in milliseconds
        scaleAnimation.setRepeatCount(1); // Repeat animation once
        scaleAnimation.setRepeatMode(ScaleAnimation.REVERSE); // Reverse animation at the end
        box.startAnimation(scaleAnimation);
    }

    private void animateDice() {
        diceImageView1.setImageResource(R.drawable.rolling_dice_animation);
        AnimationDrawable rollingDiceAnimation = (AnimationDrawable) diceImageView1.getDrawable();
        rollingDiceAnimation.start();

        // Stop the animation after 3 seconds
        new Handler().postDelayed(() -> {
            rollingDiceAnimation.stop();
        }, 500); // 3 seconds delay
    }

    private int getDiceImageResource(int diceResult) {
        switch (diceResult) {
            case 1:
                return R.drawable.dice1;
            case 2:
                return R.drawable.dice2;
            case 3:
                return R.drawable.dice3;
            case 4:
                return R.drawable.dice4;
            case 5:
                return R.drawable.dice5;
            case 6:
                return R.drawable.dice6;
            default:
                return R.drawable.dice1; // Default to dice1 if an invalid result
        }
    }
}