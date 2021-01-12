package ch.scs.cs.racer.models;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

public class Garage {
    private List<Car> cars = new ArrayList<>();

    public Garage() {
        // Starter car
        Paint car1Paint = new Paint();
        car1Paint.setColor(Color.RED);
        cars.add(new Car(
                "Red Fury",
                100,
                70,
                100,
                0,
                true,
                car1Paint
        ));
        Paint car2Paint = new Paint();
        car2Paint.setColor(Color.CYAN);
        cars.add(new Car(
                "Blue Sea",
                150,
                70,
                100,
                200,
                false,
                car2Paint
        ));
        Paint car3Paint = new Paint();
        car3Paint.setColor(Color.GREEN);
        cars.add(new Car(
                "Green Sider",
                170,
                80,
                120,
                250,
                false,
                car3Paint
        ));
        Paint car4Paint = new Paint();
        car4Paint.setColor(Color.BLUE);
        cars.add(new Car(
                "Wide Whale",
                100,
                200,
                100,
                400,
                false,
                car4Paint
        ));
    }

    public List<Car> getCars() {
        return cars;
    }

    public Car getCarAtIndex(int index)  {
        return cars.get(index);
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public boolean buyCar(int balance, int carIndex) {
        Car car = cars.get(carIndex);
        if(balance < car.getPrice()) return false;
        car.setHasBought(true);
        cars.set(carIndex, car);
        return true;
    }
}
