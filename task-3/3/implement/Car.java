package implement;

import interfaces.IProduct;
import interfaces.IProductPart;

public class Car implements IProduct {

    private final String productName;
    private IProductPart part1;
    private IProductPart part2;
    private IProductPart part3;

    public Car() {
        productName = "Автомобиль";
    }

    public String getProductName() {
        return productName;
    }

    @Override
    public void installFirstPart(IProductPart part1) {
        this.part1 = part1;
        System.out.printf("Устанавливаем первую часть: %s", part1.toString());
    }

    @Override
    public void installSecondPart(IProductPart part2) {
        this.part2 = part2;
        System.out.printf("Устанавливаем вторую часть: %s", part2.toString());
    }

    @Override
    public void installThirdPart(IProductPart part3) {
        this.part3 = part3;
        System.out.printf("Устанавливаем третью часть: %s", part3.toString());
    }

    @Override
    public String toString() {
        return "Car{" +
                "productName='" + productName + '\'' +
                ", part1=" + part1 +
                ", part2=" + part2 +
                ", part3=" + part3 +
                '}';
    }
}
