package ch.scs.cs.racer.models;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import ch.scs.cs.racer.R;

public class Game {
    private final float textSize = 50;
    private final float coinSpawnInterval = 30;
    private final float obstacleInterval = 10;
    private final int coinStartValue = 10;
    private final int minCoinsPerSpawn = 3;
    private final int maxCoinsPerSpawn = 10;
    private final int coinSpacing = 10;
    private final int coinSide = 40;

    // Game loop setup
    private Timer loopTimer = new Timer(false);
    private TimerTask loopTimerTask = new TimerTask() {
        @Override
        public void run() {
            loop();
        }
    };

    // Game coin and obstacle spawning setup
    private Timer spawnTimer = new Timer(false);
    private TimerTask spawnTimerTask = new TimerTask() {
        @Override
        public void run() {
            spawning();
        }
    };

    // Steering
    private float mappedYTilt;

    // Game canvas and view
    private Canvas canvas;
    private ImageView gameView;

    // Screen ratio
    private int screenWidth;
    private int screenHeight;

    // Game variables
    private int currentCoinCount = 0;
    private int currentCoinSpawnInterval = 0;
    private int currentObstacleSpawnInterval = 0;
    private int currentCoinValue = coinStartValue;
    private int currentCoinSpawnIntervalStep = 1;
    private int getCurrentObstacleSpawnIntervalStep = 1;

    // Game objects
    private List<Coin> coins = new ArrayList<>();
    private Rect playerRect = new Rect();

    // Paints
    private Paint playerPaint;
    private Paint coinPaint;
    private Paint coinCountTextPaint;

    // Runtime
    private Resources resources;

    public Game(Canvas canvas, ImageView gameView, Resources resources, int screenWidth, int screenHeight, float mappedYTilt) {
        this.canvas = canvas;
        this.gameView = gameView;
        this.resources = resources;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.mappedYTilt = mappedYTilt;
        loadAssets();
        initialize();
    }

    private void initialize() {
        loopTimer.scheduleAtFixedRate(loopTimerTask, 10, 10);
        spawnTimer.scheduleAtFixedRate(spawnTimerTask, 100, 100);
        clearCanvas();
    }

    private void loadAssets() {
        playerPaint = new Paint();
        playerPaint.setColor(ResourcesCompat.getColor(resources, R.color.player, null));
        playerPaint.setStrokeWidth(3);
        coinPaint = new Paint();
        coinPaint.setColor(ResourcesCompat.getColor(resources, R.color.coin, null));
        coinPaint.setStrokeWidth(3);
        coinCountTextPaint = new Paint();
        coinCountTextPaint.setColor(Color.BLACK);
        coinCountTextPaint.setStrokeWidth(2);
        coinCountTextPaint.setTextSize(textSize);
    }

    private void loop() {
        long time = System.currentTimeMillis();
        clearCanvas();
        checkPlayerCoinCollision();
        drawCoinCount();
        moveObjects();
        drawCoins();
        drawPlayer();
        gameView.invalidate();
        System.out.println(System.currentTimeMillis() - time);
    }

    private void spawning() {
        if (currentCoinSpawnInterval == coinSpawnInterval) {
            currentCoinSpawnInterval = 0;
            spawnCoins(currentCoinValue);
        }
        currentCoinSpawnInterval += currentCoinSpawnIntervalStep;
        if (currentObstacleSpawnInterval == obstacleInterval) {
            currentObstacleSpawnInterval = 0;
            spawnObstacle();
        }
        currentObstacleSpawnInterval += currentObstacleSpawnInterval;
    }

    private void clearCanvas() {
        canvas.drawColor(ResourcesCompat.getColor(resources, R.color.track, null));
    }

    private void drawCoins() {
        for (Coin coin : coins) {
            canvas.drawCircle(coin.getX(), coin.getY(), coinSide / 2, coinPaint);
        }
    }

    private void drawCoinCount() {
        canvas.drawText("Coins: " + currentCoinCount + "$", 50, 50, coinCountTextPaint);
    }

    public void drawPlayer() {
        int pos = (int) (screenWidth * (1 - mappedYTilt));
        playerRect.left = Math.max(pos - 50, 0);
        playerRect.right = pos;
        playerRect.top = screenHeight - 50;
        playerRect.bottom = screenHeight;
        canvas.drawRect(playerRect, playerPaint);
    }

    private void checkPlayerCoinCollision() {
        int coinSideHalf = coinSide / 2;
        coins = coins.stream().filter(coin -> {
            if (coin.getY() > screenHeight + 50) {
                return false;
            } else if (
                    coin.getY() > screenHeight - 50 &&
                            playerRect.intersect(coin.getX() - coinSideHalf, coin.getY() - coinSideHalf, coin.getX() + coinSideHalf, coin.getY() + coinSideHalf)
            ) {
                currentCoinCount += coin.getValue();
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    private void moveObjects() {
        for (Coin coin : coins) {
            coin.setY(coin.getY() + 5);
        }
    }

    private void spawnCoins(int value) {
        spawnCoin(value, getRandomNumber(minCoinsPerSpawn, maxCoinsPerSpawn));
    }

    private void spawnCoin(int value, int count) {
        int pos = getRandomNumber(0, screenWidth - 30);
        for (int i = 0; i < count; i++) {
            coins.add(new Coin(pos, -(i * (coinSide + coinSpacing)), value));
        }
    }

    private void spawnObstacle() {

    }


    public void setMappedYTilt(float mappedYTilt) {
        this.mappedYTilt = mappedYTilt;
        drawPlayer();
    }


    private int getRandomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
