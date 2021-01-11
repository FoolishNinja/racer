package ch.scs.cs.racer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.TextureView;
import android.widget.ImageView;

import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import ch.scs.cs.racer.R;

public class GameActivity extends AppCompatActivity {
    private final float mappedTiltLeftMax = 0f;
    private final float mappedTiltRightMax = 1f;

    private SensorManager sensorManager;
    private Sensor tiltSensor;

    private ImageView gameView;
    private Canvas canvas;
    private Bitmap bitmap;
    private Timer loopTimer = new Timer(false);
    private TimerTask loopTimerTask = new TimerTask() {
        @Override
        public void run() {
            loop();
        }
    };

    private float tiltZero;
    private float tiltLeftMax;
    private float tiltRightMax;
    private float yTilt = -10f;
    private float mappedYTilt = 0.5f;
    private int screenWidth = 0;
    private int screenHeight = 0;

    private int playerColor;

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
        loadColors();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        initializeGame();
    }

    private void loadColors() {
        playerColor = ResourcesCompat.getColor(getResources(), R.color.player, null);
    }

    private void initializeGame() {
        if (isInitialized) return;
        gameView = findViewById(R.id.gameImageView);
        loopTimer.scheduleAtFixedRate(loopTimerTask, 20, 20);

        screenWidth = gameView.getWidth();
        screenHeight = gameView.getHeight();

        bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        gameView.setImageBitmap(bitmap);

        canvas = new Canvas(bitmap);
        clearCanvas();
        isInitialized = true;
    }

    private void loop() {
    }

    private void clearCanvas() {
        canvas.drawColor(ResourcesCompat.getColor(getResources(), R.color.teal_700, null));
        gameView.invalidate();
    }

    private void drawPlayer() {
        if (canvas == null) return;
        Rect playerRect = new Rect();
        int pos = (int) (screenWidth * (1 - mappedYTilt));
        playerRect.left = pos - 50;
        playerRect.right = pos;
        playerRect.top = screenHeight - 50;
        playerRect.bottom = screenHeight;
        Paint playerPaint = new Paint(playerColor);
        clearCanvas();
        canvas.drawRect(playerRect, playerPaint);
        gameView.invalidate();
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
        if(isHardTilt(event.values[1])) return;
        yTilt = round(event.values[1], 2);
        mapYTilt();
        handleOvertilt();
        drawPlayer();
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
    }

    private float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}