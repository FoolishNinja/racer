package ch.scs.cs.racer.models;

/**
 * Coin model
 *
 * @author Carlo Schmid
 * @version 18.01.2021
 */
public class Coin {
    // X Pos of coin
    private int x;
    // Y Pos of coin
    private int y;
    // Coins value in $
    private int value;

    public Coin(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getValue() {
        return value;
    }
}
