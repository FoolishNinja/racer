package ch.scs.cs.racer.models;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Garage model
 *
 * @author Carlo Schmid
 * @version 18.01.2021
 */
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
                2000,
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
                2500,
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
                4000,
                false,
                car4Paint
        ));
    }

    public List<Car> getCars() {
        return cars;
    }

    public Car getCarAtIndex(int index) {
        return cars.get(index);
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public boolean buyCar(int balance, int carIndex) {
        Car car = cars.get(carIndex);
        if (balance < car.getPrice()) return false;
        car.setHasBought(true);
        cars.set(carIndex, car);
        return true;
    }

    public void setAsBought(String carName) {
        for (Car car : cars) {
            if (car.getName().equals(carName)) car.setHasBought(true);
        }
    }

    public Set<String> getBoughtCarNames() {
        Set<String> boughtCars = new HashSet<>();
        for (Car car : cars) {
            if (car.isHasBought()) boughtCars.add(car.getName());
        }
        return boughtCars;
    }
}
