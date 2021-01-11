package ch.scs.cs.racer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ImageView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

import ch.scs.cs.racer.models.Coin;
import ch.scs.cs.racer.models.Game;

public class GameActivity extends AppCompatActivity {
    // Statics
    private final float mappedTiltLeftMax = 0f;
    private final float mappedTiltRightMax = 1f;

    // Game object
    private Game game;

    // Sensors
    private SensorManager sensorManager;
    private Sensor tiltSensor;

    // Canvas
    private ImageView gameView;
    private Canvas canvas;
    private Bitmap bitmap;

    // Rotation sensor values
    private float tiltZero;
    private float tiltLeftMax;
    private float tiltRightMax;
    private float yTilt = -10f;
    private float mappedYTilt = 0.5f;

    // Screen ratio
    private int screenWidth = 0;
    private int screenHeight = 0;

    // Runtime
    private boolean isInitialized = false;

    private SensorEventListener tiltSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            tiltSensorChanged(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        initializeSensor();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        initializeGame();
    }

    private void initializeGame() {
        if (isInitialized) return;
        gameView = findViewById(R.id.gameImageView);


        screenWidth = gameView.getWidth();
        screenHeight = gameView.getHeight();
        bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);

        gameView.setImageBitmap(bitmap);
        canvas = new Canvas(bitmap);
        game = new Game(canvas, gameView, getResources(), screenWidth, screenHeight, mappedYTilt);
    }

    private void initializeSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        tiltSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        sensorManager.registerListener(tiltSensorEventListener, tiltSensor, SensorManager.SENSOR_DELAY_GAME);
    }

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
        if(game != null) game.setMappedYTilt(mappedYTilt);
    }

    private boolean isHardTilt(float nextTilt) {
        return Math.abs(nextTilt - yTilt) > 0.6;
    }

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

    private void mapYTilt() {
        mappedYTilt = round(1 - ((yTilt - tiltRightMax) / (tiltRightMax - tiltLeftMax) * (mappedTiltRightMax - mappedTiltLeftMax) + mappedTiltRightMax), 2);
        if (mappedYTilt > 1) mappedYTilt = 1;
        if (mappedYTilt < 0) mappedYTilt = 0;
    }

    private float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}