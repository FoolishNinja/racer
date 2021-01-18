package ch.scs.cs.racer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.math.BigDecimal;

import ch.scs.cs.racer.models.Car;
import ch.scs.cs.racer.models.Game;
import ch.scs.cs.racer.models.Garage;

/**
 * Game activity, here the game is played
 *
 * @author Carlo Schmid
 * @version 18.01.2021
 */
public class GameActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    /**
     * Statics
     */
    private final float mappedTiltLeftMax = 0f;
    private final float mappedTiltRightMax = 1f;

    /**
     * Game instance
     */
    private Game game;

    /**
     * Game sensors
     */
    private SensorManager sensorManager;
    private Sensor tiltSensor;
    private SensorEventListener tiltSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            tiltSensorChanged(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    /**
     * Game surface view
     */
    private SurfaceView gameView;

    /**
     * Rotation sensor values
     */
    private float tiltZero;
    private float tiltLeftMax;
    private float tiltRightMax;
    private float yTilt = -10f;
    private float mappedYTilt = 0.5f;

    /**
     * Screen sizes
     */
    private int screenWidth = 0;
    private int screenHeight = 0;

    /**
     * Runtime
     */
    private boolean isInitialized = false;

    /**
     * Garage and selected car
     */
    private Garage garage = new Garage();
    private Car car;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeSensor();
        gameView = new SurfaceView(this);
        setContentView(gameView);
        gameView.getHolder().addCallback(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Starts the game
     * @param surfaceHolder
     */
    private void initializeGame(SurfaceHolder surfaceHolder) {
        if (isInitialized) return;
        car = garage.getCars().stream().filter(c -> c.getName().equals(getIntent().getStringExtra("carName"))).findFirst().get();
        screenWidth = gameView.getWidth();
        screenHeight = gameView.getHeight();
        game = new Game(this::gameOver, surfaceHolder, getResources(), car, screenWidth, screenHeight, mappedYTilt);
    }

    /**
     * Ends the game, returns to main activity
     */
    private void gameOver() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("score", game.getMeters());
        intent.putExtra("coins", game.getCurrentCoinCount());
        startActivity(intent);
    }

    /**
     * Initializes the rotation sensor
     */
    private void initializeSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        tiltSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        sensorManager.registerListener(tiltSensorEventListener, tiltSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    /**
     * Digests the values provided by the rotation sensor
     * @param event
     */
    private void tiltSensorChanged(SensorEvent event) {
        if (yTilt == -10f) {
            tiltZero = event.values[1];
            yTilt = tiltZero;
            tiltLeftMax = tiltZero - 0.25f;
            tiltRightMax = tiltZero + 0.25f;
        }
        if (isHardTilt(event.values[1])) return;
        yTilt = round(event.values[1], 2);
        mapYTilt();
        handleOvertilt();
        if (game != null) game.setMappedYTilt(mappedYTilt);
    }

    /**
     * Returns true, if tilting of phone is too fast
     * @param nextTilt Next tilt of phone
     * @return Was a hard tilt
     */
    private boolean isHardTilt(float nextTilt) {
        return Math.abs(nextTilt - yTilt) > 0.6;
    }

    /**
     * Expands tilt boundaries if the player overtilts
     */
    private void handleOvertilt() {
        if (yTilt > tiltRightMax) {
            tiltRightMax = yTilt;
            tiltZero = tiltRightMax - 0.25f;
            tiltLeftMax = tiltRightMax - 0.5f;
        }
        if (yTilt < tiltLeftMax) {
            tiltLeftMax = yTilt;
            tiltZero = tiltLeftMax + 0.25f;
            tiltRightMax = tiltLeftMax + 0.5f;
        }
    }

    /**
     * Maps y tilt from -0.7 to 0.7 to 0 to 1
     */
    private void mapYTilt() {
        mappedYTilt = round(1 - ((yTilt - tiltRightMax) / (tiltRightMax - tiltLeftMax) * (mappedTiltRightMax - mappedTiltLeftMax) + mappedTiltRightMax), 2);
        if (mappedYTilt > 1) mappedYTilt = 1;
        if (mappedYTilt < 0) mappedYTilt = 0;
    }

    /**
     * Rounds a float to provided decimal place
     * @param d Float to round
     * @param decimalPlace n'th decimal place
     * @return rounded number
     */
    private float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        initializeGame(holder);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}