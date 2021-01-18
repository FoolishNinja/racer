package ch.scs.cs.racer.models;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import ch.scs.cs.racer.R;

/**
 * Game class
 *
 * @author Carlo Schmid
 * @version 18.01.2021
 */
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
    // Minimal width of an obstacle
    private final int minObstacleWidth = 100;
    // Maximal width of an obstacle
    private final int maxObstacleWidth = 200;
    // Minimal height of an obstacle
    private final int minObstacleHeight = 120;
    // Maximal height of an obstacle
    private final int maxObstacleHeight = 240;
    // Curbs width
    private final int curbLength = 70;
    // Curbs height
    private final int curbWidth = 50;

    /**
     * Loops setup
     */
    // Games loop timer which schedules all timer tasks
    private Timer loopTimer = new Timer(false);
    // Main game loop
    private TimerTask loopTimerTask = new TimerTask() {
        @Override
        public void run() {
            loop();
        }
    };
    // Game rendering loop
    private TimerTask renderTimerTask = new TimerTask() {
        @Override
        public void run() {
            render();
        }
    };
    // Games ramp up loop
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
    // Games surface holder
    private SurfaceHolder holder;
    // Game canvas where the painting happens
    private Canvas canvas;

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
    private long gameTickRender = 10;
    // Games current tick
    private int currentTick = 0;
    // Spawns tick
    private long spawnTickRender = 500;
    // Spawns current tick
    private int spawnCurrentTick = 0;
    // Count of obstacles per spawn
    private int obstaclesPerSpawn = 1;
    // Current level
    private int level = 1;
    // Meters traveled
    private int meters = 0;
    // Current curb offset for drawing
    private int currentCurbOffset = 0;
    // Is the current curb white
    private boolean isWhite = false;

    /**
     * Game objects
     */

    // All coins on screen
    private List<Coin> coins = new ArrayList<>();
    // All obstacles on screen
    private List<Obstacle> obstacles = new ArrayList<>();
    // Player car rectangle
    private Rect playerRect = new Rect();
    // Players car
    private Car car;

    /**
     * Paints
     */
    private Paint tirePaint;
    private Paint coinPaint;
    private Paint coinCountTextPaint;
    private Paint obstaclePaint;
    private Paint redPaint;
    private Paint whitePaint;

    /**
     * Runtime
     */
    // Game activity resources
    private Resources resources;
    // Runnable from game activity which triggers the gameover callback
    private Runnable gameOver;

    public Game(Runnable gameOver, SurfaceHolder surfaceHolder, Resources resources, Car car, int screenWidth, int screenHeight, float mappedYTilt) {
        this.gameOver = gameOver;
        this.holder = surfaceHolder;
        this.resources = resources;
        this.car = car;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.mappedYTilt = mappedYTilt;
        this.gameTickRender = 1000L / (long) car.getSpeed();
        this.spawnTickRender = 50000L / (long) car.getSpeed();
        loadAssets();
        initialize();
    }

    /**
     * Schedules all timer tasks
     */
    private void initialize() {
        loopTimer.scheduleAtFixedRate(loopTimerTask, 1, 1);
        loopTimer.scheduleAtFixedRate(renderTimerTask, 16, 16);
        loopTimer.scheduleAtFixedRate(rampUpTimerTask, 15000, 15000);
    }

    /**
     * Loads all colors
     */
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

    /**
     * Renders coins, obstacles, car, info text and curbs
     */
    private void render() {
        canvas = holder.lockCanvas();
        if (canvas != null) {
            clearCanvas();
            drawCurbs();
            drawCoins();
            drawObstacles();
            drawPlayer();
            drawTopText();
            holder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * Stops all loops and calls game activity gameover runnable
     */
    private void gameOver() {
        loopTimerTask.cancel();
        renderTimerTask.cancel();
        rampUpTimerTask.cancel();
        loopTimer.purge();
        gameOver.run();
    }

    /**
     * Game update loop, checks player coin and obstacle collision, moves all objects, calls spawning
     */
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

    /**
     * Increments games speed, coin values, obstacle count, coin and obstacle interval and level
     */
    private void rampUp() {
        double times = 0.0025 * (float) car.getSpeed();
        if (gameTickRender > 1 + times) gameTickRender -= times;
        if (spawnTickRender > 1 + times) spawnTickRender -= times;
        if (coinSpawnInterval > 0 + times) coinSpawnInterval -= times;
        if (obstacleInterval > 0 + times) obstacleInterval -= times;
        if (level % 2 == 0 || obstacleInterval < 1) obstaclesPerSpawn++;
        currentCoinValue += 1;
        level++;
    }


    /**
     * Spawns coins and obstacles
     */
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

    /**
     * Clears the canvas
     */
    private void clearCanvas() {
        canvas.drawColor(ResourcesCompat.getColor(resources, R.color.track, null));
    }

    /**
     * Draws all coins
     */
    private void drawCoins() {
        for (Coin coin : coins) {
            canvas.drawCircle(coin.getX(), coin.getY(), coinSide / 2, coinPaint);
        }
    }

    /**
     * Draws all curbs
     */
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

    /**
     * Draws all obstacles
     */
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

    /**
     * Draws info text
     */
    private void drawTopText() {
        canvas.drawText(resources.getString(R.string.coins) + currentCoinCount + '$' + resources.getString(R.string.level) + level + resources.getString(R.string.score) + meters + "m", 50, 50, coinCountTextPaint);
    }

    /**
     * Draws player
     */
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

    /**
     * Checks player collision with all coins
     */
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

    /**
     * Checks player collision with all obstacles, triggers gameover
     */
    private void checkPlayerObstacleCollision() {
        obstacles = obstacles.stream().filter(obstacle -> {
            if (obstacle.getY() > screenHeight + obstacle.getHeight()) return false;
            else if (
                    obstacle.getY() + obstacle.getHeight() > screenHeight - car.getHeight() &&
                            playerRect.intersect(obstacle.getX(), obstacle.getY(), obstacle.getX() + obstacle.getWidth(), obstacle.getY() + obstacle.getHeight())
            ) {
                gameOver();
                return true;
            }
            return true;
        }).collect(Collectors.toList());
    }

    /**
     * Moves coins, curbs and obstacles
     */
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

    /**
     * Spawns random amount of coins with given value
     *
     * @param value Value of the coins
     */
    private void spawnCoins(int value) {
        spawnCoin(value, getRandomNumber(minCoinsPerSpawn, maxCoinsPerSpawn));
    }

    /**
     * Spawns coins with given value and count
     *
     * @param value Value of each coin
     * @param count Amount of coins
     */
    private void spawnCoin(int value, int count) {
        int pos = getRandomNumber(curbWidth, screenWidth - curbWidth - coinSide);
        for (int i = 0; i < count; i++) {
            coins.add(new Coin(pos, -(i * (coinSide + coinSpacing)), value));
        }
    }

    /**
     * Spawns an obstacle
     */
    private void spawnObstacle() {
        int width = getRandomNumber(minObstacleWidth, maxObstacleWidth);
        int height = getRandomNumber(minObstacleHeight, maxObstacleHeight);
        int pos = getRandomNumber(curbWidth, screenWidth - width - curbWidth);
        boolean doesOverlap = true;
        while (doesOverlap) {
            pos = getRandomNumber(curbWidth, screenWidth - width - curbWidth);
            doesOverlap = false;
            for (Obstacle obstacle : obstacles) {
                if (pos > obstacle.getX() + width && pos < obstacle.getX() + obstacle.getWidth() && level < 10) {
                    doesOverlap = true;
                }
            }
        }
        obstacles.add(new Obstacle(pos, -height, width, height));
    }

    /**
     * Sets the current mappedYTilt, called by Game Activity
     *
     * @param mappedYTilt
     */
    public void setMappedYTilt(float mappedYTilt) {
        this.mappedYTilt = mappedYTilt;
    }


    /**
     * Get a random number between
     *
     * @param min lower bound
     * @param max upper bound
     * @return random number between bounds
     */
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
