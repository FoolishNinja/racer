package ch.scs.cs.racer.models;

import android.graphics.Paint;

/**
 * Car model
 *
 * @author Carlo Schmid
 * @version 18.01.2021
 */
public class Car {
    private String name;
    // Speed in kmh
    private int speed;
    private int width;
    private int height;
    private int price;
    // Car is bought
    private boolean hasBought;
    private Paint paint;

    public Car(String name, int speed, int width, int height, int price, boolean hasBought, Paint paint) {
        this.name = name;
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.price = price;
        this.hasBought = hasBought;
        this.paint = paint;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpeed() {
        return speed;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getPrice() {
        return price;
    }

    public boolean isHasBought() {
        return hasBought;
    }

    public void setHasBought(boolean hasBought) {
        this.hasBought = hasBought;
    }

    public Paint getPaint() {
        return paint;
    }
}
