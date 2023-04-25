package pass.task.four.one;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView timeRemaining, goStopView;
    private EditText etWorkoutDuration, etRestDuration;
    private Button btnStart, btnStop;
    private ProgressBar progressBar;

    private CountDownTimer workoutTimer, restTimer;
    private boolean isWorkoutPhase = true;
    private int workoutDuration, restDuration;
    private ConstraintLayout myLayout;
    private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeRemaining = findViewById(R.id.tv_time_remaining);
        etWorkoutDuration = findViewById(R.id.et_workout_duration);
        etRestDuration = findViewById(R.id.et_rest_duration);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        progressBar = findViewById(R.id.progress_bar);
        myLayout = findViewById(R.id.constraintLayout);
        goStopView = findViewById(R.id.goStopTextView);
        goStopView.setEnabled(false);
        btnStop.setVisibility(btnStop.INVISIBLE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Start timer function when button pressed
        btnStart.setOnClickListener(view -> startTimer());

        // Stop timer function when button pressed
        btnStop.setOnClickListener(view -> stopTimer());
    }

    private void startTimer() {
        //Get and Set
        workoutDuration = Integer.parseInt(etWorkoutDuration.getText().toString()) * 1000;
        restDuration = Integer.parseInt(etRestDuration.getText().toString()) * 1000;
        btnStart.setVisibility(btnStart.INVISIBLE);
        startRestTimer();

    }

    private void stopTimer() {
        // If stop timer button pressed, stop either workout or rest countdowns
        if (workoutTimer != null) {
            workoutTimer.cancel();
        }
        if (restTimer != null) {
            restTimer.cancel();
        }
        resetUI();
    }

    private void startWorkoutTimer( ) {
        // Set state to workout and set max progress bar to user set workout seconds
        isWorkoutPhase = true;
        initializeUI();
        vibrate();
        workoutTimer = new CountDownTimer(workoutDuration, 1000) {
            //Update textView every second
            @Override
            public void onTick(long millisUntilFinished) {
                updateUI(millisUntilFinished);
            }

            // When countdown ends, start the rest timer
            @Override
            public void onFinish() {
                // Play sound or vibrate device here
                startRestTimer();
            }
        };
        workoutTimer.start();
    }

    private void startRestTimer( ) {
        // Set state to non-workout and set progress max to Rest seconds
        isWorkoutPhase = false;
        goStopView.setText("REST!");
        myLayout.setBackgroundColor(Color.GRAY);
        progressBar.setMax(restDuration);
        vibrate();
        restTimer = new CountDownTimer(restDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateUI(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                // Play sound or vibrate device here
                startWorkoutTimer();
            }
        };
        restTimer.start();
    }

    private void updateUI(long millisUntilFinished) {
        // Set seconds
        int seconds = (int) (millisUntilFinished / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        String time = String.format("%02d:%02d", minutes, seconds);
        timeRemaining.setText(time);

        if (isWorkoutPhase) {
            progressBar.setProgress(workoutDuration - (int) millisUntilFinished);
        } else {
            progressBar.setProgress(restDuration - (int) millisUntilFinished);
        }
    }

    private void resetUI(){
        goStopView.setEnabled(false);
        myLayout.setBackgroundColor(Color.WHITE);
        etWorkoutDuration.setEnabled(true);
        etWorkoutDuration.setVisibility(etWorkoutDuration.VISIBLE);
        etRestDuration.setEnabled(true);
        etRestDuration.setVisibility(etRestDuration.VISIBLE);
        btnStart.setVisibility(btnStart.VISIBLE);
        progressBar.setProgress(0);
        etRestDuration.setText("");
        etWorkoutDuration.setText("");
        btnStop.setVisibility(btnStop.INVISIBLE);
    }
    private void initializeUI(){
        etWorkoutDuration.setEnabled(false);
        etWorkoutDuration.setVisibility(etWorkoutDuration.INVISIBLE);
        etRestDuration.setEnabled(false);
        etRestDuration.setVisibility(etRestDuration.INVISIBLE);
        btnStop.setVisibility(btnStop.VISIBLE);
        btnStart.setVisibility(btnStart.INVISIBLE);
        goStopView.setEnabled(true);
        goStopView.setText("GO!");
        myLayout.setBackgroundColor(Color.GREEN);
        progressBar.setMax(workoutDuration);
    }

    private void vibrate() {
        if (vibrator != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // Deprecated in API 26
                vibrator.vibrate(100);
            }
        }
    }
}