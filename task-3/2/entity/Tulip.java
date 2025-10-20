package entity;

import util.FlowerName;

public class Tulip extends Flower {

    public Tulip(double cost) {
        super(FlowerName.TULIP);
        setCost(cost);
    }

}