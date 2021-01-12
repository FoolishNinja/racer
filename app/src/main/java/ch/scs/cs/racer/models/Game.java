package ch.scs.cs.racer.models;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import ch.scs.cs.racer.R;

public class Game {
    /**
     * Statics
     */
    // Games text size
    private final float textSize = 50;
    // Value of a coin at the start of a game
    private final int coinStartValue = 1;
    // How many coins in one batch can spawn
    private final int minCoinsPerSpawn = 3;
    // How few coins in one batch can spawn
    private final int maxCoinsPerSpawn = 10;
    // Pixel spacing between coins in coin batch
    private final int coinSpacing = 10;
    // Side length of a coin
    private final int coinSide = 40;
    private final int minObstacleWidth = 100;
    private final int maxObstacleWidth = 200;
    private final int minObstacleHeight = 120;
    private final int maxObstacleHeight = 240;
    private final int curbLength = 70;
    private final int curbWidth = 50;

    /**
     * Loops setup
     */
    private Timer loopTimer = new Timer(false);
    // Main game loop
    private TimerTask loopTimerTask = new TimerTask() {
        @Override
        public void run() {
            loop();
        }
    };
    private TimerTask renderTimerTask = new TimerTask() {
        @Override
        public void run() {
            render();
        }
    };
    private TimerTask rampUpTimerTask = new TimerTask() {
        @Override
        public void run() {
            rampUp();
        }
    };

    /**
     * Steering
     */
    // Float between 0 and 1 respective to the current car position from left to right.
    private float mappedYTilt;

    /**
     * Game canvas and view
     */
    // Game canvas where the painting happens
    private SurfaceHolder holder;
    private Canvas canvas;
    // Game Image view where the canvas is rendered to.
    private SurfaceView gameView;

    /**
     * Screen ratio
     */
    private int screenWidth;
    private int screenHeight;

    /**
     * Game variables
     */
    // Coin count of given game
    private int currentCoinCount = 0;
    // Current coin spawn interval
    private int currentCoinSpawnInterval = 0;
    // Current obstacle spawn interval
    private int currentObstacleSpawnInterval = 0;
    // Current coin value
    private int currentCoinValue = coinStartValue;
    // Steps in which current coinSpawnInterval increases
    private int currentCoinSpawnIntervalStep = 1;
    // Steps in which currentObstacleSpawnInterval increases
    private int currentObstacleSpawnIntervalStep = 1;
    // Games coin spawn interval in tenth of seconds
    private float coinSpawnInterval = 5;
    // Games obstacle spawn interval in tenth of seconds
    private float obstacleInterval = 10;
    // Games tick
    private int gameTickRender = 10;
    // Games current tick
    private int currentTick = 0;
    // Spawns tick
    private int spawnTickRender = 500;
    // Spawns current tick
    private int spawnCurrentTick = 0;
    private int obstaclesPerSpawn = 1;
    private int level = 1;
    private int meters = 0;
    private int currentCurbOffset = 0;
    private boolean isWhite = false;

    // Game objects
    private List<Coin> coins = new ArrayList<>();
    private List<Obstacle> obstacles = new ArrayList<>();
    private Rect playerRect = new Rect();
    private Car car;

    // Paints
    private Paint tirePaint;
    private Paint coinPaint;
    private Paint coinCountTextPaint;
    private Paint obstaclePaint;
    private Paint redPaint;
    private Paint whitePaint;

    // Runtime
    private Resources resources;

    private Runnable gameOver;

    public Game(Runnable gameOver, SurfaceHolder surfaceHolder, SurfaceView gameView, Resources resources, Car car, int screenWidth, int screenHeight, float mappedYTilt) {
        this.gameOver = gameOver;
        this.holder = surfaceHolder;
        this.gameView = gameView;
        this.resources = resources;
        this.car = car;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.mappedYTilt = mappedYTilt;
        loadAssets();
        initialize();
    }

    private void initialize() {
        loopTimer.scheduleAtFixedRate(loopTimerTask, 1, 1);
        loopTimer.scheduleAtFixedRate(renderTimerTask, 16, 16);
        loopTimer.scheduleAtFixedRate(rampUpTimerTask, 15000, 15000);
    }

    private void loadAssets() {
        tirePaint = new Paint();
        tirePaint.setColor(Color.BLACK);
        coinPaint = new Paint();
        coinPaint.setColor(ResourcesCompat.getColor(resources, R.color.coin, null));
        coinPaint.setStrokeWidth(3);
        coinCountTextPaint = new Paint();
        coinCountTextPaint.setColor(Color.BLACK);
        coinCountTextPaint.setStrokeWidth(2);
        coinCountTextPaint.setTextSize(textSize);
        obstaclePaint = new Paint();
        obstaclePaint.setColor(ResourcesCompat.getColor(resources, R.color.obstacle, null));
        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
    }

    private void render() {
        canvas = holder.lockCanvas();
        if (canvas != null) {
            clearCanvas();
            drawCoins();
            drawObstacles();
            drawCurbs();
            drawPlayer();
            drawTopText();
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void gameOver() {
        gameOver.run();
    }

    private void loop() {
        currentTick++;
        if (currentTick > gameTickRender) {
            meters++;
            currentTick = 0;
            checkPlayerCoinCollision();
            checkPlayerObstacleCollision();
            moveObjects();
        }
        spawnCurrentTick++;
        if (spawnCurrentTick > spawnTickRender) {
            spawnCurrentTick = 0;
            spawning();
        }
    }

    private void rampUp() {
        if (gameTickRender > 0) gameTickRender -= 0.5;
        if (spawnCurrentTick > 0) spawnCurrentTick -= 0.5;
        if (coinSpawnInterval > 0) coinSpawnInterval -= 0.5;
        if (obstacleInterval > 0) obstacleInterval -= 0.5;
        if (obstacleInterval % 2 == 0 || obstacleInterval < 1) obstaclesPerSpawn++;
        currentCoinValue += 2;
        level++;
    }


    private void spawning() {
        if (currentCoinSpawnInterval > coinSpawnInterval) {
            currentCoinSpawnInterval = 0;
            spawnCoins(currentCoinValue);
        }
        currentCoinSpawnInterval += currentCoinSpawnIntervalStep;
        if (currentObstacleSpawnInterval > obstacleInterval) {
            currentObstacleSpawnInterval = 0;
            for (int i = 0; i < obstaclesPerSpawn; i++) spawnObstacle();
        }
        currentObstacleSpawnInterval += currentObstacleSpawnIntervalStep;
    }

    private void clearCanvas() {
        canvas.drawColor(ResourcesCompat.getColor(resources, R.color.track, null));
    }

    private void drawCoins() {
        for (Coin coin : coins) {
            canvas.drawCircle(coin.getX(), coin.getY(), coinSide / 2, coinPaint);
        }
    }

    private void drawCurbs() {
        boolean currentIsWhite = isWhite;
        for (int i = 0; i < screenHeight / curbLength + 5; i++) {
            Rect rect = new Rect();
            rect.top = (currentCurbOffset + (i + 1) * curbLength) - 200;
            rect.bottom = rect.top + curbLength;
            rect.left = 0;
            rect.right = curbWidth;
            canvas.drawRect(rect, currentIsWhite ? whitePaint : redPaint);
            rect.left = screenWidth - curbWidth;
            rect.right = screenWidth;
            canvas.drawRect(rect, currentIsWhite ? whitePaint : redPaint);
            currentIsWhite = !currentIsWhite;
        }
    }

    private void drawObstacles() {
        for (Obstacle obstacle : obstacles) {
            Rect rect = new Rect();
            rect.top = obstacle.getY();
            rect.left = obstacle.getX();
            rect.right = rect.left + obstacle.getWidth();
            rect.bottom = rect.top + obstacle.getHeight();
            canvas.drawRect(rect, obstaclePaint);
        }
    }

    private void drawTopText() {
        canvas.drawText("Coins: " + currentCoinCount + "$ Level: " + level + " Score: " + meters + "m", 50, 50, coinCountTextPaint);
    }

    public void drawPlayer() {
        if (canvas == null) return;
        int pos = (int) (screenWidth * (1 - mappedYTilt));
        playerRect.left = Math.max(pos - car.getWidth() / 2, 0);
        playerRect.right = Math.min(pos + car.getWidth() / 2, screenWidth);
        playerRect.top = screenHeight - car.getHeight();
        playerRect.bottom = screenHeight;
        Rect leftFrontWheel = new Rect();
        leftFrontWheel.left = playerRect.left - 20;
        leftFrontWheel.right = playerRect.right + 20;
        leftFrontWheel.top = playerRect.top + 15;
        leftFrontWheel.bottom = playerRect.top + 45;
        canvas.drawRect(leftFrontWheel, tirePaint);
        canvas.drawRect(playerRect, car.getPaint());
    }

    private void checkPlayerCoinCollision() {
        int coinSideHalf = coinSide / 2;
        coins = coins.stream().filter(coin -> {
            if (coin.getY() > screenHeight + 50) {
                return false;
            } else if (
                    coin.getY() > screenHeight - car.getHeight() &&
                            playerRect.intersect(coin.getX() - coinSideHalf, coin.getY() - coinSideHalf, coin.getX() + coinSideHalf, coin.getY() + coinSideHalf)
            ) {
                currentCoinCount += coin.getValue();
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    private void checkPlayerObstacleCollision() {
        obstacles = obstacles.stream().filter(obstacle -> {
            if (obstacle.getY() > screenHeight + obstacle.getHeight()) return false;
            else if (
                    obstacle.getY() + obstacle.getHeight() > screenHeight - car.getHeight() &&
                            playerRect.intersect(obstacle.getX(), obstacle.getY(), obstacle.getX() + obstacle.getWidth(), obstacle.getY() + obstacle.getHeight())
            ) {
                gameOver();
                return false;
            }
            return true;
        }).collect(Collectors.toList());
    }

    private void moveObjects() {
        for (Coin coin : coins) {
            coin.setY(coin.getY() + 5);
        }
        for (Obstacle obstacle : obstacles) {
            obstacle.setY(obstacle.getY() + 5);
        }
        if (currentCurbOffset >= curbLength) {
            isWhite = !isWhite;
            currentCurbOffset = 0;
        }
        currentCurbOffset += 5;
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
        int width = getRandomNumber(minObstacleWidth, maxObstacleWidth);
        int height = getRandomNumber(minObstacleHeight, maxObstacleHeight);
        int pos = getRandomNumber(0, screenWidth - width);
        boolean doesOverlap = true;
        while (doesOverlap) {
            pos = getRandomNumber(0, screenWidth - width);
            doesOverlap = false;
            for (Obstacle obstacle : obstacles) {
                if (pos > obstacle.getX() + width && pos < obstacle.getX() + obstacle.getWidth() && level < 10) {
                    doesOverlap = true;
                }
            }
        }
        obstacles.add(new Obstacle(pos, -height, width, height));
    }


    public void setMappedYTilt(float mappedYTilt) {
        this.mappedYTilt = 1 - mappedYTilt;
    }


    private int getRandomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public int getCurrentCoinCount() {
        return currentCoinCount;
    }

    public int getMeters() {
        return meters;
    }
}
