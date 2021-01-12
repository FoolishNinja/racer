package ch.scs.cs.racer.models;

import android.graphics.Paint;

public class Car {
    private String name;
    private int speed;
    private int width;
    private int height;
    private int price;
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

    public void setSpeed(int speed) {
        this.speed = speed;
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

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
