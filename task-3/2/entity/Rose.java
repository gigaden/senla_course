package entity;

import util.FlowerName;

public class Rose extends Flower {

    public Rose(double cost) {
        super(FlowerName.ROSE);
        setCost(cost);
    }

}