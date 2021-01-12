package ch.scs.cs.racer.models;

public class Shop {
    private Garage garage = new Garage();
    private int balance = 0;

    public boolean buyCar(int index) {
        boolean bought = garage.buyCar(balance, index);
        if(bought) {
            balance -= garage.getCarAtIndex(index).getPrice();
        }
        return bought;
    }
}
